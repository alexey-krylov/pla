package com.pla.grouplife.quotation.query;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Samir on 6/2/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class PremiumInstallmentDto {

    private Integer installmentNo;

    private BigDecimal installmentAmount;


    public PremiumInstallmentDto(int installmentNo, BigDecimal installmentAmount) {
        this.installmentNo = installmentNo;
        this.installmentAmount = installmentAmount;
    }
}
