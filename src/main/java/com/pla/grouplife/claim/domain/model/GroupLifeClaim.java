package com.pla.grouplife.claim.domain.model;

import com.pla.grouplife.claim.domain.event.GLClaimStatusAuditEvent;
import com.pla.grouplife.claim.domain.event.GLClaimSubmitEvent;
import com.pla.grouplife.sharedresource.model.vo.CoveragePremiumDetail;
import com.pla.grouplife.sharedresource.model.vo.PlanPremiumDetail;
import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.sharedkernel.domain.model.ClaimId;
import com.pla.sharedkernel.domain.model.ClaimNumber;
import com.pla.sharedkernel.domain.model.ClaimType;
import com.pla.sharedkernel.domain.model.Policy;
import lombok.*;
import org.axonframework.domain.AbstractAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Set;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;
import static com.google.common.base.Preconditions.checkArgument;
/**
 * Created by Mirror on 8/19/2015.
 */
@Document(collection = "group_life_claim")
@Getter
@Setter(value = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "claimId")
public class GroupLifeClaim  extends AbstractAggregateRoot<ClaimId> {

    @Id
    @AggregateIdentifier
    private ClaimId claimId;

    private ClaimNumber claimNumber;

    private ClaimType claimType;

    private Policy policy;

    private Proposer proposer;

    private AssuredDetail assuredDetail;

    private PlanPremiumDetail planPremiumDetail;

    private Set<CoveragePremiumDetail> coveragePremiumDetails;

    private BankDetails bankDetails;

    private DateTime incidenceDate ;

    private DateTime intimationDate;

    private ClaimStatus claimStatus;

    private Set<GLClaimDocument> claimDocuments;

    private ClaimRegistration deathClaimRegistration;

    private ClaimRegistration funeralClaimRegistration;

    private DisabilityClaimRegistration disabilityClaimRegistration;

    private DateTime submittedOn;

    private boolean earlyDeathClaim;

    private boolean lateClaim;

    private BigDecimal reserveAmount;

    private GLClaimSettlementData claimSettlementData;

    @Override
    public ClaimId getIdentifier() {
        return this.claimId;
    }

    public GroupLifeClaim(ClaimId claimId, ClaimNumber claimNumber, ClaimType claimType, DateTime intimationDate, ClaimStatus claimStatus) {
        checkArgument(claimId != null, "Claim ID cannot be empty");
        checkArgument(claimNumber != null, "Claim Number cannot be empty");
        checkArgument(claimType != null, "Claim Type cannot be empty");
        this.claimId = claimId;
        this.claimNumber = claimNumber;
        this.claimType = claimType;
        this.intimationDate = intimationDate;
        this.claimStatus = claimStatus;
    }
    public GroupLifeClaim(ClaimId claimId, ClaimNumber claimNumber, Policy policy, ClaimType claimType) {
        checkArgument(claimId != null, "Claim ID cannot be empty");
        checkArgument(claimNumber != null, "Claim Number cannot be empty");
        checkArgument(policy != null, "Policy cannot be empty");
        this.claimId =claimId ;
        this.claimNumber = claimNumber;
        //this.claimStatus = ClaimStatus.EVALUATION;
        this.policy = policy;
        this.claimType = claimType;
    }


    public GroupLifeClaim withProposerAndPolicy(Proposer proposer, Policy policy) {
        this.proposer = proposer;
        this.policy = policy;
        return this;
    }

    public GroupLifeClaim withAssuredDetail(AssuredDetail assuredDetail) {
        this.assuredDetail = assuredDetail;
        return this;
    }

    public GroupLifeClaim withPlanPremiumDetail(PlanPremiumDetail planPremiumDetail) {
        this.planPremiumDetail = planPremiumDetail;
        return this;
    }

    public GroupLifeClaim withCoveragePremiumDetails(Set<CoveragePremiumDetail> coveragePremiumDetails) {
        this.coveragePremiumDetails = coveragePremiumDetails;
        return this;
    }

    public GroupLifeClaim withBankDetails(BankDetails bankDetails) {
        this.bankDetails = bankDetails;
        return this;
    }

    public GroupLifeClaim withClaimDocuments(Set<GLClaimDocument> claimDocuments) {
        this.claimDocuments = claimDocuments;
        return this;
    }

    public GroupLifeClaim withDeathClaimRegistration(ClaimRegistration deathClaimRegistration) {
        this.deathClaimRegistration = deathClaimRegistration;
        this.claimStatus=claimStatus.EVALUATION;
        return this;
    }

    public GroupLifeClaim withFuneralClaimRegistration(ClaimRegistration funeralClaimRegistration) {
        this.funeralClaimRegistration = funeralClaimRegistration;
        this.claimStatus=claimStatus.EVALUATION;
        return this;
    }

    public GroupLifeClaim withDisabilityClaimRegistration(DisabilityClaimRegistration disabilityClaimRegistration) {
        this.disabilityClaimRegistration = disabilityClaimRegistration;
        this.claimStatus=claimStatus.EVALUATION;
        return this;
    }

    public void updateWithReserveAmount(BigDecimal reserveSum){
        this.reserveAmount=reserveSum;

    }

    public GroupLifeClaim withClaimSettlementData(GLClaimSettlementData claimSettlementData){
        this.claimSettlementData=claimSettlementData;
        return this;
    }


    public GroupLifeClaim submitForApproval(DateTime now, String submittedBy, String comment) {
        this.submittedOn = now;
        this.claimStatus = ClaimStatus.ROUTED;
        registerEvent(new GLClaimSubmitEvent(this.getClaimId()));
        if (isNotEmpty(comment)) {
            registerEvent(new GLClaimStatusAuditEvent(this.getClaimId(), ClaimStatus.ROUTED, submittedBy, comment, submittedOn));
        }
        return this;

    }
    public GroupLifeClaim markApproverApproval(String approvedBy, DateTime approvedOn, String comment, ClaimStatus status) {
        this.claimStatus = status;
        if (isNotEmpty(comment)) {
            registerEvent(new GLClaimStatusAuditEvent(this.getClaimId(), status, approvedBy, comment, approvedOn));
        }
        /*
        if (ClaimStatus.APPROVED.equals(this.claimStatus)) {
           // markASDocumentPending(approvedBy, approvedOn, comment);
          markAsWaitingForSettlement(approvedBy, approvedOn, comment);
        }
        */
        return this;
    }
    public GroupLifeClaim markAsWaitingForSettlement(String approvedBy, DateTime approvedOn, String comment) {
        this.claimStatus = claimStatus.AWAITING;
        if (isNotEmpty(comment)) {
            registerEvent(new GLClaimStatusAuditEvent(this.getClaimId(), ClaimStatus.AWAITING, approvedBy, comment, approvedOn));
        }

        return this;
    }

    public  GroupLifeClaim markAsReopen(String approvedBy, DateTime approvedOn, String comment){
        //this.claimStatus=claimStatus.AWAITING;
        return this;
    }
    public GroupLifeClaim returnClaim(ClaimStatus status, String username, String comment) {
        this.claimStatus = status;
        if (isNotEmpty(comment)) {
            registerEvent(new GLClaimStatusAuditEvent(this.getClaimId(), ClaimStatus.REPUDIATED, username, comment, submittedOn));
        }
        return this;
    }

    public GroupLifeClaim markAsCancelledClaim(String approvedBy, DateTime approvedOn, String comment) {
        this.claimStatus = claimStatus.CANCELLED;
        if (isNotEmpty(comment)) {
            registerEvent(new GLClaimStatusAuditEvent(this.getClaimId(), claimStatus.CANCELLED, approvedBy, comment, approvedOn));
        }

        return this;
    }
    public GroupLifeClaim markAsSettledClaim(String approvedBy, DateTime approvedOn, String comment) {
        this.claimStatus = claimStatus.PAID;
        if (isNotEmpty(comment)) {
            registerEvent(new GLClaimStatusAuditEvent(this.getClaimId(), claimStatus.CANCELLED, approvedBy, comment, approvedOn));
        }

        return this;
    }
    public GroupLifeClaim submit(DateTime effectiveDate, ClaimStatus status, String submittedBy) {
        this.claimStatus = status;
       // this.effectiveDate = effectiveDate;
        return this;
    }


    public GroupLifeClaim cancel(DateTime cancelledOn, String cancelledBy) {
        this.claimStatus = ClaimStatus.CANCELLED;
        return this;
    }

    public GroupLifeClaim approving(DateTime approvingOn, String approvingBy) {
        // this.status = ClimStatus.APPROVING;
        return this;
    }

    public GroupLifeClaim approved() {
         this.claimStatus = ClaimStatus.APPROVED;
        return this;
    }

    public GroupLifeClaim repudiated() {
        this.claimStatus = ClaimStatus.REPUDIATED;
        return this;
    }


    public GroupLifeClaim paid() {
        this.claimStatus = ClaimStatus.PAID;
        return this;
    }


}

    /*


   public GroupLifeClaim withClaimRegistration(ClaimRegistration claimRegistration){
        //this.claimRegistration=claimRegistration;
        return this;
    }
    */







