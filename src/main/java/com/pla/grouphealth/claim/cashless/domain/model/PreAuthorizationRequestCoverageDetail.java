package com.pla.grouphealth.claim.cashless.domain.model;

import lombok.*;

import java.math.BigDecimal;

/**
 * Author - Mohan Sharma Created on 1/11/2016.
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class PreAuthorizationRequestCoverageDetail {
    String coverageCode;
    String coverageName;
    BigDecimal sumAssured;
}