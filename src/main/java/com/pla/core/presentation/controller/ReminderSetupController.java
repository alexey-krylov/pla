package com.pla.core.presentation.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.application.service.notification.NotificationService;
import com.pla.core.application.service.notification.NotificationTemplateService;
import com.pla.core.domain.exception.NotificationException;
import com.pla.core.domain.model.notification.NotificationId;
import com.pla.core.domain.model.notification.NotificationRoleResolver;
import com.pla.core.domain.model.notification.NotificationTemplate;
import com.pla.core.domain.model.notification.NotificationTemplateId;
import com.pla.core.dto.NotificationEmailDto;
import com.pla.core.dto.NotificationTemplateDto;
import com.pla.core.query.NotificationFinder;
import com.pla.sharedkernel.application.CreateNotificationHistoryCommand;
import com.pla.sharedkernel.application.CreateQuotationNotificationCommand;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.exception.ProcessInfoException;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.service.MailService;
import org.apache.commons.io.IOUtils;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.pla.core.domain.exception.NotificationException.raiseProcessIsNotValid;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by pradyumna on 18-06-2015.
 */
@Controller
@RequestMapping(value = "/core/notification")
public class ReminderSetupController {

    private NotificationFinder notificationFinder;
    private NotificationService notificationService;
    private NotificationTemplateService notificationTemplateService;
    private CommandGateway commandGateway;
    private MailService mailService;

    @Autowired
     public ReminderSetupController(NotificationFinder notificationFinder, NotificationService notificationService,CommandGateway commandGateway, MailService mailService,NotificationTemplateService notificationTemplateService) {
        this.notificationFinder = notificationFinder;
        this.notificationService = notificationService;
        this.commandGateway = commandGateway;
        this.mailService = mailService;
        this.notificationTemplateService = notificationTemplateService;
    }

    @RequestMapping(value = "/rolelist")
    public Callable<ModelAndView> displayAllRoles() {
        return () -> {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("/pla/core/notification/rolelist");
            modelAndView.addObject("roleList", notificationFinder.findAllNotificationRole());
            return modelAndView;
        };
    }


    @RequestMapping(value = "/templatelist")
    public Callable<ModelAndView> displayTemplateList() {
        return () -> {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("NotificationTemplateDto", new NotificationTemplateDto());
            modelAndView.setViewName("/pla/core/notification/templatelist");
            modelAndView.addObject("templateList", notificationFinder.findAllTemplates());
            return modelAndView;
        };
    }

    @RequestMapping(value = "/createnotificationrolemapping", method = RequestMethod.POST)
    @ResponseBody
    public Callable<ResponseEntity> createNotificationRoleMapping(@RequestBody Map<String, String> notificationRoleMapping) {
        return ()->{
            try {
                LineOfBusinessEnum lineOfBusinessEnum = LineOfBusinessEnum.valueOf(notificationRoleMapping.get("lineOfBusiness"));
                ProcessType processType = ProcessType.valueOf(notificationRoleMapping.get("processType"));
                String uiRole = notificationRoleMapping.get("roleType");
                boolean isValidProcess = lineOfBusinessEnum.isValidProcess(processType);
                if (!isValidProcess) {
                    raiseProcessIsNotValid(processType.toString(),lineOfBusinessEnum.toString());
                }
                String systemRole = NotificationRoleResolver.notificationRoleResolver(lineOfBusinessEnum, uiRole);
                if (isEmpty(systemRole)){
                    return new ResponseEntity(Result.failure("No mapping available for the given combination"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
                notificationService.createNotificationRoleMapping(systemRole, lineOfBusinessEnum, processType);
            } catch (NotificationException e) {
                return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (DataIntegrityViolationException e) {
                return new ResponseEntity(Result.failure("Notification Role mapping has already configured"), HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (RuntimeException e) {
                return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(Result.success("Role Notification Mapping Configured successfully"), HttpStatus.OK);
        };
    }

    @RequestMapping(value = "/deletenotificationrolemapping",method = RequestMethod.POST)
    @ResponseBody
    public Callable<ResponseEntity> deleteNotificationRoleMapping(@RequestBody Map<String, String> notificationRoleMapping){
        return ()->{
            try {
                LineOfBusinessEnum lineOfBusinessEnum = LineOfBusinessEnum.valueOf(notificationRoleMapping.get("lineOfBusiness"));
                ProcessType processType = ProcessType.valueOf(notificationRoleMapping.get("processType"));
                String systemRole = notificationRoleMapping.get("roleType");
                boolean isValidProcess = lineOfBusinessEnum.isValidProcess(processType);
                if (!isValidProcess) {
                    raiseProcessIsNotValid(processType.toString(),lineOfBusinessEnum.toString());
                }
                notificationService.deleteNotificationRoleMapping(systemRole, lineOfBusinessEnum, processType);
            } catch (NotificationException e) {
                return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (DataIntegrityViolationException e) {
                return new ResponseEntity(Result.failure("Error occurred in deleting the notification Role mapping"), HttpStatus.INTERNAL_SERVER_ERROR);
            } catch (RuntimeException e) {
                return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(Result.success("Notification Role Mapping deleted successfully"), HttpStatus.OK);
        };
    }


    @RequestMapping(value = "/uploadnotification", method = RequestMethod.POST)
    public ModelAndView uploadNotification(@Valid @ModelAttribute NotificationTemplateDto notificationTemplateDto,BindingResult bindingResult) throws IOException {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/underwriter/routingLevelSetup/createRoutingLevelSetup");
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("message", "Error in uploading the notification template");
            return modelAndView;
        }
        MultipartFile template = notificationTemplateDto.getFile();
        if (!("text/plain".equals(notificationTemplateDto.getFile().getContentType()))) {
            modelAndView.addObject("message", "Please upload a valid file");
            return modelAndView;
        }
        try {
            boolean isCreated = notificationService.uploadNotificationTemplate(notificationTemplateDto.getLineOfBusiness(), notificationTemplateDto.getProcessType(),
                    notificationTemplateDto.getWaitingFor(), notificationTemplateDto.getReminderType(), template.getBytes());
            if (!isCreated) {
                modelAndView.addObject("message", "Error in uploading the notification template");
                return modelAndView;
            }
        } catch (RuntimeException e) {
            modelAndView.addObject("message", e.getMessage());
            return modelAndView;
        }
        modelAndView.addObject("message", "Notification Template uploaded successfully");
        return modelAndView;
    }

    @RequestMapping(value = "/getremindertype/{notificationTemplateId}", method = RequestMethod.GET,consumes = MediaType.ALL_VALUE)
    @ResponseBody
    public void getReminderFile(@PathVariable("notificationTemplateId") NotificationTemplateId notificationTemplateId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("text/plain");
        OutputStream outputStream = response.getOutputStream();
        NotificationTemplate  notificationTemplate = notificationService.getReminderFile(notificationTemplateId);
        String fileName =  notificationTemplate.getFileName();
        response.setHeader("content-disposition", "attachment; filename=" + fileName + ".txt");
        IOUtils.write(notificationTemplate.getReminderFile(), outputStream);
        outputStream.flush();
        outputStream.close();
        response.flushBuffer();
    }

    @RequestMapping(value = "/getnotificationtemplate/{notificationTemplateId}",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getNotificationTemplateById(@PathVariable("notificationTemplateId") NotificationTemplateId notificationTemplateId){
        return notificationFinder.getNotificationTemplateById(notificationTemplateId);
    }

    @RequestMapping(value = "/deletetemplate/{notificationTemplateId}",method = RequestMethod.DELETE)
    @ResponseBody
    public Callable<ResponseEntity> deleteNotificationTemplate(@PathVariable("notificationTemplateId") NotificationTemplateId notificationTemplateId){
        return()->{
            boolean isDeleted =  notificationService.deleteNotificationTemplate(notificationTemplateId);
            if (!isDeleted){
                return new ResponseEntity(Result.failure("Unable to delete the Notification Template"),HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(Result.success("Notification Template deleted successfully"),HttpStatus.OK);
        };
    }

    @RequestMapping(value = "/getnotification",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getNotification(){
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        List<Map<String,Object>> notificationList =  notificationFinder.getNotificationByRole(authorities);
        return notificationList.parallelStream().map(new Function<Map<String,Object>, Map<String,Object>>() {
            @Override
            public Map<String, Object> apply(Map<String, Object> notificationMap) {
               ProcessType processType = ProcessType.valueOf(notificationMap.get("processType").toString());
                LineOfBusinessEnum lineOfBusinessEnum  = LineOfBusinessEnum.valueOf(notificationMap.get("lineOfBusiness").toString());
                try {
                    notificationMap.put("requestNumber",notificationTemplateService.getRequestNumberBy(lineOfBusinessEnum,processType,notificationMap.get("requestNumber").toString()));
                } catch (ProcessInfoException e) {
                    e.printStackTrace();
                }
                return notificationMap;
            }
        }).collect(Collectors.toList());
    }


    @RequestMapping(value = "/getprocessbylob/{lineOfBusiness}",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getProcessByLineOfBusiness(@PathVariable("lineOfBusiness") LineOfBusinessEnum lineOfBusiness){
        List<Map<String,Object>> processList = Lists.newArrayList();
        lineOfBusiness.getProcessTypeList().forEach(process->{
            Map<String,Object> processMap = Maps.newLinkedHashMap();
            processMap.put("processType",process.name());
            processMap.put("description",process.toString());
            processList.add(processMap);
        });
        return processList;
    }

    @RequestMapping(value = "/getwaitingfor/{lineOfBusiness}/{process}",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getWaitingForByProcess(@PathVariable("lineOfBusiness") LineOfBusinessEnum lineOfBusiness,@PathVariable("process") ProcessType process) {
        return notificationService.getWaitingForBy(lineOfBusiness, process);
    }


    @RequestMapping(value = "/getnotificationtype/{lineOfBusiness}/{process}/{waitingFor}",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getNotificationType(@PathVariable("lineOfBusiness") LineOfBusinessEnum lineOfBusiness,@PathVariable("process") ProcessType process,@PathVariable("waitingFor") WaitingForEnum waitingFor) {
        return notificationService.getNotificationTypeBy(lineOfBusiness, process, waitingFor);
    }

    @RequestMapping(value = "/getnotificationrolelist", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getNotificationRoleList(){
        return  notificationFinder.findAllNotificationRole();
    }

    @RequestMapping(value = "/getnotificationtemplatelist", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getNotificationTemplateList(){
        return  notificationFinder.findAllTemplates();
    }

    @RequestMapping(value = "/openemailnotification/{notificationId}", method = RequestMethod.GET)
    public ModelAndView openEmailPage(@PathVariable("notificationId") NotificationId notificationId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/pla/core/notification/emailNotification");
        modelAndView.addObject("mailContent", notificationFinder.emailContent(notificationId));
        return modelAndView;
    }

    @RequestMapping(value = "/emailnotification", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity sendEmail(@RequestBody NotificationEmailDto notificationEmailDto,BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(Result.failure("Email cannot be sent due to wrong data"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        try {
            mailService.sendMailWithAttachment(notificationEmailDto.getSubject(),notificationEmailDto.getMailContent(),Lists.newArrayList(),notificationEmailDto.getRecipientMailAddress());
            CreateNotificationHistoryCommand createNotificationHistoryCommand = notificationService.generateHistoryDetail(notificationEmailDto.getNotificationId(),notificationEmailDto.getRecipientMailAddress());
            commandGateway.send(createNotificationHistoryCommand);
        } catch (Exception e) {
            new ResponseEntity(Result.failure(e.getMessage()),HttpStatus.OK);
        }
        return new ResponseEntity(Result.success("Email sent successfully"), HttpStatus.OK);
    }

    @RequestMapping(value = "/createnotification", method = RequestMethod.POST)
    @ResponseBody
    public Result createQuotationNotification(@RequestBody CreateQuotationNotificationCommand createQuotationNotificationCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in creating Notification", bindingResult.getAllErrors());
        }
        try {
            commandGateway.sendAndWait(createQuotationNotificationCommand);
        } catch (NotificationException e) {
            return Result.failure(e.getMessage());
        }
        return Result.success("Notification created successfully");
    }


}
