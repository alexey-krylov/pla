package com.pla.sharedkernel.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;

/**
 * Created by Admin on 9/22/2015.
 */
@Getter
@ValueObject
@EqualsAndHashCode(of = "claimId")
@Embeddable
public class ClaimId {

    private String claimId;

    public ClaimId(String claimId) {
        this.claimId = claimId;
    }

    @Override
    public String toString() {
        return claimId;
    }
}
