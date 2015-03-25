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
    public String coverageId;
    public String coverageName;
    public String description;
    public String coverageStatus;
    public List<BenefitDto> benefitDtos ;
}
