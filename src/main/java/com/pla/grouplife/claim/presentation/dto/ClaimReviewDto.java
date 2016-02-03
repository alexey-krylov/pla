package com.pla.grouplife.claim.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * Created by ak on 21/12/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ClaimReviewDto {

    private String comments;
    private DateTime timings;
    private String userNames;
}
