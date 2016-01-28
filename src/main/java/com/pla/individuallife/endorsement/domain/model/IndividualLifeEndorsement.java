package com.pla.individuallife.endorsement.domain.model;

import com.google.common.collect.Sets;
import com.pla.individuallife.endorsement.domain.event.ILEndorsementStatusAuditEvent;
import com.pla.individuallife.policy.presentation.dto.ILPolicyDto;
import com.pla.individuallife.sharedresource.model.ILEndorsementType;
import com.pla.individuallife.sharedresource.model.vo.ILProposerDocument;
import com.pla.individuallife.sharedresource.model.vo.PremiumDetail;
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
import org.hibernate.annotations.Type;
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
 * Created by Raghu on 8/3/2015.
 */
@Document(collection = "individual_life_endorsement")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class IndividualLifeEndorsement extends AbstractAggregateRoot<String> {

    @Id
    @AggregateIdentifier
    private EndorsementId endorsementId;

    /*
    * Endorsement Request number
    *
    * */
    private String endorsementRequestNumber;

    /*
    * Endorsement number
    *
    * */
    private String endorsementNumber;

    private ILEndorsementType endorsementType;

    private EndorsementStatus status;

    private ILEndorsement endorsement;

    private Policy policy;

    private DateTime effectiveDate;

    private Set<ILProposerDocument> proposerDocuments;

    private DateTime submittedOn;

    private PremiumDetail premiumDetail;

    private ILPolicyDto ilPolicyDto;

    public IndividualLifeEndorsement(EndorsementId endorsementId, String endorsementRequestNumber, ILPolicyDto ilPolicyDto, ILEndorsementType endorsementType, DateTime effectiveDate) {
        checkArgument(endorsementId != null, "Endorsement Id cannot be empty");
        checkArgument(endorsementRequestNumber != null, "Endorsement Request Number cannot be empty");
        checkArgument(ilPolicyDto != null, "Policy cannot be empty");
        checkArgument(endorsementType != null, "Endorsement Type cannot be empty");
        this.endorsementId = endorsementId;
        this.endorsementRequestNumber = endorsementRequestNumber;
        this.status = EndorsementStatus.DRAFT;
        //this.policy = policy;
        this.ilPolicyDto = ilPolicyDto;
        this.endorsementType = endorsementType;
        this.effectiveDate = effectiveDate;
    }

    public IndividualLifeEndorsement updateWithEndorsementDetail(ILEndorsement endorsement) {
        this.endorsement = endorsement;
        return this;
    }

    public IndividualLifeEndorsement updateWithDocuments(Set<ILProposerDocument> proposerDocuments) {
        this.proposerDocuments = proposerDocuments;
        return this;
    }

    public IndividualLifeEndorsement submit(DateTime effectiveDate, EndorsementStatus status, String submittedBy) {
        this.status = status;
        this.effectiveDate = effectiveDate;
        return this;
    }

    public IndividualLifeEndorsement cancel(DateTime cancelledOn, String cancelledBy) {
        this.status = EndorsementStatus.CANCELLED;
        return this;
    }

    public IndividualLifeEndorsement reject(DateTime rejectedOn, String rejectedBy) {
        this.status = EndorsementStatus.REJECTED;
        return this;
    }

    public IndividualLifeEndorsement putOnHold(DateTime holdOn, String holdBy) {
        this.status = EndorsementStatus.HOLD;
        return this;
    }

    public IndividualLifeEndorsement paymentPending() {
        this.status = EndorsementStatus.PAYMENT_PENDING;
        return this;
    }

    public IndividualLifeEndorsement paymentReceived() {
        this.status = EndorsementStatus.PAYMENT_RECEIVED;
        return this;
    }

    public IndividualLifeEndorsement refundPending() {
        this.status = EndorsementStatus.REFUND_PENDING;
        return this;
    }

    public IndividualLifeEndorsement refundProcessed() {
        this.status = EndorsementStatus.REFUND_PROCESSED;
        return this;
    }

    public IndividualLifeEndorsement submitForApproval(DateTime now, String username, String comment) {
        this.submittedOn = now;
        this.status = EndorsementStatus.APPROVER_PENDING_ACCEPTANCE;
        if (isNotEmpty(comment)) {
            registerEvent(new ILEndorsementStatusAuditEvent(this.getEndorsementId(), EndorsementStatus.APPROVER_PENDING_ACCEPTANCE, username, comment, submittedOn));
        }
        return this;
    }

    public IndividualLifeEndorsement returnEndorsement(EndorsementStatus status, String username, String comment) {
        this.status = status;
        if (isNotEmpty(comment)) {
            registerEvent(new ILEndorsementStatusAuditEvent(this.getEndorsementId(), EndorsementStatus.RETURN, username, comment, submittedOn));
        }
        return this;
    }

    public IndividualLifeEndorsement approve(DateTime now , String username, String comment, String endorsementUniqueNumber) {
        this.effectiveDate = now;
        this.endorsementNumber= endorsementUniqueNumber;
        this.status = EndorsementStatus.APPROVED;
        if (isNotEmpty(comment)) {
            registerEvent(new ILEndorsementStatusAuditEvent(this.getEndorsementId(), EndorsementStatus.APPROVED, username, comment, submittedOn));
        }
        //raiseEventIfTypeIsMemberDeletion();
        return this;
    }

    @Override
    public String getIdentifier() {
        return endorsementId.getEndorsementId();
    }

     public IndividualLifeEndorsement updateWithPremiumDetail(PremiumDetail premiumDetail) {
        this.premiumDetail = premiumDetail;
        return this;
    }
}
