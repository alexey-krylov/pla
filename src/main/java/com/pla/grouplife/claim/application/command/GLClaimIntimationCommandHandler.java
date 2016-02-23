package com.pla.grouplife.claim.application.command;

import com.google.common.collect.Sets;
import com.pla.grouplife.claim.application.service.GLClaimService;
import com.pla.grouplife.claim.domain.model.*;
import com.pla.grouplife.claim.domain.service.GLClaimFactory;
import com.pla.grouplife.claim.domain.service.GLClaimSettlementFactory;
import com.pla.grouplife.claim.domain.service.GroupLifeClaimRoleAdapter;
import com.pla.grouplife.claim.presentation.dto.*;
import com.pla.grouplife.proposal.presentation.dto.GLProposalMandatoryDocumentDto;
import com.pla.sharedkernel.domain.model.ClaimId;
import com.pla.sharedkernel.domain.model.ClaimNumber;
import com.pla.sharedkernel.domain.model.ClaimType;
import com.pla.sharedkernel.domain.model.RoutingLevel;
import com.pla.underwriter.domain.model.UnderWriterLineItem;
import com.pla.underwriter.domain.model.UnderWriterRoutingLevel;
import com.pla.underwriter.domain.model.UnderWritingRoutingLevelItem;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.repository.Repository;
import org.joda.time.DateTime;
import org.nthdimenzion.utils.UtilValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
/**
 * Created by ak
 */

@Component
public class GLClaimIntimationCommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GLClaimIntimationCommandHandler.class);

    private Repository<GroupLifeClaim> glClaimMongoRepository;

    @Autowired
   private Repository<GLClaimSettlement> glClaimSettlementMongoRepository;

    private GLClaimFactory glClaimFactory;
    @Autowired
    private GLClaimSettlementFactory glClaimSettlementFactory;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GLClaimService glClaimService;

    @Autowired
    GroupLifeClaimRoleAdapter groupLifeClaimRoleAdapter;

    @Autowired
    public GLClaimIntimationCommandHandler(Repository<GroupLifeClaim> glClaimMongoRepository, GLClaimFactory glClaimFactory) {
        this.glClaimMongoRepository = glClaimMongoRepository;
        this.glClaimFactory = glClaimFactory;
    }

    @CommandHandler
    public String createClaimIntimation(CreateGLClaimIntimationCommand createGLClaimIntimationCommand) {
          GroupLifeClaim groupLifeClaim = glClaimFactory.createClaim(createGLClaimIntimationCommand);
         GLClaimProcessor glClaimProcessor = groupLifeClaimRoleAdapter.userToGLClaimProcessor(createGLClaimIntimationCommand.getUserDetails());
         groupLifeClaim=glClaimProcessor. submitClaimIntimation(DateTime.now(), groupLifeClaim) ;
         glClaimMongoRepository.add(groupLifeClaim);
        return groupLifeClaim.getClaimId().getClaimId();
    }

    @CommandHandler
    public void uploadMandatoryDocument(GLClaimDocumentCommand glClaimDocumentCommand) throws IOException {

        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(glClaimDocumentCommand.getClaimId()));
        String fileName = glClaimDocumentCommand.getFile() != null ? glClaimDocumentCommand.getFile().getOriginalFilename() : "";
        Set<GLClaimDocument> documents = groupLifeClaim.getClaimDocuments();
        if (isEmpty(documents)) {
            documents = Sets.newHashSet();
        }
        String gridFsDocId = gridFsTemplate.store(glClaimDocumentCommand.getFile().getInputStream(), fileName, glClaimDocumentCommand.getFile().getContentType()).getId().toString();
        GLClaimDocument currentDocument = new  GLClaimDocument(glClaimDocumentCommand.getDocumentId(), fileName, gridFsDocId,glClaimDocumentCommand.getFile().getContentType(), glClaimDocumentCommand.isMandatory());
        if (!documents.add(currentDocument)) {
            GLClaimDocument existingDocument = documents.stream().filter(new Predicate<GLClaimDocument>() {
                @Override
                public boolean test(GLClaimDocument ghClaimDocument) {
                    return currentDocument.equals(ghClaimDocument);
                }
            }).findAny().get();
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(existingDocument.getGridFsDocId())));
            existingDocument = existingDocument.updateWithNameAndContent(glClaimDocumentCommand.getFilename(), gridFsDocId, glClaimDocumentCommand.getFile().getContentType());
        }
        groupLifeClaim = groupLifeClaim.withClaimDocuments(documents);

    }

    @CommandHandler
    public  String registerClaim(GLClaimRegistrationCommand glClaimRegistrationCommand) throws IOException {

        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(glClaimRegistrationCommand.getClaimId()));
        RoutingLevel definedRoutingLevel=null;
        ClaimRegistrationDto claimRegistrationDetail=glClaimRegistrationCommand.getIncidentDetails();
        ClaimRegistration claimRegistration = new ClaimRegistration(claimRegistrationDetail.getCauseOfDeath(), claimRegistrationDetail.getPlaceOfDeath()
                , claimRegistrationDetail.getDateOfDeath(), claimRegistrationDetail.getTimeOfDeath(),claimRegistrationDetail.getDurationOfIllness(),claimRegistrationDetail.getNameOfDoctorAndHospitalAddress()
                , claimRegistrationDetail.getContactNumber(), claimRegistrationDetail.getFirstConsultation(),claimRegistrationDetail.getTreatmentTaken(),
                claimRegistrationDetail.getIsCauseOfDeathAccidental(),claimRegistrationDetail.getTypeOfAccident(), claimRegistrationDetail.getPlaceOfAccident(),claimRegistrationDetail.getDateOfAccident()
                , claimRegistrationDetail.getTimeOfAccident(),claimRegistrationDetail.getIsPostMortemAutopsyDone(),claimRegistrationDetail.getIsPoliceReportRegistered(),
                claimRegistrationDetail.getRegistrationNumber(),claimRegistrationDetail.getPoliceStationName());
        Set<GLClaimDocument> uploadedDocuments = groupLifeClaim.getClaimDocuments();

        if (isEmpty(uploadedDocuments)) {
            uploadedDocuments = Sets.newHashSet();
        }
        Set<GLClaimDocumentCommand> updatedDocuments = glClaimRegistrationCommand.getUploadedDocuments();
        if(updatedDocuments!=null) {
            for (GLClaimDocumentCommand glClaimDocumentCommand : updatedDocuments) {
                uploadedDocuments = getAllUpdatedDocuments(glClaimDocumentCommand, uploadedDocuments);
            }
        }


        if (groupLifeClaim.getClaimType() == ClaimType.DEATH) {
            groupLifeClaim.withClaimRegistration(claimRegistration);
        }
        if (groupLifeClaim.getClaimType() == ClaimType.FUNERAL) {
            groupLifeClaim.withClaimRegistration(claimRegistration);
        }
 //       glClaimMongoRepository.add(groupLifeClaim);

        //check for routing configured for this plan or not
        /*
        if(glClaimService.configuredForPlan(glClaimRegistrationCommand.getClaimId())==null) {
            raiseClaimRoutingNotConfiguredForPlan();
            return;
        }
       */
        String planId=groupLifeClaim.getPlanDetail().getPlanId().getPlanId();
        UnderWriterRoutingLevel underWriterRoutingLevel=glClaimService.configuredForSelectedPlan(planId);
        BigDecimal sumAssured=groupLifeClaim.getReserveAmount();

        if(underWriterRoutingLevel!=null){
            Set<UnderWritingRoutingLevelItem> underWritingRoutingLevelItems=underWriterRoutingLevel.getUnderWritingRoutingLevelItems();
            for(UnderWritingRoutingLevelItem underWritingRoutingLevelItem :underWritingRoutingLevelItems){
                Set<UnderWriterLineItem> underWriterLineItems=underWritingRoutingLevelItem.getUnderWriterLineItems();
                for(UnderWriterLineItem underWriterLineItem:underWriterLineItems){
                    BigDecimal desValue=new BigDecimal(underWriterLineItem.getInfluencingItemTo());
                    if ( desValue.compareTo(sumAssured)<0){
                        definedRoutingLevel=underWritingRoutingLevelItem.getRoutingLevel();
                    }
                }

            }

        }



        groupLifeClaim.taggedWithRoutingLevel(definedRoutingLevel);

        GLClaimRegistrationProcessor glClaimRegistrationProcessor = groupLifeClaimRoleAdapter.userToGLClaimRegistrationProcessor(glClaimRegistrationCommand.getUserDetails());
         groupLifeClaim=glClaimRegistrationProcessor.submitClaimRegistration(DateTime.now(), groupLifeClaim, glClaimRegistrationCommand.getComments()) ;
         glClaimMongoRepository.add(groupLifeClaim);
        return groupLifeClaim.getClaimId().getClaimId();


    }


    @CommandHandler
    public String registerDisabilityClaim(GLDisabilityClaimRegistrationCommand glClaimCommand) throws IOException {
        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(glClaimCommand.getClaimId()));
        RoutingLevel definedRoutingLevel=null;
        ClaimDisabilityRegistrationDto claimDisabilityDetails=glClaimCommand.getDisabilityIncidentDetails();
        DisabilityClaimRegistration claimRegistration = new DisabilityClaimRegistration(claimDisabilityDetails.getDateOfDisability(), claimDisabilityDetails.getNatureOfDisability(), claimDisabilityDetails.getExtendOfDisability(),
                claimDisabilityDetails.getDateOfDiagnosis(), claimDisabilityDetails.getExactDiagnosis(), claimDisabilityDetails.getNameOfDoctorAndHospitalAddress(), claimDisabilityDetails.getContactNumberOfHospital(),
                claimDisabilityDetails.getDateOfFirstConsultation(),claimDisabilityDetails.getTreatmentTaken(),claimDisabilityDetails.getCapabilityOfAssuredDailyLiving(),claimDisabilityDetails.getAssuredGainfulActivities(),claimDisabilityDetails.getDetailsOfWorkActivities(),
                claimDisabilityDetails.getFromActivitiesDate(),claimDisabilityDetails.getAssuredConfinedToIndoor(),claimDisabilityDetails.getFromIndoorDate(), claimDisabilityDetails.getAssuredIndoorDetails(),claimDisabilityDetails.getAssuredAbleToGetOutdoor(),
                claimDisabilityDetails.getFromOutdoorDate(),claimDisabilityDetails.getAssuredOutdoorDetails(),claimDisabilityDetails.getVisitingMedicalOfficerDetails());

        Set<GLClaimDocument> uploadedDocuments = groupLifeClaim.getClaimDocuments();

        if (isEmpty(uploadedDocuments)) {
            uploadedDocuments = Sets.newHashSet();
        }
        Set<GLClaimDocumentCommand> updatedDocuments = glClaimCommand.getUploadedDocuments();
        if(updatedDocuments!=null) {
            for (GLClaimDocumentCommand glClaimDocumentCommand : updatedDocuments) {
                uploadedDocuments = getAllUpdatedDocuments(glClaimDocumentCommand, uploadedDocuments);
            }
        }
        groupLifeClaim.withDisabilityClaimRegistration(claimRegistration);
        //glClaimMongoRepository.add(groupLifeClaim);
        /*
        if(glClaimService.configuredForPlan(glClaimRegistrationCommand.getClaimId())==null) {
            raiseClaimRoutingNotConfiguredForPlan();
            return;
        }
       */
        String planId=groupLifeClaim.getPlanDetail().getPlanId().getPlanId();
        UnderWriterRoutingLevel underWriterRoutingLevel=glClaimService.configuredForSelectedPlan(planId);
        BigDecimal sumAssured=groupLifeClaim.getReserveAmount();

        if(underWriterRoutingLevel!=null){
            Set<UnderWritingRoutingLevelItem> underWritingRoutingLevelItems=underWriterRoutingLevel.getUnderWritingRoutingLevelItems();
            for(UnderWritingRoutingLevelItem underWritingRoutingLevelItem :underWritingRoutingLevelItems){
                Set<UnderWriterLineItem> underWriterLineItems=underWritingRoutingLevelItem.getUnderWriterLineItems();
                for(UnderWriterLineItem underWriterLineItem:underWriterLineItems){
                    BigDecimal desValue=new BigDecimal(underWriterLineItem.getInfluencingItemTo());
                    if ( desValue.compareTo(sumAssured)<0){
                        definedRoutingLevel=underWritingRoutingLevelItem.getRoutingLevel();
                    }
                }

            }

        }



        groupLifeClaim.taggedWithRoutingLevel(definedRoutingLevel);

        GLClaimRegistrationProcessor glClaimRegistrationProcessor = groupLifeClaimRoleAdapter.userToGLClaimRegistrationProcessor(glClaimCommand.getUserDetails());
        groupLifeClaim=glClaimRegistrationProcessor.submitClaimRegistration(DateTime.now(), groupLifeClaim,glClaimCommand.getComments()) ;
        glClaimMongoRepository.add(groupLifeClaim);
        return groupLifeClaim.getClaimId().getClaimId();
    }


    @CommandHandler
    public void updateClaimDetail(GLClaimUpdateCommand glClaimUpdateCommand) throws IOException {
        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(glClaimUpdateCommand.getClaimId()));
        groupLifeClaim=glClaimFactory.updateClaimDetails(groupLifeClaim, glClaimUpdateCommand);

        Set<GLClaimDocument> documents = groupLifeClaim.getClaimDocuments();
        if (isEmpty(documents)) {
            documents = Sets.newHashSet();
        }
        Set<GLClaimDocumentCommand> updatedDocuments = glClaimUpdateCommand.getUploadedDocuments();
        if( updatedDocuments!=null) {

            for (GLClaimDocumentCommand glClaimDocumentCommand : updatedDocuments) {
                documents = getAllUpdatedDocuments(glClaimDocumentCommand, documents);
            }
        }
        groupLifeClaim = groupLifeClaim.withClaimDocuments(documents);

        glClaimMongoRepository.add(groupLifeClaim);

    }

    public Set<GLClaimDocument> getAllUpdatedDocuments(GLClaimDocumentCommand glClaimDocumentCommand, Set<GLClaimDocument> documents) throws IOException {
        String fileName = glClaimDocumentCommand.getFile() != null ? glClaimDocumentCommand.getFile().getName() : "";
        //String gridFsDocId = gridFsTemplate.store(FileUtils.openInputStream(glClaimDocumentCommand.getFile()), fileName, "").getId().toString();
        String gridFsDocId = gridFsTemplate.store(glClaimDocumentCommand.getFile().getInputStream(), fileName, glClaimDocumentCommand.getFile().getContentType()).getId().toString();
        //GLClaimDocument currentDocument = new GLClaimDocument(glClaimDocumentCommand.getDocumentId(), fileName, gridFsDocId, "", glClaimDocumentCommand.isMandatory());
        GLClaimDocument currentDocument = new  GLClaimDocument(glClaimDocumentCommand.getDocumentId(), fileName, gridFsDocId,glClaimDocumentCommand.getFile().getContentType(), glClaimDocumentCommand.isMandatory());
        if (!documents.add(currentDocument)) {
            GLClaimDocument existingDocument = documents.stream().filter(new Predicate<GLClaimDocument>() {
                @Override
                public boolean test(GLClaimDocument glClaimDocument) {
                    return currentDocument.equals(glClaimDocument);
                }
            }).findAny().get();
            gridFsTemplate.delete(new Query(Criteria.where("_id").is(existingDocument.getGridFsDocId())));
            existingDocument = existingDocument.updateWithNameAndContent(glClaimDocumentCommand.getFilename(), gridFsDocId, "");
        }
        return documents;
    }


    @CommandHandler
    public String submitClaim(SubmitGLClaimCommand submitGLClaimCommand) {
        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(submitGLClaimCommand.getClaimId()));
        GLClaimRegistrationProcessor glClaimRegistrationProcessor = groupLifeClaimRoleAdapter.userToGLClaimRegistrationProcessor(submitGLClaimCommand.getUserDetails());
        groupLifeClaim=glClaimRegistrationProcessor.submitClaimRegistrationToUnderWriter(DateTime.now(), groupLifeClaim,submitGLClaimCommand.getComment()) ;
        glClaimMongoRepository.add(groupLifeClaim);
        return groupLifeClaim.getIdentifier().getClaimId();
    }

    @CommandHandler
    public String reopenClaim(GLClaimReopenCommand glClaimReopenCommand) {
        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(glClaimReopenCommand.getClaimId()));
        ClaimReopenProcessor reopenProcessor = groupLifeClaimRoleAdapter.userToClaimReopenProcessor(glClaimReopenCommand.getUserDetails());
        groupLifeClaim=reopenProcessor .submitForReopen(DateTime.now(), glClaimReopenCommand.getComment(), groupLifeClaim) ;
        glClaimMongoRepository.add(groupLifeClaim);
        return groupLifeClaim.getClaimNumber().getClaimNumber();
    }
    @CommandHandler
    public String returnClaim(ReturnGLClaimCommand returnGLClaimCommand) {
        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(returnGLClaimCommand.getClaimId()));
        ClaimApproverPlanDto planDetail=returnGLClaimCommand.getClaimApprovalPlanDetail();
        GLClaimApproverPlanDetail glClaimApproverPlanDetail=null;
        if(planDetail!=null){
            glClaimApproverPlanDetail=new GLClaimApproverPlanDetail(planDetail.getPlanName(),planDetail.getPlanSumAssured(),
                    planDetail.getAdditionalAmount(),planDetail.getApprovedAmount(),planDetail.getAmendedAmount(),planDetail.getRecoveryOrAdditional(),planDetail.getRemarks());
        }

        List<ClaimApproverCoverageDetailDto> coverageDetails=returnGLClaimCommand.getClaimApprovalCoverageDetails();
        List<ApproverCoverageDetail> coverageDetailList= new ArrayList<ApproverCoverageDetail>();
        if(coverageDetails!=null){
            for(ClaimApproverCoverageDetailDto claimApproverCoverageDetailDto:coverageDetails){
                ApproverCoverageDetail approverCoverageDetail=new ApproverCoverageDetail(claimApproverCoverageDetailDto.getCoverageName(),
                        claimApproverCoverageDetailDto.getSumAssured(),claimApproverCoverageDetailDto.getApprovedAmount(),claimApproverCoverageDetailDto.getAmendedAmount(),claimApproverCoverageDetailDto.getAdditionalAmount(),
                        claimApproverCoverageDetailDto.getRecoveryOrAdditional(),claimApproverCoverageDetailDto.getRemarks() ) ;
                coverageDetailList.add(approverCoverageDetail);
            }
        }

        BigDecimal totalApprovedAmount=returnGLClaimCommand.getTotalApprovedAmount();

        BigDecimal totalRecoveredAmount=returnGLClaimCommand.getTotalRecoveredAmount()!= null ? returnGLClaimCommand.getTotalRecoveredAmount() : null;
        String comments=returnGLClaimCommand.getComments()!= null ? returnGLClaimCommand.getComments() : null;
        DateTime response=returnGLClaimCommand.getResponseReceivedOn() != null ? new DateTime(returnGLClaimCommand.getResponseReceivedOn()) : null;
        DateTime refer=returnGLClaimCommand.getReferredToReassureOn() != null ? new DateTime(returnGLClaimCommand.getReferredToReassureOn()) : null;
        DateTime claimApprovalDate=new DateTime();
        GlClaimUnderWriterApprovalDetail approvalDetail=new GlClaimUnderWriterApprovalDetail(glClaimApproverPlanDetail, coverageDetailList, totalApprovedAmount,  comments, refer, response);

        approvalDetail.withClaimApprovedOn(claimApprovalDate);

        //adding review table
        List<ClaimReviewDetail> claimReviewDetails=new ArrayList<ClaimReviewDetail>();
        List<ClaimReviewDto> reviewDetails=returnGLClaimCommand.getReviewDetails();
        String userName=returnGLClaimCommand.getUserDetails().getUsername();

        ClaimReviewDetail claimReviewDetail=new ClaimReviewDetail(returnGLClaimCommand.getComments(),claimApprovalDate,userName);
        claimReviewDetails.add(claimReviewDetail) ;

        approvalDetail.withClaimReviewDetails(claimReviewDetails);


        GLClaimApprover glClaimApprover = groupLifeClaimRoleAdapter.userToClaimApprover(returnGLClaimCommand.getUserDetails());
        groupLifeClaim = glClaimApprover.returnClaimRecord(DateTime.now(), returnGLClaimCommand.getComments(), groupLifeClaim);
        //groupLifeClaim = groupLifeClaim.markApproverApproval(glClaimApprovalCommand.getUserDetails().getUsername(), DateTime.now(), glClaimApprovalCommand.getComments(), ClaimStatus.APPROVED);
        return groupLifeClaim.getIdentifier().getClaimId();
    }

    @CommandHandler
    public String claimApproval(GLClaimApprovalCommand glClaimApprovalCommand) {
        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(glClaimApprovalCommand.getClaimId()));
        GroupLifeClaim newGroupLifeClaim=null;
        if(glClaimApprovalCommand.getCriteria()!=null && glClaimApprovalCommand.getCriteria().equals("amendment")){
             newGroupLifeClaim=glClaimFactory.makeAmendment(glClaimApprovalCommand,groupLifeClaim);
            glClaimMongoRepository.add(newGroupLifeClaim);
        }
        /*else if(glClaimApprovalCommand.getCriteria()!=null && glClaimApprovalCommand.getCriteria().equals("reopen")){
            // Logic for ReOpen
        }
         */
        ClaimApproverPlanDto planDetail=glClaimApprovalCommand.getClaimApprovalPlanDetail();
        GLClaimApproverPlanDetail glClaimApproverPlanDetail=null;
        if(planDetail!=null){
             glClaimApproverPlanDetail=new GLClaimApproverPlanDetail(planDetail.getPlanName(),planDetail.getPlanSumAssured(),
                    planDetail.getAdditionalAmount(),planDetail.getApprovedAmount(),planDetail.getAmendedAmount(),planDetail.getRecoveryOrAdditional(),planDetail.getRemarks());
        }

        List<ClaimApproverCoverageDetailDto> coverageDetails=glClaimApprovalCommand.getClaimApprovalCoverageDetails();
        List<ApproverCoverageDetail> coverageDetailList= new ArrayList<ApproverCoverageDetail>();
        if(coverageDetails!=null){
            for(ClaimApproverCoverageDetailDto claimApproverCoverageDetailDto:coverageDetails){
                ApproverCoverageDetail approverCoverageDetail=new ApproverCoverageDetail(claimApproverCoverageDetailDto.getCoverageName(),
                        claimApproverCoverageDetailDto.getSumAssured(),claimApproverCoverageDetailDto.getApprovedAmount(),claimApproverCoverageDetailDto.getAmendedAmount(),claimApproverCoverageDetailDto.getAdditionalAmount(),
                        claimApproverCoverageDetailDto.getRecoveryOrAdditional(),claimApproverCoverageDetailDto.getRemarks() ) ;
                coverageDetailList.add(approverCoverageDetail);
            }
        }

        BigDecimal totalApprovedAmount=glClaimApprovalCommand.getTotalApprovedAmount();

        BigDecimal totalRecoveredAmount=glClaimApprovalCommand.getTotalRecoveredAmount()!= null ? glClaimApprovalCommand.getTotalRecoveredAmount() : null;
        String comments=glClaimApprovalCommand.getComments()!= null ? glClaimApprovalCommand.getComments() : null;
        DateTime response=glClaimApprovalCommand.getResponseReceivedOn() != null ? new DateTime(glClaimApprovalCommand.getResponseReceivedOn()) : null;
        DateTime refer=glClaimApprovalCommand.getReferredToReassureOn() != null ? new DateTime(glClaimApprovalCommand.getReferredToReassureOn()) : null;
        DateTime claimApprovalDate=new DateTime();
        GlClaimUnderWriterApprovalDetail approvalDetail=new GlClaimUnderWriterApprovalDetail(glClaimApproverPlanDetail, coverageDetailList, totalApprovedAmount,  comments, refer, response);

        approvalDetail.withClaimApprovedOn(claimApprovalDate);

        //adding review table
        List<ClaimReviewDetail> claimReviewDetails=new ArrayList<ClaimReviewDetail>();
        List<ClaimReviewDto> reviewDetails=glClaimApprovalCommand.getReviewDetails();
        String userName=glClaimApprovalCommand.getUserDetails().getUsername();

                ClaimReviewDetail claimReviewDetail=new ClaimReviewDetail(glClaimApprovalCommand.getComments(),claimApprovalDate,userName);
        claimReviewDetails.add(claimReviewDetail) ;

        approvalDetail.withClaimReviewDetails(claimReviewDetails);


        if(glClaimApprovalCommand.getCriteria()!=null && glClaimApprovalCommand.getCriteria().equals("amendment")){

            newGroupLifeClaim.withUnderWriterData(approvalDetail);
        }
        else{
            groupLifeClaim.withUnderWriterData(approvalDetail);
        }


        if(glClaimApprovalCommand.getCriteria()!=null && glClaimApprovalCommand.getCriteria().equals("amendment")){

            GLClaimApprover glClaimApprover = groupLifeClaimRoleAdapter.userToClaimApprover(glClaimApprovalCommand.getUserDetails());
            newGroupLifeClaim = glClaimApprover.submitApproval(DateTime.now(), glClaimApprovalCommand.getComments(), newGroupLifeClaim);
            //groupLifeClaim = groupLifeClaim.markApproverApproval(glClaimApprovalCommand.getUserDetails().getUsername(), DateTime.now(), glClaimApprovalCommand.getComments(), ClaimStatus.APPROVED);
            return groupLifeClaim.getIdentifier().getClaimId();
        }
        else{
            GLClaimApprover glClaimApprover = groupLifeClaimRoleAdapter.userToClaimApprover(glClaimApprovalCommand.getUserDetails());
            groupLifeClaim = glClaimApprover.submitApproval(DateTime.now(), glClaimApprovalCommand.getComments(), groupLifeClaim);
            //groupLifeClaim = groupLifeClaim.markApproverApproval(glClaimApprovalCommand.getUserDetails().getUsername(), DateTime.now(), glClaimApprovalCommand.getComments(), ClaimStatus.APPROVED);
            return groupLifeClaim.getIdentifier().getClaimId();
        }

    }

   @CommandHandler
    public String settleClaim(GLClaimSettlementCommand glClaimSettlementCommand) {
        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(glClaimSettlementCommand.getClaimId()));

        //claim settlement to be created
      //
        ClaimNumber claimNumber=groupLifeClaim.getClaimNumber();
         GLClaimSettlement glClaimSettlement=glClaimSettlementFactory.createSettlement(glClaimSettlementCommand,claimNumber);
        glClaimSettlementMongoRepository.add(glClaimSettlement);
         GLClaimSettlementData glClaimSettlementData=new GLClaimSettlementData();
         glClaimSettlementData.setClaimSettlementId(glClaimSettlement.getClaimSettlementId());
         glClaimSettlementData.setClaimStatus(glClaimSettlement.getClaimStatus());
        glClaimSettlementData.setClaimApprovedOn(glClaimSettlement.getClaimApprovedOn());
        glClaimSettlementData.setApprovedAmount(glClaimSettlement.getApprovedAmount());
        glClaimSettlementData.setPaymentMode(glClaimSettlement.getPaymentMode());
        glClaimSettlementData.setPaymentDate(glClaimSettlement.getPaymentDate());
        glClaimSettlementData.setBankName(glClaimSettlement.getBankName());
        glClaimSettlementData.setBankBranchName(glClaimSettlement.getBankBranchName());
        glClaimSettlementData.setAccountType(glClaimSettlement.getAccountType());
        glClaimSettlementData.setAccountNumber(glClaimSettlement.getAccountNumber());
        glClaimSettlementData.setInstrumentDate(glClaimSettlement.getInstrumentDate());
        glClaimSettlementData.setInstrumentNumber(glClaimSettlement.getInstrumentNumber());
        glClaimSettlementData.setDebitAmount(glClaimSettlement.getDebitAmount());
        groupLifeClaim.withClaimSettlementData(glClaimSettlementData);
       GLClaimSettlementProcessor glClaimProcessor=groupLifeClaimRoleAdapter.userToGLClaimSettlementProcessor(glClaimSettlementCommand.getUserDetails());
       groupLifeClaim = glClaimProcessor.submitForClaimSettlement(DateTime.now(), glClaimSettlementCommand.getComment(), groupLifeClaim);
        return groupLifeClaim.getIdentifier().getClaimId();
    }

    @CommandHandler
    public String amendClaim(GLClaimAmendmentCommand glClaimAmendmentCommand){
        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(glClaimAmendmentCommand.getClaimId()));
        groupLifeClaim =glClaimFactory.createAmendment(glClaimAmendmentCommand, groupLifeClaim);
        ClaimAmendmentProcessor glClaimAmendmentProcessor = groupLifeClaimRoleAdapter.userToClaimAmendmentProcessor(glClaimAmendmentCommand.getUserDetails());
         groupLifeClaim=glClaimAmendmentProcessor.submitForClaimAmendment(DateTime.now(),  glClaimAmendmentCommand.getComments(),groupLifeClaim) ;
         glClaimMongoRepository.add(groupLifeClaim);
        return groupLifeClaim.getIdentifier().getClaimId();
    }
    @CommandHandler
    public String waiveDocumentCommandHandler(GLClaimWaiveMandatoryDocumentCommand cmd) {
        GLClaimApprover glClaimApprover = groupLifeClaimRoleAdapter.userToClaimApprover(cmd.getUserDetails());
        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(cmd.getClaimId()));
        Set<GLClaimDocument> documents = groupLifeClaim.getClaimDocuments();
        if (isEmpty(documents)) {
            documents = Sets.newHashSet();
        }
        for (GLProposalMandatoryDocumentDto claimDocument : cmd.getWaivedDocuments()) {
            GLClaimDocument glClaimDocument = new GLClaimDocument (claimDocument.getDocumentId(), claimDocument.getMandatory(), claimDocument.getIsApproved());
            documents.add(glClaimDocument);
        }
        groupLifeClaim = glClaimApprover.updateWithDocuments(groupLifeClaim, documents);
        return groupLifeClaim.getIdentifier().getClaimId();
    }

    @CommandHandler
    public boolean removeGLProposalClaimDocument(GLClaimDocumentRemoveCommand glClaimDocumentRemoveCommand) {

        boolean result = Boolean.FALSE;
        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(glClaimDocumentRemoveCommand.getClaimId()));
        if(groupLifeClaim != null){
            Set<GLClaimDocument> glClaimDocuments = groupLifeClaim.getClaimDocuments();
            result =  removeDocumentByGridFsDocId(glClaimDocuments, glClaimDocumentRemoveCommand.getGridFsDocId());
        }
        return result;
    }

    private boolean removeDocumentByGridFsDocId(Set<GLClaimDocument> glClaimDocuments, String gridFsDocId) {
        if(UtilValidator.isNotEmpty(glClaimDocuments)) {
            for (Iterator iterator = glClaimDocuments.iterator(); iterator.hasNext(); ) {
                GLClaimDocument glProposerDocument = (GLClaimDocument) iterator.next();
                if (glProposerDocument.getGridFsDocId().equals(gridFsDocId)) {
                    iterator.remove();
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    @CommandHandler
    public String rejectClaim(GLClaimRejectCommand rejectGLClaimCommand) {

        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(rejectGLClaimCommand.getClaimId()));
        ClaimApproverPlanDto planDetail=rejectGLClaimCommand.getClaimApprovalPlanDetail();
        GLClaimApproverPlanDetail glClaimApproverPlanDetail=null;
        if(planDetail!=null){
            glClaimApproverPlanDetail=new GLClaimApproverPlanDetail(planDetail.getPlanName(),planDetail.getPlanSumAssured(),
                    planDetail.getAdditionalAmount(),planDetail.getApprovedAmount(),planDetail.getAmendedAmount(),planDetail.getRecoveryOrAdditional(),planDetail.getRemarks());
        }

        List<ClaimApproverCoverageDetailDto> coverageDetails=rejectGLClaimCommand.getClaimApprovalCoverageDetails();
        List<ApproverCoverageDetail> coverageDetailList= new ArrayList<ApproverCoverageDetail>();
        if(coverageDetails!=null){
            for(ClaimApproverCoverageDetailDto claimApproverCoverageDetailDto:coverageDetails){
                ApproverCoverageDetail approverCoverageDetail=new ApproverCoverageDetail(claimApproverCoverageDetailDto.getCoverageName(),
                        claimApproverCoverageDetailDto.getSumAssured(),claimApproverCoverageDetailDto.getApprovedAmount(),claimApproverCoverageDetailDto.getAmendedAmount(),claimApproverCoverageDetailDto.getAdditionalAmount(),
                        claimApproverCoverageDetailDto.getRecoveryOrAdditional(),claimApproverCoverageDetailDto.getRemarks() ) ;
                coverageDetailList.add(approverCoverageDetail);
            }
        }

        BigDecimal totalApprovedAmount=rejectGLClaimCommand.getTotalApprovedAmount();

        BigDecimal totalRecoveredAmount=rejectGLClaimCommand.getTotalRecoveredAmount()!= null ? rejectGLClaimCommand.getTotalRecoveredAmount() : null;
        String comments=rejectGLClaimCommand.getComments()!= null ? rejectGLClaimCommand.getComments() : null;
        DateTime response=rejectGLClaimCommand.getResponseReceivedOn() != null ? new DateTime(rejectGLClaimCommand.getResponseReceivedOn()) : null;
        DateTime refer=rejectGLClaimCommand.getReferredToReassureOn() != null ? new DateTime(rejectGLClaimCommand.getReferredToReassureOn()) : null;
        DateTime claimApprovalDate=new DateTime();
        GlClaimUnderWriterApprovalDetail approvalDetail=new GlClaimUnderWriterApprovalDetail(glClaimApproverPlanDetail, coverageDetailList, totalApprovedAmount,  comments, refer, response);

        approvalDetail.withClaimApprovedOn(claimApprovalDate);

        //adding review table
        List<ClaimReviewDetail> claimReviewDetails=new ArrayList<ClaimReviewDetail>();
        List<ClaimReviewDto> reviewDetails=rejectGLClaimCommand.getReviewDetails();
        String userName=rejectGLClaimCommand.getUserDetails().getUsername();

        ClaimReviewDetail claimReviewDetail=new ClaimReviewDetail(rejectGLClaimCommand.getComments(),claimApprovalDate,userName);
        claimReviewDetails.add(claimReviewDetail) ;

        approvalDetail.withClaimReviewDetails(claimReviewDetails);


        GLClaimApprover glClaimApprover = groupLifeClaimRoleAdapter.userToClaimApprover(rejectGLClaimCommand.getUserDetails());
        groupLifeClaim = glClaimApprover.rejectClaimRecord(DateTime.now(),rejectGLClaimCommand.getComments(), groupLifeClaim);
        return groupLifeClaim.getIdentifier().getClaimId();
    }


    @CommandHandler
    public String routeClaimToSeniorApprover(GLClaimSeniorApproverCommand command) {

        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(command.getClaimId()));
        ClaimApproverPlanDto planDetail=command.getClaimApprovalPlanDetail();
        GLClaimApproverPlanDetail glClaimApproverPlanDetail=null;
        if(planDetail!=null){
            glClaimApproverPlanDetail=new GLClaimApproverPlanDetail(planDetail.getPlanName(),planDetail.getPlanSumAssured(),
                    planDetail.getAdditionalAmount(),planDetail.getApprovedAmount(),planDetail.getAmendedAmount(),planDetail.getRecoveryOrAdditional(),planDetail.getRemarks());
        }

        List<ClaimApproverCoverageDetailDto> coverageDetails=command.getClaimApprovalCoverageDetails();
        List<ApproverCoverageDetail> coverageDetailList= new ArrayList<ApproverCoverageDetail>();
        if(coverageDetails!=null){
            for(ClaimApproverCoverageDetailDto claimApproverCoverageDetailDto:coverageDetails){
                ApproverCoverageDetail approverCoverageDetail=new ApproverCoverageDetail(claimApproverCoverageDetailDto.getCoverageName(),
                        claimApproverCoverageDetailDto.getSumAssured(),claimApproverCoverageDetailDto.getApprovedAmount(),claimApproverCoverageDetailDto.getAmendedAmount(),claimApproverCoverageDetailDto.getAdditionalAmount(),
                        claimApproverCoverageDetailDto.getRecoveryOrAdditional(),claimApproverCoverageDetailDto.getRemarks() ) ;
                coverageDetailList.add(approverCoverageDetail);
            }
        }

        BigDecimal totalApprovedAmount=command.getTotalApprovedAmount();

        BigDecimal totalRecoveredAmount=command.getTotalRecoveredAmount()!= null ? command.getTotalRecoveredAmount() : null;
        String comments=command.getComments()!= null ? command.getComments() : null;
        DateTime response=command.getResponseReceivedOn() != null ? new DateTime(command.getResponseReceivedOn()) : null;
        DateTime refer=command.getReferredToReassureOn() != null ? new DateTime(command.getReferredToReassureOn()) : null;
        DateTime claimApprovalDate=new DateTime();
        GlClaimUnderWriterApprovalDetail approvalDetail=new GlClaimUnderWriterApprovalDetail(glClaimApproverPlanDetail, coverageDetailList, totalApprovedAmount,  comments, refer, response);

        approvalDetail.withClaimApprovedOn(claimApprovalDate);

        //adding review table
        List<ClaimReviewDetail> claimReviewDetails=new ArrayList<ClaimReviewDetail>();
        List<ClaimReviewDto> reviewDetails=command.getReviewDetails();
        String userName=command.getUserDetails().getUsername();

        ClaimReviewDetail claimReviewDetail=new ClaimReviewDetail(command.getComments(),claimApprovalDate,userName);
        claimReviewDetails.add(claimReviewDetail) ;

        approvalDetail.withClaimReviewDetails(claimReviewDetails);


        GLClaimApprover glClaimApprover = groupLifeClaimRoleAdapter.userToClaimApprover(command.getUserDetails());
        groupLifeClaim = glClaimApprover.routeClaimRecordToSeniorApprover(DateTime.now(),command.getComments(), groupLifeClaim);
        return groupLifeClaim.getIdentifier().getClaimId();
    }

}




