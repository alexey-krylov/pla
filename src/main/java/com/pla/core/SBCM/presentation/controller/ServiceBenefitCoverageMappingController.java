package com.pla.core.SBCM.presentation.controller;

import com.pla.core.SBCM.application.command.CreateSBCMCommand;
import com.pla.core.SBCM.application.command.UpdateSBCMCommand;
import com.pla.core.SBCM.application.service.SBCMService;
import com.pla.core.SBCM.domain.model.ServiceBenefitCoverageMapping;
import com.pla.core.SBCM.domain.model.ServiceBenefitCoverageMappingId;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

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
        modelAndView.setViewName("pla/core/SBCM/createsbcm");
        return modelAndView;
    }

    @RequestMapping(value="/getAllPlanWithRelatedBenefitCoverages" ,method =  RequestMethod.GET)
    public List<Map<String,Object>> getAllPlanWithRelatedBenefitCoverages(){
        return sbcmService.getAllPlanWithCoverageAndBenefits();
    }

    @RequestMapping(value="/getAllServicesFromHCPRate" ,method =  RequestMethod.GET)
    public List<String> getAllServicesFromHCPRate(@RequestParam String planCode){
        return sbcmService.getAllServicesFromHCPRate(planCode);
    }

    @RequestMapping(value = "/createServiceBenefitCoverageMapping", method = RequestMethod.POST)
    public Result createServiceBenefitCoverageMapping(@Valid @RequestBody CreateSBCMCommand createSBCMCommand, BindingResult result, ModelMap modelMap){
        if(result.hasErrors()){
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", result);
            return Result.failure("Error creating ServiceBenefitCoverageMapping", result.getAllErrors());
        }
        try {
            ServiceBenefitCoverageMapping serviceBenefitCoverageMapping = commandGateway.sendAndWait(createSBCMCommand);
            return Result.success("serviceBenefitCoverageMapping created successfully", serviceBenefitCoverageMapping.getServiceBenefitCoverageMappingId());
        } catch(Exception e){
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value="/allsbcmview",method = RequestMethod.GET)
    public ModelAndView allsbcmview(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("pla/core/SBCM/searchsbcm");
        return modelAndView;
    }

    @RequestMapping(value="/getSBCMForGivenPage",method = RequestMethod.GET)
    public List<UpdateSBCMCommand> getSBCMForGivenPage(@RequestParam int pageNo){
        return sbcmService.getSBCMForGivenPage(pageNo);
    }

    @RequestMapping(value="/numberOfSBCMAvailable",method = RequestMethod.GET)
    public @ResponseBody int getSBCMForGivenPage(){
        return sbcmService.numberOfSBCMAvailable();
    }

    @RequestMapping(value="/getAllSBCM",method = RequestMethod.GET)
    public List<UpdateSBCMCommand> getAllSBCM(){
        return sbcmService.getAllSBCM();
    }

    @RequestMapping(value="/getSBCMBySBCMId",method = RequestMethod.GET)
    public CreateSBCMCommand getSBCMBySBCMId(@RequestParam String serviceBenefitCoverageMappingId, HttpServletResponse response) throws IOException {
        if (isEmpty(serviceBenefitCoverageMappingId)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "serviceBenefitCoverageMappingId cannot be null");
        }
        return sbcmService.getSBCMBySBCMId(new ServiceBenefitCoverageMappingId(serviceBenefitCoverageMappingId));
    }

    @RequestMapping(value="/updateSBCMStatus", method = RequestMethod.POST)
    public  Result  updateStatus( @Valid @RequestBody UpdateSBCMCommand updateStatusCommand, BindingResult bindingResult, ModelMap modelMap){
        if(bindingResult.hasErrors()){
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("Error Creating When  Updated The Status",bindingResult.getAllErrors());
        }
        try {
            return commandGateway.sendAndWait(updateStatusCommand) ?  Result.success("Status Updated Successfully") :  Result.failure();
        } catch (Exception e){
            return Result.failure(e.getMessage());
        }
    }
}
