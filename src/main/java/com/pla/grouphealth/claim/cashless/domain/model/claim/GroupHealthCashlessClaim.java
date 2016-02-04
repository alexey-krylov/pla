package com.pla.grouphealth.claim.cashless.domain.model.claim;

import com.pla.grouphealth.claim.cashless.domain.model.sharedmodel.AdditionalDocument;
import com.pla.grouphealth.claim.cashless.domain.model.sharedmodel.CommentDetail;
import com.pla.grouphealth.sharedresource.model.vo.GHProposer;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerDocument;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Set;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */
@Document(collection = "GROUP_HEALTH_CASHLESS_CLAIM")
@NoArgsConstructor
@Getter
public class GroupHealthCashlessClaim {

    @AggregateIdentifier
    @Id
    private String groupHealthCashlessClaimId;
    private String category;
    private String relationship;
    private String claimType;
    private LocalDate claimIntimationDate;
    private String batchNumber;
    private String batchUploaderUserId;
    private GHProposer ghProposer;
    private Status status;
    private DateTime createdOn;
    private LocalDate preAuthorizationDate;
    private GroupHealthCashlessClaimPolicyDetail groupHealthCashlessClaimPolicyDetail;
    private GroupHealthCashlessClaimHCPDetail preAuthorizationRequestHCPDetail;
    private Set<GroupHealthCashlessClaimDiagnosisTreatmentDetail> preAuthorizationRequestDiagnosisTreatmentDetails;
    private GroupHealthCashlessClaimIllnessDetail preAuthorizationRequestIllnessDetail;
    private Set<GroupHealthCashlessClaimDrugService> groupHealthCashlessClaimDrugServices;
    private Set<GHProposerDocument> proposerDocuments;
    private Map<String, ScheduleToken> scheduledTokens;
    private Set<CommentDetail> commentDetails;
    private boolean submitted;
    private LocalDate submissionDate;
    private String preAuthorizationProcessorUserId;
    private String preAuthorizationUnderWriterUserId;
    private String underWriterRoutedToSeniorUnderWriterUserId;
    private boolean firstReminderSent;
    private boolean secondReminderSent;
    private boolean rejectionEmailSent;
    private boolean additionalRequirementEmailSent;
    private Set<AdditionalDocument> additionalRequiredDocumentsByUnderwriter;

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
