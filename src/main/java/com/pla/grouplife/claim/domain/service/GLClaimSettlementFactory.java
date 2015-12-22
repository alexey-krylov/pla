package com.pla.grouplife.claim.domain.service;

import com.pla.grouplife.claim.application.command.GLClaimSettlementCommand;
import com.pla.grouplife.claim.application.service.GLClaimService;
import com.pla.grouplife.claim.domain.model.ClaimStatus;
import com.pla.grouplife.claim.domain.model.GLClaimSettlement;
import com.pla.grouplife.claim.domain.model.PaymentMode;
import com.pla.grouplife.claim.query.GLClaimFinder;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.sharedkernel.domain.model.ClaimId;
import com.pla.sharedkernel.domain.model.ClaimNumber;
import com.pla.sharedkernel.domain.model.ClaimSettlementId;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Created by nthdimensioncompany on 21/12/2015.
 */


@Component
public class GLClaimSettlementFactory {

    private GLFinder glFinder;
    private SequenceGenerator sequenceGenerator;
    private ClaimNumberGenerator claimNumberGenerator;
    private IIdGenerator idGenerator;
    private GLClaimFinder glClaimFinder;

    @Autowired
    private GLClaimService glClaimService;
    @Autowired
    public GLClaimSettlementFactory(GLFinder glFinder,GLClaimFinder glClaimFinder,IIdGenerator idGenerator){
        this.glFinder=glFinder;
        this.glClaimFinder=glClaimFinder;
        this.idGenerator=idGenerator;
    }


    public GLClaimSettlement createSettlement(GLClaimSettlementCommand glClaimSettlementCommand){
          ClaimSettlementId claimSettlementId  = new ClaimSettlementId(ObjectId.get().toString());
          ClaimNumber claimNumber=new ClaimNumber(glClaimSettlementCommand.getClaimNumber());
          ClaimId claimId=new ClaimId(glClaimSettlementCommand.getClaimId());
          BigDecimal approvedAmount=glClaimSettlementCommand.getApprovedAmount();
          BigDecimal paidAmount=glClaimSettlementCommand.getPaidAmount();
          DateTime approvedDate=glClaimSettlementCommand.getClaimApprovedOn();
          PaymentMode paymentMode=glClaimSettlementCommand.getPaymentMode();
          DateTime paymentDate=glClaimSettlementCommand.getPaymentDate();
          String bankName=glClaimSettlementCommand.getBankName();
          String bankBranchName=glClaimSettlementCommand.getBankBranchName();
          String accountType=glClaimSettlementCommand.getAccountType();
          String accountNumber=glClaimSettlementCommand.getAccountNumber();
          String instrumentNumber=glClaimSettlementCommand.getInstrumentNumber();
           DateTime instrumentDate=glClaimSettlementCommand.getInstrumentDate();
          BigDecimal debitAmount=glClaimSettlementCommand.getApprovedAmount();
          ClaimStatus claimStatus=glClaimSettlementCommand.getClaimStatus();
          GLClaimSettlement glClaimSettlement=new GLClaimSettlement(claimSettlementId,claimId,claimNumber);
          glClaimSettlement.withClaimStatus(claimStatus);
          glClaimSettlement.withApprovedDate(approvedDate);
          glClaimSettlement.withApproveAmountAndDebitNote( approvedAmount);
          glClaimSettlement. withBankDetail(bankName,bankBranchName,accountType,accountNumber);
          glClaimSettlement.withInstrumentNumberAndDate(instrumentNumber,instrumentDate);
          glClaimSettlement.withClaimPaidAmount(paidAmount);

        return glClaimSettlement;
    }
}
