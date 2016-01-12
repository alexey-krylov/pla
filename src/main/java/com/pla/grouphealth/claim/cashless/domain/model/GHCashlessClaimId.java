package com.pla.grouphealth.claim.cashless.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */
@Getter
@ValueObject
@EqualsAndHashCode(of = "ghCashlessClaimId")
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class GHCashlessClaimId implements Serializable{
    private String ghCashlessClaimId;
}
