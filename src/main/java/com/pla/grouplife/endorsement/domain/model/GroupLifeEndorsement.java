package com.pla.grouplife.endorsement.domain.model;

import com.google.common.collect.Lists;
import com.pla.grouplife.endorsement.domain.event.GLEndorsementStatusAuditEvent;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.model.vo.GLProposerDocument;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.PremiumDetail;
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
import java.util.Set;

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
        return this;
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
            this.endorsement.setMemberDeletionEndorsements(Lists.newArrayList(insureds));
        return this;
    }

    @Override
    public EndorsementId getIdentifier() {
        return endorsementId;
    }




    public BigDecimal getNetAnnualPremiumPaymentAmount(PremiumDetail premiumDetail) {
        BigDecimal totalBasicPremium = this.getTotalBasicPremiumForInsured();
        BigDecimal hivDiscountAmount = premiumDetail.getHivDiscount() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getHivDiscount().divide(new BigDecimal(100))));
        BigDecimal valuedClientDiscountAmount = premiumDetail.getValuedClientDiscount() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getValuedClientDiscount().divide(new BigDecimal(100))));
        BigDecimal longTermDiscountAmount = premiumDetail.getLongTermDiscount() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getLongTermDiscount().divide(new BigDecimal(100))));
        BigDecimal addOnBenefitAmount = premiumDetail.getAddOnBenefit() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getAddOnBenefit().divide(new BigDecimal(100))));
        BigDecimal profitAndSolvencyAmount = premiumDetail.getProfitAndSolvency() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getProfitAndSolvency().divide(new BigDecimal(100))));
        BigDecimal industryLoadingFactor = BigDecimal.ZERO;
        BigDecimal totalLoadingAmount = (addOnBenefitAmount.add(profitAndSolvencyAmount).add(industryLoadingFactor)).subtract((hivDiscountAmount.add(valuedClientDiscountAmount).add(longTermDiscountAmount)));
        BigDecimal totalInsuredPremiumAmount = totalBasicPremium.add(totalLoadingAmount);
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
            for (Insured insured : this.endorsement.getMemberDeletionEndorsements()) {
                totalBasicAnnualPremium = totalBasicAnnualPremium.add(insured.getBasicAnnualPremium());
            }
        }
        return totalBasicAnnualPremium;
    }

    public GroupLifeEndorsement updateWithPremiumDetail(PremiumDetail premiumDetail) {
        this.premiumDetail = premiumDetail;
        return this;
    }



}
