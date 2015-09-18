package com.pla.core.dto;

import com.google.common.collect.Maps;
import com.pla.sharedkernel.domain.model.ClaimType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    public CoverageClaimTypeDto(String coverageId, String coverageName, Set<String> claimTypes) {
        this.coverageId = coverageId;
        this.coverageName = coverageName;
        this.claimTypes = claimTypes;

    }

   public CoverageClaimTypeDto updateWithClaimTypeMap(Set<String>claimTypes){
        this.claimTypeMap=getClaimTypeMap(claimTypes);
       return this;
   }

    public List<Map<String, Object>> getClaimTypeMap(Set<String>claimTypes) {
        List<ClaimType> claimTypeList=new ArrayList<ClaimType>();
        for(String s:claimTypes) {
            claimTypeList.add(ClaimType.valueOf(s));
        }
        return claimTypeList.parallelStream().map(new Function<ClaimType, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(ClaimType claimType) {
                Map<String, Object> claimTypeMap = Maps.newLinkedHashMap();
                claimTypeMap.put("claimType", claimType.name());
                claimTypeMap.put("description", claimType.toString());
                return claimTypeMap;
            }
        }).collect(Collectors.toList());

    }
    }





