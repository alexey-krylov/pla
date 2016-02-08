package com.pla.grouphealth.claim.cashless.domain.model.claim;

import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationRequestAssuredDetail;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationRequestCoverageDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Set;

/**
 * Author - Mohan Sharma Created on 2/8/2016.
 */
@NoArgsConstructor
@Getter
public class PreAuthorizationDetail {
    private String preAuthorizationRequestId;
    private String clientId;
    private LocalDate preAuthorizationDate;
    private String policyNumber;
    private String policyName;
    private String planCode;
    private String planName;
    private BigDecimal sumAssured;
    private Set<PreAuthorizationRequestCoverageDetail> coverageDetails;
    private PreAuthorizationRequestAssuredDetail assuredDetail;
}
