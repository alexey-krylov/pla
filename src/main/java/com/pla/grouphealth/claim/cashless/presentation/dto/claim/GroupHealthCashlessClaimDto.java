package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import com.google.common.collect.Sets;
import com.pla.grouphealth.claim.cashless.domain.model.claim.*;
import com.pla.grouphealth.claim.cashless.domain.model.sharedmodel.AdditionalDocument;
import com.pla.grouphealth.claim.cashless.domain.model.sharedmodel.CommentDetail;
import com.pla.grouphealth.sharedresource.model.vo.GHProposer;
import com.pla.sharedkernel.domain.model.Relationship;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 2/4/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class GroupHealthCashlessClaimDto {
    private String groupHealthCashlessClaimId;
    private String category;
    private String relationship;
    private String claimType;
    private LocalDate claimIntimationDate;
    private String batchNumber;
    private String batchUploaderUserId;
    private String status;
    private GHProposerDto ghProposer;
    private GroupHealthCashlessClaimPolicyDetailDto groupHealthCashlessClaimPolicyDetail;
    private GroupHealthCashlessClaimHCPDetailDto groupHealthCashlessClaimHCPDetail;
    private Set<GroupHealthCashlessClaimDiagnosisTreatmentDetailDto> groupHealthCashlessClaimDiagnosisTreatmentDetails;
    private GroupHealthCashlessClaimIllnessDetailDto groupHealthCashlessClaimIllnessDetail;
    private Set<GroupHealthCashlessClaimDrugServiceDto> groupHealthCashlessClaimDrugServices;
    private Set<CommentDetail> commentDetails;
    private boolean submitted;
    private boolean submitEventFired;
    private LocalDate submissionDate;
    private String claimProcessorUserId;
    private String claimUnderWriterUserId;
    private String billMismatchProcessorId;
    private String serviceMismatchProcessorId;
    private String claimRejectedBy;
    private String underWriterRoutedToSeniorUnderWriterUserId;
    private boolean firstReminderSent;
    private boolean secondReminderSent;
    private boolean rejectionEmailSent;
    private boolean additionalRequirementEmailSent;
    private Set<AdditionalDocument> additionalRequiredDocumentsByUnderwriter;
    private Set<PreAuthorizationDetailTaggedToClaim> preAuthorizationDetails;

    public GroupHealthCashlessClaimDto updateWithGroupHealthCashlessClaimId(String groupHealthCashlessClaimId) {
        this.groupHealthCashlessClaimId = groupHealthCashlessClaimId;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithCategory(String category) {
        this.category = category;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithRelationship(Relationship relationship) {
        if(isNotEmpty(relationship))
            this.relationship = relationship.description;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithClaimType(String claimType) {
        this.claimType = claimType;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithClaimIntimationDate(LocalDate claimIntimationDate) {
        this.claimIntimationDate = claimIntimationDate;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithBatchUploaderUserId(String batchUploaderUserId) {
        this.batchUploaderUserId = batchUploaderUserId;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithStatus(GroupHealthCashlessClaim.Status status) {
        if(isNotEmpty(status))
            this.status = status.getDescription();
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithGhProposer(GHProposer ghProposer) {
        if(isNotEmpty(ghProposer)){
            this.ghProposer = new GHProposerDto().updateWithDetails(ghProposer);
        }
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithGroupHealthCashlessClaimHCPDetail(GroupHealthCashlessClaimHCPDetail groupHealthCashlessClaimHCPDetail) {
        if(isNotEmpty(groupHealthCashlessClaimHCPDetail)){
            this.groupHealthCashlessClaimHCPDetail = new GroupHealthCashlessClaimHCPDetailDto().updateWithDetails(groupHealthCashlessClaimHCPDetail);
        }
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithGroupHealthCashlessClaimDiagnosisTreatmentDetails(Set<GroupHealthCashlessClaimDiagnosisTreatmentDetail> groupHealthCashlessClaimDiagnosisTreatmentDetails) {
        this.groupHealthCashlessClaimDiagnosisTreatmentDetails = isNotEmpty(groupHealthCashlessClaimDiagnosisTreatmentDetails) ? groupHealthCashlessClaimDiagnosisTreatmentDetails.stream().map(detail -> new GroupHealthCashlessClaimDiagnosisTreatmentDetailDto().updateWithDetails(detail)).collect(Collectors.toSet()) : Sets.newHashSet();
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithGroupHealthCashlessClaimIllnessDetail(GroupHealthCashlessClaimIllnessDetail groupHealthCashlessClaimIllnessDetail) {
        if(isNotEmpty(groupHealthCashlessClaimIllnessDetail))
            this.groupHealthCashlessClaimIllnessDetail  = new GroupHealthCashlessClaimIllnessDetailDto().updateWithDetails(groupHealthCashlessClaimIllnessDetail);
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithGroupHealthCashlessClaimDrugServices(Set<GroupHealthCashlessClaimDrugService> groupHealthCashlessClaimDrugServices) {
        this.groupHealthCashlessClaimDrugServices = isNotEmpty(groupHealthCashlessClaimDrugServices) ? groupHealthCashlessClaimDrugServices.stream().map(detail -> new GroupHealthCashlessClaimDrugServiceDto().updateWithDetails(detail)).collect(Collectors.toSet()) : Sets.newHashSet();
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithCommentDetails(Set<CommentDetail> commentDetails) {
        this.commentDetails = commentDetails;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithSubmittedFlag(boolean submitted) {
        this.submitted = submitted;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithClaimProcessorUserId(String claimProcessorUserId) {
        this.claimProcessorUserId = claimProcessorUserId;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithClaimUnderWriterUserId(String claimUnderWriterUserId) {
        this.claimUnderWriterUserId = claimUnderWriterUserId;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithUnderWriterRoutedToSeniorUnderWriterUserId(String underWriterRoutedToSeniorUnderWriterUserId) {
        this.underWriterRoutedToSeniorUnderWriterUserId = underWriterRoutedToSeniorUnderWriterUserId;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithFirstReminderSent(boolean firstReminderSent) {
        this.firstReminderSent = firstReminderSent;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithSecondReminderSent(boolean secondReminderSent) {
        this.secondReminderSent = secondReminderSent;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithRejectionEmailSent(boolean rejectionEmailSent) {
        this.rejectionEmailSent = rejectionEmailSent;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithAdditionalRequirementEmailSent(boolean additionalRequirementEmailSent) {
        this.additionalRequirementEmailSent = additionalRequirementEmailSent;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithAdditionalRequiredDocumentsByUnderwriter(Set<AdditionalDocument> additionalRequiredDocumentsByUnderwriter) {
        if(isEmpty(additionalRequiredDocumentsByUnderwriter)){
            additionalRequiredDocumentsByUnderwriter = Sets.newHashSet();
        }
        this.additionalRequiredDocumentsByUnderwriter = additionalRequiredDocumentsByUnderwriter;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithGroupHealthCashlessClaimPolicyDetail(GroupHealthCashlessClaimPolicyDetail groupHealthCashlessClaimPolicyDetail) {
        this.groupHealthCashlessClaimPolicyDetail = new GroupHealthCashlessClaimPolicyDetailDto().updateWithDetails(groupHealthCashlessClaimPolicyDetail);
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithGroupHealthCashlessClaimPolicyDetailDto(GroupHealthCashlessClaimPolicyDetailDto groupHealthCashlessClaimPolicyDetailDto) {
        this.groupHealthCashlessClaimPolicyDetail = groupHealthCashlessClaimPolicyDetailDto;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithGroupHealthCashlessClaimPolicyNumber(GroupHealthCashlessClaimPolicyDetail groupHealthCashlessClaimPolicyDetail) {
        this.groupHealthCashlessClaimPolicyDetail = constructPolicyDetail(groupHealthCashlessClaimPolicyDetail);
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithPreAuthorizationDetails(Set<PreAuthorizationDetailTaggedToClaim> preAuthorizationDetails) {
        this.preAuthorizationDetails = preAuthorizationDetails;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithBillMismatchProcessorId(String billMismatchProcessorId) {
        this.billMismatchProcessorId = billMismatchProcessorId;
        return this;
    }

    public GroupHealthCashlessClaimDto updateWithServiceMismatchProcessorId(String serviceMismatchProcessorId) {
        this.serviceMismatchProcessorId = serviceMismatchProcessorId;
        return this;
    }

    public GroupHealthCashlessClaimDto updateClaimRejectedBy(String claimRejectedBy) {
        this.claimRejectedBy = claimRejectedBy;
        return this;
    }

    private GroupHealthCashlessClaimPolicyDetailDto constructPolicyDetail(GroupHealthCashlessClaimPolicyDetail groupHealthCashlessClaimPolicyDetail) {
        return new GroupHealthCashlessClaimPolicyDetailDto()
                .updateWithPolicyNumber(groupHealthCashlessClaimPolicyDetail.getPolicyNumber())
                .updateWithPolicyName(groupHealthCashlessClaimPolicyDetail.getPolicyName())
                .updateWithPlanName(groupHealthCashlessClaimPolicyDetail.getPlanName())
                .updateWithPlanCode(groupHealthCashlessClaimPolicyDetail.getPlanCode())
                .updateWithClientId(groupHealthCashlessClaimPolicyDetail.getAssuredDetail());
    }

    public BigDecimal getSumOfAllProbableClaimAmount() {
        BigDecimal sumOfAllProbableClaimAmount = BigDecimal.ZERO;
        if(isNotEmpty(this.groupHealthCashlessClaimPolicyDetail)) {
            Set<GroupHealthCashlessClaimCoverageDetail> coverageDetails = this.groupHealthCashlessClaimPolicyDetail.getCoverageDetails();
            if(isNotEmpty(coverageDetails)){
                for(GroupHealthCashlessClaimCoverageDetail groupHealthCashlessClaimCoverageDetail :  coverageDetails){
                    Set<GroupHealthCashlessClaimBenefitDetail> benefitDetails = groupHealthCashlessClaimCoverageDetail.getBenefitDetails();
                    if(isNotEmpty(benefitDetails)){
                        for(GroupHealthCashlessClaimBenefitDetail benefitDetail : benefitDetails){
                            sumOfAllProbableClaimAmount = sumOfAllProbableClaimAmount.add(benefitDetail.getProbableClaimAmount());
                        }
                    }
                }
            }
        }
        return sumOfAllProbableClaimAmount;
    }

    public int getAgeOfTheClient() {
        return isNotEmpty(this.groupHealthCashlessClaimPolicyDetail) ? isNotEmpty(this.groupHealthCashlessClaimPolicyDetail.getAssuredDetail()) ?  this.groupHealthCashlessClaimPolicyDetail.getAssuredDetail().getAgeNextBirthday()  : 0 : 0;
    }
}
