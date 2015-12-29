package com.pla.grouplife.claim.application.command;

import com.google.common.collect.Sets;
import com.pla.grouplife.claim.application.service.GLClaimService;
import com.pla.grouplife.claim.domain.model.*;
import com.pla.grouplife.claim.domain.service.GLClaimFactory;
import com.pla.grouplife.claim.domain.service.GLClaimSettlementFactory;
import com.pla.grouplife.claim.domain.service.GroupLifeClaimRoleAdapter;
import com.pla.grouplife.proposal.presentation.dto.GLProposalMandatoryDocumentDto;
import com.pla.sharedkernel.domain.model.ClaimId;
import com.pla.sharedkernel.domain.model.ClaimType;
import org.apache.commons.io.FileUtils;
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
import java.util.Iterator;
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
       // this.glClaimSettlementMongoRepository=glClaimSettlementMongoRepository;
        this.glClaimFactory = glClaimFactory;
    }

    @CommandHandler
    public void createClaimIntimation(CreateGLClaimIntimationCommand createGLClaimIntimationCommand) {
        // GLClaimProcessor glClaimProcessor = groupLifeClaimRoleAdapter.userToClaimProcessor(createGLClaimIntimationCommand.getUserDetails());

        GroupLifeClaim groupLifeClaim = glClaimFactory.createClaim(createGLClaimIntimationCommand);
        glClaimMongoRepository.add(groupLifeClaim);
    }

    @CommandHandler
    public void uploadMandatoryDocument(GLClaimDocumentCommand glClaimDocumentCommand) throws IOException {

        /*
        * multipart file
        * file
        * content type
        * */

        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(glClaimDocumentCommand.getClaimId()));
        //GroupLifeClaim groupLifeClaim=glClaimMongoRepository.load(glClaimDocumentCommand.getClaimId());
        String fileName = glClaimDocumentCommand.getFile() != null ? glClaimDocumentCommand.getFile().getName() : "";
        Set<GLClaimDocument> documents = groupLifeClaim.getClaimDocuments();
        if (isEmpty(documents)) {
            documents = Sets.newHashSet();
        }
        String gridFsDocId = gridFsTemplate.store(FileUtils.openInputStream(glClaimDocumentCommand.getFile()), fileName, "").getId().toString();
        GLClaimDocument currentDocument = new GLClaimDocument(glClaimDocumentCommand.getDocumentId(), fileName, gridFsDocId, "", glClaimDocumentCommand.isMandatory());
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
        groupLifeClaim = groupLifeClaim.withClaimDocuments(documents);
    }

    @CommandHandler
    public void registerClaim(GLClaimRegistrationCommand glClaimRegistrationCommand) throws IOException {

        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(glClaimRegistrationCommand.getClaimId()));
        ClaimRegistration claimRegistration = new ClaimRegistration(glClaimRegistrationCommand.getCauseOfDeath(), glClaimRegistrationCommand.getPlaceOfDeath()
                , glClaimRegistrationCommand.getDateOfDeath(), glClaimRegistrationCommand.getTimeOfDeath(), glClaimRegistrationCommand.getDurationOfIllness(), glClaimRegistrationCommand.getNameOfDocterAndHospialAddress()
                , glClaimRegistrationCommand.getContactNumber(), glClaimRegistrationCommand.getFirstConsultation(), glClaimRegistrationCommand.getTreatementTaken(),
                glClaimRegistrationCommand.getCauseOfDeathAccidental(), glClaimRegistrationCommand.getTypeOfAccident(), glClaimRegistrationCommand.getPlaceOfAccident(), glClaimRegistrationCommand.getDateOfAccident()
                , glClaimRegistrationCommand.getTimeOfAccident(), glClaimRegistrationCommand.getPostMortemAutopsyDone(), glClaimRegistrationCommand.getPoliceReportRegistered(),
                glClaimRegistrationCommand.getRegistrationNumber(), glClaimRegistrationCommand.getPoliceStationName());
        Set<GLClaimDocument> uploadedDocuments = groupLifeClaim.getClaimDocuments();

        if (isEmpty(uploadedDocuments)) {
            uploadedDocuments = Sets.newHashSet();
        }
        Set<GLClaimDocumentCommand> updatedDocuments = glClaimRegistrationCommand.getUploadedDocuments();
        for (GLClaimDocumentCommand glClaimDocumentCommand : updatedDocuments) {
            uploadedDocuments = getAllUpdatedDocuments(glClaimDocumentCommand, uploadedDocuments);
        }

        //groupLifeClaim.withclaimRegistration(claimRegistration);
        if (groupLifeClaim.getClaimType() == ClaimType.DEATH) {
            groupLifeClaim.withDeathClaimRegistration(claimRegistration);
        }
        if (groupLifeClaim.getClaimType() == ClaimType.FUNERAL) {
            groupLifeClaim.withFuneralClaimRegistration(claimRegistration);
        }
        glClaimMongoRepository.add(groupLifeClaim);

        //check for routing configured for this plan or not
        /*
        if(glClaimService.configuredForPlan(glClaimRegistrationCommand.getClaimId())==null) {
            raiseClaimRoutingNotConfiguredForPlan();
            return;
        }
       */

    }

    @CommandHandler
    public void registerDisabilityClaim(GLDisabilityClaimRegistrationCommand glClaimCommand) throws IOException {
        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(glClaimCommand.getClaimId()));
        DisabilityClaimRegistration claimRegistration = new DisabilityClaimRegistration(glClaimCommand.getDateOfDisability(), glClaimCommand.getNatureOfDisability(), glClaimCommand.getExtendOfDisability(),
                glClaimCommand.getDateOfDiagnosis(), glClaimCommand.getExactDiagnosis(), glClaimCommand.getNameOfDoctorAndHospitalAddress(), glClaimCommand.getContactNumberOfHospital(),
                glClaimCommand.getDateOfFirstConsultation(), glClaimCommand.getTreatmentTaken(), glClaimCommand.getCapabilityOfAssuredDailyLiving(), glClaimCommand.getAssuredGainfulActivities(), glClaimCommand.getDetailsOfWorkActivities(),
                glClaimCommand.getFromActivitiesDate(), glClaimCommand.getAssuredConfinedToIndoor(), glClaimCommand.getFromIndoorDate(), glClaimCommand.getAssuredIndoorDetails(), glClaimCommand.getAssuredAbleToGetOutdoor(),
                glClaimCommand.getFromOutdoorDate(), glClaimCommand.getAssuredOutdoorDetails(), glClaimCommand.getVisitingMedicalOfficerDetails());

        Set<GLClaimDocument> uploadedDocuments = groupLifeClaim.getClaimDocuments();

        if (isEmpty(uploadedDocuments)) {
            uploadedDocuments = Sets.newHashSet();
        }
        Set<GLClaimDocumentCommand> updatedDocuments = glClaimCommand.getUploadedDocuments();
        for (GLClaimDocumentCommand glClaimDocumentCommand : updatedDocuments) {
            uploadedDocuments = getAllUpdatedDocuments(glClaimDocumentCommand, uploadedDocuments);
        }

        groupLifeClaim.withDisabilityClaimRegistration(claimRegistration);
        glClaimMongoRepository.add(groupLifeClaim);
        /*
        if(glClaimService.configuredForPlan(glClaimRegistrationCommand.getClaimId())==null) {
            raiseClaimRoutingNotConfiguredForPlan();
            return;
        }
       */
    }

    @CommandHandler
    public void updateClaimDetail(GLClaimUpdateCommand glClaimUpdateCommand) throws IOException {
        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(glClaimUpdateCommand.getClaimId()));

        Set<GLClaimDocument> documents = groupLifeClaim.getClaimDocuments();
        if (isEmpty(documents)) {
            documents = Sets.newHashSet();
        }
        Set<GLClaimDocumentCommand> updatedDocuments = glClaimUpdateCommand.getUploadedDocuments();
        for (GLClaimDocumentCommand glClaimDocumentCommand : updatedDocuments) {
            documents = getAllUpdatedDocuments(glClaimDocumentCommand, documents);
        }

        groupLifeClaim = groupLifeClaim.withClaimDocuments(documents);

        glClaimMongoRepository.add(groupLifeClaim);

    }

    public Set<GLClaimDocument> getAllUpdatedDocuments(GLClaimDocumentCommand glClaimDocumentCommand, Set<GLClaimDocument> documents) throws IOException {
        String fileName = glClaimDocumentCommand.getFile() != null ? glClaimDocumentCommand.getFile().getName() : "";
        String gridFsDocId = gridFsTemplate.store(FileUtils.openInputStream(glClaimDocumentCommand.getFile()), fileName, "").getId().toString();
        GLClaimDocument currentDocument = new GLClaimDocument(glClaimDocumentCommand.getDocumentId(), fileName, gridFsDocId, "", glClaimDocumentCommand.isMandatory());
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
        groupLifeClaim = groupLifeClaim.submitForApproval(DateTime.now(), submitGLClaimCommand.getUserDetails().getUsername(), submitGLClaimCommand.getComment());
        return groupLifeClaim.getIdentifier().getClaimId();
    }



    @CommandHandler
    public String returnClaim(ReturnGLClaimCommand returnGLClaimCommand) {
        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(returnGLClaimCommand.getClaimId()));
        groupLifeClaim = groupLifeClaim.returnClaim(returnGLClaimCommand.getStatus(), returnGLClaimCommand.getUserDetails().getUsername(), returnGLClaimCommand.getComment());
        return groupLifeClaim.getIdentifier().getClaimId();
    }

    @CommandHandler
    public String claimApproval(GLClaimApprovalCommand glClaimApprovalCommand) {
        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(glClaimApprovalCommand.getClaimId()));
       // GLClaimApprover glClaimApprover = groupLifeClaimRoleAdapter.userToClaimApprover(glClaimApprovalCommand.getUserDetails());
        //groupLifeClaim = glClaimApprover.submitApproval(DateTime.now(), glClaimApprovalCommand.getComment(), groupLifeClaim, glClaimApprovalCommand.getStatus());
        groupLifeClaim = groupLifeClaim.markApproverApproval( glClaimApprovalCommand.getUserDetails().getUsername(),DateTime.now(), glClaimApprovalCommand.getComment(),glClaimApprovalCommand.getStatus());
        return groupLifeClaim.getIdentifier().getClaimId();
    }

   @CommandHandler
    public String settleClaim(GLClaimSettlementCommand glClaimSettlementCommand) {
        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(glClaimSettlementCommand.getClaimId()));
        //GLClaimSettlementProcessor glClaimProcessor=groupLifeClaimRoleAdapter.userToGLClaimSettlementProcessor(glClaimSettlementCommand.getUserDetails());
       // groupLifeClaim = glClaimProcessor.submitClaimSettlement(DateTime.now(), glClaimSettlementCommand.getComment(), groupLifeClaim, glClaimSettlementCommand.getStatus());
        //claim settlement to be created
      //
         GLClaimSettlement glClaimSettlement=glClaimSettlementFactory.createSettlement(glClaimSettlementCommand);
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
}


/*
    @CommandHandler
    public String submitClaim(SubmitGLClaimCommand submitGLClaimCommand) {
        GroupLifeClaim groupLifeClaim = glClaimMongoRepository.load(new ClaimId(submitGLClaimCommand.getClaimId()));
        groupLifeClaim = groupLifeClaim.submitForApproval(DateTime.now(),submitGLClaimCommand.getUserDetails().getUsername(), submitGLClaimCommand.getComment());
        return submitGLClaimCommand.getClaimId();
    }

   */


