package com.pla.grouphealth.claim.cashless.presentation.dto.claim;

import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaimDrugService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 2/4/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class GroupHealthCashlessClaimDrugServiceDto {
    private String type;
    private String serviceName;
    private String drugName;
    private String drugType;
    private String accommodationType;
    private String duration;
    private int lengthOfStay;
    private String strength;
    private String status;
    private BigDecimal billAmount;

    public GroupHealthCashlessClaimDrugServiceDto updateWithDetails(GroupHealthCashlessClaimDrugService groupHealthCashlessClaimDrugService) {
        if(isNotEmpty(groupHealthCashlessClaimDrugService)){
            this.type = groupHealthCashlessClaimDrugService.getType();
            this.serviceName = groupHealthCashlessClaimDrugService.getServiceName();
            this.drugName = groupHealthCashlessClaimDrugService.getDrugName();
            this.drugType = groupHealthCashlessClaimDrugService.getDrugType();
            this.accommodationType = groupHealthCashlessClaimDrugService.getAccommodationType();
            this.duration = groupHealthCashlessClaimDrugService.getDuration();
            this.lengthOfStay = groupHealthCashlessClaimDrugService.getLengthOfStay();
            this.strength = groupHealthCashlessClaimDrugService.getStrength();
            this.billAmount = groupHealthCashlessClaimDrugService.getBillAmount();
            this.status = isNotEmpty(groupHealthCashlessClaimDrugService.getStatus()) ? groupHealthCashlessClaimDrugService.getStatus().name() : "PROCESS";
        }
        return this;
    }
}
