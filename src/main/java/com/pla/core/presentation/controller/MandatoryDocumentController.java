package com.pla.core.presentation.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.application.CreateMandatoryDocumentCommand;
import com.pla.core.application.UpdateMandatoryDocumentCommand;
import com.pla.core.domain.exception.MandatoryDocumentException;
import com.pla.core.domain.model.ProcessType;
import com.pla.core.domain.service.MandatoryDocumentService;
import com.pla.core.dto.MandatoryDocumentDto;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUserDetail;

/**
 * Created by Admin on 3/30/2015.
 */
@Controller
@RequestMapping(value = "/core/mandatorydocument")
public class MandatoryDocumentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MandatoryDocumentController.class);

    private MandatoryDocumentService mandatoryDocumentService;

    private CommandGateway commandGateway;

    @Autowired
    public MandatoryDocumentController(CommandGateway commandGateway,MandatoryDocumentService mandatoryDocumentService) {
        this.commandGateway = commandGateway;
        this.mandatoryDocumentService = mandatoryDocumentService;
    }


    @RequestMapping(value = "/view" ,method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView getAllMandatoryDocument(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/mandatorydocument/viewMandatoryDocument");
        modelAndView.addObject("listOfMandatoryDocument", mandatoryDocumentService.getMandatoryDocuments());
        return modelAndView;
    }

    @RequestMapping(value ="/redirecttoupdatepage", method = RequestMethod.GET)
    public ModelAndView redirectToUpdatePage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/mandatorydocument/updateMandatoryDocument");
        return modelAndView;
    }


    @RequestMapping(value ="/opencreatepage", method = RequestMethod.GET)
    public ModelAndView openCreatePageMandatoryDocument() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/mandatorydocument/createMandatoryDocument");
        return modelAndView;
    }

    @RequestMapping(value ="/getmandatorydocumentdetail/{documentId}", method = RequestMethod.GET)
    @ResponseBody
    public List<MandatoryDocumentDto> getMandatoryDocumentDetail(@PathVariable("documentId") Long documentId) {
        return mandatoryDocumentService.getMandatoryDocumentById(documentId);
    }

    @RequestMapping(value = "/getallmandatorydocument" ,method = RequestMethod.GET)
    @ResponseBody
    public List<MandatoryDocumentDto> getMandatoryDocument(){
        return mandatoryDocumentService.getMandatoryDocuments();
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public
    @ResponseBody
    Result createMandatoryDocument(@RequestBody CreateMandatoryDocumentCommand createMandatoryDocumentCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in creating benefit", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
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
            UserDetails userDetails = getLoggedInUserDetail(request);
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
    public List<Map<String,String>> getDefinedProcess(){
        List<Map<String,String>> processTypeList = Lists.newArrayList();
        for (ProcessType processType : ProcessType.values()){
            Map<String,String> processTypeMap = Maps.newLinkedHashMap();
            processTypeMap.put("processType",processType.name());
            processTypeMap.put("description", processType.description);
            processTypeList.add(processTypeMap);
        }
        return processTypeList;
    }

}

