package com.pla.grouphealth.claim.cashless.presentation.controller;

import com.pla.grouphealth.claim.cashless.application.service.PreAuthorizationRequestService;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationId;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequestId;
import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationClaimantDetailCommand;
import com.pla.grouphealth.proposal.presentation.dto.GHProposalMandatoryDocumentDto;
import com.pla.sharedkernel.domain.model.FamilyId;
import com.wordnik.swagger.annotations.ApiOperation;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Created by Mohan Sharma on 1/6/2016.
 */
@RestController
@RequestMapping(value = "/grouphealth/claim/cashless/preauthorizationrequest")
public class PreAuthorizationRequestController {
    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private PreAuthorizationRequestService preAuthorizationRequestService;

    @RequestMapping(value = "/getpreauthorizationbypreauthorizationIdandclientId", method = RequestMethod.GET)
    public @ResponseBody
    PreAuthorizationClaimantDetailCommand getPreAuthorizationByPreAuthorizationIdAndClientId(@RequestParam String preAuthorizationId, @RequestParam String clientId){
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

    @RequestMapping(value = "/getmandatorydocuments/{clientId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list mandatory documents which is being configured in Mandatory Document SetUp")
    public List<GHProposalMandatoryDocumentDto> findMandatoryDocuments(@PathVariable("clientId") String clientId) {
        List<GHProposalMandatoryDocumentDto> ghProposalMandatoryDocumentDtos = preAuthorizationRequestService.findMandatoryDocuments(new FamilyId(clientId));
        return ghProposalMandatoryDocumentDtos;
    }
}
