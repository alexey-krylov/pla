package com.pla.core.dto;

import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by Admin on 9/3/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProductClaimTypeDto {
    private String planCode;
    private String planName;
    private String productClaimId;
    private String planId;
    private LineOfBusinessEnum lineOfBusiness;
    private String lineOfBusinessDescription;
    private List<CoverageClaimTypeDto> coverageClaimType;

    public ProductClaimTypeDto(String productClaimId,String planCode,String planId,String planName,LineOfBusinessEnum lineOfBusiness,String lineOfBusinessDescription){
        this.productClaimId = productClaimId;
        this.planCode = planCode;
        this.planName = planName;
        this.planId = planId;
        this.lineOfBusiness = lineOfBusiness;
        this.lineOfBusinessDescription = lineOfBusinessDescription;
    }

}
