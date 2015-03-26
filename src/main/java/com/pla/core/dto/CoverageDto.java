package com.pla.core.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

/**
 * Created by Admin on 3/24/2015.
 */
@Getter
@Setter
public class CoverageDto {
    private String coverageId;
    private String coverageName;
    private String description;
    private String coverageStatus;
    private List<BenefitDto> benefitDtos ;
}
