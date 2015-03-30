package com.pla.core.presentation.controller;

import com.pla.core.application.CreateMandatoryDocumentCommand;
import com.pla.core.application.UpdateMandatoryDocumentCommand;
import com.pla.core.domain.exception.MandatoryDocumentException;
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

import javax.servlet.http.HttpServletRequest;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUSerDetail;

/**
 * Created by Admin on 3/30/2015.
 */
@Controller
@RequestMapping(value = "/core")
public class MandatoryDocumentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MandatoryDocumentController.class);

    private CommandGateway commandGateway;

    @Autowired
    public MandatoryDocumentController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @RequestMapping(value = "/mandatorydocument/create", method = RequestMethod.POST)
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

    @RequestMapping(value = "/mandatorydocument/update", method = RequestMethod.POST)
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
}

