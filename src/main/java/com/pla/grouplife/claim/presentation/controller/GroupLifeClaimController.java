package com.pla.grouplife.claim.presentation.controller;

import com.mongodb.gridfs.GridFSDBFile;
import com.pla.core.query.MasterFinder;
import com.pla.grouplife.claim.application.command.*;
import com.pla.grouplife.claim.application.service.GLClaimService;
import com.pla.grouplife.claim.domain.model.*;
import com.pla.grouplife.claim.exception.GLClaimException;
import com.pla.grouplife.claim.presentation.dto.*;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
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

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @RequestMapping(value = "/openpolicysearchpage", method = RequestMethod.GET)
    public ModelAndView searchPolicy() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/claim/searchPolicy");
        modelAndView.addObject("searchCriteria", new SearchGLPolicyDto());
        return modelAndView;
    }

    @RequestMapping(value = "/searchpolicy", method = RequestMethod.POST)
    public ModelAndView searchPolicy(SearchPolicyDto searchPolicyDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/claim/searchPolicy");
        modelAndView.addObject("searchResult", glClaimService.searchGLPolicy(searchPolicyDto));
        modelAndView.addObject("searchCriteria", searchPolicyDto);
        return modelAndView;

    }

    @RequestMapping(value = "/openclaimintimationpage", method = RequestMethod.GET)
    public ModelAndView claimIntimation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/claim/claimintimation");
        return modelAndView;
    }

    @RequestMapping(value = "/getclaimant/{policyId}", method = RequestMethod.GET)
    @ResponseBody
    public ClaimantDetailDto claimantSearch(@PathVariable("policyId") String policyNumber) {
        return glClaimService.claimantDetailSearch(policyNumber);
    }


    @RequestMapping(value = "/getrelationship/{policyId}/{category}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getConfiguredRelationshipForCategoryAndPolicy(@PathVariable("policyId") String policyId, @PathVariable("category") String category) {
        return glClaimService.getConfiguredRelationShip(policyId, category);
    }

    @RequestMapping(value = "/getplandetail/{policyId}/{category}/{relationship}", method = RequestMethod.GET)
    @ResponseBody
    public PlanCoverageDetailDto getPlanDetails(@PathVariable("policyId") String policyId, @PathVariable("category") String category, @PathVariable("relationship") String relationship) {
        return glClaimService.getPlanDetailForCategoryAndRelationship(policyId, category, relationship);
    }

    @RequestMapping(value = "/assuredsearch", method = RequestMethod.POST)
    @ResponseBody
    public List<GLInsuredDetailDto> assuredSearch(@RequestBody AssuredSearchDto assuredSearchDto) {
        return glClaimService.assuredSearch(assuredSearchDto);
    }

    @RequestMapping(value = "/assureddetail/{policyId}/{clientId}", method = RequestMethod.GET)
    @ResponseBody
    public ClaimAssuredDetailDto assuredSearch(@PathVariable("policyId") String policyId, @PathVariable("clientId") String clientId) {
        return glClaimService.getAssuredDetails(policyId, clientId);
    }

    @RequestMapping(value = "/createclaimintimation", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Result> createClaimIntimation(@RequestBody CreateGLClaimIntimationCommand glClaimIntimationCommand, BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity(Result.failure("Error in creating  Claim Intimation Record", bindingResult.getAllErrors()), HttpStatus.OK);
        }

        GLClaimIntimationDto claimData=null;
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            glClaimIntimationCommand.setUserDetails(userDetails);
            claimData = commandGateway.sendAndWait(glClaimIntimationCommand);
        } catch (GLClaimException e) {
            LOGGER.error("Error in creating  Claim Intimation", e);
            return new ResponseEntity(Result.failure("Error in creating  Claim Intimation"), HttpStatus.OK);
        }
        return new ResponseEntity(Result.success(" Claim Intimation created successfully",claimData), HttpStatus.OK);

    }

    @RequestMapping(value = "/openclaimintimationsearchpage", method = RequestMethod.GET)
    public ModelAndView searchClaimIntimation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/claim/searchClaimIntimation");
        modelAndView.addObject("searchCriteria", new SearchClaimIntimationDto());
        return modelAndView;
    }
    @RequestMapping(value = "/searchclaimintimation", method = RequestMethod.POST)
    public ModelAndView getClaimIntimationDetail(SearchClaimIntimationDto searchClaimIntimationDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/claim/searchClaimIntimation");
        modelAndView.addObject("searchResult", glClaimService.getClaimIntimationRecord(searchClaimIntimationDto));
        modelAndView.addObject("searchCriteria", searchClaimIntimationDto);
        return modelAndView;

    }

    @RequestMapping(value = "/getclaimdetail/{claimId}", method = RequestMethod.GET)
    @ResponseBody
    public GLClaimIntimationDetailsDto getIntimationDetail(@PathVariable("claimId") String claimId) {
        return glClaimService.getClaimIntimationDetails(claimId);

    }

    @RequestMapping(value = "/openclaimregistrationpage", method = RequestMethod.GET)
    public ModelAndView claimRegistration() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/claim/claimregistration");
        return modelAndView;
    }

    @RequestMapping(value = "/claimregistration", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity registerClaim(@RequestBody GLClaimRegistrationCommand glClaimRegistrationCommand,BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity(Result.failure("Error in creating  Claim Registration Record", bindingResult.getAllErrors()), HttpStatus.OK);
        }
        String claimId = "";
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            glClaimRegistrationCommand.setUserDetails(userDetails);
            claimId=commandGateway.sendAndWait(glClaimRegistrationCommand);
        } catch (Exception e) {
            LOGGER.error("Error in creating  Claim Registration", e);
            return new ResponseEntity(Result.failure("Error in creating Claim Registration"), HttpStatus.OK);
        }
        return new ResponseEntity(Result.success("Claim registered successfully"), HttpStatus.OK);
    }

    @RequestMapping(value = "/disabilityclaimregistration", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity registerDisabilityClaim(@RequestBody GLDisabilityClaimRegistrationCommand glClaimRegistrationCommand,BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            return new ResponseEntity(Result.failure("Error in creating  Disability Claim Registration Record", bindingResult.getAllErrors()), HttpStatus.OK);
        }
        String claimId = "";
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            glClaimRegistrationCommand.setUserDetails(userDetails);
            claimId = commandGateway.sendAndWait(glClaimRegistrationCommand);
        } catch (Exception e) {
            LOGGER.error("Error in creating  Disability Claim Registration", e);
            return new ResponseEntity(Result.failure("Error in creating Disability Claim Registration"), HttpStatus.OK);
        }
        return new ResponseEntity(Result.success("Disability Claim registered successfully"), HttpStatus.OK);
    }


    @RequestMapping(value = "/searchclaim", method = RequestMethod.POST)
    @ResponseBody
    public List<GLClaimIntimationDto> claimSearch(@RequestBody SearchClaimDto searchClaimDto) {
        return glClaimService.getClaimDetail(searchClaimDto);
    }


    @RequestMapping(value = "/updateclaim", method = RequestMethod.POST)
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

    @RequestMapping(value = "/searchclaimforapproval", method = RequestMethod.POST)
    public ModelAndView getClaimDetailForApproval(SearchClaimIntimationDto searchClaimIntimationDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/claim/viewApprovalClaimIntimation");
        modelAndView.addObject("searchResult", glClaimService.getApprovedClaimDetail(searchClaimIntimationDto, new String[]{"UNDERWRITING"}));
        modelAndView.addObject("searchCriteria", searchClaimIntimationDto);
        return modelAndView;

    }

     @RequestMapping(value = "/getclaimsforapproval", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list CLAIMS which are routed for approval")
    public List<GLClaimDataDto> getRoutedClaim(@RequestBody SearchClaimIntimationDto searchClaimIntimationDto) {
         return glClaimService.getApprovedClaimDetail(searchClaimIntimationDto, new String[]{"UNDERWRITING"});

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
    @ApiOperation(httpMethod = "POST", value = "To return claim")
    public ResponseEntity returnClaim(@RequestBody ReturnGLClaimCommand returnCommand, HttpServletRequest request) {
        String claimNumber = "";

        try {
            returnCommand.setUserDetails(getLoggedInUserDetail(request));
            claimNumber= commandGateway.sendAndWait(returnCommand);
            return new ResponseEntity(Result.success("claim returned",claimNumber), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/reject", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To reject claim")
    public ResponseEntity rejectClaim(@RequestBody GLClaimRejectCommand rejectCommand, HttpServletRequest request) {
        try {
            rejectCommand.setUserDetails(getLoggedInUserDetail(request));
            commandGateway.sendAndWait(rejectCommand);
            return new ResponseEntity(Result.success("claim rejected"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @RequestMapping(value = "/routetonextlevel", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To route claim to senior underwriter")
    public ResponseEntity rejectClaim(@RequestBody GLClaimSeniorApproverCommand command, HttpServletRequest request) {
        try {
            command.setUserDetails(getLoggedInUserDetail(request));
            commandGateway.sendAndWait(command);
            return new ResponseEntity(Result.success("claim routed to senior underwriter successfully"), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/listapprovedclaims", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list CLAIMS which are approved")
    public List<GLClaimDataDto> getApprovedClaims() {
        return glClaimService.getAllApprovedClaimDetail();

    }

@RequestMapping(value = "/searchclaimforsettlement", method = RequestMethod.POST)
    public ModelAndView getApprovedClaimDetail(SearchClaimIntimationDto searchClaimIntimationDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/claim/viewClaimSettlement");
        modelAndView.addObject("searchResult",glClaimService.getClaimDetailForSettlement(searchClaimIntimationDto, new String[]{"APPROVED","PAID_DISBURSED"}));
        modelAndView.addObject("searchCriteria", searchClaimIntimationDto);
        return modelAndView;

    }

    @RequestMapping(value = "/createclaimsettlement", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Result> createClaimSettlement(@RequestBody GLClaimSettlementCommand glClaimSettlementCommand, BindingResult bindingResult, HttpServletRequest request) {

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


    @RequestMapping(value = "/getclaimreopen", method = RequestMethod.POST)
    @ResponseBody
    public List<GLClaimDataDto> reopenClaimDetail(@RequestBody SearchClaimDto searchClaimDto) {

        return glClaimService.getClaimRecordForReopen(searchClaimDto);

    }


    @RequestMapping(value = "/listclaimsforamendment", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list CLAIMS which are for amendment")
    public List<GLClaimDataDto> getApprovedOrPaidClaims() {
        return glClaimService.getAllApprovedOrPaidClaimDetail();

    }


    @RequestMapping(value = "/searchclaimforamendment", method = RequestMethod.POST)
    @ResponseBody
    public List<GLClaimDataDto> amendmentClaimDetail(@RequestBody SearchClaimIntimationDto searchClaimIntimationDto) {

        return glClaimService.getApprovedClaimDetail(searchClaimIntimationDto, new String[]{"APPROVED","PAID_DISBURSED"});

    }
    @RequestMapping(value = "/claimamendment", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity amendClaim(@RequestBody GLClaimAmendmentCommand glClaimAmendmentCommand, HttpServletRequest request) {
        glClaimAmendmentCommand.setUserDetails(getLoggedInUserDetail(request));
        try {

            commandGateway.sendAndWait(glClaimAmendmentCommand);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity(Result.success("Claim amended successfully"), HttpStatus.OK);
    }
    @RequestMapping(value = "/getclaimdetailforsettlement/{claimId}", method = RequestMethod.GET)
    @ResponseBody
    public GLClaimSettlementDataDto getClaimDetailsForSettlement(@PathVariable("claimId") String claimId, HttpServletRequest request) {



        // return  glClaimService.getClaimRecordForSettlement(claimId);
        return null;
    }

    @RequestMapping(value = "/waivedocument", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To waive mandatory document by Approver")
    public ResponseEntity waiveDocument(@RequestBody GLClaimWaiveMandatoryDocumentCommand cmd, HttpServletRequest request) {
        try {
            cmd.setUserDetails(getLoggedInUserDetail(request));
            String claimId = commandGateway.sendAndWait(cmd);
            return new ResponseEntity(Result.success("Mandatory Document waives successfully", claimId), HttpStatus.OK);
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
        if (UtilValidator.isEmpty(removeCommand.getGridFsDocId()) || UtilValidator.isEmpty(removeCommand.getClaimId())) {
            return new ResponseEntity(Result.failure("request parameter cannot be empty"), HttpStatus.BAD_REQUEST);
        }

        try {
            removeCommand.setUserDetails(getLoggedInUserDetail(request));
            if (commandGateway.sendAndWait(removeCommand))

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
    public ResponseEntity uploadMandatoryDocument(GLClaimDocumentCommand glClaimDocumentCommand, HttpServletRequest request) {
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

    //3jan
    @RequestMapping(value = "/getallrequiredmandatorydocuments/{planId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list mandatory documents which is being configured in Mandatory Document SetUp")
    public List<GLClaimMandatoryDocumentDto> findAllMandatoryDocuments(@PathVariable("planId") String planId) {
        List<GLClaimMandatoryDocumentDto> glClaimMandatoryDocuments = glClaimService.getAllMandatoryDocumentsForClaim(planId);
        return glClaimMandatoryDocuments;

    }

    //31dec
    @RequestMapping(value = "/getclaimdmandatorydocuments/{planId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list mandatory documents which is being configured in Mandatory Document SetUp")
    public List<GLClaimMandatoryDocumentDto> findAllClaimMandatoryDocuments(@PathVariable("planId") String planId) {
        List<GLClaimMandatoryDocumentDto> glClaimMandatoryDocuments = glClaimService.getAllClaimMandatoryDocuments(planId);
        return glClaimMandatoryDocuments;

    }
    @RequestMapping(value = "/downloadmandatorydocument/{gridfsdocid}", method = RequestMethod.GET)
    public void downloadMandatoryDocument(@PathVariable("gridfsdocid") String gridfsDocId, HttpServletResponse response) throws IOException {
        GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(gridfsDocId)));
        response.reset();
        response.setContentType(gridFSDBFile.getContentType());
        response.setHeader("content-disposition", "attachment; filename=" + gridFSDBFile.getFilename() + "");
        OutputStream outputStream = response.getOutputStream();
        org.apache.commons.io.IOUtils.copy(gridFSDBFile.getInputStream(), outputStream);
        outputStream.flush();
        outputStream.close();
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
    public List<Map<String, Object>> getDisabilityExtent() {
        return DisabilityExtent.getDisabilityExtent();
    }

    @RequestMapping(value = "/getassuredconfined", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getAssuredConfined() {
        return AssuredConfinedToHouse.getAssuredConfinedTypes();
    }

    @RequestMapping(value = "/getassuredoutdooractivity", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getAssuredOutdoorActive() {
        return AssuredOutdoorActive.getAssuredOutdoorActivity();
    }
    @RequestMapping(value = "/getassureddailytask", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getAssuredDailyTask() {
        return AssuredTaskOfDailyLiving.getAllAssuredTaskTypes();
    }

    @RequestMapping(value = "/getapplycondition", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getApplyConditionTypes() {
        return ApplyConditionTypes.getApplyConditionTypes();
    }

    @RequestMapping(value = "/openclaimintimation/{claimId}", method = RequestMethod.GET)
    public ResponseEntity createClaim(@PathVariable("claimId") String claimId, HttpServletRequest request) {
        if (glClaimService.configuredForPlan(claimId) == null) {
            return new ResponseEntity(Result.failure("The claim routing for  plan is not configured"), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity(Result.success("Claim routing  configured for plan"), HttpStatus.OK);
    }

    @RequestMapping(value = "/getclaimplanrouting/{planId}", method = RequestMethod.GET)
    @ResponseBody
    public UnderWriterRoutingLevel getClaimRouting(@PathVariable("planId") String planId, HttpServletRequest request) {


        //return glClaimService.configuredForPlan(planId);
        return  glClaimService.configuredForSelectedPlan(planId);
    }


    @RequestMapping(value = "/getcategory/{policyId}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getConfiguredCategoryForPolicy(@PathVariable("policyId") String policyId) {
        return glClaimService.getConfiguredCategory(policyId);
    }

    @RequestMapping(value = "/getclaimsforunderwriterone", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list CLAIMS which are routed for approval")
    public List<GLClaimDataDto> getClaimDetailForUnderWriterOne(@RequestBody SearchClaimIntimationDto searchClaimIntimationDto) {
        return glClaimService.getClaimDetailForLevelOne(searchClaimIntimationDto, new String[]{"UNDERWRITING"});

    }

    @RequestMapping(value = "/listclaimsforunderwriterone", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list CLAIMS which are routed  to approver one")
    public List<GLClaimDataDto> getClaimsForUnderWriterOne() {
        return glClaimService.getAllClaimDetailForApproverOne();

    }

    @RequestMapping(value = "/openapprovalclaim", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open Approval proposal page")
    public ModelAndView gotoApprovalClaimPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/claim/viewApprovalClaimIntimation");
        modelAndView.addObject("searchCriteria", new SearchClaimIntimationDto());
        return modelAndView;
    }
    @RequestMapping(value = "/openclaimsettlement", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open ClaimSearchPage")
    public ModelAndView gotoClaimSettlementPage(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("pla/groupLife/claim/viewClaimSettlement");
        modelAndView.addObject("searchResult",glClaimService.getAllApprovedClaimDetail());
        modelAndView.addObject("searchCriteria", new SearchClaimIntimationDto());
        return modelAndView;
    }


    @RequestMapping(value = "/viewapprovalclaim", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open Approval Claim page in view Mode")
    public ModelAndView gotoApprovalClaim() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/claim/createApprovalClaim");
        return modelAndView;
    }
    @RequestMapping(value = "/viewsettlement", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open Approval Claim page in view Mode")
    public ModelAndView gotoCreateSettmentPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/claim/createSettlement");
        return modelAndView;
    }
    @RequestMapping(value = "/reopenclaim", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open viewClaimReopen page")
    public ModelAndView gotoReopenClaimViewPage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/claim/viewClaimReopen");
        modelAndView.addObject("searchResult",glClaimService.getAllRejectedOrClosedClaimDetail());
        modelAndView.addObject("searchCriteria", new SearchClaimIntimationDto());
        return modelAndView;
    }

     @RequestMapping(value = "/listallclaimsforreopen", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list CLAIMS which are approved")
    public List<GLClaimDataDto> getClosedClaims() {
        return glClaimService.getAllRejectedOrClosedClaimDetail();

    }

    @RequestMapping(value = "/searchclaimforreopen", method = RequestMethod.POST)
    public ModelAndView getClosedClaimToReopen(SearchClaimIntimationDto searchClaimIntimationDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/claim/viewClaimReopen");
        modelAndView.addObject("searchResult",glClaimService.getClaimDetailForReopen(searchClaimIntimationDto, new String[]{"CANCELLED","REPUDIATED"}));
        modelAndView.addObject("searchCriteria", searchClaimIntimationDto);
        return modelAndView;

    }

    @RequestMapping(value = "/listallclaimstoreopen/{claimId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list CLAIMS which are to be reopened")
    public String getClosedClaims(@PathVariable("claimId")String claimId, HttpServletRequest request) {
        UserDetails userDetails  = getLoggedInUserDetail(request);
        //ModelAndView modelAndView = new ModelAndView();
       // modelAndView.setViewName("pla/groupLife/claim/viewClaimReOpen");
        String message=null;
        GLClaimReopenCommand glClaimOpenCommand=new GLClaimReopenCommand();
        glClaimOpenCommand.setClaimId(claimId);
        glClaimOpenCommand.setUserDetails(userDetails);
            String claimNumber=glClaimService.reopenClaim(glClaimOpenCommand);
            message="Claim Submitted For Reopen Sucessfully";
         return claimNumber;
    }

    @RequestMapping(value = "/reopen", method = RequestMethod.POST)
    @ResponseBody
    @ApiOperation(httpMethod = "POST", value = "To reopen a claim")
    public ResponseEntity returnClaim(@RequestBody GLClaimReopenCommand  command, HttpServletRequest request) {
        String claimNumber = "";

        try {
            command.setUserDetails(getLoggedInUserDetail(request));
            claimNumber= commandGateway.sendAndWait(command);
            return new ResponseEntity(Result.success("claim reopened successfully",claimNumber), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity(Result.failure(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}





