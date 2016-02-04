package com.pla.grouphealth.claim.cashless.domain.model.claim;

import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationRequest;
import com.pla.grouphealth.claim.cashless.domain.model.sharedmodel.AdditionalDocument;
import com.pla.grouphealth.claim.cashless.domain.model.sharedmodel.CommentDetail;
import com.pla.grouphealth.sharedresource.model.vo.GHProposer;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerDocument;
import com.pla.sharedkernel.domain.model.Relationship;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.domain.AbstractAggregateRoot;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Set;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */
@Document(collection = "GROUP_HEALTH_CASHLESS_CLAIM")
@NoArgsConstructor
@Getter
public class GroupHealthCashlessClaim extends AbstractAggregateRoot<String> {

    @AggregateIdentifier
    @Id
    private String groupHealthCashlessClaimId;
    private String category;
    private Relationship relationship;
    private String claimType;
    private LocalDate claimIntimationDate;
    private String batchNumber;
    private String batchUploaderUserId;
    private Status status;
    private DateTime createdOn;
    private GHProposer ghProposer;
    private GroupHealthCashlessClaimPolicyDetail groupHealthCashlessClaimPolicyDetail;
    private GroupHealthCashlessClaimHCPDetail groupHealthCashlessClaimHCPDetail;
    private Set<GroupHealthCashlessClaimDiagnosisTreatmentDetail> groupHealthCashlessClaimDiagnosisTreatmentDetails;
    private GroupHealthCashlessClaimIllnessDetail groupHealthCashlessClaimIllnessDetail;
    private Set<GroupHealthCashlessClaimDrugService> groupHealthCashlessClaimDrugServices;
    private Set<GHProposerDocument> proposerDocuments;
    private Map<String, ScheduleToken> scheduledTokens;
    private Set<CommentDetail> commentDetails;
    private boolean submitted;
    private LocalDate submissionDate;
    private String claimProcessorUserId;
    private String claimUnderWriterUserId;
    private String underWriterRoutedToSeniorUnderWriterUserId;
    private boolean firstReminderSent;
    private boolean secondReminderSent;
    private boolean rejectionEmailSent;
    private boolean additionalRequirementEmailSent;
    private Set<AdditionalDocument> additionalRequiredDocumentsByUnderwriter;

    public GroupHealthCashlessClaim(Status status){
        this.status = status;
    }


    public GroupHealthCashlessClaim updateWithGroupHealthCashlessClaimId(String groupHealthCashlessClaimId) {
        this.groupHealthCashlessClaimId = groupHealthCashlessClaimId;
        return this;
    }

    public GroupHealthCashlessClaim updateWithCreationDate(DateTime batchDate) {
        if(isEmpty(batchDate)){
            batchDate = DateTime.now();
        }
        this.createdOn = batchDate;
        this.claimIntimationDate = batchDate.toLocalDate();
        return this;
    }

    public GroupHealthCashlessClaim updateWithBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
        return this;
    }

    public GroupHealthCashlessClaim updateWithBatchUploaderUserId(String batchUploaderUserId) {
        this.batchUploaderUserId = batchUploaderUserId;
        return this;
    }

    @Override
    public String getIdentifier() {
        return groupHealthCashlessClaimId;
    }

    public GroupHealthCashlessClaim updateWithProposerDetails(GHProposer ghProposer) {
        this.ghProposer = ghProposer;
        return this;
    }

    public GroupHealthCashlessClaim updateWithGroupHealthCashlessClaimHCPDetail(GroupHealthCashlessClaimHCPDetail groupHealthCashlessClaimHCPDetail) {
        this.groupHealthCashlessClaimHCPDetail = groupHealthCashlessClaimHCPDetail;
        return this;
    }

    public GroupHealthCashlessClaim updateWithGroupHealthCashlessClaimDiagnosisTreatmentDetails(Set<GroupHealthCashlessClaimDiagnosisTreatmentDetail> groupHealthCashlessClaimDiagnosisTreatmentDetails) {
        this.groupHealthCashlessClaimDiagnosisTreatmentDetails = groupHealthCashlessClaimDiagnosisTreatmentDetails;
        return this;
    }

    public GroupHealthCashlessClaim updateWithGroupHealthCashlessClaimIllnessDetail(GroupHealthCashlessClaimIllnessDetail groupHealthCashlessClaimIllnessDetail) {
        this.groupHealthCashlessClaimIllnessDetail = groupHealthCashlessClaimIllnessDetail;
        return this;
    }

    public GroupHealthCashlessClaim updateWithGroupHealthCashlessClaimDrugServices(Set<GroupHealthCashlessClaimDrugService> groupHealthCashlessClaimDrugServices) {
        this.groupHealthCashlessClaimDrugServices = groupHealthCashlessClaimDrugServices;
        return this;
    }

    public GroupHealthCashlessClaim updateWithGroupHealthCashlessClaimPolicyDetail(GroupHealthCashlessClaimPolicyDetail groupHealthCashlessClaimPolicyDetail) {
        this.groupHealthCashlessClaimPolicyDetail = groupHealthCashlessClaimPolicyDetail;
        return this;
    }

    public GroupHealthCashlessClaim updateWithCategory(String category) {
        this.category = category;
        return this;
    }

    public GroupHealthCashlessClaim updateWithRelationship(Relationship relationship) {
        this.relationship = relationship;
        return this;
    }

    public GroupHealthCashlessClaim updateWithClaimType(String claimType) {
        this.claimType = claimType;
        return this;
    }

    public GroupHealthCashlessClaim updateWithScheduledTokens(Map<String, ScheduleToken> scheduledTokens) {
        this.scheduledTokens = scheduledTokens;
        return this;
    }

    public GroupHealthCashlessClaim updateFlagForFirstReminderSent(Boolean firstReminderSent) {
        this.firstReminderSent = firstReminderSent;
        return this;
    }

    public GroupHealthCashlessClaim updateFlagForSecondReminderSent(Boolean secondReminderSent) {
        this.secondReminderSent = secondReminderSent;
        return this;
    }

    public GroupHealthCashlessClaim updateStatus(Status status) {
        this.status = status;
        return this;
    }

    public enum Status {
        INTIMATION("Intimation"), EVALUATION("Evaluation"), CANCELLED("Cancelled"), UNDERWRITING_LEVEL1("Underwriting"), UNDERWRITING_LEVEL2("Underwriting"), APPROVED("Approved"), REJECTED("Rejected"), RETURNED("Evaluation");

        private String description;

        private Status(String description){
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
