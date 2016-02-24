package com.pla.grouphealth.claim.cashless.domain.model.claim;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Author - Mohan Sharma Created on 2/24/2016.
 */
@Document(collection = "GROUP_HEALTH_CASHLESS_CLAIM_BATCH_DETAIL")
@NoArgsConstructor
@Getter
public class GroupHealthCashlessClaimBatchDetail {
    @Id
    private String batchNumber;
    private String hcpName;
    private String hcpCode;
    private LocalDate batchDate;
    private LocalDate batchClosedOnDate;
    private LocalDate batchDisbursementDate;
    private List<BatchClaimDetail> batchClaimDetails;
    private GroupHealthCashlessClaimBatchAccountingDetail accountingDetail;
    private GroupHealthCashlessClaimBatchBankDetail groupHealthCashlessClaimBatchBankDetail;
}
