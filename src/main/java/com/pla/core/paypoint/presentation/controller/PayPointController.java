package com.pla.core.paypoint.presentation.controller;

import com.google.common.base.Preconditions;
import com.pla.core.paypoint.application.command.PayPointCommand;
import com.pla.core.paypoint.application.service.PayPointService;
import com.pla.core.paypoint.domain.model.PayPoint;
import com.pla.core.paypoint.domain.model.PayPointGrade;
import com.pla.core.paypoint.domain.model.PayPointId;
import com.pla.core.paypoint.domain.model.PayPointStatus;
import com.pla.core.paypoint.exception.PaypointApplicationException;
import com.pla.core.paypoint.query.PaypointFinder;
import com.pla.core.query.MasterFinder;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Created by Rudra on 12/7/2015.
 */
@RestController
@RequestMapping(value="/core/paypoint")
public class PayPointController {

    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(PayPointController.class);

    @Autowired
    private MasterFinder masterFinder;
    @Autowired
    private PaypointFinder paypointFinder;
    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private PayPointService payPointService;

    @RequestMapping(value ="/create", method = RequestMethod.POST)
    @ResponseBody
    public Result createPayPoint(@RequestBody @Valid PayPointCommand paypointCommand,BindingResult bindingResult ) {
        if (bindingResult.hasErrors()) {
            Result.failure("Error in creating Paypoint", bindingResult.getAllErrors());
        }
        try {
            PayPoint payPoint = commandGateway.sendAndWait(paypointCommand);
            return Result.success("paypoint created successfully.", payPoint.getPayPointId());
        } catch (PaypointApplicationException e) {
            LOGGER.error("Error in creating paypoint", e);
            return Result.failure(e.getMessage());
        } catch (Exception e) {
            LOGGER.error("Error in creating paypoint", e);
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/openpaypointpage", method = RequestMethod.GET)
    public ModelAndView createPayPoint() {
        return new ModelAndView("pla/core/paypoint/createpaypoint");
    }

    @RequestMapping(value="/update",method=RequestMethod.GET)
    public ModelAndView update(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("pla/core/paypoint/createpaypoint");
        return modelAndView;
    }

    @RequestMapping(value ="/getAllList" , method=RequestMethod.GET)
    public ModelAndView search(){
        ModelAndView modelAndView = new ModelAndView();
        List<PayPointCommand> payPointCommandList = payPointService.getAllPayPoints();
        modelAndView.addObject("searchResult", payPointCommandList);
        modelAndView.setViewName("pla/core/paypoint/searchpaypoint");
        return modelAndView;
    }

    @RequestMapping(value ="/view", method=RequestMethod.GET)
    @ResponseBody
    public ModelAndView view( @RequestParam String payPointId){
        Preconditions.checkNotNull(payPointId,"PayPointId should not be null");
        return new ModelAndView("pla/core/paypoint/createpaypoint");
    }

    @RequestMapping(value ="/getPayPointByPayPointId", method=RequestMethod.GET)
    @ResponseBody
    public PayPointCommand getPayPointByPayPointId( @RequestParam String payPointId){
        Preconditions.checkNotNull(payPointId,"PayPointId should not be null");
        return payPointService.getPayPointByPayPointId(new PayPointId(payPointId));
    }

    @RequestMapping(value="/getallbankdetail",method=RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getAllBankDetail(){
        return masterFinder.getAllBank();
    }

    @RequestMapping(value="/getbankbranchname/{bankCode}",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getAllBranch(@PathVariable("bankCode") String bankCode){
        return masterFinder.getAllBankBranch(bankCode);
    }

    @RequestMapping(value="/getallpaypointstatus",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getAllPaypointStatus(){
        return PayPointStatus.getAllPaypointStatus();
    }

    @RequestMapping(value="/getallpaypointgrade",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getAllPaypointgrade(){
        return PayPointGrade.getAllPaypointGrade();
    }
}

