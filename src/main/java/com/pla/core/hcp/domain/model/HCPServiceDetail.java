package com.pla.core.hcp.domain.model;

import lombok.*;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;

/**
 * Created by Mohan Sharma on 12/21/2015.
 */
@ValueObject
@NoArgsConstructor
@Getter
@Setter(value = AccessLevel.PACKAGE)
@EqualsAndHashCode
public class HCPServiceDetail {
    private String serviceDepartment;
    private String serviceAvailed;
    private BigDecimal normalAmount;
    private int afterHours;

    public HCPServiceDetail updateWithServiceDepartment(String serviceDepartment){
        this.serviceDepartment = serviceDepartment;
        return this;
    }

    public HCPServiceDetail updateWithServiceAvailed(String serviceAvailed){
        this.serviceAvailed = serviceAvailed;
        return this;
    }

    public HCPServiceDetail updateWithNormalAmount(BigDecimal normalAmount){
        this.normalAmount = normalAmount;
        return this;
    }

    public HCPServiceDetail updateWithAfterHours(int afterHours){
        this.afterHours = afterHours;
        return this;
    }
}
