package com.pla.core.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 3/24/2015.
 */
@Getter
@Setter
@EqualsAndHashCode
public class CoverageDto {
    private String coverageId;
    private String coverageName;
    private String coverageCode;
    private String description;
    private String coverageStatus;
    private List<Map<String,Object>> benefitDtos ;
    private List<String> benefitIds;

    public CoverageDto() {
    }

    public CoverageDto(String coverageId,String coverageName,String coverageCode){
        this.coverageId = coverageId;
        this.coverageName = coverageName;
        this.coverageCode = coverageCode;
    }

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
