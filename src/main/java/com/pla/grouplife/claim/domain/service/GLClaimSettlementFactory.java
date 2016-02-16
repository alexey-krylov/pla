package com.pla.grouplife.claim.domain.service;

import com.pla.grouplife.claim.application.command.GLClaimSettlementCommand;
import com.pla.grouplife.claim.application.service.GLClaimService;
import com.pla.grouplife.claim.domain.model.ClaimStatus;
import com.pla.grouplife.claim.domain.model.GLClaimSettlement;
import com.pla.grouplife.claim.presentation.dto.GLClaimSettlementDataDto;
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


    public GLClaimSettlement createSettlement(GLClaimSettlementCommand glClaimSettlementCommand,ClaimNumber claimNumber){
          ClaimSettlementId claimSettlementId  = new ClaimSettlementId(ObjectId.get().toString());
        GLClaimSettlementDataDto claimSettlementDetails=glClaimSettlementCommand.getClaimSettlementDetails();
            ClaimId claimId=new ClaimId(glClaimSettlementCommand.getClaimId());
            BigDecimal approvedAmount=claimSettlementDetails.getApprovedAmount();
            BigDecimal paidAmount=claimSettlementDetails.getPaidAmount();
            DateTime approvedDate=claimSettlementDetails.getClaimApprovedOn();
            String paymentMode=claimSettlementDetails.getPaymentMode();
            DateTime paymentDate=claimSettlementDetails.getPaymentDate();
            String bankName=claimSettlementDetails.getBankName();
            String bankBranchName=claimSettlementDetails.getBankBranchName();
            String accountType=claimSettlementDetails.getAccountType();
            String accountNumber=claimSettlementDetails.getAccountNumber();
            String instrumentNumber=claimSettlementDetails.getInstrumentNumber();
            DateTime instrumentDate=claimSettlementDetails.getInstrumentDate();
            BigDecimal debitAmount=claimSettlementDetails.getApprovedAmount();


          ClaimStatus claimStatus=glClaimSettlementCommand.getClaimStatus();
          GLClaimSettlement glClaimSettlement=new GLClaimSettlement(claimSettlementId,claimId,claimNumber);
          glClaimSettlement.withClaimStatus(claimStatus);
          glClaimSettlement.withApprovedDate(approvedDate);
          glClaimSettlement.withApproveAmountAndDebitNote( approvedAmount);
          glClaimSettlement. withBankDetail(bankName,bankBranchName,accountType,accountNumber);
          glClaimSettlement.withInstrumentNumberAndDate(instrumentNumber,instrumentDate);
          glClaimSettlement.withClaimPaidAmount(paidAmount);
          glClaimSettlement.withPaymentDate(paymentDate);
          glClaimSettlement.withPaymentMode(paymentMode);
        return glClaimSettlement;
    }
}
