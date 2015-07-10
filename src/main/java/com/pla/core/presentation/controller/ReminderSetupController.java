package com.pla.core.presentation.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.application.service.notification.NotificationService;
import com.pla.core.domain.exception.NotificationException;
import com.pla.core.domain.model.notification.NotificationRoleResolver;
import com.pla.core.domain.model.notification.NotificationTemplate;
import com.pla.core.domain.model.notification.NotificationTemplateId;
import com.pla.core.dto.NotificationTemplateDto;
import com.pla.core.query.NotificationFinder;
import com.pla.sharedkernel.application.CreateQuotationNotificationCommand;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.apache.commons.io.FileUtils;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

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
    private CommandGateway commandGateway;

    @Autowired
    public ReminderSetupController(NotificationFinder notificationFinder, NotificationService notificationService,CommandGateway commandGateway) {
        this.notificationFinder = notificationFinder;
        this.notificationService = notificationService;
        this.commandGateway = commandGateway;
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
                String systemRole = NotificationRoleResolver.notificationRoleResolver(lineOfBusinessEnum,uiRole);
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
                String uiRole = notificationRoleMapping.get("roleType");
                boolean isValidProcess = lineOfBusinessEnum.isValidProcess(processType);
                if (!isValidProcess) {
                    raiseProcessIsNotValid(processType.toString(),lineOfBusinessEnum.toString());
                }
                String systemRole = NotificationRoleResolver.notificationRoleResolver(lineOfBusinessEnum,uiRole);
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

    @RequestMapping(value = "/isnotificationtemplateexists",method = RequestMethod.POST)
    @ResponseBody
    public Callable<ResponseEntity> isNotificationTemplateExists(@RequestBody NotificationTemplateDto notificationTemplateDto){
        return ()-> {
            boolean isTemplateExist = notificationService.isNotificationTemplateExists(notificationTemplateDto.getLineOfBusiness(), notificationTemplateDto.getProcessType(),
                    notificationTemplateDto.getWaitingFor(), notificationTemplateDto.getReminderType());
            Map<String,Boolean> notificationTemplateMap = Maps.newLinkedHashMap();
            notificationTemplateMap.put("isTemplateExist",false);
            if (isTemplateExist) {
                notificationTemplateMap.put("isTemplateExist",true);
                return new ResponseEntity(Result.success("",notificationTemplateMap),HttpStatus.OK) ;
            }
            return new ResponseEntity(Result.success("",notificationTemplateMap),HttpStatus.OK);
        };
    }


    /*
    * @TODO check the file extension before uploading
    * */
    @RequestMapping(value = "/uploadnotification", method = RequestMethod.POST)
    public Callable<ResponseEntity> uploadNotification(@Valid @ModelAttribute NotificationTemplateDto notificationTemplateDto,BindingResult bindingResult) throws IOException {
        return ()-> {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("pla/core/underwriter/routingLevelSetup/createRoutingLevelSetup");
            if (bindingResult.hasErrors()) {
                modelAndView.addObject("message", "Error in uploading the notification template");
                return new ResponseEntity(modelAndView, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            MultipartFile template = notificationTemplateDto.getFile();
            if (!("text/plain".equals(notificationTemplateDto.getFile().getContentType()))) {
                modelAndView.addObject("message", "Please upload a valid file");
                return new ResponseEntity(modelAndView, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            try {

                boolean isCreated = notificationService.uploadNotificationTemplate(notificationTemplateDto.getLineOfBusiness(), notificationTemplateDto.getProcessType(),
                        notificationTemplateDto.getWaitingFor(), notificationTemplateDto.getReminderType(), template.getBytes());
                if (!isCreated) {
                    modelAndView.addObject("message", "Error in uploading the notification template");
                    return new ResponseEntity(modelAndView, HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } catch (RuntimeException e) {
                modelAndView.addObject("message", e.getMessage());
                return new ResponseEntity(modelAndView, HttpStatus.INTERNAL_SERVER_ERROR);
            }
            modelAndView.addObject("message", "Notification Template uploaded successfully");
            return new ResponseEntity(modelAndView, HttpStatus.OK);
        };
    }

    @RequestMapping(value = "/getnotificationtemplate/{notificationTemplateId}",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getNotificationTemplateById(@PathVariable("notificationTemplateId") NotificationTemplateId notificationTemplateId){
        return notificationFinder.getNotificationTemplateById(notificationTemplateId);
    }

    @RequestMapping(value = "/getremindertype/{notificationTemplateId}", method = RequestMethod.GET)
    @ResponseBody
    public void getReminderFile(@PathVariable("notificationTemplateId") NotificationTemplateId notificationTemplateId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("text/plain");
        OutputStream outputStream = response.getOutputStream();
        NotificationTemplate  notificationTemplate = notificationService.getReminderFile(notificationTemplateId);
        String fileName =  notificationTemplate.getFileName();
        response.setHeader("content-disposition", "attachment; filename=" + fileName+ ".vm");
        outputStream.write(notificationTemplate.getReminderFile());
        outputStream.flush();
        outputStream.close();
        response.flushBuffer();
    }

    @RequestMapping(value = "/createnotification", method = RequestMethod.POST)
    public
    @ResponseBody
    Result createQuotationNotification(@RequestBody CreateQuotationNotificationCommand createQuotationNotificationCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in creating benefit", bindingResult.getAllErrors());
        }
        try {
            commandGateway.sendAndWait(createQuotationNotificationCommand);
        } catch (NotificationException e) {
            return Result.failure(e.getMessage());
        }
        return Result.success("Notification created successfully");
    }

    @RequestMapping(value = "/getnotification",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getNotification(){
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return notificationFinder.getNotificationByRole(authorities);
    }

    @RequestMapping(value = "/gettemplate",method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity getTemplate(@RequestBody NotificationTemplateDto notificationTemplateDto, HttpServletRequest request, HttpServletResponse response) throws IOException {
        OutputStream outputStream = null;
        try {
            response.reset();
            outputStream = response.getOutputStream();
            response.setContentType("text/plain");
            String fileName = notificationTemplateDto.getLineOfBusiness().name()+ "_" + notificationTemplateDto.getProcessType().name()+ "_" + notificationTemplateDto.getWaitingFor().name() + "_" + notificationTemplateDto.getReminderType().name();
            response.setHeader("content-disposition", "attachment; filename=" + fileName.toLowerCase() + ".vm");
            File tempFile = new File("./src/main/resources/emailtemplate/notification/" + fileName.toLowerCase() + ".vm");
            byte[] template = FileUtils.readFileToByteArray(tempFile);
            outputStream.write(template);
            outputStream.flush();
        }catch (Exception e) {
            if (outputStream != null) {
                outputStream.close();
                return new ResponseEntity(Result.failure("The Template Not Found for the given Line Of business"), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        outputStream.close();
        response.flushBuffer();
        return new ResponseEntity(Result.success(),HttpStatus.OK);
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
        return notificationService.getWaitingForBy(lineOfBusiness,process);
    }


    @RequestMapping(value = "/getnotificationtype/{lineOfBusiness}/{process}/{waitingFor}",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getNotificationType(@PathVariable("lineOfBusiness") LineOfBusinessEnum lineOfBusiness,@PathVariable("process") ProcessType process,@PathVariable("waitingFor") WaitingForEnum waitingFor) {
        return notificationService.getNotificationTypeBy(lineOfBusiness,process,waitingFor);
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
}
