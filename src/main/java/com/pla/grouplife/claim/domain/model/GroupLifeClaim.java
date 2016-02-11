package com.pla.grouplife.claim.domain.model;

import com.pla.grouplife.claim.domain.event.GLClaimStatusAuditEvent;
import com.pla.grouplife.claim.domain.event.GLClaimSubmitEvent;
import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.sharedkernel.domain.model.*;
import lombok.*;
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

    private ClaimNumber amendedNewClaimNumber;

    private String relationship;

    private String category;

    private ClaimType claimType;

    private Policy policy;

    private Proposer proposer;

    private ClaimAssuredDetail assuredDetail;

    private PlanDetail planDetail;

    private Set<CoverageDetail> coverageDetails;

    private BankDetails bankDetails;

    private DateTime incidenceDate ;

    private DateTime intimationDate;

    private ClaimStatus claimStatus;

    private Set<GLClaimDocument> claimDocuments;

    private ClaimRegistration claimRegistration;

    private DisabilityClaimRegistration disabilityClaimRegistration;

    private DateTime submittedOn;

    private boolean isEarlyDeathClaim;

    private boolean isLateClaim;

    private BigDecimal reserveAmount;

    private BigDecimal claimAmount;

    private BigDecimal updatedClaimAmount;

    private BigDecimal recoveredAmount;

    private RoutingLevel taggedRoutingLevel;

    private GLClaimSettlementData claimSettlementData;

    private GlClaimUnderWriterApprovalDetail underWriterReviewDetail;

    @Override
    public ClaimId getIdentifier() {
        return this.claimId;
    }

    public GroupLifeClaim(ClaimId claimId, ClaimNumber claimNumber, ClaimType claimType, DateTime intimationDate) {
        checkArgument(claimId != null, "Claim ID cannot be empty");
        checkArgument(claimNumber != null, "Claim Number cannot be empty");
        checkArgument(claimType != null, "Claim Type cannot be empty");
        this.claimId = claimId;
        this.claimNumber = claimNumber;
        this.claimType = claimType;
        this.intimationDate = intimationDate;

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
    public GroupLifeClaim withIncidenceDate(DateTime incidenceDate){
        this.incidenceDate=incidenceDate;
        return this;
    }
    public GroupLifeClaim withEarlyClaim(boolean isEarlyDeathClaim){
        this.isEarlyDeathClaim=isEarlyDeathClaim;
        return this;
    }
    public GroupLifeClaim withCategoryAndRelationship(String category,String relationship){
        this.category=category;
        this.relationship=relationship;
        return this;
    }
    public GroupLifeClaim withLateClaim(boolean isLateClaim){
        this.isLateClaim=isLateClaim;
        return this;
    }
    public GroupLifeClaim withProposerAndPolicy(Proposer proposer, Policy policy) {
        this.proposer = proposer;
        this.policy = policy;
        return this;
    }

    public GroupLifeClaim withAssuredDetail(ClaimAssuredDetail assuredDetail) {
        this.assuredDetail = assuredDetail;
        return this;
    }

    public GroupLifeClaim withPlanDetail(PlanDetail planDetail) {
        this.planDetail = planDetail;
        return this;
    }

    public GroupLifeClaim withCoverageDetails(Set<CoverageDetail> coverageDetails) {
        this.coverageDetails = coverageDetails;
        return this;
    }

    public GroupLifeClaim withBankDetails(BankDetails bankDetails) {
        this.bankDetails = bankDetails;
        return this;
    }

    public GroupLifeClaim withClaimStatus(ClaimStatus claimStatus) {
        this.claimStatus= claimStatus;
        return this;
    }
    public GroupLifeClaim withClaimDocuments(Set<GLClaimDocument> claimDocuments) {
        this.claimDocuments = claimDocuments;
        return this;
    }

    public GroupLifeClaim withClaimRegistration(ClaimRegistration claimRegistration) {
        this.claimRegistration = claimRegistration;
       // this.claimStatus=claimStatus.EVALUATION;
        return this;
    }

    public GroupLifeClaim withDisabilityClaimRegistration(DisabilityClaimRegistration disabilityClaimRegistration) {
        this.disabilityClaimRegistration = disabilityClaimRegistration;
       // this.claimStatus=claimStatus.EVALUATION;
        return this;
    }

    public void taggedWithRoutingLevel(RoutingLevel routingLevel)
    {
        this.taggedRoutingLevel=routingLevel;
    }
    public void updateWithReserveAmount(BigDecimal reserveSum){
        this.reserveAmount=reserveSum;

    }
    public void updateWithClaimAmount(BigDecimal claimSum){
        this.claimAmount=claimSum;

    }
    public void updateWithRevisedClaimAmount(BigDecimal claimSum){
        this.updatedClaimAmount=claimSum;

    }
    public void updateWithRecoveredAmount(BigDecimal recoveredAmount){
        this.recoveredAmount=recoveredAmount;
    }
    public void updateWithNewClaimNumberForAmendment(ClaimNumber amendedNewClaimNumber){
        this.amendedNewClaimNumber=amendedNewClaimNumber;

    }
    public GroupLifeClaim withClaimSettlementData(GLClaimSettlementData claimSettlementData){
        this.claimSettlementData=claimSettlementData;
        return this;
    }
    public GroupLifeClaim withUnderWriterData(GlClaimUnderWriterApprovalDetail underWriterReviewDetail){
        this.underWriterReviewDetail=underWriterReviewDetail;
        return this;
    }

    public GroupLifeClaim submitForClaimIntimationCreation(DateTime now, String submittedBy) {

        this.submittedOn = now;
        this.claimStatus = ClaimStatus.INTIMATION;
        registerEvent(new GLClaimSubmitEvent(this.getClaimId()));
        if (isNotEmpty(submittedBy)) {
            registerEvent(new GLClaimStatusAuditEvent(this.getClaimId(), ClaimStatus.INTIMATION, submittedBy, null, submittedOn));
        }
        return this;
    }

  public  GroupLifeClaim createClaimRegistrationRecord(DateTime now,String submittedBy, String comment){
      //comment="evaluated";
      this.submittedOn = now;
      this.claimStatus = ClaimStatus.EVALUATION;
      registerEvent(new GLClaimSubmitEvent(this.getClaimId()));
      if (isNotEmpty(submittedBy)) {
          registerEvent(new GLClaimStatusAuditEvent(this.getClaimId(), ClaimStatus.EVALUATION, submittedBy, comment, submittedOn));
      }
       return this;
   }
    public GroupLifeClaim submitForApproval(DateTime now, String submittedBy, String comment) {
        this.submittedOn = now;
        this.claimStatus = ClaimStatus.UNDERWRITING;
        registerEvent(new GLClaimSubmitEvent(this.getClaimId()));
        if (isNotEmpty(submittedBy)) {
            registerEvent(new GLClaimStatusAuditEvent(this.getClaimId(), ClaimStatus.UNDERWRITING, submittedBy, comment, submittedOn));
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
        this.claimStatus = claimStatus.AWAITING_DISBURSEMENT;
        if (isNotEmpty(comment)) {
            registerEvent(new GLClaimStatusAuditEvent(this.getClaimId(), ClaimStatus.AWAITING_DISBURSEMENT, approvedBy, comment, approvedOn));
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
        this.claimStatus = claimStatus.PAID_DISBURSED;
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
        this.claimStatus = ClaimStatus.PAID_DISBURSED;
        return this;
    }


}

    /*


   public GroupLifeClaim withClaimRegistration(ClaimRegistration claimRegistration){
        //this.claimRegistration=claimRegistration;
        return this;
    }
    */







