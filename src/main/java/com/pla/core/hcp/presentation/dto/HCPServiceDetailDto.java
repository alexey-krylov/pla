package com.pla.core.hcp.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by Mohan Sharma on 12/21/2015.
 */
@Getter
@NoArgsConstructor
public class HCPServiceDetailDto {
    private String serviceDepartment;
    private String serviceAvailed;
    private BigDecimal normalAmount;
    private int afterHours;

    public HCPServiceDetailDto updateWithServiceDepartment(String serviceDepartment){
        this.serviceDepartment = serviceDepartment;
        return this;
    }

    public HCPServiceDetailDto updateWithServiceAvailed(String serviceAvailed){
        this.serviceAvailed = serviceAvailed;
        return this;
    }

    public HCPServiceDetailDto updateWithNormalAmount(BigDecimal normalAmount){
        this.normalAmount = normalAmount;
        return this;
    }

    public HCPServiceDetailDto updateWithAfterHours(int afterHours){
        this.afterHours = afterHours;
        return this;
    }
}
