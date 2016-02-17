package com.pla.grouphealth.claim.cashless.domain.model.claim;

import com.google.common.collect.Sets;
import com.pla.grouphealth.claim.cashless.domain.event.claim.GroupHealthCashlessClaimFollowUpReminderEvent;
import com.pla.grouphealth.claim.cashless.domain.exception.GenerateReminderFollowupException;
import com.pla.grouphealth.claim.cashless.domain.model.sharedmodel.AdditionalDocument;
import com.pla.grouphealth.claim.cashless.domain.model.sharedmodel.CommentDetail;
import com.pla.grouphealth.claim.cashless.presentation.dto.claim.*;
import com.pla.grouphealth.sharedresource.model.vo.GHProposer;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerDocument;
import com.pla.sharedkernel.domain.model.Relationship;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.beanutils.BeanUtils;
import org.axonframework.domain.AbstractAggregateRoot;
import org.axonframework.eventhandling.scheduling.ScheduleToken;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    private GroupHealthCashlessClaimBankDetail groupHealthCashlessClaimBankDetail;
    private boolean submitted;
    private LocalDate submissionDate;
    private String claimProcessorUserId;
    private String claimUnderWriterUserId;
    private String billMismatchProcessorId;
    private String serviceMismatchProcessorId;
    private String underWriterRoutedToSeniorUnderWriterUserId;
    private String claimRejectedBy;
    private boolean firstReminderSent;
    private boolean secondReminderSent;
    private boolean rejectionEmailSent;
    private boolean additionalRequirementEmailSent;
    private Set<AdditionalDocument> additionalRequiredDocumentsByUnderwriter;
    private Set<PreAuthorizationDetailTaggedToClaim> preAuthorizationDetails;
    private LocalDate claimRejectionDate;

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

    public void savedRegisterFollowUpReminders() throws GenerateReminderFollowupException {
        try {
            registerEvent(new GroupHealthCashlessClaimFollowUpReminderEvent(this.getGroupHealthCashlessClaimId()));
        } catch (Exception e){
            throw new GenerateReminderFollowupException(e.getMessage());
        }
    }

    public GroupHealthCashlessClaim updateWithGhProposerDto(GHProposerDto ghProposerDto) {
        if(isNotEmpty(ghProposerDto)) {
            GHProposer ghProposer = isNotEmpty(this.getGhProposer()) ? this.getGhProposer() : getInstance(GHProposer.class);
            assert ghProposer != null;
            this.ghProposer = ghProposer.updateWithDetails(ghProposerDto);
        }
        return this;
    }

    public GroupHealthCashlessClaim updateWithClaimIntimationDate(LocalDate claimIntimationDate) {
        this.claimIntimationDate = claimIntimationDate;
        return this;
    }

    public GroupHealthCashlessClaim updateWithStatus(Status status) {
        this.status = status;
        return this;
    }

    public GroupHealthCashlessClaim updateWithCreatedOn(DateTime createdOn) {
        this.createdOn = createdOn;
        return this;
    }

    public GroupHealthCashlessClaim updateWithGroupHealthCashlessClaimHCPDetailFromDto(GroupHealthCashlessClaimHCPDetailDto groupHealthCashlessClaimHCPDetailDto) {
        if(isNotEmpty(groupHealthCashlessClaimHCPDetailDto)) {
            GroupHealthCashlessClaimHCPDetail groupHealthCashlessClaimHCPDetail = isNotEmpty(this.getGroupHealthCashlessClaimHCPDetail()) ? this.getGroupHealthCashlessClaimHCPDetail() : getInstance(GroupHealthCashlessClaimHCPDetail.class);
            try {
                BeanUtils.copyProperties(groupHealthCashlessClaimHCPDetail, groupHealthCashlessClaimHCPDetailDto);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            this.groupHealthCashlessClaimHCPDetail = groupHealthCashlessClaimHCPDetail;
        }
        return this;
    }

    public GroupHealthCashlessClaim updateWithGroupHealthCashlessClaimDiagnosisTreatmentDetailsFromDto(Set<GroupHealthCashlessClaimDiagnosisTreatmentDetailDto> groupHealthCashlessClaimDiagnosisTreatmentDetails) {
        if(isNotEmpty(groupHealthCashlessClaimDiagnosisTreatmentDetails)) {
            this.groupHealthCashlessClaimDiagnosisTreatmentDetails = groupHealthCashlessClaimDiagnosisTreatmentDetails.parallelStream().map(treatmentDiagnosis -> {
                GroupHealthCashlessClaimDiagnosisTreatmentDetail groupHealthCashlessClaimDiagnosisTreatmentDetail = getInstance(GroupHealthCashlessClaimDiagnosisTreatmentDetail.class);
                try {
                    BeanUtils.copyProperties(groupHealthCashlessClaimDiagnosisTreatmentDetail, treatmentDiagnosis);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return groupHealthCashlessClaimDiagnosisTreatmentDetail;
            }).collect(Collectors.toSet());
        }
        return this;
    }

    public GroupHealthCashlessClaim updateWithGroupHealthCashlessClaimIllnessDetailFromDto(GroupHealthCashlessClaimIllnessDetailDto groupHealthCashlessClaimIllnessDetailDto) {
        if(isNotEmpty(groupHealthCashlessClaimIllnessDetailDto)){
            GroupHealthCashlessClaimIllnessDetail groupHealthCashlessClaimIllnessDetail = isNotEmpty(this.groupHealthCashlessClaimIllnessDetail) ? this.groupHealthCashlessClaimIllnessDetail : getInstance(GroupHealthCashlessClaimIllnessDetail.class);
            try {
                BeanUtils.copyProperties(groupHealthCashlessClaimIllnessDetail, groupHealthCashlessClaimIllnessDetailDto);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            this.groupHealthCashlessClaimIllnessDetail = groupHealthCashlessClaimIllnessDetail;
        }
        return this;
    }

    public GroupHealthCashlessClaim updateWithGroupHealthCashlessClaimDrugServicesFromDto(Set<GroupHealthCashlessClaimDrugServiceDto> groupHealthCashlessClaimDrugServices) {
        this.groupHealthCashlessClaimDrugServices = isNotEmpty(groupHealthCashlessClaimDrugServices) ? groupHealthCashlessClaimDrugServices.parallelStream().map(drugServiceDto -> {
            GroupHealthCashlessClaimDrugService groupHealthCashlessClaimDrugService = getInstance(GroupHealthCashlessClaimDrugService.class);
            return groupHealthCashlessClaimDrugService.updateDetails(drugServiceDto);
        }).collect(Collectors.toSet()) : Sets.newHashSet();
        return this;
    }

    public GroupHealthCashlessClaim updateWithCommentDetails(Set<CommentDetail> commentDetails, String userName) {
        this.commentDetails = isNotEmpty(commentDetails) ? commentDetails.stream().map(comment -> {
            if(isNotEmpty(comment.getComments()) && isEmpty(comment.getCommentDateTime())){
                comment.updateWithCommentDateTime(DateTime.now());
                comment.updateWithUserName(userName);
            }
            return comment;
        }).collect(Collectors.toSet()) : Sets.newHashSet();
        return this;
    }

    public GroupHealthCashlessClaim updateWithSubmittedFlag(boolean submitted) {
        this.submitted = submitted;
        return this;
    }

    public GroupHealthCashlessClaim updateWithSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
        return this;
    }

    public GroupHealthCashlessClaim updateWithClaimProcessorUserId(String claimProcessorUserId) {
        this.claimProcessorUserId = claimProcessorUserId;
        return this;
    }

    public GroupHealthCashlessClaim updateWithClaimUnderWriterUserId(String claimUnderWriterUserId) {
        this.claimUnderWriterUserId = claimUnderWriterUserId;
        return this;
    }

    public GroupHealthCashlessClaim updateWithUnderWriterRoutedToSeniorUnderWriterUserId(String underWriterRoutedToSeniorUnderWriterUserId) {
        this.underWriterRoutedToSeniorUnderWriterUserId = underWriterRoutedToSeniorUnderWriterUserId;
        return this;
    }

    public GroupHealthCashlessClaim updateWithFirstReminderSent(boolean firstReminderSent) {
        this.firstReminderSent = firstReminderSent;
        return this;
    }

    public GroupHealthCashlessClaim updateWithSecondReminderSent(boolean secondReminderSent) {
        this.secondReminderSent = secondReminderSent;
        return this;
    }

    public GroupHealthCashlessClaim updateWithRejectionEmailSent(boolean rejectionEmailSent) {
        this.rejectionEmailSent = rejectionEmailSent;
        return this;
    }

    public GroupHealthCashlessClaim updateWithAdditionalRequirementEmailSent(boolean additionalRequirementEmailSent) {
        this.additionalRequirementEmailSent = additionalRequirementEmailSent;
        return this;
    }

    public GroupHealthCashlessClaim updateWithAdditionalRequiredDocumentsByUnderwriter(Set<AdditionalDocument> additionalRequiredDocumentsByUnderwriter) {
        this.additionalRequiredDocumentsByUnderwriter = additionalRequiredDocumentsByUnderwriter;
        return this;
    }

    public GroupHealthCashlessClaim updateWithGroupHealthCashlessClaimPolicyDetailFromDto(GroupHealthCashlessClaimPolicyDetailDto groupHealthCashlessClaimPolicyDetailDto) {
        if(isNotEmpty(groupHealthCashlessClaimPolicyDetailDto)){
            GroupHealthCashlessClaimPolicyDetail groupHealthCashlessClaimPolicyDetail = isNotEmpty(this.groupHealthCashlessClaimPolicyDetail) ? this.groupHealthCashlessClaimPolicyDetail : getInstance(GroupHealthCashlessClaimPolicyDetail.class);
            try {
                BeanUtils.copyProperties(groupHealthCashlessClaimPolicyDetail, groupHealthCashlessClaimPolicyDetailDto);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            this.groupHealthCashlessClaimPolicyDetail = groupHealthCashlessClaimPolicyDetail;
        }
        return this;
    }

    public GroupHealthCashlessClaim updateWithDocuments(Set<GHProposerDocument> documents) {
        this.proposerDocuments = documents;
        return this;
    }

    public GroupHealthCashlessClaim updateRejectionEmailSentFlag(Boolean rejectionEmailSent) {
        this.rejectionEmailSent = rejectionEmailSent;
        return this;
    }

    public GroupHealthCashlessClaim updateRequirementEmailSentFlag(Boolean additionalRequirementEmailSent) {
        this.additionalRequirementEmailSent = additionalRequirementEmailSent;
        return this;
    }

    public GroupHealthCashlessClaim updateWithPreAuthorizationDetails(Set<PreAuthorizationDetailTaggedToClaim> preAuthorizationDetails) {
        this.preAuthorizationDetails = preAuthorizationDetails;
        return this;
    }
    public GroupHealthCashlessClaim updateWithBillMismatchProcessorId(String billMismatchProcessorId) {
        this.billMismatchProcessorId = billMismatchProcessorId;
        return this;
    }

    public GroupHealthCashlessClaim updateWithServiceMismatchProcessorId(String serviceMismatchProcessorId) {
        this.serviceMismatchProcessorId = serviceMismatchProcessorId;
        return this;
    }

    public GroupHealthCashlessClaim updateClaimRejectedBy(String claimRejectedBy) {
        this.claimRejectedBy = claimRejectedBy;
        return this;
    }

    public GroupHealthCashlessClaim updateWithRejectionDate(LocalDate claimRejectionDate) {
        this.claimRejectionDate = claimRejectionDate;
        return this;
    }

    public enum Status {
        INTIMATION("Intimation"), EVALUATION("Evaluation"), CANCELLED("Cancelled"), UNDERWRITING_LEVEL1("Underwriting"), UNDERWRITING_LEVEL2("Underwriting"),
        APPROVED("Approved"), REPUDIATED("Repudiated"), RETURNED("Evaluation"), AWAITING_DISBURSEMENT("Awaiting Disbursement"), DISBURSED("Disbursed"), BILL_MISMATCHED("Evaluation"), SERVICE_MISMATCHED("Evaluation");

        private String description;

        Status(String description){
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public static Status getStatus(String status) {
            for (Status s : Status.values()) {
                if (status.equals(s.description)) {
                    return s;
                }
            }
            return null;
        }
    }

    public GroupHealthCashlessClaim populateDetailsToGroupHealthCashlessClaim(GroupHealthCashlessClaimDto groupHealthCashlessClaimDto, String userName){
        if(isNotEmpty(this)){
            this.updateWithCategory(groupHealthCashlessClaimDto.getCategory())
                    .updateWithRelationship(Relationship.getRelationship(groupHealthCashlessClaimDto.getRelationship()))
                    .updateWithClaimType(groupHealthCashlessClaimDto.getClaimType())
                    .updateWithClaimIntimationDate(groupHealthCashlessClaimDto.getClaimIntimationDate())
                    .updateWithBatchNumber(groupHealthCashlessClaimDto.getBatchNumber())
                    .updateWithBatchNumber(groupHealthCashlessClaimDto.getBatchNumber())
                    .updateWithBatchUploaderUserId(groupHealthCashlessClaimDto.getBatchUploaderUserId())
                    .updateWithGhProposerDto(groupHealthCashlessClaimDto.getGhProposer())
                    .updateWithGroupHealthCashlessClaimHCPDetailFromDto(groupHealthCashlessClaimDto.getGroupHealthCashlessClaimHCPDetail())
                    .updateWithGroupHealthCashlessClaimDiagnosisTreatmentDetailsFromDto(groupHealthCashlessClaimDto.getGroupHealthCashlessClaimDiagnosisTreatmentDetails())
                    .updateWithGroupHealthCashlessClaimIllnessDetailFromDto(groupHealthCashlessClaimDto.getGroupHealthCashlessClaimIllnessDetail())
                    .updateWithGroupHealthCashlessClaimDrugServicesFromDto(groupHealthCashlessClaimDto.getGroupHealthCashlessClaimDrugServices())
                    .updateWithCommentDetails(groupHealthCashlessClaimDto.getCommentDetails(), userName)
                    .updateWithClaimProcessorUserId(groupHealthCashlessClaimDto.getClaimProcessorUserId())
                    .updateWithClaimUnderWriterUserId(groupHealthCashlessClaimDto.getClaimUnderWriterUserId())
                    .updateWithAdditionalRequiredDocumentsByUnderwriter(groupHealthCashlessClaimDto.getAdditionalRequiredDocumentsByUnderwriter())
                    .updateWithGroupHealthCashlessClaimPolicyDetailFromDto(groupHealthCashlessClaimDto.getGroupHealthCashlessClaimPolicyDetail());
        }
        return this;
    }

    private <T> T getInstance(Class<T> tClass) {
        try {
            Constructor<T> constructor = tClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
