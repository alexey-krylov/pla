package com.pla.sharedkernel.identifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bson.types.ObjectId;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author: pradyumna
 * @since 1.0 11/03/2015
 */
@Embeddable
@EqualsAndHashCode
@Getter
public class PlanId implements Serializable {

    private String planId;

    public PlanId(String s) {
        this.planId = s;
    }

    public PlanId() {
        this.planId = new ObjectId().toString();
    }

    public String toString() {
        return planId;
    }
}
