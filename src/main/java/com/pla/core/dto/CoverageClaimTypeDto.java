package com.pla.core.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Admin on 9/2/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class CoverageClaimTypeDto {
    private String coverageId;
    private String coverageName;
    private Long coverageClaimId;
    private Set<String> claimTypes;
    private List<Map<String,Object>> claimTypeMap;

    public CoverageClaimTypeDto(String coverageId,String coverageName,Set<String> claimTypes) {
        this.coverageId = coverageId;
        this.coverageName = coverageName;
        this.claimTypes = claimTypes;
    }

}
