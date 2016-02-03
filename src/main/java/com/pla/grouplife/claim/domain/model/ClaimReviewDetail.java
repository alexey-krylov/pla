package com.pla.grouplife.claim.domain.model;

import lombok.*;
import org.joda.time.DateTime;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

/**
 * Created by ak on 21/1/2016.
 */

@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class ClaimReviewDetail {

    private String comments;
    private DateTime timings;
    private String userName;
}
