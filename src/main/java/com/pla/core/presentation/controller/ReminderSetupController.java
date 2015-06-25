package com.pla.core.presentation.controller;

import com.google.common.collect.Maps;
import com.pla.core.application.service.notification.NotificationService;
import com.pla.core.domain.exception.NotificationException;
import com.pla.core.dto.NotificationTemplateDto;
import com.pla.core.query.NotificationFinder;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by pradyumna on 18-06-2015.
 */
@Controller
@RequestMapping(value = "/core/notification")
public class ReminderSetupController {

    private NotificationFinder notificationFinder;
    private NotificationService notificationService;

    @Autowired
    public ReminderSetupController(NotificationFinder notificationFinder, NotificationService notificationService) {
        this.notificationFinder = notificationFinder;
        this.notificationService = notificationService;
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
                String roleType = notificationRoleMapping.get("roleType");
                checkArgument(roleType != null, "role type cannot be null");
                boolean isValidProcess = lineOfBusinessEnum.isValidProcess(processType);
                if (!isValidProcess) {
                    throw new NotificationException("The process " + processType + " is not associated with " + lineOfBusinessEnum);
                }
                notificationService.createNotificationRoleMapping(roleType, lineOfBusinessEnum, processType);
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
    @ResponseBody
    public Callable<ResponseEntity> uploadNotification(@RequestBody NotificationTemplateDto notificationTemplateDto) throws IOException {
        return ()-> {
            try {
                MultipartFile template = notificationTemplateDto.getTemplate();
                boolean isCreated = notificationService.uploadNotificationTemplate(notificationTemplateDto.getLineOfBusiness(), notificationTemplateDto.getProcessType(),
                        notificationTemplateDto.getWaitingFor(), notificationTemplateDto.getReminderType(), template.getBytes());
                if (!isCreated) {
                    return new ResponseEntity(Result.failure("Error in uploading the notification template"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } catch (RuntimeException e) {
                return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(Result.success("Notification Template uploaded successfully"), HttpStatus.OK);
        };
    }


    @RequestMapping(value = "/reloadnotification", method = RequestMethod.POST)
    @ResponseBody
    public Callable<ResponseEntity> reloadNotification(@RequestBody NotificationTemplateDto notificationTemplateDto) throws IOException {
        return ()-> {
            try {
                MultipartFile template = notificationTemplateDto.getTemplate();
                boolean isCreated = notificationService.reloadNotificationTemplate(notificationTemplateDto.getNotificationTemplateId(), template.getBytes());
                if (!isCreated) {
                    return new ResponseEntity(Result.failure("Error in Updating the notification template"), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            } catch (RuntimeException e) {
                return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity(Result.success("Notification Template updated successfully"), HttpStatus.OK);
        };
    }

}
