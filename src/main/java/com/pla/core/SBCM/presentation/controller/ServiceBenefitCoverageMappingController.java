package com.pla.core.SBCM.presentation.controller;

import com.pla.core.SBCM.application.command.CreateSBCMCommand;
import com.pla.core.SBCM.application.service.SBCMService;
import com.pla.core.SBCM.domain.model.ServiceBenefitCoverageMapping;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * Created by Mohan Sharma on 12/24/2015.
 */
@RequestMapping(value = "/core/sbcm")
@RestController
public class ServiceBenefitCoverageMappingController {
    @Autowired
    CommandGateway commandGateway;
    @Autowired
    SBCMService sbcmService;

    @RequestMapping(value = "/getsbcmview" ,method= RequestMethod.GET)
    public ModelAndView getsbcmview(){
        ModelAndView modelAndView= new ModelAndView();
        modelAndView.addObject("createsbcmcommand",new CreateSBCMCommand());
        modelAndView.setViewName("pla/core/sbcm/createsbcm");
        return modelAndView;
    }

   @RequestMapping(value="/getAllPlanWithRelatedBenefitCoverages" ,method =  RequestMethod.GET)
    public List<Map<String,Object>> getAllPlanWithRelatedBenefitCoverages(){
       return sbcmService.getAllPlanWithCoverageAndBenefits();
   }

    @RequestMapping(value="/getAllServicesFromHCPRate" ,method =  RequestMethod.GET)
    public List<String> getAllServicesFromHCPRate(){
        return sbcmService.getAllServicesFromHCPRate();
    }

    @RequestMapping(value = "/createServiceBenefitCoverageMapping", method = RequestMethod.GET)
    public Result createServiceBenefitCoverageMapping(@ModelAttribute("createsbcmcommand") CreateSBCMCommand createSBCMCommand, BindingResult result){
        if(result.hasErrors()){
            return Result.failure("Error creating ServiceBenefitCoverageMapping", result.getAllErrors());
        }
        try {
        ServiceBenefitCoverageMapping serviceBenefitCoverageMapping = commandGateway.sendAndWait(createSBCMCommand);
            return Result.success("serviceBenefitCoverageMapping created successfully", serviceBenefitCoverageMapping.getServiceBenefitCoverageMappingId());
        } catch(Exception e){
            return Result.failure(e.getMessage());
        }
    }

}