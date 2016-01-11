package com.pla.grouplife.endorsement.domain.model;

import com.google.common.collect.Sets;
import com.pla.grouplife.endorsement.domain.event.GLEndorsementStatusAuditEvent;
import com.pla.grouplife.sharedresource.event.GLPolicyInsuredDeleteEvent;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.model.vo.*;
import com.pla.sharedkernel.domain.model.EndorsementNumber;
import com.pla.sharedkernel.domain.model.EndorsementStatus;
import com.pla.sharedkernel.domain.model.EndorsementUniqueNumber;
import com.pla.sharedkernel.domain.model.Policy;
import com.pla.sharedkernel.identifier.EndorsementId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.domain.AbstractAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 8/3/2015.
 */
@Document(collection = "group_life_endorsement")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GroupLifeEndorsement extends AbstractAggregateRoot<EndorsementId> {

    @Id
    @AggregateIdentifier
    private EndorsementId endorsementId;

    /*
    * Endorsement Request number
    *
    * */
    private EndorsementNumber endorsementNumber;

    /*
    * Endorsement number
    *
    * */
    private EndorsementUniqueNumber endorsementUniqueNumber;

    private GLEndorsementType endorsementType;

    private EndorsementStatus status;

    private GLEndorsement endorsement;

    private Policy policy;

    private DateTime effectiveDate;

    private Set<GLProposerDocument> proposerDocuments;

    private DateTime submittedOn;

    private PremiumDetail premiumDetail;

    private UnderWriterFactor underWriterFactor;

    public GroupLifeEndorsement(EndorsementId endorsementId, EndorsementNumber endorsementNumber, Policy policy, GLEndorsementType endorsementType) {
        checkArgument(endorsementId != null, "Endorsement ID cannot be empty");
        checkArgument(endorsementNumber != null, "Endorsement Number cannot be empty");
        checkArgument(policy != null, "Policy cannot be empty");
        this.endorsementId = endorsementId;
        this.endorsementNumber = endorsementNumber;
        this.status = EndorsementStatus.DRAFT;
        this.policy = policy;
        this.endorsementType = endorsementType;
    }

    public GroupLifeEndorsement updateWithEndorsementDetail(GLEndorsement endorsement) {
        this.endorsement = endorsement;
        return this;
    }

    public GroupLifeEndorsement updateWithDocuments(Set<GLProposerDocument> proposerDocuments) {
        this.proposerDocuments = proposerDocuments;
        return this;
    }

    public GroupLifeEndorsement submit(DateTime effectiveDate, EndorsementStatus status, String submittedBy) {
        this.status = status;
        this.effectiveDate = effectiveDate;
        return this;
    }

    public GroupLifeEndorsement cancel(DateTime cancelledOn, String cancelledBy) {
        this.status = EndorsementStatus.CANCELLED;
        return this;
    }

    public GroupLifeEndorsement reject(DateTime rejectedOn, String rejectedBy) {
        this.status = EndorsementStatus.REJECTED;
        return this;
    }

    public GroupLifeEndorsement putOnHold(DateTime holdOn, String holdBy) {
        this.status = EndorsementStatus.HOLD;
        return this;
    }

    public GroupLifeEndorsement paymentPending() {
        this.status = EndorsementStatus.PAYMENT_PENDING;
        return this;
    }

    public GroupLifeEndorsement paymentReceived() {
        this.status = EndorsementStatus.PAYMENT_RECEIVED;
        return this;
    }

    public GroupLifeEndorsement refundPending() {
        this.status = EndorsementStatus.REFUND_PENDING;
        return this;
    }

    public GroupLifeEndorsement refundProcessed() {
        this.status = EndorsementStatus.REFUND_PROCESSED;
        return this;
    }

    public GroupLifeEndorsement withFCLEndorsement(GLEndorsement freeCoverLimitEndorsement){
        this.endorsement = freeCoverLimitEndorsement;
        return this;
    }

    public GroupLifeEndorsement submitForApproval(DateTime now, String username, String comment) {
        this.submittedOn = now;
        this.status = EndorsementStatus.APPROVER_PENDING_ACCEPTANCE;
        if (isNotEmpty(comment)) {
            registerEvent(new GLEndorsementStatusAuditEvent(this.getEndorsementId(), EndorsementStatus.APPROVER_PENDING_ACCEPTANCE, username, comment, submittedOn));
        }
        return this;
    }

    public GroupLifeEndorsement returnEndorsement(EndorsementStatus status, String username, String comment) {
        this.status = status;
        if (isNotEmpty(comment)) {
            registerEvent(new GLEndorsementStatusAuditEvent(this.getEndorsementId(), EndorsementStatus.RETURN, username, comment, submittedOn));
        }
        return this;
    }

    public GroupLifeEndorsement approve(DateTime now , String username, String comment, EndorsementUniqueNumber endorsementUniqueNumber) {
        this.effectiveDate = now;
        this.endorsementUniqueNumber= endorsementUniqueNumber;
        this.status = EndorsementStatus.APPROVED;
        if (isNotEmpty(comment)) {
            registerEvent(new GLEndorsementStatusAuditEvent(this.getEndorsementId(), EndorsementStatus.APPROVED, username, comment, submittedOn));
        }
        raiseEventIfTypeIsMemberDeletion();
        return this;
    }

    private void raiseEventIfTypeIsMemberDeletion() {
        if (this.getEndorsementType().equals(GLEndorsementType.ASSURED_MEMBER_DELETION)){
            Set<Insured>  deletedInsuredDetails = this.getEndorsement().getMemberDeletionEndorsements().getInsureds();
            List<String> deletedInsuredFamilyIds = deletedInsuredDetails.parallelStream().filter(new Predicate<Insured>() {
                @Override
                public boolean test(Insured insured) {
                    return insured.getNoOfAssured()==null;
                }
            }).map(new Function<Insured, String>() {
                @Override
                public String apply(Insured insured) {
                    return insured.getFamilyId().getFamilyId();
                }
            }).collect(Collectors.toList());

            for (Insured insured : deletedInsuredDetails){
                insured.getInsuredDependents().parallelStream().filter(new Predicate<InsuredDependent>() {
                    @Override
                    public boolean test(InsuredDependent insuredDependent) {
                        return insuredDependent.getNoOfAssured()==null;
                    }
                }).map(new Function<InsuredDependent, String>() {
                    @Override
                    public String apply(InsuredDependent insuredDependent) {
                        deletedInsuredFamilyIds.add(insuredDependent.getFamilyId().getFamilyId());
                        return insuredDependent.getFamilyId().getFamilyId();
                    }
                }).collect(Collectors.toList());
            }
            registerEvent(new GLPolicyInsuredDeleteEvent(this.policy.getPolicyId(),deletedInsuredFamilyIds));
        }
    }

    /*
    * @TODO change according to the type
    * */
    public GroupLifeEndorsement updateWithInsureds(Set<Insured> insureds) {
        if (this.endorsementType.equals(GLEndorsementType.NEW_CATEGORY_RELATION))
            this.endorsement.getNewCategoryRelationEndorsement().setInsureds(insureds);
        else if (this.endorsementType.equals(GLEndorsementType.ASSURED_MEMBER_ADDITION))
            this.endorsement.getMemberEndorsement().setInsureds(insureds);
        else if  (this.endorsementType.equals(GLEndorsementType.ASSURED_MEMBER_DELETION))
            this.endorsement.getMemberDeletionEndorsements().setInsureds(insureds);
        return this;
    }

    public GroupLifeEndorsement updateWithGLEndorsementInsured(GLEndorsementInsured glEndorsementInsured){
        this.endorsement.updateWithFCLEndorsement(glEndorsementInsured);
        return this;
    }
    @Override
    public EndorsementId getIdentifier() {
        return endorsementId;
    }




    public BigDecimal getNetAnnualPremiumPaymentAmount(PremiumDetail premiumDetail,Industry industry) {
        BigDecimal totalBasicPremium = this.getTotalBasicPremiumForInsured();
        BigDecimal hivDiscountAmount = premiumDetail.getHivDiscount() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getHivDiscount().divide(new BigDecimal(100))));
        BigDecimal valuedClientDiscountAmount = premiumDetail.getValuedClientDiscount() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getValuedClientDiscount().divide(new BigDecimal(100))));
        BigDecimal longTermDiscountAmount = premiumDetail.getLongTermDiscount() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getLongTermDiscount().divide(new BigDecimal(100))));
        BigDecimal addOnBenefitAmount = premiumDetail.getAddOnBenefit() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getAddOnBenefit().divide(new BigDecimal(100))));
        BigDecimal profitAndSolvencyAmount = premiumDetail.getProfitAndSolvency() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getProfitAndSolvency().divide(new BigDecimal(100))));
        BigDecimal industryLoadingFactor = BigDecimal.ONE;
        if (industry!=null) {
            industryLoadingFactor = industry.getLoadingFactor();
        }
        BigDecimal totalLoadingAmount = (addOnBenefitAmount.add(profitAndSolvencyAmount)).subtract((hivDiscountAmount.add(valuedClientDiscountAmount).add(longTermDiscountAmount)));
        BigDecimal totalInsuredPremiumAmount = totalBasicPremium.add(totalLoadingAmount);
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.multiply(industryLoadingFactor);
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.setScale(2, BigDecimal.ROUND_CEILING);
        return totalInsuredPremiumAmount;
    }


    public BigDecimal getNetFCLPremiumPaymentAmount(PremiumDetail premiumDetail,Industry industry,UnderWriterFactor underWriterFactor) {
        BigDecimal totalBasicPremium = this.getTotalBasicPremiumForInsured();
        BigDecimal underWritingFactor = underWriterFactor.getUnderWritingFactor()!=null?underWriterFactor.getUnderWritingFactor():BigDecimal.ZERO;
        BigDecimal underWritingAmount = totalBasicPremium!=null?totalBasicPremium.multiply(underWritingFactor):BigDecimal.ZERO;
        BigDecimal hivDiscountAmount = premiumDetail.getHivDiscount() == null ? BigDecimal.ZERO : underWritingAmount.multiply((premiumDetail.getHivDiscount().divide(new BigDecimal(100))));
        BigDecimal valuedClientDiscountAmount = premiumDetail.getValuedClientDiscount() == null ? BigDecimal.ZERO : underWritingAmount.multiply((premiumDetail.getValuedClientDiscount().divide(new BigDecimal(100))));
        BigDecimal longTermDiscountAmount = premiumDetail.getLongTermDiscount() == null ? BigDecimal.ZERO : underWritingAmount.multiply((premiumDetail.getLongTermDiscount().divide(new BigDecimal(100))));
        BigDecimal addOnBenefitAmount = premiumDetail.getAddOnBenefit() == null ? BigDecimal.ZERO : underWritingAmount.multiply((premiumDetail.getAddOnBenefit().divide(new BigDecimal(100))));
        BigDecimal profitAndSolvencyAmount = premiumDetail.getProfitAndSolvency() == null ? BigDecimal.ZERO : underWritingAmount.multiply((premiumDetail.getProfitAndSolvency().divide(new BigDecimal(100))));
        BigDecimal industryLoadingFactor = BigDecimal.ONE;
        if (industry!=null) {
            industryLoadingFactor = industry.getLoadingFactor();
        }
        BigDecimal totalLoadingAmount = (addOnBenefitAmount.add(profitAndSolvencyAmount)).subtract((hivDiscountAmount.add(valuedClientDiscountAmount).add(longTermDiscountAmount)));
        BigDecimal totalInsuredPremiumAmount = underWritingAmount.add(totalLoadingAmount);
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.multiply(industryLoadingFactor);
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.setScale(2, BigDecimal.ROUND_CEILING);
        return totalInsuredPremiumAmount;
    }


    /*
    * @TODO change according to type
    * */
    public BigDecimal getTotalBasicPremiumForInsured() {
        BigDecimal totalBasicAnnualPremium = BigDecimal.ZERO;
        if (this.endorsementType.equals(GLEndorsementType.NEW_CATEGORY_RELATION)) {
            for (Insured insured : this.endorsement.getNewCategoryRelationEndorsement().getInsureds()) {
                totalBasicAnnualPremium = totalBasicAnnualPremium.add(insured.getBasicAnnualPremium());
            }
        }
        else if (this.endorsementType.equals(GLEndorsementType.ASSURED_MEMBER_ADDITION)) {
            for (Insured insured : this.endorsement.getMemberEndorsement().getInsureds()) {
                totalBasicAnnualPremium = totalBasicAnnualPremium.add(insured.getBasicAnnualPremium());
            }
        }
        else if (this.endorsementType.equals(GLEndorsementType.ASSURED_MEMBER_DELETION)) {
            for (Insured insured : this.endorsement.getMemberDeletionEndorsements().getInsureds()) {
                totalBasicAnnualPremium = totalBasicAnnualPremium.add(insured.getBasicAnnualPremium());
            }
        }
        else if (this.endorsementType.equals(GLEndorsementType.MEMBER_PROMOTION)) {
            for (Insured insured : this.endorsement.getPremiumEndorsement().getInsureds()) {
                totalBasicAnnualPremium = totalBasicAnnualPremium.add(insured.getBasicAnnualPremium());
            }
        }
        return totalBasicAnnualPremium;
    }

    public GroupLifeEndorsement updateWithPremiumDetail(PremiumDetail premiumDetail) {
        this.premiumDetail = premiumDetail;
        return this;
    }

    public BigDecimal getNetAnnualPremiumPaymentAmountWithoutDiscount(PremiumDetail premiumDetail) {
        BigDecimal totalInsuredPremiumAmount = this.getTotalBasicPremiumForInsured();
        BigDecimal addOnBenefitAmount = premiumDetail.getAddOnBenefit() == null ? BigDecimal.ZERO : totalInsuredPremiumAmount.multiply((premiumDetail.getAddOnBenefit().divide(new BigDecimal(100))));
        BigDecimal profitAndSolvencyAmount = premiumDetail.getProfitAndSolvency() == null ? BigDecimal.ZERO : totalInsuredPremiumAmount.multiply((premiumDetail.getProfitAndSolvency().divide(new BigDecimal(100))));
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.add((addOnBenefitAmount.add(profitAndSolvencyAmount)));
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.setScale(2, BigDecimal.ROUND_CEILING);
        return totalInsuredPremiumAmount;
    }

    public Integer getTotalNoOfLifeCovered() {
        Set<Insured> insureds  = Sets.newLinkedHashSet();
        if (this.endorsementType.equals(GLEndorsementType.ASSURED_MEMBER_ADDITION)){
            insureds = this.getEndorsement().getMemberEndorsement().getInsureds();
        }
        if (this.endorsementType.equals(GLEndorsementType.ASSURED_MEMBER_DELETION)){
            insureds = this.getEndorsement().getMemberDeletionEndorsements().getInsureds();
        }
        if (this.endorsementType.equals(GLEndorsementType.NEW_CATEGORY_RELATION)){
            insureds = this.getEndorsement().getNewCategoryRelationEndorsement().getInsureds();
        }
        if (this.endorsementType.equals(GLEndorsementType.MEMBER_PROMOTION)){
            insureds = this.getEndorsement().getPremiumEndorsement().getInsureds();
        }
        Integer totalNoOfLifeCovered = 0;
        totalNoOfLifeCovered = insureds.parallelStream().mapToInt(new ToIntFunction<Insured>() {
            @Override
            public int applyAsInt(Insured value) {
                return value.getNoOfAssured()!=null?value.getNoOfAssured():value.getCategory()!=null?1:0;
            }
        }).sum();
        for (Insured insured : insureds){
            if (isNotEmpty(insured.getInsuredDependents())) {
                Integer dependentSize = insured.getInsuredDependents().parallelStream().mapToInt(new ToIntFunction<InsuredDependent>() {
                    @Override
                    public int applyAsInt(InsuredDependent value) {
                        return value.getNoOfAssured()!=null?value.getNoOfAssured():value.getCategory()!=null?1:0;
                    }
                }).sum();
                totalNoOfLifeCovered = totalNoOfLifeCovered + dependentSize;
            }
        }
        return totalNoOfLifeCovered;
    }


    public BigDecimal getTotalSumAssured() {
        Set<Insured> insureds  = Sets.newLinkedHashSet();
        if (this.endorsementType.equals(GLEndorsementType.ASSURED_MEMBER_ADDITION)){
            insureds = this.getEndorsement().getMemberEndorsement().getInsureds();
        }
        if (this.endorsementType.equals(GLEndorsementType.ASSURED_MEMBER_DELETION)){
            insureds = this.getEndorsement().getMemberDeletionEndorsements().getInsureds();
        }
        if (this.endorsementType.equals(GLEndorsementType.NEW_CATEGORY_RELATION)){
            insureds = this.getEndorsement().getNewCategoryRelationEndorsement().getInsureds();
        }
        if (this.endorsementType.equals(GLEndorsementType.MEMBER_PROMOTION)){
            insureds = this.getEndorsement().getPremiumEndorsement().getInsureds();
        }
        BigDecimal totalSumAssured = BigDecimal.ZERO;
        if (isNotEmpty(insureds)) {
            for (Insured insured : insureds) {
                PlanPremiumDetail insuredPlanPremiumDetail = insured.getPlanPremiumDetail();
                if (insuredPlanPremiumDetail!=null)
                    totalSumAssured = totalSumAssured.add(insuredPlanPremiumDetail.getSumAssured());
                if (isNotEmpty(insured.getInsuredDependents())) {
                    for (InsuredDependent insuredDependent : insured.getInsuredDependents()) {
                        totalSumAssured = totalSumAssured.add(insuredDependent.getPlanPremiumDetail().getSumAssured());
                    }
                }
            }
        }
        return totalSumAssured;
    }


}
