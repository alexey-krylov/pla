package com.pla.grouphealth.claim.cashless.presentation.controller;

import com.pla.grouphealth.claim.cashless.application.command.UpdateCommentCommand;
import com.pla.grouphealth.claim.cashless.application.service.PreAuthorizationRequestService;
import com.pla.grouphealth.claim.cashless.domain.model.CommentDetail;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationId;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequestId;
import com.pla.grouphealth.claim.cashless.presentation.dto.GHClaimDocumentCommand;
import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationClaimantDetailCommand;
import com.pla.grouphealth.claim.cashless.presentation.dto.SearchPreAuthorizationRecordDto;
import com.pla.grouphealth.proposal.presentation.dto.GHProposalMandatoryDocumentDto;
import com.pla.sharedkernel.domain.model.FamilyId;
import com.wordnik.swagger.annotations.ApiOperation;
import lombok.Synchronized;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.time.DateTime;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUserDetail;

/**
 * Author - Mohan Sharma Created on 1/6/2016.
 */
@RestController
@RequestMapping(value = "/grouphealth/claim/cashless/preauthorizationrequest")
public class PreAuthorizationRequestController {
    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private PreAuthorizationRequestService preAuthorizationRequestService;

    @RequestMapping(value = "/getpreauthorizationbypreauthorizationIdandclientId/{preAuthorizationId}/{clientId}", method = RequestMethod.GET)
    public @ResponseBody
    PreAuthorizationClaimantDetailCommand getPreAuthorizationByPreAuthorizationIdAndClientId(@PathVariable("preAuthorizationId") String preAuthorizationId, @PathVariable("clientId") String clientId){
        return preAuthorizationRequestService.getPreAuthorizationByPreAuthorizationIdAndClientId(new PreAuthorizationId(preAuthorizationId), clientId);
    }

    @RequestMapping(value = "/createorupdate", method = RequestMethod.POST)
    public Result createUpdate(@Valid @RequestBody PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletResponse response){

        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while creating Pre Authorization Request", bindingResult.getAllErrors());
        }
        try {
            PreAuthorizationRequestId preAuthorizationRequestId = commandGateway.sendAndWait(preAuthorizationClaimantDetailCommand);
            return Result.success("Pre Authorization Request successfully created with PreAuthorizationRequestId - "+ preAuthorizationRequestId.getPreAuthorizationRequestId());
        } catch (Exception e){
            return Result.failure(e.getMessage());
        }
    }


    @RequestMapping(value="/loadpreauthorizationrequest",method = RequestMethod.GET)
    public ModelAndView searchPreAuthorizationRecor() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/claim/preAuthorizationRequest");

        return modelAndView;
    }

    @RequestMapping(value = "/getmandatorydocuments/{clientId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list mandatory documents which is being configured in Mandatory Document SetUp")
    public List<GHProposalMandatoryDocumentDto> findMandatoryDocuments(@PathVariable("clientId") String clientId) {
        List<GHProposalMandatoryDocumentDto> ghProposalMandatoryDocumentDtos = preAuthorizationRequestService.findMandatoryDocuments(new FamilyId(clientId));
        return ghProposalMandatoryDocumentDtos;
    }

    @RequestMapping(value = "/getpreauthorizationrequestbycriteria", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView getPreAuthorizationRequestByCriteria(SearchPreAuthorizationRecordDto searchPreAuthorizationRecordDto) {
        ModelAndView modelAndView = new ModelAndView("pla/grouphealth/claim/searchPreAuthorizationRequestRecord");
        List<PreAuthorizationClaimantDetailCommand> searchResult = preAuthorizationRequestService.getPreAuthorizationRequestByCriteria(searchPreAuthorizationRecordDto);
        modelAndView.addObject("searchResult", searchResult);
        modelAndView.addObject("searchResult", searchPreAuthorizationRecordDto);
        return modelAndView;
    }

    @RequestMapping(value = "/getpreauthorizationfordefaultlist", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView getPreAuthorizationForDefaultList() {
        ModelAndView modelAndView = new ModelAndView("pla/grouphealth/claim/searchPreAuthorizationRecord");
        List<PreAuthorizationClaimantDetailCommand> searchResult = preAuthorizationRequestService.getPreAuthorizationForDefaultList();
        modelAndView.addObject("preAuthorizationResult", searchResult);
        modelAndView.addObject("searchCriteria", new SearchPreAuthorizationRecordDto());
        return modelAndView;
    }

    @Synchronized
    @RequestMapping(value = "/uploadmandatorydocument", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity uploadMandatoryDocument(GHClaimDocumentCommand ghClaimDocumentCommand, HttpServletRequest request) {
        ghClaimDocumentCommand.setUserDetails(getLoggedInUserDetail(request));
        try {
            commandGateway.send(ghClaimDocumentCommand);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.OK);
        }
        return new ResponseEntity(Result.success("Documents uploaded successfully"), HttpStatus.OK);
    }

    @RequestMapping(value = "/updatewithcomments", method = RequestMethod.POST)
    public @ResponseBody Result updateWithComment(@Valid @RequestBody UpdateCommentCommand updateCommentCommand, BindingResult bindingResult, ModelMap modelMap, HttpServletRequest request){
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while updating comments", bindingResult.getAllErrors());
        }
        try{
            updateCommentCommand.setUserDetails(getLoggedInUserDetail(request));
            updateCommentCommand.setCommentDateTime(DateTime.now());
            Set<CommentDetail> comments = commandGateway.sendAndWait(updateCommentCommand);
            return Result.success("Proposal submitted successfully", comments);
        } catch (Exception e){
            e.printStackTrace();
            return Result.failure(e.getMessage());
        }
    }
}
