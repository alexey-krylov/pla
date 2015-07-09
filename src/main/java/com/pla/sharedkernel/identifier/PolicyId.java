package com.pla.sharedkernel.identifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by Samir on 7/7/2015.
 */
@Getter
@ValueObject
@EqualsAndHashCode(of = "policyId")
@Embeddable
public class PolicyId implements Serializable {

    private String policyId;

    public PolicyId(String policyId) {
        this.policyId = policyId;
    }

    @Override
    public String toString() {
        return policyId;
    }
}
