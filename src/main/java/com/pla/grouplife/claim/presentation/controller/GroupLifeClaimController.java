package com.pla.grouplife.claim.presentation.controller;

import com.google.common.collect.Lists;
import com.pla.core.dto.MandatoryDocumentDto;
import com.pla.core.query.MasterFinder;
import com.pla.grouplife.claim.application.command.*;
import com.pla.grouplife.claim.application.service.GLClaimService;
import com.pla.grouplife.claim.domain.model.*;
import com.pla.grouplife.claim.exception.GLClaimException;
import com.pla.grouplife.claim.presentation.dto.*;
import com.pla.grouplife.sharedresource.dto.GLPolicyDetailDto;
import com.pla.grouplife.sharedresource.dto.SearchGLPolicyDto;
import com.pla.sharedkernel.domain.model.ClaimType;
import com.pla.underwriter.domain.model.UnderWriterRoutingLevel;
import com.wordnik.swagger.annotations.ApiOperation;
import lombok.Synchronized;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.nthdimenzion.utils.UtilValidator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUserDetail;
/**
 * Created by Mirror on 8/6/2015.
 */

@Controller
@RequestMapping(value = "/grouplife/claim")
public class GroupLifeClaimController {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(GroupLifeClaimController.class);
    @Autowired
    private GLClaimService glClaimService;

    @Autowired
    private MasterFinder masterFinder;

    @Autowired
    private CommandGateway commandGateway;



    @RequestMapping(value = "/openpolicysearchpage", method = RequestMethod.GET)
    public ModelAndView searchPolicy() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/claim/searchPolicy");
        modelAndView.addObject("searchCriteria", new SearchGLPolicyDto());
        return modelAndView;
    }


    @RequestMapping(value = "/searchpolicy", method = RequestMethod.POST)
    @ResponseBody
    public List<GLPolicyDetailDto> getPolicyDetail(@RequestBody SearchPolicyDto searchPolicyDto) {
       /*public ModelAndView searchPolicy(SearchPolicyDto searchPolicyDto){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/claim/searchPolicy");
        modelAndView.addObject("searchResult", glClaimService.searchPolicy(searchPolicyDto));
        modelAndView.addObject("searchCriteria", searchPolicyDto);
        return modelAndView;
         */

        return glClaimService.searchGLPolicy(searchPolicyDto);

    }


    @RequestMapping(value = "/openclaimintimationpage", method = RequestMethod.GET)
    public ModelAndView claimIntimation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/claim/claimintimation");
        return modelAndView;
    }


    @RequestMapping(value = "/assuredsearch", method = RequestMethod.POST)
    @ResponseBody
    public List<GLInsuredDetailDto> assuredSearch(@RequestBody AssuredSearchDto assuredSearchDto) {
        return glClaimService.assuredSearch(assuredSearchDto);
    }


    @RequestMapping(value = "/createclaimintimation", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Result> createClaimIntimation(@RequestBody CreateGLClaimIntimationCommand glClaimIntimationCommand, BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity(Result.failure("Error in creating  Claim Intimation Record", bindingResult.getAllErrors()), HttpStatus.OK);
        }
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            glClaimIntimationCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(glClaimIntimationCommand);
        } catch (GLClaimException e) {
            LOGGER.error("Error in creating  Claim Intimation", e);
            return new ResponseEntity(Result.failure("Error in creating  Claim Intimation"), HttpStatus.OK);
        }
        return new ResponseEntity(Result.success(" Claim Intimation created successfully"), HttpStatus.OK);

    }
    @RequestMapping(value = "/openclaimintimationsearchpage", method = RequestMethod.GET)
    public ModelAndView searchClaimIntimation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/claim/searchClaimIntimation");
        modelAndView.addObject("searchCriteria", new SearchClaimIntimationDto());
        return modelAndView;
    }


@RequestMapping(value = "/searchclaimintimation", method = RequestMethod.POST)
@ResponseBody
public List<ClaimIntimationDetailDto> getClaimIntimationDetail(@RequestBody SearchClaimIntimationDto searchClaimIntimationDto) {

    return glClaimService.getClaimIntimationRecord(searchClaimIntimationDto);

}
    @RequestMapping(value = "/openclaimregistrationpage", method = RequestMethod.GET)
    public ModelAndView claimRegistration() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/claim/claimregistration");
        return modelAndView;
    }


    @RequestMapping(value = "/claimregistration", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity registerClaim(@RequestBody GLClaimRegistrationCommand glClaimRegistrationCommand, HttpServletRequest request) {
        glClaimRegistrationCommand.setUserDetails(getLoggedInUserDetail(request));
        try {

            commandGateway.sendAndWait(glClaimRegistrationCommand);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(Result.success("Claim registered successfully"), HttpStatus.OK);
    }

    @RequestMapping(value = "/disabilityclaimregistration", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity registerDisabilityClaim(@RequestBody GLDisabilityClaimRegistrationCommand glClaimRegistrationCommand, HttpServletRequest request) {
        glClaimRegistrationCommand.setUserDetails(getLoggedInUserDetail(request));
        try {
            commandGateway.sendAndWait(glClaimRegistrationCommand);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(Result.success("Disability Claim registered successfully"), HttpStatus.OK);
    }

    @RequestMapping(value = "/viewclaimdetail", method = RequestMethod.POST)
    @ResponseBody
    public List<GLClaimDetailDto> claimSearch(@RequestBody SearchClaimDto searchClaimDto) {
        return glClaimService.getClaimDetail(searchClaimDto);
    }

    @RequestMapping(value = "/claimregistration/updateclaim", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity updateClaim(@RequestBody GLClaimUpdateCommand glClaimUpdateCommand, HttpServletRequest request) {
        glClaimUpdateCommand.setUserDetails(getLoggedInUserDetail(request));
        try {
            commandGateway.sendAndWait(glClaimUpdateCommand);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(Result.success("Claim updated successfully"), HttpStatus.OK);
    }

    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To submit claim for approval")
    public ResponseEntity submitClaim(@RequestBody SubmitGLClaimCommand submitGLClaimCommand, HttpServletRequest request) {
        try {
            submitGLClaimCommand.setUserDetails(getLoggedInUserDetail(request));
            commandGateway.sendAndWait(submitGLClaimCommand);
            return new ResponseEntity(Result.success("Claim submitted successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/approve", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To approve claim")
    public ResponseEntity approveClaim(@RequestBody GLClaimApprovalCommand glClaimApprovalCommand, HttpServletRequest request) {
        try {
            glClaimApprovalCommand.setUserDetails(getLoggedInUserDetail(request));
            glClaimApprovalCommand.setStatus(ClaimStatus.APPROVED);
            commandGateway.sendAndWait(glClaimApprovalCommand);
            return new ResponseEntity(Result.success("Claim approved successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/return", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To reject claim")
    public ResponseEntity returnClaim(@RequestBody GLClaimApprovalCommand glClaimApprovalCommand, HttpServletRequest request) {
        try {
            glClaimApprovalCommand.setUserDetails(getLoggedInUserDetail(request));
            glClaimApprovalCommand.setStatus(ClaimStatus.REPUDIATED);
            commandGateway.sendAndWait(glClaimApprovalCommand);
            return new ResponseEntity(Result.success("claim rejected"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/getapprovedclaim", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list CLAIMS which are approved")
    public List<GLClaimDataDto> getApprovedClaim(@RequestBody  SearchClaimDto searchClaimDto) {
       return  glClaimService.getApprovedClaimDetail(searchClaimDto,new String[]{"APPROVED"});

    }


    @RequestMapping(value = "/getclaimreopen", method = RequestMethod.POST)
    @ResponseBody
    public List<GLClaimDataDto> reopenClaimDetail(@RequestBody  SearchClaimDto searchClaimDto ) {

        return glClaimService.getClaimRecordForReopen(searchClaimDto);

    }
    @RequestMapping(value = "/getclaimforamendment", method = RequestMethod.POST)
    @ResponseBody
    public List<GLClaimDataDto> amendmentClaimDetail(@RequestBody  SearchClaimDto searchClaimDto ) {

        return  glClaimService.getApprovedClaimDetail(searchClaimDto,new String[]{"APPROVED","PAID"});

    }

    @RequestMapping(value = "/settelment", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To submit claim for settlement")
    public ResponseEntity submitClaim(@RequestBody GLClaimSettlementCommand glClaimSettlementCommand, HttpServletRequest request) {
        try {
            glClaimSettlementCommand.setUserDetails(getLoggedInUserDetail(request));
            commandGateway.sendAndWait(glClaimSettlementCommand);
            return new ResponseEntity(Result.success("Claim submitted for settlement successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @RequestMapping(value = "/waivedocument", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To waive mandatory document by Approver")
    public ResponseEntity waiveDocument(@RequestBody GLClaimWaiveMandatoryDocumentCommand cmd, HttpServletRequest request) {
        try {
            cmd.setUserDetails(getLoggedInUserDetail(request));
            String claimId =  commandGateway.sendAndWait(cmd);
            return new ResponseEntity(Result.success("Mandatory Document waives successfully",claimId), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Synchronized
    @RequestMapping(value = "/removedocument", method = RequestMethod.POST)
    @ResponseBody
    //public ResponseEntity removeGLClaimDocument(@RequestParam String claimId, @RequestParam String gridFsDocId, HttpServletRequest request) {
    public ResponseEntity removeGLClaimDocument(@RequestBody GLClaimDocumentRemoveCommand removeCommand, HttpServletRequest request) {
        if(UtilValidator.isEmpty(removeCommand.getGridFsDocId()) || UtilValidator.isEmpty(removeCommand.getClaimId())){
            return new ResponseEntity(Result.failure("request parameter cannot be empty"), HttpStatus.BAD_REQUEST);
        }

        try {
            removeCommand.setUserDetails(getLoggedInUserDetail(request));
            if(commandGateway.sendAndWait(removeCommand))

                return new ResponseEntity(Result.success("Document deleted successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.OK);
        }
        return new ResponseEntity(Result.success("Document cannot be deleted"), HttpStatus.OK);
    }



    @Synchronized
    @RequestMapping(value = "/uploadmandatorydocument", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity uploadMandatoryDocument(@RequestBody GLClaimDocumentCommand glClaimDocumentCommand, HttpServletRequest request) {
        glClaimDocumentCommand.setUserDetails(getLoggedInUserDetail(request));
        try {
            commandGateway.sendAndWait(glClaimDocumentCommand);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(Result.success("Documents uploaded successfully"), HttpStatus.OK);
    }

    @RequestMapping(value = "/getmandatorydocuments/{claimId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list mandatory documents which is being configured in Mandatory Document SetUp")
    public List<GLClaimMandatoryDocumentDto> findMandatoryDocuments(@PathVariable("claimId") String claimId) {
        List<GLClaimMandatoryDocumentDto> glClaimMandatoryDocumentDtos = glClaimService.findMandatoryDocuments(claimId);
        return glClaimMandatoryDocumentDtos;

    }

    @RequestMapping(value = "/getadditionaldocuments/{claimId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list additional documents which is being configured in Mandatory Document SetUp")
    public Set<GLClaimMandatoryDocumentDto> findAdditionalDocuments(@PathVariable("claimId") String claimId) {
        Set<GLClaimMandatoryDocumentDto> ghClaimMandatoryDocumentDtos = glClaimService.findAdditionalDocuments(claimId);
        return ghClaimMandatoryDocumentDtos;
    }


    @RequestMapping(value = "/getallrequiredmandatorydocuments/{planId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list mandatory documents which is being configured in Mandatory Document SetUp")
    public List<MandatoryDocumentDto> findAllMandatoryDocuments(@PathVariable("planId") String planId) {
        List<MandatoryDocumentDto> glClaimMandatoryDocuments = glClaimService.getAllMandatoryDocumentsForClaim(planId);
        return glClaimMandatoryDocuments;

    }

    @RequestMapping(value = "/getrelationshipandcategory/{policyId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getConfiguredRelationshipAndCategory(@PathVariable("policyId") String policyId) {
        return glClaimService.getConfiguredRelationShipAndCategory(policyId);
    }
    @RequestMapping(value = "/getclaimtypes", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getClaimType() {
        return ClaimType.getAllClaimType();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllBankNames")
    @ResponseBody
    public List<Map<String, Object>> getAllBankNames() {
        return masterFinder.getAllBank();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllBankBranchNames/{bankCode}")
    @ResponseBody
    public List<Map<String, Object>> getAllBankBranchNames(@PathVariable("bankCode") String bankCode) {
        return masterFinder.getAllBankBranch(bankCode);
    }


    @RequestMapping(value = "/getaccidenttypes", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getAccidentTypes() {
        return AccidentTypes.getAllAccidentTypes();
    }

    @RequestMapping(value = "/getdisabilitynature", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getdisabilityNatureTypes() {
        return DisabilityNature.getDisabilityNature();
    }
    @RequestMapping(value = "/getdisabilityextent", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>>  getDisabilityExtent() {
        return DisabilityExtent. getDisabilityExtent();
    }

    @RequestMapping(value = "/getassuredconfined", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>>  getAssuredConfined() {
        return AssuredConfinedToHouse.getAssuredConfinedTypes();
    }

    @RequestMapping(value = "/getassuredoutdooractivity", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>>  getAssuredOutdoorActive() {
        return AssuredOutdoorActive.getAssuredOutdoorActivity();
    }


    @RequestMapping(value = "/getuploadeddocuments", method = RequestMethod.POST)
    @ResponseBody
    public List<GLClaimDocument> findUploadedDocuments(@RequestBody GLClaimDocumentCommand gldocs) {
        List<GLClaimDocument> glClaimDocuments = Lists.newArrayList();
        GLClaimDocument docs = null;
        try {
            docs = glClaimService.getAddedDocument(gldocs.getFile(), gldocs.getDocumentId(), gldocs.isMandatory());

        } catch (IOException ioException) {
            ioException.printStackTrace();
            ;
        }
        glClaimDocuments.add(docs);
        return glClaimDocuments;
    }

    @RequestMapping(value = "/openclaimintimation/{claimId}", method = RequestMethod.GET)
    public ResponseEntity createClaim(@PathVariable("claimId") String claimId, HttpServletRequest request) {
        if (glClaimService.configuredForPlan(claimId)==null) {
            return new ResponseEntity(Result.failure("The claim routing for  plan is not configured"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(Result.success("Claim routing  configured for plan"), HttpStatus.OK);
    }

    @RequestMapping(value = "/getclaimplanrouting/{planId}", method = RequestMethod.GET)
    @ResponseBody
    public UnderWriterRoutingLevel getClaimRouting(@PathVariable("planId") String planId, HttpServletRequest request) {


        return glClaimService.configuredForSelectedPlan(planId);
    }



    @RequestMapping(value = "/claimintimationdetail/{policyNumber}", method = RequestMethod.GET)
    @ResponseBody
    public List<ClaimIntimationDetailDto> getClaimIntimationDetail(@PathVariable("policyNumber") String policyNumber) {
        return glClaimService.getClaimIntimationDetail(policyNumber);
    }

    @RequestMapping(value = "/createclaimisettlement", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Result> createClaimIntimation(@RequestBody GLClaimSettlementCommand glClaimSettlementCommand, BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity(Result.failure("Error in creating  Claim Settlement Record", bindingResult.getAllErrors()), HttpStatus.OK);
        }
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            glClaimSettlementCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(glClaimSettlementCommand);
        } catch (GLClaimException e) {
            LOGGER.error("Error in creating  Claim  Settlement", e);
            return new ResponseEntity(Result.failure("Error in creating  Claim Settlement"), HttpStatus.OK);
        }
        return new ResponseEntity(Result.success(" Claim Settlement created successfully"), HttpStatus.OK);

    }


}



