package com.pla.sharedkernel.identifier;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author: pradyumna
 * @since 1.0 11/03/2015
 */
@EqualsAndHashCode(of = "planId")
@Embeddable
@NoArgsConstructor
@Getter
@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
public class PlanId implements Serializable {

    private String planId;

    public PlanId(String s) {
        this.planId = s;
    }

    public String toString() {
        return planId;
    }
}
