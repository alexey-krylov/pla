package com.pla.grouplife.claim.domain.model;

import com.pla.sharedkernel.domain.model.ClaimId;
import com.pla.sharedkernel.domain.model.ClaimNumber;
import com.pla.sharedkernel.domain.model.ClaimSettlementId;
import lombok.*;
import org.axonframework.domain.AbstractAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by ak
 */
@Document(collection = "group_life_claim_settlement")
@Getter
@Setter(value = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "claimSettlementId")

public class GLClaimSettlement  extends AbstractAggregateRoot<ClaimSettlementId> {
    @Id
    @AggregateIdentifier

    private ClaimSettlementId claimSettlementId;

    private ClaimId claimId;

    private ClaimNumber claimNumber;

    private DateTime claimApprovedOn;
    private BigDecimal approvedAmount;
    private PaymentMode paymentMode;
    private DateTime paymentDate;
    private BigDecimal paidAmount;
    private String bankName;
    private String bankBranchName;
    private String accountType;
    private String accountNumber;
    private String instrumentNumber;
    private DateTime instrumentDate;
    private BigDecimal debitAmount;
   // private ClaimType claimType;
    private ClaimStatus claimStatus;



    public GLClaimSettlement(ClaimSettlementId claimSettlementId,ClaimId claimId,ClaimNumber claimNumber){
        checkArgument(claimSettlementId!= null, "Claim Type cannot be empty");
        checkArgument(claimId != null, "Claim ID cannot be empty");
        checkArgument(claimNumber != null, "Claim Number cannot be empty");
        this.claimSettlementId=claimSettlementId;
        this.claimId=claimId;
        this.claimNumber=claimNumber;
    }
    public GLClaimSettlement  withApprovedDate(DateTime approvedDate){
        this.claimApprovedOn=approvedDate;
        return this;
    }
    public GLClaimSettlement withApproveAmountAndDebitNote(BigDecimal approvedAmount){
        this.approvedAmount=approvedAmount;
        this.debitAmount=approvedAmount;
        return this;
    }
    public GLClaimSettlement  withBankDetail(String bankName, String bankBranchName, String accountType,String accountNumber ){
        this.bankName=bankName;
        this.bankBranchName=bankBranchName;
        this.accountType=accountType;
        this.accountNumber=accountNumber;
        return this;
    }
    public  GLClaimSettlement withInstrumentNumberAndDate(String instrumentNumber,DateTime instrumentDate){
        this.instrumentNumber=instrumentNumber;
        this.instrumentDate=instrumentDate;
        return this;
    }
    public GLClaimSettlement withClaimStatus(ClaimStatus claimStatus){
        this.claimStatus=claimStatus;
        return this;
    }

    public GLClaimSettlement withClaimPaidAmount(BigDecimal paidAmount){
        this.paidAmount=paidAmount;
        return this;
    }

    @Override
    public ClaimSettlementId getIdentifier() {
        return this.claimSettlementId;
    }

}
