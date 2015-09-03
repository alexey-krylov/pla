package com.pla.core.dto;

import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Admin on 9/3/2015.
 */
@Getter
@Setter
public class ProductClaimTypeDto {
    private String planCode;
    private String planName;
    private LineOfBusinessEnum lineOfBusiness;
    private String lineOfBusinessDescription;
    private List<CoverageClaimTypeDto> coverageClaimType;

}
