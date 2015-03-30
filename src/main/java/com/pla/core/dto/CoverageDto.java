package com.pla.core.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Created by Admin on 3/24/2015.
 */
@Getter
@Setter
@ToString
public class CoverageDto {
    private String coverageId;
    private String coverageName;
    private String description;
    private String coverageStatus;
    private List<BenefitDto> benefitDtos ;
}
