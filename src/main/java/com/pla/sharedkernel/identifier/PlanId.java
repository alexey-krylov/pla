package com.pla.sharedkernel.identifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

/**
 * @author: pradyumna
 * @since 1.0 11/03/2015
 */
@Embeddable
@EqualsAndHashCode
@Getter
@Setter
public class PlanId implements Serializable {

    @Column(name = "plan_id")
    private String planId;

    public PlanId(String planId) {
        this.planId = planId;
    }

    public PlanId() {
        this.planId = UUID.randomUUID().toString();
    }

    public String toString() {
        return planId;
    }
}
