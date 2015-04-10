package com.pla.core.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 3/24/2015.
 */
@Getter
@Setter
public class CoverageDto {
    private String coverageId;
    private String coverageName;
    private String coverageCode;
    private String description;
    private String coverageStatus;
    private List<Map<String,Object>> benefitDtos ;

    @Override
    public String toString() {
        return "CoverageDto{" +
                "benefitDtos=" + benefitDtos +
                ", coverageStatus='" + coverageStatus + '\'' +
                ", description='" + description + '\'' +
                ", coverageCode='" + coverageCode + '\'' +
                ", coverageName='" + coverageName + '\'' +
                ", coverageId='" + coverageId + '\'' +
                '}';
    }
}
