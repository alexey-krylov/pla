package com.pla.core.presentation.controller;

import com.pla.core.application.CreateMandatoryDocumentCommand;
import com.pla.core.application.UpdateMandatoryDocumentCommand;
import com.pla.core.domain.exception.MandatoryDocumentException;
import com.pla.core.domain.model.ProcessType;
import com.pla.core.dto.MandatoryDocumentDto;
import com.pla.core.query.MandatoryDocumentFinder;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUSerDetail;

/**
 * Created by Admin on 3/30/2015.
 */
@Controller
@RequestMapping(value = "/core/mandatorydocument")
public class MandatoryDocumentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MandatoryDocumentController.class);

    private MandatoryDocumentFinder mandatoryDocumentFinder;

    private CommandGateway commandGateway;

    @Autowired
    public MandatoryDocumentController(CommandGateway commandGateway,MandatoryDocumentFinder mandatoryDocumentFinder) {
        this.commandGateway = commandGateway;
        this.mandatoryDocumentFinder=mandatoryDocumentFinder;
    }


    /*
    *
    * API  to get all created mandatory document
    *
    * */

    @RequestMapping(value = "/view" ,method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView getAllMandatoryDocument(){
        /*
        * change is needed for view name
        * */
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/mandatorydocument/viewMandatoryDocument");
        modelAndView.addObject("listOfMandatoryDocument", mandatoryDocumentFinder.getAllMandatoryDocument());
        return modelAndView;
    }


    @RequestMapping(value ="/opencreatepage", method = RequestMethod.GET)
    public ModelAndView openCreatePageMandatoryDocument() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/mandatorydocument/createMandatoryDocument");
        return modelAndView;
    }

    @RequestMapping(value = "/getallmandatorydocument" ,method = RequestMethod.GET)
    @ResponseBody
    public List<MandatoryDocumentDto> getMandatoryDocument(){
        return mandatoryDocumentFinder.getAllMandatoryDocument();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public
    @ResponseBody
    Result createMandatoryDocument(@RequestBody CreateMandatoryDocumentCommand createMandatoryDocumentCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in creating benefit", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUSerDetail(request);
            createMandatoryDocumentCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(createMandatoryDocumentCommand);
        } catch (MandatoryDocumentException e) {
            LOGGER.error("Error in creating mandatory document", e);
            return Result.failure(e.getMessage());
        }
        return Result.success("Mandatory document created successfully");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public
    @ResponseBody
    Result updateMandatoryDocument(@RequestBody UpdateMandatoryDocumentCommand updateMandatoryDocumentCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in creating benefit", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUSerDetail(request);
            updateMandatoryDocumentCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(updateMandatoryDocumentCommand);
        } catch (MandatoryDocumentException e) {
            LOGGER.error("Error in updating mandatory document", e);
            return Result.failure(e.getMessage());
        }
        return Result.success("Mandatory document updated successfully");
    }

    @RequestMapping(value = "/getallprocess",method = RequestMethod.GET)
    @ResponseBody
    public List getDefinedProcess(){
        return Arrays.asList(ProcessType.values());
    }
}

