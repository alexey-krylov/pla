package com.pla.grouplife.claim.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.core.domain.model.notification.NotificationBuilder;
import com.pla.core.dto.CoverageClaimTypeDto;
import com.pla.core.dto.CoverageDto;
import com.pla.core.dto.MandatoryDocumentDto;
import com.pla.core.dto.ProductClaimTypeDto;
import com.pla.core.query.CoverageFinder;
import com.pla.core.query.PlanFinder;
import com.pla.core.query.ProductClaimMapperFinder;
import com.pla.grouplife.claim.domain.model.*;
import com.pla.grouplife.claim.presentation.dto.*;
import com.pla.grouplife.claim.query.GLClaimFinder;
import com.pla.grouplife.policy.query.GLPolicyFinder;
import com.pla.grouplife.sharedresource.dto.ContactPersonDetailDto;
import com.pla.grouplife.sharedresource.dto.GLPolicyDetailDto;
import com.pla.grouplife.sharedresource.dto.SearchGLPolicyDto;
import com.pla.grouplife.sharedresource.model.vo.*;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
import com.pla.publishedlanguage.dto.ClientDocumentDto;
import com.pla.publishedlanguage.dto.SearchDocumentDetailDto;
import com.pla.publishedlanguage.dto.UnderWriterRoutingLevelDetailDto;
import com.pla.publishedlanguage.underwriter.contract.IUnderWriterAdapter;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.exception.ProcessInfoException;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PolicyId;
import com.pla.underwriter.domain.model.UnderWriterRoutingLevel;
import com.pla.underwriter.finder.UnderWriterFinder;
import org.apache.commons.io.IOUtils;
import org.joda.time.*;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Mirror on 8/26/2015.
 */
@Service
public class GLClaimService implements Serializable{

    @Autowired
    private GLFinder glFinder;

    @Autowired
    private GLClaimFinder glClaimFinder;

    @Autowired
    private ProductClaimMapperFinder productClaimMapperFinder;

    @Autowired
    private CoverageFinder coverageFinder;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GLPolicyFinder glPolicyFinder;

    @Autowired
    UnderWriterFinder underWriterFinder;

    @Autowired
    private PlanFinder planFinder;

    @Autowired
    private IUnderWriterAdapter underWriterAdapter;

    @Autowired
    IProcessInfoAdapter iProcessInfoAdapter;

    public List<GLPolicyDetailDto> searchPolicy(SearchGLPolicyDto searchGLPolicyDto) {
        List<Map> searchedPolices = glFinder.searchPolicy(searchGLPolicyDto.getPolicyNumber(), searchGLPolicyDto.getPolicyHolderName(), searchGLPolicyDto.getClientId(), new String[]{"IN_FORCE"}, searchGLPolicyDto.getProposalNumber());
        if (isEmpty(searchedPolices)) {
            return Lists.newArrayList();
        }
        searchedPolices = searchByClientId(searchGLPolicyDto.getClientId(), searchedPolices);
        List<GLPolicyDetailDto> transformedPolicies = searchedPolices.stream().map(new Function<Map, GLPolicyDetailDto>() {
            @Override
            public GLPolicyDetailDto apply(Map map) {
                GLPolicyDetailDto policyDetailDto = transformToDto(map);
                return policyDetailDto;
            }
        }).collect(Collectors.toList());
        return transformedPolicies;
    }

   public int daysRequiredForEarlyClaim(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType){
       int days=0;
       try{
           days= iProcessInfoAdapter.getEarlyDeathClaimTimePeriod(lineOfBusinessEnum,processType);
       }
       catch(ProcessInfoException e){
           e.printStackTrace();;
       }
       return days;
   }

    public boolean getCoverageCoverTypeAsAccelerated(String planId,String coverageId){
        Map planMap=planFinder.findPlanByPlanId(new PlanId(planId));
        boolean result=false;

        List<LinkedHashMap> coverageList=(List<LinkedHashMap>)planMap.get("coverages");

        for(LinkedHashMap planCoverage:coverageList) {
            String dbCoverageId=(String)planCoverage.get("coverageId");
            String  dbCoverageCover=(String)planCoverage.get("coverageCover");
            if (dbCoverageId.equals(coverageId) && dbCoverageCover.equals("ACCELERATED")) {

                result=true;
            }
        }
            return result;
        }


    public List<GLPolicyDetailDto> searchGLPolicy(SearchPolicyDto searchPolicyDto) {

        List<Map> searchedPolices = glClaimFinder.searchPolicy(searchPolicyDto.getPolicyNumber(), searchPolicyDto.getPolicyHolderName(), searchPolicyDto.getClientId(), new String[]{"IN_FORCE"});

        if (isEmpty(searchedPolices)) {
            return Lists.newArrayList();
        }
        //searchedPolices = searchByClientId(searchPolicyDto.getClientId(), searchedPolices);
        List<GLPolicyDetailDto> transformedPolicies = searchedPolices.stream().map(new Function<Map, GLPolicyDetailDto>() {
            @Override
            public GLPolicyDetailDto apply(Map map) {
                GLPolicyDetailDto policyDetailDto = transformToDto(map);
                return policyDetailDto;
            }
        }).collect(Collectors.toList());

        return transformedPolicies;
    }

    public ClaimantDetailDto claimantDetailSearch(String policyId){
        ClaimantDetailDto claimantDetailDto=new ClaimantDetailDto();
        Map policyMap= glFinder.findPolicyById(policyId);
        String schemeName=(String)policyMap.get("schemeName");
        PolicyNumber policyNumberObj=(PolicyNumber)policyMap.get("policyNumber");
        String policyNumber=policyNumberObj.getPolicyNumber();
        List<InsuredDependent> insuredDependentList = Lists.newArrayList();
        List<Insured> insuredList = (List<Insured>)policyMap.get("insureds");
        boolean isAssuredSharedAvailable=false;
        for (Insured insured : insuredList) {
            if(insured.getNoOfAssured()==null) {
                isAssuredSharedAvailable=true;
            }
            if(insured.getNoOfAssured()!=null) {
                Set<InsuredDependent> insuredDependents = insured.getInsuredDependents();
                for (InsuredDependent insuredDependent : insuredDependents) {
                    if(insuredDependent.getNoOfAssured()!=null){
                        isAssuredSharedAvailable=true;
                    }
                    else{
                        isAssuredSharedAvailable=false;
                    }
                }
            }
        }
        claimantDetailDto.setIsAssuredDetailsShared(isAssuredSharedAvailable);

        Proposer proposer=(Proposer)policyMap.get("proposer");
        ProposerContactDetail proposerContactDetail=proposer.getContactDetail();
        claimantDetailDto.setSchemeName(schemeName);
        ClaimantDetail claimantDetail=new ClaimantDetail(proposer.getProposerName(),proposerContactDetail.getAddressLine1(),proposerContactDetail.getAddressLine2()
                ,proposerContactDetail.getPostalCode(),proposerContactDetail.getProvince(),proposerContactDetail.getTown(),proposerContactDetail.getEmailAddress(),proposerContactDetail.getEmailAddress());
        //claimantDetail.setProposerName(proposer.getProposerName());

        List<ProposerContactDetail.ContactPersonDetail> contactPersonDetailList=proposerContactDetail.getContactPersonDetail();
        List<ContactPersonDetailDto> contactPersonDetailDtoList=new ArrayList<ContactPersonDetailDto>();
        List<ContactPersonDetail> contactPersonDetailsList=new ArrayList<ContactPersonDetail>();
        for(ProposerContactDetail.ContactPersonDetail contactPersonDetails:contactPersonDetailList){
            ContactPersonDetail contactPersonDetail=new ContactPersonDetail();
            contactPersonDetail.setContactPersonName(contactPersonDetails.getContactPersonName());
            contactPersonDetail.setContactPersonEmail(contactPersonDetails.getContactPersonEmail());
            contactPersonDetail.setContactPersonMobileNumber(contactPersonDetails.getMobileNumber());
            contactPersonDetail.setContactPersonWorkPhoneNumber(contactPersonDetails.getWorkPhoneNumber());
            contactPersonDetailsList.add(contactPersonDetail);
        }
        claimantDetail.withContactPersonDetails(contactPersonDetailsList);
        claimantDetailDto.setClaimantDetail(claimantDetail);
        Set<String> categorySet=getCategory(policyId);
        claimantDetailDto.setCategorySet(categorySet);
        claimantDetailDto.setPolicyNumber(policyNumber);
        return claimantDetailDto;
    }


    public List<GLClaimIntimationDto> getClaimIntimationRecord(SearchClaimIntimationDto searchClaimIntimationDto) {
        List<Map> searchedClaimRecords = glClaimFinder.getClaimIntimationDetail(searchClaimIntimationDto.getClaimNumber(), searchClaimIntimationDto.getPolicyNumber(),
                searchClaimIntimationDto.getPolicyHolderName(), searchClaimIntimationDto.getPolicyHolderClientId(), searchClaimIntimationDto.getAssuredName(),
                searchClaimIntimationDto.getAssuredClientId(), searchClaimIntimationDto.getAssuredNrcNumber());

        if (isEmpty(searchedClaimRecords)) {
            return Lists.newArrayList();
        }
        return searchedClaimRecords.parallelStream().map(new Function<Map, GLClaimIntimationDto>() {
            @Override
            public GLClaimIntimationDto apply(Map map) {
                GLClaimIntimationDto glClaimIntimationDto = new  GLClaimIntimationDto();
                String claimId = map.get("_id").toString();
                glClaimIntimationDto.setClaimId(claimId);
                Proposer proposer = (Proposer) map.get("proposer");
                Policy policy=(Policy)map.get("policy");
                String policyHolderName=policy.getPolicyHolderName();
                glClaimIntimationDto.setPolicyHolderName( policyHolderName);
                ClaimNumber claimNumber=(ClaimNumber)map.get("claimNumber");
                glClaimIntimationDto.setClaimNumber(claimNumber.getClaimNumber());
                ClaimAssuredDetail claimAssuredDetail=(ClaimAssuredDetail) map.get("assuredDetail");

                glClaimIntimationDto.setAssuredName(claimAssuredDetail.getFirstName());
                glClaimIntimationDto.setAssuredNrcNumber(claimAssuredDetail.getNrcNumber());
                String policyNumber=policy.getPolicyNumber().getPolicyNumber();
                glClaimIntimationDto.setPolicyNumber(policyNumber);
                String claimTypeString=(String)map.get("claimType");
                ClaimType claimType=(ClaimType)ClaimType.valueOf(claimTypeString);
                glClaimIntimationDto.setClaimType(claimType);
                String claimStatusInString=(String)map.get("claimStatus");
                ClaimStatus claimStatus=(ClaimStatus)ClaimStatus.valueOf(claimStatusInString);
                String claimStatusResult=claimStatus.getDescription();
                glClaimIntimationDto.setClaimStatus(claimStatusResult);
                return glClaimIntimationDto;
            }
        }).collect(Collectors.toList());


    }

    public GLClaimIntimationDetailsDto  getClaimIntimationDetails(String claimId) {
        Map claimRecordMap = glClaimFinder.findClaimById(claimId);

        GLClaimIntimationDetailsDto claimIntimationDetailDto = new GLClaimIntimationDetailsDto();
        String claimIdFromDb = claimRecordMap.get("_id").toString();
        claimIntimationDetailDto.setClaimId(claimIdFromDb);
        String claimNumber = ((ClaimNumber) claimRecordMap.get("claimNumber")).getClaimNumber();
        claimIntimationDetailDto.setClaimNumber(claimNumber);
        String category = (String) claimRecordMap.get("category");
        String relationship = (String) claimRecordMap.get("relationship");
        claimIntimationDetailDto.setCategory(category);
        claimIntimationDetailDto.setRelationship(relationship);
        Policy policy = (Policy) claimRecordMap.get("policy");
        PolicyNumber policyNumber = policy.getPolicyNumber();
        claimIntimationDetailDto.setPolicyNumber(policyNumber.getPolicyNumber());
        PolicyId policyId = policy.getPolicyId();
        String policyIdInString = policyId.getPolicyId();
        claimIntimationDetailDto.setPolicyId(policyIdInString);
        claimIntimationDetailDto.setSchemeName((String) claimRecordMap.get("schemeName"));
        // claimIntimationDetailDto.setPlanDetail(claimRecordMap.get(""));
        //claimIntimationDetailDto.setCategory(claimRecordMap.get(""));
        String claimTypeString = (String) claimRecordMap.get("claimType");
        ClaimType claimType = (ClaimType) ClaimType.valueOf(claimTypeString);
        claimIntimationDetailDto.setClaimType(claimType);
        DateTime intimationDate = claimRecordMap.get("intimationDate") != null ? new DateTime(claimRecordMap.get("intimationDate")) : null;
        DateTime incidenceDate = claimRecordMap.get("incidenceDate") != null ? new DateTime(claimRecordMap.get("incidenceDate")) : null;
        java.util.Date date = ((Date) claimRecordMap.get("intimationDate"));
        DateTimeZone dtz = DateTimeZone.getDefault();// Gets the default time zone.
        DateTime dateTime = new DateTime(date.getTime(), dtz);
        claimIntimationDetailDto.setClaimIntimationDate(intimationDate);
        claimIntimationDetailDto.setClaimIncidenceDate(incidenceDate);
        Proposer proposer = (Proposer) claimRecordMap.get("proposer");
        ProposerContactDetail proposerContactDetail = proposer.getContactDetail();
        List<ProposerContactDetail.ContactPersonDetail> contactPersonDetails = proposerContactDetail.getContactPersonDetail();
        List<ContactPersonDetail> contactPersonDetailList = new ArrayList<ContactPersonDetail>();
        for (ProposerContactDetail.ContactPersonDetail contactPersonDetail : contactPersonDetails) {
            ContactPersonDetail contactPerson = new ContactPersonDetail(contactPersonDetail.getContactPersonName(), contactPersonDetail.getContactPersonEmail(), contactPersonDetail.getMobileNumber(), contactPersonDetail.getWorkPhoneNumber());
            contactPersonDetailList.add(contactPerson);
        }
        ClaimantDetail claimantDetail = new ClaimantDetail(proposer.getProposerName(), proposerContactDetail.getAddressLine1(), proposerContactDetail.getAddressLine2(),
                proposerContactDetail.getPostalCode(), proposerContactDetail.getProvince(), proposerContactDetail.getTown(), proposerContactDetail.getEmailAddress(), null);
        claimantDetail.withContactPersonDetails(contactPersonDetailList);
        claimIntimationDetailDto.withClaimant(claimantDetail);
        PlanDetail planDetail = (PlanDetail) claimRecordMap.get("planDetail");
        PlanDetailDto planDetailDto = new PlanDetailDto(planDetail.getPlanId().getPlanId(),  planDetail.getPlanName(),planDetail.getPlanCode(), planDetail.getPremiumAmount(), planDetail.getSumAssured());
        claimIntimationDetailDto.setPlanDetail(planDetailDto);
        List<CoverageDetail> coverageDetails = (List<CoverageDetail>) claimRecordMap.get("coverageDetails");
        Set<CoverageDetailDto> coverageDetailsSet = new LinkedHashSet<CoverageDetailDto>();
        for (CoverageDetail coverageDetail : coverageDetails) {
            CoverageDetailDto coverageDetailDto = new CoverageDetailDto(coverageDetail.getCoverageCode(), coverageDetail.getCoverageId().getCoverageId(), coverageDetail.getCoverageName(), coverageDetail.getSumAssured(), coverageDetail.getPremium());
            coverageDetailsSet.add(coverageDetailDto);
        }
        claimIntimationDetailDto.setCoverageDetails(coverageDetailsSet);
        BankDetails bankDetails = (BankDetails) claimRecordMap.get("bankDetails");
        BankDetailsDto bankDetailsDto = new BankDetailsDto(bankDetails.getBankName(), bankDetails.getBankBranchName(), bankDetails.getBankAccountType(), bankDetails.getBankAccountNumber());
        claimIntimationDetailDto.withBankDetails(bankDetailsDto);
        ClaimAssuredDetail assuredDetail = (ClaimAssuredDetail) claimRecordMap.get("assuredDetail");
        ClaimMainAssuredDetail claimMainAssuredDetail = assuredDetail.getClaimMainAssuredDetail();
        ClaimMainAssuredDetailDto claimMainAssuredDetailDto = new ClaimMainAssuredDetailDto(claimMainAssuredDetail.getFullName(), claimMainAssuredDetail.getRelationship(), claimMainAssuredDetail.getNrcNumber(), claimMainAssuredDetail.getManNumber(), claimMainAssuredDetail.getLastSalary());
        ClaimAssuredDetailDto claimAssuredDetail = new ClaimAssuredDetailDto();
        claimAssuredDetail.setTitle(assuredDetail.getTitle());
        claimAssuredDetail.setFirstName(assuredDetail.getFirstName());
        claimAssuredDetail.setSurName(assuredDetail.getSurName());
        claimAssuredDetail.setDateOfBirth(assuredDetail.getDateOfBirth());
        claimAssuredDetail.setAgeOnNextBirthDate(assuredDetail.getAgeOnNextBirthDate());
        claimAssuredDetail.setNrcNumber(assuredDetail.getNrcNumber());
        claimAssuredDetail.setGender(assuredDetail.getGender());
        claimAssuredDetail.setSumAssured(assuredDetail.getSumAssured());
        claimAssuredDetail.setReserveAmount(assuredDetail.getReserveAmount());
        claimAssuredDetail.setCategory(assuredDetail.getCategory());
        claimAssuredDetail.setManNumber(assuredDetail.getManNumber());
        claimAssuredDetail.setLastSalary(assuredDetail.getLastSalary());
        claimAssuredDetail.setOccupation(assuredDetail.getOccupation());
        claimAssuredDetail.updateWithMainAssured(claimMainAssuredDetailDto);
        claimIntimationDetailDto.withAssuredDetail(claimAssuredDetail);
        ClaimDisabilityRegistrationDto disabilityRegistrationDetails = null;
        ClaimRegistrationDto claimRegistrationDetails = null;
        ClaimRegistration claimRegistration = (ClaimRegistration) claimRecordMap.get("claimRegistration");
        DisabilityClaimRegistration disabilityClaimRegistration = (DisabilityClaimRegistration) claimRecordMap.get("disabilityClaimRegistration");

        if (claimRegistration != null) {
            claimRegistrationDetails = new ClaimRegistrationDto();
            claimRegistrationDetails.setCauseOfDeath(claimRegistration.getCauseOfDeath());
            claimRegistrationDetails.setPlaceOfDeath(claimRegistration.getPlaceOfDeath());
            claimRegistrationDetails.setDateOfDeath(claimRegistration.getDateOfDeath());
            claimRegistrationDetails.setTimeOfDeath(claimRegistration.getTimeOfDeath());
            claimRegistrationDetails.setDurationOfIllness(claimRegistration.getDurationOfIllness());
            claimRegistrationDetails.setNameOfDoctorAndHospitalAddress(claimRegistration.getNameOfDoctorAndHospitalAddress());
            claimRegistrationDetails.setContactNumber(claimRegistration.getContactNumber());
            claimRegistrationDetails.setFirstConsultation(claimRegistration.getDateOfFirstConsultation());
            claimRegistrationDetails.setTreatmentTaken(claimRegistration.getTreatmentTaken());
            claimRegistrationDetails.setIsCauseOfDeathAccidental(claimRegistration.getIsCauseOfDeathAccidental());
            claimRegistrationDetails.setTypeOfAccident(claimRegistration.getTypeOfAccident());
            claimRegistrationDetails.setPlaceOfAccident(claimRegistration.getPlaceOfAccident());
            claimRegistrationDetails.setDateOfAccident(claimRegistration.getDateOfAccident());
            claimRegistrationDetails.setTimeOfAccident(claimRegistration.getTimeOfAccident());
            claimRegistrationDetails.setIsPostMortemAutopsyDone(claimRegistration.getIsPostMortemAutopsyDone());
            claimRegistrationDetails.setIsPoliceReportRegistered(claimRegistration.getIsPoliceReportRegistered());
            claimRegistrationDetails.setRegistrationNumber(claimRegistration.getRegistrationNumber());
            claimRegistrationDetails.setPoliceStationName(claimRegistration.getPoliceStationName());
        }
        claimIntimationDetailDto.withClaimRegistration(claimRegistrationDetails);
        if (disabilityClaimRegistration != null) {
            disabilityRegistrationDetails = new ClaimDisabilityRegistrationDto();
            disabilityRegistrationDetails.setDateOfDisability(disabilityClaimRegistration.getDateOfDisability());
            disabilityRegistrationDetails.setNatureOfDisability(disabilityClaimRegistration.getNatureOfDisability());
            disabilityRegistrationDetails.setExtendOfDisability(disabilityClaimRegistration.getExtendOfDisability());
            disabilityRegistrationDetails.setDateOfDiagnosis(disabilityClaimRegistration.getDateOfDiagnosis());
            disabilityRegistrationDetails.setExactDiagnosis(disabilityClaimRegistration.getExactDiagnosis());
            disabilityRegistrationDetails.setNameOfDoctorAndHospitalAddress(disabilityClaimRegistration.getNameOfDoctorAndHospitalAddress());
            disabilityRegistrationDetails.setContactNumberOfHospital(disabilityClaimRegistration.getContactNumberOfHospital());
            disabilityRegistrationDetails.setDateOfFirstConsultation(disabilityClaimRegistration.getDateOfFirstConsultation());
            disabilityRegistrationDetails.setTreatmentTaken(disabilityClaimRegistration.getTreatmentTaken());
            disabilityRegistrationDetails.setCapabilityOfAssuredDailyLiving(disabilityClaimRegistration.getCapabilityOfAssuredDailyLiving());
            disabilityRegistrationDetails.setAssuredGainfulActivities(disabilityClaimRegistration.getAssuredGainfulActivities());
            disabilityRegistrationDetails.setDetailsOfWorkActivities(disabilityClaimRegistration.getDetailsOfWorkActivities());
            disabilityRegistrationDetails.setFromActivitiesDate(disabilityClaimRegistration.getFromActivitiesDate());
            disabilityRegistrationDetails.setAssuredConfinedToIndoor(disabilityClaimRegistration.getAssuredConfinedToIndoor());
            disabilityRegistrationDetails.setFromIndoorDate(disabilityClaimRegistration.getFromIndoorDate());
            disabilityRegistrationDetails.setAssuredIndoorDetails(disabilityClaimRegistration.getAssuredIndoorDetails());
            disabilityRegistrationDetails.setAssuredAbleToGetOutdoor(disabilityClaimRegistration.getAssuredAbleToGetOutdoor());
            disabilityRegistrationDetails.setFromOutdoorDate(disabilityClaimRegistration.getFromOutdoorDate());
            disabilityRegistrationDetails.setAssuredOutdoorDetails(disabilityClaimRegistration.getAssuredOutdoorDetails());
            disabilityRegistrationDetails.setVisitingMedicalOfficerDetails(disabilityClaimRegistration.getVisitingMedicalOfficerDetails());
        }
        claimIntimationDetailDto.withDisabilityRegistration(disabilityRegistrationDetails);
        GlClaimUnderWriterApprovalDetail underWriterApprovalDetail = (GlClaimUnderWriterApprovalDetail) claimRecordMap.get("underWriterReviewDetail");
        if(underWriterApprovalDetail!=null){
        //creating approver plan detail
        ApprovalDetailsDto approvalDetails = new ApprovalDetailsDto();
        ClaimApproverPlanDto planDetails = new ClaimApproverPlanDto();
        GLClaimApproverPlanDetail approverPlanDetail = underWriterApprovalDetail.getPlanDetail();
        if (approverPlanDetail != null) {
            planDetails.setPlanName(approverPlanDetail.getPlanName());
            planDetails.setPlanSumAssured(approverPlanDetail.getPlanSumAssured());
            planDetails.setApprovedAmount(approverPlanDetail.getApprovedAmount());
            planDetails.setAmendedAmount(approverPlanDetail.getAmendedAmount());
            //Adding plan details
            approvalDetails.setPlanDetails(planDetails);
        }
        //getting and adding coverage details
        BigDecimal tempTotalApprovedAmount = underWriterApprovalDetail.getTotalApprovedAmount();
        approvalDetails.setTotalApprovedAmount(tempTotalApprovedAmount);
        BigDecimal totalRecoveredAmout = underWriterApprovalDetail.getTotalRecoveredAmount();
        approvalDetails.setTotalRecoveredOrAdditionalAmount(totalRecoveredAmout);
        BigDecimal additionalAmountPaid = underWriterApprovalDetail.getAdditionalAmountPaid();
        List<ApproverCoverageDetail> coverageDetailList = underWriterApprovalDetail.getCoverageDetails();
        List<ClaimApproverCoverageDetailDto> approverCoverageList = new ArrayList<ClaimApproverCoverageDetailDto>();
        if (coverageDetailList != null) {
            for (ApproverCoverageDetail approverCoverageDetail : coverageDetailList) {
                ClaimApproverCoverageDetailDto coverageDetailDto = new ClaimApproverCoverageDetailDto();
                coverageDetailDto.setCoverageName(approverCoverageDetail.getCoverageName());
                coverageDetailDto.setSumAssured(approverCoverageDetail.getSumAssured());
                coverageDetailDto.setApprovedAmount(approverCoverageDetail.getApprovedAmount());
                coverageDetailDto.setAmendedAmount(approverCoverageDetail.getAmendedAmount());
                //if(coverageDetailDto.getSumAssured()!=null){
                //   tempSumAssured=tempSumAssured.add(coverageDetailDto.getSumAssured());
                //}
                approverCoverageList.add(coverageDetailDto);
            }
        }
        approvalDetails.setCoverageDetails(approverCoverageList);
        //getting and adding review deails
        List<ClaimReviewDetail> reviewDetailsList = underWriterApprovalDetail.getReviewDetails();
        List<ClaimReviewDto> reviewDetails = new ArrayList<ClaimReviewDto>();
        if (reviewDetailsList != null) {
            for (ClaimReviewDetail claimReviewDetail : reviewDetailsList) {
                ClaimReviewDto claimReviewDto = new ClaimReviewDto(claimReviewDetail.getComments(), claimReviewDetail.getTimings(), claimReviewDetail.getUserName());
                reviewDetails.add(claimReviewDto);
            }
        }
        approvalDetails.setReviewDetails(reviewDetails);
        String comments = underWriterApprovalDetail.getComment();
        approvalDetails.setComments(comments);
        DateTime referredToReassuredOn = underWriterApprovalDetail.getReferredToReassuredOn();
        approvalDetails.setReferredToReassuredOn(referredToReassuredOn);
        DateTime responseReceivedOn = underWriterApprovalDetail.getResponseReceivedOn();
        approvalDetails.setReferredToReassuredOn(responseReceivedOn);

        claimIntimationDetailDto.withApprovalDetail(approvalDetails);

    }
            return claimIntimationDetailDto;
        }

    public List<GLClaimIntimationDto> getClaimDetail(SearchClaimDto searchClaimDto) {
        List<Map> searchedClaimRecords = glClaimFinder.getClaimDetails(searchClaimDto.getClaimNumber(), searchClaimDto.getPolicyNumber(),
                searchClaimDto.getPolicyHolderName(), searchClaimDto.getAssuredName(),
                searchClaimDto.getAssuredClientId(), searchClaimDto.getAssuredNrcNumber());
        if (isEmpty(searchedClaimRecords)) {
            return Lists.newArrayList();
        }
        return searchedClaimRecords.parallelStream().map(new Function<Map, GLClaimIntimationDto>() {
            @Override
            public GLClaimIntimationDto apply(Map map) {
                GLClaimIntimationDto glClaimIntimationDto = new  GLClaimIntimationDto();
                String claimId = map.get("_id").toString();
                glClaimIntimationDto.setClaimId(claimId);
                Proposer proposer = (Proposer) map.get("proposer");
                Policy policy=(Policy)map.get("policy");
                String policyHolderName=policy.getPolicyHolderName();
                glClaimIntimationDto.setPolicyHolderName( policyHolderName);
                ClaimNumber claimNumber=(ClaimNumber)map.get("claimNumber");
                glClaimIntimationDto.setClaimNumber(claimNumber.getClaimNumber());
                ClaimAssuredDetail claimAssuredDetail=(ClaimAssuredDetail) map.get("assuredDetail");
                glClaimIntimationDto.setAssuredName(claimAssuredDetail.getFirstName());
                glClaimIntimationDto.setAssuredNrcNumber(claimAssuredDetail.getNrcNumber());
                String policyNumber=policy.getPolicyNumber().getPolicyNumber();
                glClaimIntimationDto.setPolicyNumber(policyNumber);
                String claimTypeString=(String)map.get("claimType");
                ClaimType claimType=(ClaimType)ClaimType.valueOf(claimTypeString);
                glClaimIntimationDto.setClaimType(claimType);
                String claimStatusInString=(String)map.get("claimStatus");
                ClaimStatus claimStatus=(ClaimStatus)ClaimStatus.valueOf(claimStatusInString);
                String claimStatusResult=claimStatus.getDescription();
                glClaimIntimationDto.setClaimStatus(claimStatusResult);
                return glClaimIntimationDto;
            }
        }).collect(Collectors.toList());
    }


    public List<ClaimIntimationDetailDto> getClaimIntimationDetail(String policyNumber) {
        List<Map> assuredSearchList = glFinder.assuredSearch(policyNumber);
        return assuredSearchList.parallelStream().map(new Function<Map, ClaimIntimationDetailDto>() {
            @Override
            public ClaimIntimationDetailDto apply(Map map) {
                ClaimIntimationDetailDto claimIntimationDetailDto = new ClaimIntimationDetailDto();
                Proposer proposer = (Proposer) map.get("proposer");
                claimIntimationDetailDto.withProposer(proposer);
               /* List<InsuredDependent> insuredDependentList = Lists.newArrayList();
                List<Insured> insureds = (List<Insured>) map.get("insureds");
                for (Insured insured : insureds) {
                    Set<InsuredDependent> insuredDependents = insured.getInsuredDependents();
                    for (InsuredDependent insuredDependent : insuredDependents) {
                        String familyId = insuredDependent.getFamilyId() != null ? insuredDependent.getFamilyId().getFamilyId() : "";
                        if (familyId.equals(clientId)) {
                            insuredDependentList.add(insuredDependent);
                        }
                    }
                }
                List<Insured> insuredsList = Lists.newArrayList();
                for (Insured insured : insureds) {
                    String familyId = insured.getFamilyId() != null ? insured.getFamilyId().getFamilyId() : "";
                    if (familyId.equals(clientId)) {
                        insuredsList.add(insured);
                    }
                }
                if (isNotEmpty(insuredDependentList)) {
                    claimIntimationDetailDto.withInsuredDependentAssuredDetail(insuredDependentList.get(0));
                }
                if (isNotEmpty(insuredsList)) {
                    claimIntimationDetailDto.withInsuredAssuredDetail(insuredsList.get(0));
                }*/
                return claimIntimationDetailDto;
            }
        }).collect(Collectors.toList());
    }

    public List<GLInsuredDetailDto> assuredSearch(AssuredSearchDto assuredSearchDto) {
        List<Map> assuredSearchList = glFinder.assuredSearch(assuredSearchDto.getPolicyNumber());
        List<InsuredDependent> insuredDependentList = Lists.newArrayList();
        for (Map assuredSearchMap : assuredSearchList) {
            List<Insured> insureds = (List<Insured>) assuredSearchMap.get("insureds");
            for (Insured insured : insureds) {
                Set<InsuredDependent> insuredDependents = insured.getInsuredDependents();
                for (InsuredDependent insuredDependent : insuredDependents) {
                    String category = insuredDependent.getCategory() != null ? insuredDependent.getCategory() : "";
                    String relationship = insuredDependent.getRelationship() != null ? insuredDependent.getRelationship().name(): "";
                    if (category.equals(assuredSearchDto.getCategory()) && relationship.equals(assuredSearchDto.getRelationShip())) {
                        insuredDependentList.add(insuredDependent);

                    }
                }
            }
        }

        List<Insured> insureds = Lists.newArrayList();
        for (Map insuredMap : assuredSearchList) {
            List<Insured> insuredsList = (List<Insured>) insuredMap.get("insureds");
            for (Insured insured : insuredsList) {
                String category = insured.getCategory() != null ? insured.getCategory() : "";
                String relationship = insured.getRelationship() != null ? insured.getRelationship().description : Relationship.SELF.description;
                if (category.equals(assuredSearchDto.getCategory()) && relationship.equals(assuredSearchDto.getRelationShip())) {
                    insureds.add(insured);
                }
            }
        }

        List<GLInsuredDetailDto> searchList = Lists.newArrayList();
        for (InsuredDependent insuredDependent : insuredDependentList) {
            GLInsuredDetailDto glInsuredDetailDto = new GLInsuredDetailDto(insuredDependent.getFirstName(), insuredDependent.getLastName(),
                    insuredDependent.getDateOfBirth() != null ? insuredDependent.getDateOfBirth().toString(AppConstants.DD_MM_YYY_FORMAT) : "", insuredDependent.getGender() != null ? insuredDependent.getGender().name() : "", insuredDependent.getNrcNumber(),
                    insuredDependent.getManNumber(), insuredDependent.getFamilyId() != null ? insuredDependent.getFamilyId().getFamilyId() : "");
            searchList.add(glInsuredDetailDto);
        }
        for (Insured insured : insureds) {
            GLInsuredDetailDto glInsuredDetailDto = new GLInsuredDetailDto(insured.getFirstName(), insured.getLastName(),
                    insured.getDateOfBirth() != null ? insured.getDateOfBirth().toString(AppConstants.DD_MM_YYY_FORMAT) : "", insured.getGender() != null ? insured.getGender().name() : "", insured.getNrcNumber(),
                    insured.getManNumber(), insured.getFamilyId() != null ? insured.getFamilyId().getFamilyId() : "");
            searchList.add(glInsuredDetailDto);
        }

        List<GLInsuredDetailDto> advancedSearchList = Lists.newArrayList();
        for (GLInsuredDetailDto glInsuredDetailDto : searchList) {
            String clientId = assuredSearchDto.getClientId() != null ? assuredSearchDto.getClientId() : "";
            String firstName = assuredSearchDto.getFirstName() != null ? assuredSearchDto.getFirstName() : "";
            String lastName = assuredSearchDto.getSurName() != null ? assuredSearchDto.getSurName() : "";
            String nrcNumber = assuredSearchDto.getNrcNumber() != null ? assuredSearchDto.getNrcNumber() : "";
            String manNumber = assuredSearchDto.getManNumber() != null ? assuredSearchDto.getManNumber() : "";
            Gender gender =assuredSearchDto.getGender() != null ? assuredSearchDto.getGender() : null;
            LocalDate dateOfBirth = assuredSearchDto.getDateOfBirth() != null ? assuredSearchDto.getDateOfBirth() : null;

            /*
            if(glInsuredDetailDto.getFirstName().equals(firstName)&&glInsuredDetailDto.getSurName().equals(lastName)&&
                    glInsuredDetailDto.getNrcNumber().equals(nrcNumber) &&glInsuredDetailDto.getManNumber().equals(manNumber)&&
                    glInsuredDetailDto.getGender().equals(gender)&&glInsuredDetailDto.getDateOfBirth().equals(dateOfBirth)&&
            glInsuredDetailDto.getClientId().equals(clientId))
            */
            if(glInsuredDetailDto.getFirstName().equals(firstName)||glInsuredDetailDto.getSurName().equals(lastName)||
                    glInsuredDetailDto.getNrcNumber().equals(nrcNumber) ||glInsuredDetailDto.getManNumber().equals(manNumber)||
                    glInsuredDetailDto.getGender().equals(gender)||glInsuredDetailDto.getDateOfBirth().equals(dateOfBirth)||
                    glInsuredDetailDto.getClientId().equals(clientId))
            {
                advancedSearchList.add(glInsuredDetailDto);
            }
        }
        return advancedSearchList;
    }

    public ClaimAssuredDetailDto getAssuredDetails(String policyId,String searchClientId){
        ClaimAssuredDetailDto claimAssuredDetailDto=null ;

        Map policy = glFinder.findPolicyById(policyId);
        if (isEmpty(policy)) {
            //return Collections.EMPTY_MAP;
            return new ClaimAssuredDetailDto();
        }

        List<Insured> insuredsList = (List<Insured>) policy.get("insureds");
        for (Insured insured : insuredsList) {
            String clientId = insured.getFamilyId().getFamilyId() != null ? insured.getFamilyId().getFamilyId() : "";

            if (clientId.equals(searchClientId) ) {
                ClaimAssuredDetailDto searchClaimAssuredDetailDto  =new ClaimAssuredDetailDto() ;
                searchClaimAssuredDetailDto.setTitle(insured.getSalutation());
                searchClaimAssuredDetailDto.setFirstName(insured.getFirstName());
                searchClaimAssuredDetailDto.setSurName(insured.getLastName());
                searchClaimAssuredDetailDto.setDateOfBirth(insured.getDateOfBirth());
                LocalDate birthday=insured.getDateOfBirth();
                LocalDate today = LocalDate.now();
                Period period = new Period(birthday, today, PeriodType.yearMonthDay());
                int assuredAge=period.getYears()+1;
                searchClaimAssuredDetailDto.setAgeOnNextBirthDate(assuredAge);
                searchClaimAssuredDetailDto.setNrcNumber(insured.getNrcNumber());
                searchClaimAssuredDetailDto.setGender(insured.getGender());
                BigDecimal assuredSumAssured=insured.getPlanPremiumDetail().getSumAssured();
                Set<CoveragePremiumDetail> coveragePremiumDetails=insured.getCoveragePremiumDetails();
                if(coveragePremiumDetails!=null){
                    for(CoveragePremiumDetail coveragePremiumDetail:coveragePremiumDetails){
                        assuredSumAssured= coveragePremiumDetail.getSumAssured();
                    }
                }
                searchClaimAssuredDetailDto.setSumAssured(assuredSumAssured);
                searchClaimAssuredDetailDto.setReserveAmount(assuredSumAssured);
                searchClaimAssuredDetailDto.setCategory(insured.getCategory());
                searchClaimAssuredDetailDto.setManNumber(insured.getManNumber());
                searchClaimAssuredDetailDto.setOccupation(insured.getOccupationClass());
                claimAssuredDetailDto =searchClaimAssuredDetailDto;
            }
        }

        if(claimAssuredDetailDto==null){

            List<Insured> insuredList = (List<Insured>) policy.get("insureds");;
            for (Insured insured :insuredList) {
                Set<InsuredDependent> insuredDependents = insured.getInsuredDependents();
                for (InsuredDependent insuredDependent : insuredDependents) {
                    String clientId = insuredDependent.getFamilyId().getFamilyId() != null ? insuredDependent.getFamilyId().getFamilyId() : "";
                    if (clientId.equals(searchClientId) )  {
                        ClaimAssuredDetailDto searchClaimAssuredDetailDto  =new ClaimAssuredDetailDto() ;
                        searchClaimAssuredDetailDto.setTitle(insuredDependent.getSalutation());
                        searchClaimAssuredDetailDto.setFirstName(insuredDependent.getFirstName());
                        searchClaimAssuredDetailDto.setSurName(insuredDependent.getLastName());
                        searchClaimAssuredDetailDto.setDateOfBirth(insuredDependent.getDateOfBirth());
                        LocalDate birthday=insuredDependent.getDateOfBirth();
                        LocalDate today = LocalDate.now();
                        Period period = new Period(birthday, today, PeriodType.yearMonthDay());
                        int assuredAge=period.getYears();
                        searchClaimAssuredDetailDto.setAgeOnNextBirthDate(assuredAge);
                        searchClaimAssuredDetailDto.setNrcNumber(insuredDependent.getNrcNumber());
                        searchClaimAssuredDetailDto.setGender(insuredDependent.getGender());
                        BigDecimal assuredSumAssured=insuredDependent.getPlanPremiumDetail().getSumAssured();
                        Set<CoveragePremiumDetail> coveragePremiumDetails=insuredDependent.getCoveragePremiumDetails();
                        if(coveragePremiumDetails!=null){
                            for(CoveragePremiumDetail coveragePremiumDetail:coveragePremiumDetails){
                                assuredSumAssured= coveragePremiumDetail.getSumAssured();
                            }
                        }
                        searchClaimAssuredDetailDto.setSumAssured(assuredSumAssured);
                        searchClaimAssuredDetailDto.setReserveAmount(assuredSumAssured);
                        searchClaimAssuredDetailDto.setCategory(insuredDependent.getCategory());
                        searchClaimAssuredDetailDto.setManNumber(insuredDependent.getManNumber());
                        // searchClaimAssuredDetailDto.set(insured.);
                        searchClaimAssuredDetailDto.setOccupation(insuredDependent.getOccupationClass());
                        ClaimMainAssuredDetailDto mainAssuredDetailDto=new ClaimMainAssuredDetailDto();
                        mainAssuredDetailDto.setFullName(insured.getFirstName());
                        mainAssuredDetailDto .setRelationship(insured.getRelationship().description);
                        mainAssuredDetailDto.setManNumber(insured.getManNumber());
                        mainAssuredDetailDto.setNrcNumber(insured.getNrcNumber());
                        claimAssuredDetailDto.updateWithMainAssured(mainAssuredDetailDto);
                        claimAssuredDetailDto =searchClaimAssuredDetailDto;
                    }
                }
            }
        }
        return claimAssuredDetailDto;
    }
    public GLClaimSettlementDataDto getClaimRecordForSettlement(String claimId){
        return  null;
    }



    public Map<String, Object> getConfiguredRelationShipAndCategory(String policyId) {
        Set<String> categoryList = Sets.newLinkedHashSet();
        Set<String> relationShipList = Sets.newLinkedHashSet();
        Map policy = glFinder.findPolicyById(policyId);
        if (isEmpty(policy)) {
            return Collections.EMPTY_MAP;
        }
        List<Insured> insuredList = (List<Insured>) policy.get("insureds");
        relationShipList.add(Relationship.SELF.description);
        for (Insured insured : insuredList) {
            categoryList.add(insured.getCategory() != null ? insured.getCategory() : "");
            Set<InsuredDependent> insuredDependentList = insured.getInsuredDependents();
            for (InsuredDependent insuredDependentMap : insuredDependentList) {
                categoryList.add(insuredDependentMap.getCategory() != null ? insuredDependentMap.getCategory() : "");
                relationShipList.add(insuredDependentMap.getRelationship() != null ? insuredDependentMap.getRelationship().name() : "");
            }
        }
        Map<String, Object> relationCategoryMap = Maps.newLinkedHashMap();
        relationCategoryMap.put("relationship", relationShipList);
        relationCategoryMap.put("category", categoryList);
        PolicyNumber policyNumber = policy.get("policyNumber") != null ? (PolicyNumber) policy.get("policyNumber") : null;
        relationCategoryMap.put("policyNumber", policyNumber);
        return relationCategoryMap;
    }

    public Set<String> getCategory(String policyId) {
        Set<String> categoryList = Sets.newLinkedHashSet();
        Map policy = glFinder.findPolicyById(policyId);
        if (isEmpty(policy)) {
            return Collections.EMPTY_SET;
        }
        List<Insured> insuredList = (List<Insured>) policy.get("insureds");
        for (Insured insured : insuredList) {
            categoryList.add(insured.getCategory() != null ? insured.getCategory() : "");
            Set<InsuredDependent> insuredDependentList = insured.getInsuredDependents();
            for (InsuredDependent insuredDependentMap : insuredDependentList) {
                categoryList.add(insuredDependentMap.getCategory() != null ? insuredDependentMap.getCategory() : "");
            }
        }

        return categoryList;
    }


    public Map<String, Object> getConfiguredCategory(String policyId) {
        Set<String> categoryList = Sets.newLinkedHashSet();
        Map policy = glFinder.findPolicyById(policyId);
        if (isEmpty(policy)) {
            return Collections.EMPTY_MAP;
        }
        List<Insured> insuredList = (List<Insured>) policy.get("insureds");
        for (Insured insured : insuredList) {
            categoryList.add(insured.getCategory() != null ? insured.getCategory() : "");
            Set<InsuredDependent> insuredDependentList = insured.getInsuredDependents();
            for (InsuredDependent insuredDependentMap : insuredDependentList) {
                categoryList.add(insuredDependentMap.getCategory() != null ? insuredDependentMap.getCategory() : "");
            }
        }
        Map<String, Object> categoryMap = Maps.newLinkedHashMap();
        categoryMap.put("category", categoryList);
        PolicyNumber policyNumber = policy.get("policyNumber") != null ? (PolicyNumber) policy.get("policyNumber") : null;
        categoryMap.put("policyNumber", policyNumber);
        return categoryMap;
    }


    public Map<String, Object> getConfiguredRelationShip(String policyId,String categorySearch) {
        Set<String> categoryList = Sets.newLinkedHashSet();
        Set<String> relationShipList = Sets.newLinkedHashSet();
        Map policy = glFinder.findPolicyById(policyId);
        if (isEmpty(policy)) {
            return Collections.EMPTY_MAP;
        }
        List<Insured> insuredList = (List<Insured>) policy.get("insureds");

        for (Insured insured : insuredList) {
            if(insured.getCategory() != null && insured.getCategory().equals(categorySearch)){
                String relationship = insured.getRelationship() != null ? insured.getRelationship().description : Relationship.SELF.description;
                relationShipList.add(relationship);

            }
            Set<InsuredDependent> insuredDependentList = insured.getInsuredDependents();
            for (InsuredDependent insuredDependents : insuredDependentList) {
                String category=insuredDependents.getCategory();
                if(category != null &&category.equals(categorySearch) ){
                    relationShipList.add(insuredDependents.getRelationship() != null ? insuredDependents.getRelationship().name() : "");
                }
            }
        }
        Map<String, Object> relationMap = Maps.newLinkedHashMap();
        relationMap.put("relationship", relationShipList);

        PolicyNumber policyNumber = policy.get("policyNumber") != null ? (PolicyNumber) policy.get("policyNumber") : null;
        relationMap.put("policyNumber", policyNumber);
        return relationMap;
    }

    public PlanCoverageDetailDto getPlanDetailForCategoryAndRelationship(String policyId, String searchCategory,String searchRelationship){
        Map policy = glFinder.findPolicyById(policyId);
        if (isEmpty(policy)) {
            return new PlanCoverageDetailDto();
        }
        PlanCoverageDetailDto planCoverageDetailDto=new PlanCoverageDetailDto();
        List<Insured> insuredList = (List<Insured>) policy.get("insureds");

        List<InsuredDependent> insuredDependentList = Lists.newArrayList();
        Set<Insured> insuredLists = Sets.newLinkedHashSet();
        Set<InsuredDependent> relationShipList = Sets.newLinkedHashSet();
        for (Insured insured : insuredList) {
            Set<InsuredDependent> insuredDependents = insured.getInsuredDependents();
            for (InsuredDependent insuredDependent : insuredDependents) {
                String category = insuredDependent.getCategory() != null ? insuredDependent.getCategory() : "";
                String relationship = insuredDependent.getRelationship() != null ? insuredDependent.getRelationship().name() : "";
                if (category.equals(searchCategory) && relationship.equals(searchRelationship)) {
                    insuredDependentList.add(insuredDependent);

                }
            }
            List<Insured> insureds = Lists.newArrayList();
            List<Insured> insuredsList = (List<Insured>) policy.get("insureds");
            PolicyNumber policyNumberObj = policy.get("policyNumber") != null ? (PolicyNumber) policy.get("policyNumber") : null;
            String policyNumber=policyNumberObj.getPolicyNumber();
            for (Insured insure : insuredsList) {
                String category = insure.getCategory() != null ? insure.getCategory() : "";
                String relationship = insured.getRelationship() != null ? insured.getRelationship().description : Relationship.SELF.description;
                // String relationship = insure.getRelationship() != null ? insure.getRelationship().name() : "";

                if (category.equals(searchCategory) && relationship.equals(searchRelationship)) {
                    insureds.add(insure);

                }
            }


            if (insureds!=null){
                Insured insuredResult=insureds.get(0);
                PlanPremiumDetail planPremiumDetail=insuredResult.getPlanPremiumDetail();
                String planName=null;
                Set<String>claimTypes=new LinkedHashSet<String>();
                String searchPlanCode=planPremiumDetail.getPlanCode();
                List<Map<String, Object>> planMap=productClaimMapperFinder.getPlanDetailBy(LineOfBusinessEnum.GROUP_LIFE);
                for(Map<String,Object>plan:planMap){
                    String mapPlanCode=(String)plan.get("planCode");

                    if(searchPlanCode.equals(mapPlanCode)) {
                        planName=(String)plan.get("planName");
                    }

                }

                List<ProductClaimTypeDto> productClaimList=productClaimMapperFinder.searchProductClaimMap(null,planName);
                //  List<ProductClaimTypeDto> productClaimList=productClaimMapperFinder.searchProductClaimMap(LineOfBusinessEnum.GROUP_LIFE,"Life Cover");

                for(ProductClaimTypeDto productClaimTypeDto:productClaimList){
                    List<CoverageClaimTypeDto> coverageClaimType= productClaimTypeDto.getCoverageClaimType();
                    for(CoverageClaimTypeDto coverageClaimTypeDto:coverageClaimType){
                        Set<String> claimTypesResult=  coverageClaimTypeDto.getClaimTypes();
                        for(String claimString:claimTypesResult){
                            claimTypes.add(claimString);
                        }
                    }

                }
                PlanPremiumDetail planPremiumDetails=insured.getPlanPremiumDetail();
                PlanDetailDto planDetailDto=new PlanDetailDto();
                planDetailDto.setPlanId(planPremiumDetails.getPlanId().getPlanId());
                planDetailDto.setPlanCode(planPremiumDetails.getPlanCode());
                planDetailDto.setPlanName(planName);
                planDetailDto.setPremiumAmount(planPremiumDetails.getPremiumAmount());
                planDetailDto.setSumAssured(planPremiumDetails.getSumAssured());
                planCoverageDetailDto.setPlanDetailDto(planDetailDto);
                Set<CoveragePremiumDetail>coveragePremiumDetailList=insured.getCoveragePremiumDetails();
                Set<CoverageDetailDto> coverageDetailDtos=new LinkedHashSet<CoverageDetailDto>();

                if(coveragePremiumDetailList!=null) {
                    for (CoveragePremiumDetail coveragePremiumDetail : coveragePremiumDetailList) {
                        CoverageDetailDto coverageDetailDto = new CoverageDetailDto();
                        String coverageIdInString=coveragePremiumDetail.getCoverageId().getCoverageId();
                        CoverageDto coverageDto=coverageFinder.findCoverageById(coverageIdInString);
                        coverageDetailDto.setCoverageId(coveragePremiumDetail.getCoverageId().getCoverageId());
                        coverageDetailDto.setCoverageCode(coveragePremiumDetail.getCoverageCode());
                        coverageDetailDto.setCoverageName(coverageDto.getCoverageName());
                        coverageDetailDto.setSumAssured(coveragePremiumDetail.getSumAssured());
                        coverageDetailDto.setPremium(coveragePremiumDetail.getPremium());
                        coverageDetailDtos.add(coverageDetailDto);

                    }
                }

                planCoverageDetailDto.setCoverageDetailDtos(coverageDetailDtos);
                planCoverageDetailDto.setPolicyNumber(policyNumber);
                planCoverageDetailDto.setClaimTypes(claimTypes);

            }
            else{

                InsuredDependent  insuredDependent=insuredDependentList.get(0) ;
                PlanPremiumDetail planPremiumDetail=insuredDependent.getPlanPremiumDetail();
                String planName=null;
                Set<String>claimTypes=null;
                List<Map<String, Object>> planMap=productClaimMapperFinder.getPlanDetailBy(LineOfBusinessEnum.GROUP_LIFE);
                for(Map<String,Object>plan:planMap){
                    if(((String)plan.get("planCode")).equals(planPremiumDetail.getPlanCode())){
                        planName=(String)plan.get("planName");
                    }
                }

                List<ProductClaimTypeDto> productClaimList=productClaimMapperFinder.searchProductClaimMap(LineOfBusinessEnum.GROUP_LIFE,planName);
                for(ProductClaimTypeDto productClaimTypeDto:productClaimList){
                    List<CoverageClaimTypeDto> coverageClaimType= productClaimTypeDto.getCoverageClaimType();
                    for(CoverageClaimTypeDto coverageClaimTypeDto:coverageClaimType){
                        claimTypes=  coverageClaimTypeDto.getClaimTypes();
                    }
                }

                PlanPremiumDetail planPremiumDetails=insured.getPlanPremiumDetail();
                PlanDetailDto planDetailDto=new PlanDetailDto();
                planDetailDto.setPlanId(planPremiumDetails.getPlanId().getPlanId());
                planDetailDto.setPlanCode(planPremiumDetails.getPlanCode());
                planDetailDto.setPlanName(planName);
                planDetailDto.setPremiumAmount(planPremiumDetails.getPremiumAmount());
                planDetailDto.setSumAssured(planPremiumDetails.getSumAssured());
                planCoverageDetailDto.setPlanDetailDto(planDetailDto);
                Set<CoveragePremiumDetail>coveragePremiumDetailList=insuredDependent.getCoveragePremiumDetails();
                Set<CoverageDetailDto> coverageDetailDtos=new LinkedHashSet<CoverageDetailDto>();
                if(coveragePremiumDetailList!=null) {
                    for (CoveragePremiumDetail coveragePremiumDetail : coveragePremiumDetailList) {
                        CoverageDetailDto coverageDetailDto = new CoverageDetailDto();
                        String coverageIdInString=coveragePremiumDetail.getCoverageId().getCoverageId();
                        CoverageDto coverageDto=coverageFinder.findCoverageById(coverageIdInString);
                        coverageDetailDto.setCoverageId(coveragePremiumDetail.getCoverageId().getCoverageId());
                        coverageDetailDto.setCoverageCode(coveragePremiumDetail.getCoverageCode());
                        coverageDetailDto.setCoverageName(coverageDto.getCoverageName());
                        coverageDetailDto.setSumAssured(coveragePremiumDetail.getSumAssured());
                        coverageDetailDto.setPremium(coveragePremiumDetail.getPremium());
                        coverageDetailDtos.add(coverageDetailDto);

                    }
                }
                planCoverageDetailDto.setCoverageDetailDtos(coverageDetailDtos);
                planCoverageDetailDto.setPolicyNumber(policyNumber);
                planCoverageDetailDto.setClaimTypes(claimTypes);
            }
        }
        return planCoverageDetailDto;
    }

    private GLPolicyDetailDto transformToDto(Map policyMap) {
        DateTime inceptionDate = policyMap.get("inceptionOn") != null ? new DateTime((Date) policyMap.get("inceptionOn")) : null;
        DateTime expiryDate = policyMap.get("expiredOn") != null ? new DateTime((Date) policyMap.get("expiredOn")) : null;
        Proposer glProposer = policyMap.get("proposer") != null ? (Proposer) policyMap.get("proposer") : null;
        PolicyNumber policyNumber = policyMap.get("policyNumber") != null ? (PolicyNumber) policyMap.get("policyNumber") : null;
        GLPolicyDetailDto policyDetailDto = new GLPolicyDetailDto();
        policyDetailDto.setPolicyId(policyMap.get("_id").toString());
        policyDetailDto.setInceptionDate(inceptionDate);
        policyDetailDto.setExpiryDate(expiryDate);
        policyDetailDto.setPolicyHolderName(glProposer != null ? glProposer.getProposerName() : "");
        policyDetailDto.setPolicyNumber(policyNumber.getPolicyNumber());
        policyDetailDto.setStatus((String) policyMap.get("status"));
        return policyDetailDto;
    }

    private List<Map> searchByClientId(String clientId, List<Map> searchedPolices) {
        if (isNotEmpty(clientId)) {
            return searchedPolices.parallelStream().filter(new Predicate<Map>() {
                @Override
                public boolean test(Map searchedPolicyMap) {
                    boolean isFound = false;
                    List<Insured> insuredList = (List<Insured>) searchedPolicyMap.get("insureds");
                    for (Insured insured : insuredList) {
                        FamilyId familyId = insured.getFamilyId();
                        if (familyId != null) {
                            isFound = clientId.equals(familyId.getFamilyId());
                        }
                        if (isFound) {
                            return isFound;
                        }
                        Set<InsuredDependent> insuredDependentList = insured.getInsuredDependents();
                        for (InsuredDependent insuredDependentMap : insuredDependentList) {
                            FamilyId dependentFamilyId = insuredDependentMap.getFamilyId();
                            if (dependentFamilyId != null) {
                                return clientId.equals(dependentFamilyId.getFamilyId());
                            }
                        }
                    }
                    return false;
                }
            }).collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

    private List<GLClaimMandatoryDocumentDto> getAllMandatoryDocuments(List<GLClaimDocument> uploadedDocuments,List<ClaimMandatoryDocumentDto> mandatoryDocumentRequiredForSubmission) {
        List<GLClaimMandatoryDocumentDto> mandatoryDocumentList = Lists.newArrayList();
        if (isNotEmpty(mandatoryDocumentRequiredForSubmission)) {
            mandatoryDocumentList = mandatoryDocumentRequiredForSubmission.stream().map(new Function<ClaimMandatoryDocumentDto, GLClaimMandatoryDocumentDto>() {
                @Override
                public GLClaimMandatoryDocumentDto apply(ClaimMandatoryDocumentDto documentDto) {
                    GLClaimMandatoryDocumentDto mandatoryDocumentDto = new GLClaimMandatoryDocumentDto(documentDto.getDocumentCode(), documentDto.getDocumentName());
                    Optional<GLClaimDocument> claimDocumentOptional = uploadedDocuments.stream().filter(new Predicate<GLClaimDocument>() {
                        @Override
                        public boolean test(GLClaimDocument glClaimDocument) {
                            return documentDto.getDocumentCode().equals(glClaimDocument.getDocumentId());
                        }
                    }).findAny();
                    if (claimDocumentOptional.isPresent()) {
                        try {
                            if (isNotEmpty(claimDocumentOptional.get().getGridFsDocId())) {
                                GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(claimDocumentOptional.get().getGridFsDocId())));
                                mandatoryDocumentDto.setFileName(gridFSDBFile.getFilename());
                                mandatoryDocumentDto.setContentType(gridFSDBFile.getContentType());
                                mandatoryDocumentDto.setGridFsDocId(gridFSDBFile.getId().toString());
                                mandatoryDocumentDto.updateWithContent(IOUtils.toByteArray(gridFSDBFile.getInputStream()));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return mandatoryDocumentDto;
                }
            }).collect(Collectors.toList());
        }
        return mandatoryDocumentList;
    }


    public GLClaimDocument getAddedDocument(MultipartFile file,String documentId,boolean mandatory)throws IOException{

        File file1 = null;
        String contentType ="";
        GLClaimDocument addedDocument=null;
        String fileName = file != null ? file.getName() :contentType;
        // String gridFsDocId = gridFsTemplate.store(glProposalDocumentCommand.getFile().getInputStream(), fileName, glProposalDocumentCommand.getFile().getContentType()).getId().toString();
        String gridFsDocId = gridFsTemplate.store(file.getInputStream(), fileName, contentType).getId().toString();
        addedDocument = new GLClaimDocument(documentId, fileName, gridFsDocId, "", mandatory);
        return addedDocument;
    }



    ////////////////////////////////////////////////////////////////////
    public boolean isClaimIntimationAvailable(String claimId){
        Map claimMap = glClaimFinder.findClaimById(claimId);
        return isNotEmpty(claimMap)?true:false;

    }



    public PolicyId getPolicyIdFromClaim(String claimId) {
        Map claimMap = glClaimFinder.findClaimById(claimId);
        Policy policy =claimMap != null ? (Policy) claimMap.get("policy") : null;
        PolicyId policyId = policy != null ? policy.getPolicyId() : null;
        return policyId;
    }

    public   List<GLClaimMandatoryDocumentDto>getAllClaimMandatoryDocuments(String searchPlanId){
        List<SearchDocumentDetailDto> documentDetailDtos = Lists.newArrayList();
        PlanId planId=new PlanId(searchPlanId);

        SearchDocumentDetailDto searchDocumentDetailDto=new SearchDocumentDetailDto(planId);

        documentDetailDtos.add(searchDocumentDetailDto);
        Set<ClientDocumentDto> mandatoryDocuments = underWriterAdapter.getMandatoryDocumentsForApproverApproval(documentDetailDtos, ProcessType.CLAIM);

        List<GLClaimMandatoryDocumentDto> mandatoryDocumentDtoList=new ArrayList<GLClaimMandatoryDocumentDto>();
        for(ClientDocumentDto clientDocumentDto:mandatoryDocuments){
            GLClaimMandatoryDocumentDto mandatoryDocumentDto = new GLClaimMandatoryDocumentDto(clientDocumentDto.getDocumentCode(), clientDocumentDto.getDocumentName());
            mandatoryDocumentDtoList.add(mandatoryDocumentDto);
        }
        // GLClaimMandatoryDocumentDto mandatoryDocumentDto = new GLClaimMandatoryDocumentDto(clientDocumentDto.getDocumentCode(), clientDocumentDto.getDocumentName());
        return  mandatoryDocumentDtoList;
    }

    public List<GLClaimMandatoryDocumentDto> findMandatoryDocuments(String claimId) {
        Map claimMap = glClaimFinder.findClaimById(claimId);
        PolicyId policyId = getPolicyIdFromClaim(claimId);
        Map policyMap = glPolicyFinder.findPolicyById(policyId.getPolicyId());
        List<Insured> insureds = (List<Insured>) policyMap.get("insureds");

        List<GLClaimDocument> uploadedDocuments = claimMap.get("claimDocuments") != null ? (List<GLClaimDocument>) claimMap.get("claimDocuments") : Lists.newArrayList();

        List<SearchDocumentDetailDto> documentDetailDtos = Lists.newArrayList();
        PlanDetail planDetail=(PlanDetail)claimMap.get("planDetail");
        PlanId planId=planDetail.getPlanId();
        List<CoverageDetail> coverageDetailsList=(List<CoverageDetail>)claimMap.get("coverageDetails");
        List<CoverageId> coverageIdList=new ArrayList<CoverageId>();


        if(coverageDetailsList !=null){
            for(CoverageDetail coverageDetail:coverageDetailsList ){
                CoverageId coverageId=coverageDetail.getCoverageId();
                coverageIdList.add(coverageId);
            }
        }

        SearchDocumentDetailDto searchDocumentDetailDto = new SearchDocumentDetailDto(planId,coverageIdList);
        documentDetailDtos.add(searchDocumentDetailDto);

        Set<ClientDocumentDto> mandatoryDocuments = underWriterAdapter.getMandatoryDocumentsForApproverApproval(documentDetailDtos, ProcessType.CLAIM);
        List<GLClaimMandatoryDocumentDto> mandatoryDocumentDtos = Lists.newArrayList();
        if (isNotEmpty(mandatoryDocuments)) {
            mandatoryDocumentDtos = mandatoryDocuments.stream().map(new Function<ClientDocumentDto, GLClaimMandatoryDocumentDto>() {
                @Override
                public GLClaimMandatoryDocumentDto apply(ClientDocumentDto clientDocumentDto) {
                    GLClaimMandatoryDocumentDto mandatoryDocumentDto = new GLClaimMandatoryDocumentDto(clientDocumentDto.getDocumentCode(), clientDocumentDto.getDocumentName());
                    Optional<GLClaimDocument> claimDocumentOptional = uploadedDocuments.stream().filter(new Predicate<GLClaimDocument>() {
                        @Override
                        public boolean test(GLClaimDocument glClaimDocument) {
                            return clientDocumentDto.getDocumentCode().equals(glClaimDocument.getDocumentId());
                        }
                    }).findAny();
                    if (claimDocumentOptional.isPresent()) {
                        mandatoryDocumentDto.setRequireForSubmission(claimDocumentOptional.get().isRequireForSubmission());
                        mandatoryDocumentDto.setIsApproved(claimDocumentOptional.get().isApproved());
                        mandatoryDocumentDto.setMandatory(claimDocumentOptional.get().isMandatory());
                        mandatoryDocumentDto.setSubmitted(claimDocumentOptional.get().isApproved());
                        try {
                            if (isNotEmpty(claimDocumentOptional.get().getGridFsDocId())) {
                                GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(claimDocumentOptional.get().getGridFsDocId())));
                                mandatoryDocumentDto.setFileName(gridFSDBFile.getFilename());
                                mandatoryDocumentDto.setContentType(gridFSDBFile.getContentType());
                                mandatoryDocumentDto.setGridFsDocId(gridFSDBFile.getId().toString());
                                mandatoryDocumentDto.updateWithContent(IOUtils.toByteArray(gridFSDBFile.getInputStream()));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return mandatoryDocumentDto;
                }
            }).collect(Collectors.toList());
        }
        return mandatoryDocumentDtos;
    }

    public Set<GLClaimMandatoryDocumentDto> findAdditionalDocuments(String claimId) {
        Map claimMap = glClaimFinder.findClaimById(claimId);
        List<GLClaimDocument> uploadedDocuments = claimMap.get("claimDocuments") != null ? (List<GLClaimDocument>) claimMap.get("claimDocuments") : Lists.newArrayList();
        Set<GLClaimMandatoryDocumentDto> mandatoryDocumentDtos = Sets.newHashSet();
        if (isNotEmpty(uploadedDocuments)) {
            mandatoryDocumentDtos = uploadedDocuments.stream().filter(uploadedDocument -> !uploadedDocument.isMandatory()).map(new Function<GLClaimDocument, GLClaimMandatoryDocumentDto>() {
                @Override
                public GLClaimMandatoryDocumentDto apply(GLClaimDocument glClaimDocument) {
                    GLClaimMandatoryDocumentDto mandatoryDocumentDto = new GLClaimMandatoryDocumentDto(glClaimDocument.getDocumentId(), glClaimDocument.getDocumentName());
                    GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(glClaimDocument.getGridFsDocId())));
                    mandatoryDocumentDto.setFileName(gridFSDBFile.getFilename());
                    mandatoryDocumentDto.setContentType(gridFSDBFile.getContentType());
                    mandatoryDocumentDto.setGridFsDocId(gridFSDBFile.getId().toString());
                    try {
                        mandatoryDocumentDto.updateWithContent(IOUtils.toByteArray(gridFSDBFile.getInputStream()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return mandatoryDocumentDto;
                }
            }).collect(Collectors.toSet());
        }
        return mandatoryDocumentDtos;
    }

    public List<GLClaimMandatoryDocumentDto> getAllMandatoryDocumentsForClaim(String planId){
        List<MandatoryDocumentDto>documentList=glClaimFinder.getAllClaimMandatoryDocuments(planId, ProcessType.CLAIM);
        //List<GLClaimMandatoryDocumentDto> glClaimMandatoryDocumentList=new ArrayList<GLClaimMandatoryDocumentDto>();
        List<GLClaimMandatoryDocumentDto> mandatoryDocumentDtoList=new ArrayList<GLClaimMandatoryDocumentDto>();
        for(MandatoryDocumentDto mandatoryDocumentDto:documentList){
            List<Map<String,Object>> documents=mandatoryDocumentDto.getDocuments();
            for(Map<String,Object> docMap:documents){
                GLClaimMandatoryDocumentDto documentDto = new GLClaimMandatoryDocumentDto((String)docMap.get("documentCode"),(String)docMap.get("documentName"));
                mandatoryDocumentDtoList.add(documentDto);
            }
            //GLClaimMandatoryDocumentDto DocumentDto = new GLClaimMandatoryDocumentDto((String)documents.get("documentCode"), clientDocumentDto.getDocumentName());

        }

        return  mandatoryDocumentDtoList;
    }

     public ClaimReviewDto getClaimReviewDetails(String claimId){
       List<Map> claimReviewMap=glClaimFinder.getClaimReviewByClaimId(claimId);
         ClaimReviewDto claimReviewDto=new ClaimReviewDto();
        // List<String> commentList=new ArrayList<String>();
         //List<String> userNamesList=new ArrayList<String>();
        // List<DateTime> dateTimes=new ArrayList<DateTime>();
         for(Map reviewMap:claimReviewMap){
          String comment=(String)reviewMap.get("comment");
          String user=(String) reviewMap.get("modifiedBy");
          DateTime   dateTime = reviewMap.get("modifiedOn") != null ? new DateTime((Date) reviewMap.get("modifiedOn")) : null;
             claimReviewDto.setComments(comment);
             claimReviewDto.setUserNames(user);
             claimReviewDto.setTimings(dateTime);
            // commentList.add(comment);
            // userNamesList.add(user);
            // dateTimes.add(dateTime);
         }

         return claimReviewDto;
    }
    public  UnderWriterRoutingLevel configuredForSelectedPlan(String planIdInString){

        String  processType="CLAIM";
        PlanId planId=new PlanId(planIdInString);
        UnderWriterRoutingLevelDetailDto underWriterRoutingLevelDetailDto=new UnderWriterRoutingLevelDetailDto(planId, LocalDate.now(),processType);
        UnderWriterRoutingLevel underWriterRoutingLevel=underWriterFinder.findUnderWriterRoutingLevel(underWriterRoutingLevelDetailDto);
        return underWriterRoutingLevel;
    }

    public RoutingLevel configuredForPlan(String claimId){
        Map claimMap = glClaimFinder.findClaimById(claimId);
        PlanDetail planDetail=(PlanDetail)claimMap.get("planDetail");

        PlanId planId=planDetail.getPlanId();
        List<CoverageDetail > coverageDetails=(List<CoverageDetail >)claimMap.get("coverageDetails");
        String  processType="CLAIM";
        UnderWriterRoutingLevelDetailDto underWriterRoutingLevelDetailDto=new UnderWriterRoutingLevelDetailDto(planId, LocalDate.now(),processType);
        RoutingLevel routingLevel=underWriterAdapter.getRoutingLevel(underWriterRoutingLevelDetailDto);

        return routingLevel;
    }
    String getReminderStatus(String claimId){
        NotificationBuilder claimNotificationBuilder=new NotificationBuilder();

        return "reminder sent";
    }

    public List<GLClaimDocument> getUploadedMandatoryDocument(String claimId){
        Map claim = glClaimFinder.findClaimById(claimId);
        List<GLClaimDocument> uploadedDocuments = claim.get("claimDocuments") != null ? (List<GLClaimDocument>)claim.get("claimDocuments") : Lists.newArrayList();
        return uploadedDocuments;
    }

    public Set<ClientDocumentDto> getMandatoryDocumentRequiredForSubmission(String claimId){
        Map claim = glClaimFinder.findClaimById(claimId);

        Map claimMap = glClaimFinder.findClaimById(claimId);
        PolicyId policyId = getPolicyIdFromClaim(claimId);
        Map policyMap = glPolicyFinder.findPolicyById(policyId.getPolicyId());
        List<Insured> insureds = (List<Insured>) policyMap.get("insureds");

        List<GLClaimDocument> uploadedDocuments = claimMap.get("claimDocuments") != null ? (List<GLClaimDocument>) claimMap.get("claimDocuments") : Lists.newArrayList();

        List<SearchDocumentDetailDto> documentDetailDtos = Lists.newArrayList();
        PlanDetail planDetail=(PlanDetail)claimMap.get("planDetail");
        PlanId planId=planDetail.getPlanId();
        List<CoverageDetail> coverageDetailsList=(List<CoverageDetail>)claimMap.get("coverageDetails");
        List<CoverageId> coverageIdList=new ArrayList<CoverageId>();


        if(coverageDetailsList !=null){
            for(CoverageDetail coverageDetail:coverageDetailsList ){
                CoverageId coverageId=coverageDetail.getCoverageId();
                coverageIdList.add(coverageId);
            }
        }
        SearchDocumentDetailDto searchDocumentDetailDto = new SearchDocumentDetailDto(planId,coverageIdList);
        documentDetailDtos.add(searchDocumentDetailDto);
        Set<ClientDocumentDto> mandatoryDocuments = underWriterAdapter.getMandatoryDocumentsForApproverApproval(documentDetailDtos, ProcessType.CLAIM);
        return mandatoryDocuments;
    }


    public List<GLClaimDataDto> getClaimDetailForApproval(SearchClaimDto searchClaimDto,String[] statuses){
        List<Map> searchedClaimRecords = glClaimFinder.getApprovedClaimDetails(searchClaimDto.getClaimNumber(), searchClaimDto.getPolicyNumber(),
                searchClaimDto.getPolicyHolderName(), searchClaimDto.getAssuredName(),
                searchClaimDto.getAssuredClientId(), searchClaimDto.getAssuredNrcNumber(),statuses);


    return null;
    }
    /*
        List<Map> searchedClaimRecords = glClaimFinder.getApprovedClaimDetails(searchClaimDto.getClaimNumber(), searchClaimDto.getPolicyNumber(),
                searchClaimDto.getPolicyHolderName(), searchClaimDto.getAssuredName(),
                searchClaimDto.getAssuredClientId(), searchClaimDto.getAssuredNrcNumber(),statuses);
        if (isEmpty(searchedClaimRecords)) {
            return Lists.newArrayList();
        }
        return searchedClaimRecords.parallelStream().map(new Function<Map, GLClaimDataDto>() {
            @Override
            public GLClaimDataDto apply(Map map) {
                GLClaimDataDto claimDataDto = new GLClaimDataDto();
                String claimId = map.get("_id").toString();
                ClaimNumber claimNumber = (ClaimNumber) map.get("claimNumber");
                //ClaimType.valueOf((String)map.get("claimType"));
                ClaimStatus claimStatus = ClaimStatus.valueOf((String) map.get("claimStatus"));
                String claimNumberInString = claimNumber.getClaimNumber();
                claimDataDto.withClaimNumberAndClaimId(claimNumberInString, claimId);
                Policy policy = (Policy) map.get("policy");
                String policyNumber = policy.getPolicyNumber().getPolicyNumber();
                String policyHolderName = policy.getPolicyHolderName();
                ClaimAssuredDetail assuredDetail = ( ClaimAssuredDetail) map.get("assuredDetail");
                String assuredName = assuredDetail.getFirstName();
                claimDataDto.setAssuredName(assuredName);
                claimDataDto.setClaimStatus(claimStatus.toString());
                claimDataDto.setPolicyNumber(policyNumber);
                claimDataDto.setPolicyHolderName(policyHolderName);

                //claimDataDto.setModifiedOn();
                return claimDataDto;
            }
        }).collect(Collectors.toList());

    }
     */


    public List<GLClaimDataDto> getApprovedClaimDetail (SearchClaimIntimationDto  searchClaimDto,String[] statuses){
        List<Map> searchedClaimRecords = glClaimFinder.getApprovedClaimDetails(searchClaimDto.getClaimNumber(), searchClaimDto.getPolicyNumber(),
                searchClaimDto.getPolicyHolderName(), searchClaimDto.getAssuredName(),
                searchClaimDto.getPolicyHolderClientId(), searchClaimDto.getAssuredNrcNumber(),statuses);
        if (isEmpty(searchedClaimRecords)) {
            return Lists.newArrayList();
        }
        return searchedClaimRecords.parallelStream().map(new Function<Map, GLClaimDataDto>() {
            @Override
            public GLClaimDataDto apply(Map map) {
                GLClaimDataDto claimDataDto = new GLClaimDataDto();
                String claimId = map.get("_id").toString();
                ClaimNumber claimNumber = (ClaimNumber) map.get("claimNumber");
                String claimTypeString = (String) map.get("claimType");
                ClaimType claimType = (ClaimType) ClaimType.valueOf(claimTypeString);
                claimDataDto.setClaimType(claimType);
                String claimStatusInString = (String) map.get("claimStatus");
                ClaimStatus claimStatus = (ClaimStatus) ClaimStatus.valueOf(claimStatusInString);
                String claimStatusResult = claimStatus.getDescription();
                claimDataDto.setClaimStatus(claimStatusResult);
                String routingLevelInString = (String) map.get("taggedRoutingLevel");
                String resultRoutingLevel="";
                if(routingLevelInString!=null){
                    RoutingLevel routingLevel = (RoutingLevel) RoutingLevel.valueOf(routingLevelInString);
                     resultRoutingLevel = routingLevel.getDescription();
                }
                claimDataDto.setRoutingLevel(resultRoutingLevel);
                String claimNumberInString = claimNumber.getClaimNumber();
                claimDataDto.withClaimNumberAndClaimId(claimNumberInString, claimId);
                Policy policy = (Policy) map.get("policy");
                String policyNumber = policy.getPolicyNumber().getPolicyNumber();
                claimDataDto.setPolicyNumber(policyNumber);
                String policyHolderName = policy.getPolicyHolderName();
                claimDataDto.setPolicyHolderName(policyHolderName);
                PlanDetail planDetail=(PlanDetail)map.get("planDetail");
                if(planDetail!=null){
                  String planName=planDetail.getPlanName();
                    claimDataDto.setPlanName(planName);
                }

                ClaimAssuredDetail assuredDetail = (ClaimAssuredDetail) map.get("assuredDetail");
                if (assuredDetail != null) {
                    String title = assuredDetail.getTitle();
                    String assuredFirstName = assuredDetail.getFirstName();
                    String assuredSurName = assuredDetail.getSurName();
                    claimDataDto.setAssuredName(title + "  " + assuredFirstName+"  "+assuredSurName);

                }

                GlClaimUnderWriterApprovalDetail underWriterApprovalDetail = (GlClaimUnderWriterApprovalDetail) map.get("underWriterReviewDetail");
                if (underWriterApprovalDetail!=null){
                    BigDecimal underWriterApprovedAmount=underWriterApprovalDetail.getTotalApprovedAmount();
                    claimDataDto.setApprovedAmount(underWriterApprovedAmount);
                }
                DateTime intimationDate = map.get("intimationDate") != null ? new DateTime(map.get("intimationDate")) : null;
                claimDataDto.setClaimIntimationDate(intimationDate);

                DateTime approvalDate = map.get("submittedOn") != null ? new DateTime((Date) map.get("submittedOn")) : null;
                //claimDataDto.setApprovedOn(approvalDate);

                DateTime today = new DateTime();
                Duration duration = new Duration(intimationDate, today);
                Long gapInDays=duration.getStandardDays();
                int gapInDaysInInteger= Integer.valueOf(gapInDays.toString());
                claimDataDto.setRecordCreationInDays(gapInDaysInInteger);
                String claimAmountInString=(String)map.get("claimAmount");
               // BigDecimal claimAmount=new BigDecimal((String)map.get("claimAmount"));
                BigDecimal  resultantClaimAmount=BigDecimal.ZERO;
                if (claimAmountInString!=null){
                    resultantClaimAmount=new BigDecimal(claimAmountInString);
                }
                claimDataDto.setClaimAmount(resultantClaimAmount);
                return claimDataDto;
            }
        }).collect(Collectors.toList());

    }
    public  List<GLClaimDataDto>  getAllApprovedClaimDetail(){
        List<Map> searchedClaimRecords = glClaimFinder.getAllApprovedClaimRecords(new String[]{"APPROVED"});
        if (isEmpty(searchedClaimRecords)) {
            return Lists.newArrayList();
        }
        return searchedClaimRecords.parallelStream().map(new Function<Map, GLClaimDataDto>() {
            @Override
            public GLClaimDataDto apply(Map map) {
                GLClaimDataDto claimDataDto = new GLClaimDataDto();
                String claimId = map.get("_id").toString();
                ClaimNumber claimNumber = (ClaimNumber) map.get("claimNumber");
                String claimTypeString = (String) map.get("claimType");
                ClaimType claimType = (ClaimType) ClaimType.valueOf(claimTypeString);
                claimDataDto.setClaimType(claimType);
                String claimStatusInString = (String) map.get("claimStatus");
                ClaimStatus claimStatus = (ClaimStatus) ClaimStatus.valueOf(claimStatusInString);
                String claimStatusResult = claimStatus.getDescription();
                claimDataDto.setClaimStatus(claimStatusResult);
                String routingLevelInString = (String) map.get("taggedRoutingLevel");
                String resultRoutingLevel="";
                if(routingLevelInString!=null){
                    RoutingLevel routingLevel = (RoutingLevel) RoutingLevel.valueOf(routingLevelInString);
                    resultRoutingLevel = routingLevel.getDescription();
                }
                claimDataDto.setRoutingLevel(resultRoutingLevel);
                String claimNumberInString = claimNumber.getClaimNumber();
                claimDataDto.withClaimNumberAndClaimId(claimNumberInString, claimId);
                Policy policy = (Policy) map.get("policy");
                String policyNumber = policy.getPolicyNumber().getPolicyNumber();
                claimDataDto.setPolicyNumber(policyNumber);
                String policyHolderName = policy.getPolicyHolderName();
                claimDataDto.setPolicyHolderName(policyHolderName);
                PlanDetail planDetail=(PlanDetail)map.get("planDetail");
                if(planDetail!=null){
                    String planName=planDetail.getPlanName();
                    claimDataDto.setPlanName(planName);
                }

                ClaimAssuredDetail assuredDetail = (ClaimAssuredDetail) map.get("assuredDetail");
                if (assuredDetail != null) {
                    String title = assuredDetail.getTitle();
                    String assuredFirstName = assuredDetail.getFirstName();
                    String assuredSurName = assuredDetail.getSurName();
                    claimDataDto.setAssuredName(title + "  " + assuredFirstName+"  "+assuredSurName);

                }

                GlClaimUnderWriterApprovalDetail underWriterApprovalDetail = (GlClaimUnderWriterApprovalDetail) map.get("underWriterReviewDetail");
                if (underWriterApprovalDetail!=null){
                    BigDecimal underWriterApprovedAmount=underWriterApprovalDetail.getTotalApprovedAmount();
                    claimDataDto.setApprovedAmount(underWriterApprovedAmount);
                }

                DateTime intimationDate = map.get("intimationDate") != null ? new DateTime(map.get("intimationDate")) : null;
                claimDataDto.setClaimIntimationDate(intimationDate);

                DateTime approvalDate = map.get("submittedOn") != null ? new DateTime((Date) map.get("submittedOn")) : null;
                claimDataDto.setApprovedOn(approvalDate);

                DateTime today = new DateTime();
                Duration duration = new Duration(intimationDate, today);
                Long gapInDays=duration.getStandardDays();
                int gapInDaysInInteger= Integer.valueOf(gapInDays.toString());
                claimDataDto.setRecordCreationInDays(gapInDaysInInteger);
                String claimAmountInString=(String)map.get("claimAmount");
                // BigDecimal claimAmount=new BigDecimal((String)map.get("claimAmount"));
                BigDecimal  resultantClaimAmount=BigDecimal.ZERO;
                if (claimAmountInString!=null){
                    resultantClaimAmount=new BigDecimal(claimAmountInString);
                }
                claimDataDto.setClaimAmount(resultantClaimAmount);
                return claimDataDto;
            }
        }).collect(Collectors.toList());

    }

public List<GLClaimDataDto> getClaimDetailForSettlement (SearchClaimIntimationDto  searchClaimDto,String[] statuses){
        List<Map> searchedClaimRecords = glClaimFinder.getRequiredApprovedClaimDetails(searchClaimDto.getClaimNumber(), searchClaimDto.getPolicyNumber(),
                searchClaimDto.getPolicyHolderName(), searchClaimDto.getAssuredName(),
                searchClaimDto.getPolicyHolderClientId(), searchClaimDto.getAssuredNrcNumber(),statuses);
        if (isEmpty(searchedClaimRecords)) {
            return Lists.newArrayList();
        }
        return searchedClaimRecords.parallelStream().map(new Function<Map, GLClaimDataDto>() {
            @Override
            public GLClaimDataDto apply(Map map) {
                GLClaimDataDto claimDataDto = new GLClaimDataDto();
                String claimId = map.get("_id").toString();
                ClaimNumber claimNumber = (ClaimNumber) map.get("claimNumber");
                String claimTypeString = (String) map.get("claimType");
                ClaimType claimType = (ClaimType) ClaimType.valueOf(claimTypeString);
                claimDataDto.setClaimType(claimType);
                String claimStatusInString = (String) map.get("claimStatus");
                ClaimStatus claimStatus = (ClaimStatus) ClaimStatus.valueOf(claimStatusInString);
                String claimStatusResult = claimStatus.getDescription();
                claimDataDto.setClaimStatus(claimStatusResult);
                String routingLevelInString = (String) map.get("taggedRoutingLevel");
                String resultRoutingLevel="";
                if(routingLevelInString!=null){
                    RoutingLevel routingLevel = (RoutingLevel) RoutingLevel.valueOf(routingLevelInString);
                     resultRoutingLevel = routingLevel.getDescription();
                }
                claimDataDto.setRoutingLevel(resultRoutingLevel);
                String claimNumberInString = claimNumber.getClaimNumber();
                claimDataDto.withClaimNumberAndClaimId(claimNumberInString, claimId);
                Policy policy = (Policy) map.get("policy");
                String policyNumber = policy.getPolicyNumber().getPolicyNumber();
                claimDataDto.setPolicyNumber(policyNumber);
                String policyHolderName = policy.getPolicyHolderName();
                claimDataDto.setPolicyHolderName(policyHolderName);
                PlanDetail planDetail=(PlanDetail)map.get("planDetail");
                if(planDetail!=null){
                  String planName=planDetail.getPlanName();
                    claimDataDto.setPlanName(planName);
                }

                ClaimAssuredDetail assuredDetail = (ClaimAssuredDetail) map.get("assuredDetail");
                if (assuredDetail != null) {
                    String title = assuredDetail.getTitle();
                    String assuredFirstName = assuredDetail.getFirstName();
                    String assuredSurName = assuredDetail.getSurName();
                    claimDataDto.setAssuredName(title + "  " + assuredFirstName+"  "+assuredSurName);

                }

                GlClaimUnderWriterApprovalDetail underWriterApprovalDetail = (GlClaimUnderWriterApprovalDetail) map.get("underWriterReviewDetail");
                if (underWriterApprovalDetail!=null){
                    BigDecimal underWriterApprovedAmount=underWriterApprovalDetail.getTotalApprovedAmount();
                    claimDataDto.setApprovedAmount(underWriterApprovedAmount);
                }
                DateTime intimationDate = map.get("intimationDate") != null ? new DateTime(map.get("intimationDate")) : null;
                claimDataDto.setClaimIntimationDate(intimationDate);

                DateTime approvalDate = map.get("submittedOn") != null ? new DateTime((Date) map.get("submittedOn")) : null;
                claimDataDto.setApprovedOn(approvalDate);

                DateTime today = new DateTime();
                Duration duration = new Duration(intimationDate, today);
                Long gapInDays=duration.getStandardDays();
                int gapInDaysInInteger= Integer.valueOf(gapInDays.toString());
                claimDataDto.setRecordCreationInDays(gapInDaysInInteger);
                String claimAmountInString=(String)map.get("claimAmount");
               // BigDecimal claimAmount=new BigDecimal((String)map.get("claimAmount"));
                BigDecimal  resultantClaimAmount=BigDecimal.ZERO;
                if (claimAmountInString!=null){
                    resultantClaimAmount=new BigDecimal(claimAmountInString);
                }
                claimDataDto.setClaimAmount(resultantClaimAmount);
                return claimDataDto;
            }
        }).collect(Collectors.toList());

    }




    public List<GLClaimDataDto> getApprovedClaimDetailLevelOne (SearchClaimDto searchClaimDto,String[] statuses){
        List<Map> searchedClaimRecords = glClaimFinder.getApprovedClaimDetailsLevelOne(searchClaimDto.getClaimNumber(), searchClaimDto.getPolicyNumber(),
                searchClaimDto.getPolicyHolderName(), searchClaimDto.getAssuredName(),
                searchClaimDto.getAssuredClientId(), searchClaimDto.getAssuredNrcNumber(),statuses);
        if (isEmpty(searchedClaimRecords)) {
            return Lists.newArrayList();
        }
        return searchedClaimRecords.parallelStream().map(new Function<Map, GLClaimDataDto>() {
            @Override
            public GLClaimDataDto apply(Map map) {
                GLClaimDataDto claimDataDto = new GLClaimDataDto();
                String claimId = map.get("_id").toString();
                ClaimNumber claimNumber = (ClaimNumber) map.get("claimNumber");
                //ClaimType.valueOf((String)map.get("claimType"));
                ClaimStatus claimStatus = ClaimStatus.valueOf((String) map.get("claimStatus"));
                String claimNumberInString = claimNumber.getClaimNumber();
                claimDataDto.withClaimNumberAndClaimId(claimNumberInString, claimId);
                Policy policy = (Policy) map.get("policy");
                String policyNumber = policy.getPolicyNumber().getPolicyNumber();
                String policyHolderName = policy.getPolicyHolderName();
                ClaimAssuredDetail assuredDetail = ( ClaimAssuredDetail) map.get("assuredDetail");
                String assuredName = assuredDetail.getFirstName();
                claimDataDto.setAssuredName(assuredName);
                claimDataDto.setClaimStatus(claimStatus.toString());
                claimDataDto.setPolicyNumber(policyNumber);
            DateTime approvalDate = map.get("submittedOn") != null ? new DateTime((Date) map.get("submittedOn")) : null;

                claimDataDto.setApprovedOn(approvalDate);
                return claimDataDto;
            }
        }).collect(Collectors.toList());

    }
    //get claim record for reopen


    public  List<GLClaimDataDto> getClaimRecordForReopen(SearchClaimDto searchClaimDto){

        List<Map> searchedClaimRecords = glClaimFinder.getReopenClaimDetails(searchClaimDto.getClaimNumber(), searchClaimDto.getPolicyNumber(),
                searchClaimDto.getPolicyHolderName(), searchClaimDto.getAssuredName(),
                searchClaimDto.getAssuredClientId(), searchClaimDto.getAssuredNrcNumber());

        if (isEmpty(searchedClaimRecords)) {
            return Lists.newArrayList();
        }
        return searchedClaimRecords.parallelStream().map(new Function<Map, GLClaimDataDto>() {
            @Override
            public GLClaimDataDto apply(Map map) {
                GLClaimDataDto claimDataDto = new GLClaimDataDto();
                String claimId = map.get("_id").toString();
                ClaimNumber claimNumber = (ClaimNumber) map.get("claimNumber");
                //ClaimType.valueOf((String)map.get("claimType"));
                ClaimStatus claimStatus = ClaimStatus.valueOf((String) map.get("claimStatus"));
                String claimNumberInString = claimNumber.getClaimNumber();
                claimDataDto.withClaimNumberAndClaimId(claimNumberInString, claimId);
                Policy policy = (Policy) map.get("policy");
                String policyNumber = policy.getPolicyNumber().getPolicyNumber();
                String policyHolderName = policy.getPolicyHolderName();
                ClaimAssuredDetail assuredDetail = (ClaimAssuredDetail) map.get("assuredDetail");
                String assuredName = assuredDetail.getFirstName();
                claimDataDto.setAssuredName(assuredName);
              /*  AssuredDetail assuredDetail = (AssuredDetail) map.get("assuredDetail");
                String assuredName=assuredDetail.getFirstName();
                claimDataDto.setAssuredName(assuredName);
                */
                claimDataDto.setClaimStatus(claimStatus.toString());
                claimDataDto.setPolicyNumber(policyNumber);

                //claimDataDto.setModifiedOn();
                return claimDataDto;
            }
        }).collect(Collectors.toList());

    }

    public List<ClaimantClaimInformationDto >isAssuredAvailClaimBefore(String firstName,String lastName,BigDecimal amount){
        List<Map> searchedClaimRecords = glClaimFinder.getEarlierAssuredClaimDetails(firstName,lastName,amount);
        if (isEmpty(searchedClaimRecords)) {
            return Lists.newArrayList();
        }

        return searchedClaimRecords.parallelStream().map(new Function<Map,ClaimantClaimInformationDto>() {
            @Override
            public ClaimantClaimInformationDto apply(Map map) {
                ClaimantClaimInformationDto claimInformation=new ClaimantClaimInformationDto();
                String claimId = map.get("_id").toString();
                ClaimNumber claimNumber = (ClaimNumber) map.get("claimNumber");
                String claimNumberStr=claimNumber.getClaimNumber();
                ClaimAssuredDetail assuredDetail=(ClaimAssuredDetail)map.get("assuredDetail");
                String firstName=assuredDetail.getFirstName();
                String lastName=assuredDetail.getSurName();
                BigDecimal amount=(BigDecimal)map.get("claimAmount");
                String claimTypeString=(String)map.get("claimType");
                ClaimType claimType=(ClaimType)ClaimType.valueOf(claimTypeString);
                claimInformation.setClaimId(claimId);
                claimInformation.setClaimNumber(claimNumberStr);
                claimInformation.setFirstName(firstName);
                claimInformation.setLastName(lastName);
                claimInformation.setClaimAmount(amount);
                claimInformation.setClaimType(claimType);
                claimInformation.setClaimId(claimId );
             return claimInformation;
            }
        }).collect(Collectors.toList());

    }

    public List<GLClaimDataDto> getClaimRecordForAmendment(SearchClaimDto searchClaimDto){
        return null;
    }


     public String getCoverageTypeFromPlan(String planId,String coverageId){
         return "";
     }
}


