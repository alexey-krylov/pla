package com.pla.grouplife.claim.presentation.dto;

import com.pla.grouplife.claim.domain.model.ClaimantDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * Created by nthdimensioncompany on 23/12/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ClaimantDetailDto {

    private String schemeName;
    private String policyNumber;
    private ClaimantDetail claimantDetail;
    private Set<String> categorySet;
    boolean IsAssuredDetailsShared;
}
