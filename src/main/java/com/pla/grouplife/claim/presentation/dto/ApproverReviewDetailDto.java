package com.pla.grouplife.claim.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by nthdimensioncompany on 16/2/2016.
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class ApproverReviewDetailDto {

   private String ClaimId;
    private List<ClaimReviewDto>  claimReviewList;

}
