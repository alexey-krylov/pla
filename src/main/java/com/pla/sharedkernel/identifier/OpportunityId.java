package com.pla.sharedkernel.identifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;

/**
 * Created by Samir on 6/22/2015.
 */
@Getter
@ValueObject
@EqualsAndHashCode(of = "opportunityId")
@Embeddable
public class OpportunityId {

    private String opportunityId;

    public OpportunityId(String opportunityId) {
        this.opportunityId = opportunityId;
    }
}
