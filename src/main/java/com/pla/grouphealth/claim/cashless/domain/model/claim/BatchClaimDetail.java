package com.pla.grouphealth.claim.cashless.domain.model.claim;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

/**
 * Author - Mohan Sharma Created on 2/24/2016.
 */
@ValueObject
@Immutable
@Embeddable
@EqualsAndHashCode
@Getter
@Setter
public class BatchClaimDetail {
    private String claimNumber;
    private String assuredName;
    private BigDecimal billedAmount;
    private BigDecimal agreedRate;
    private BigDecimal approvedAmount;
}
