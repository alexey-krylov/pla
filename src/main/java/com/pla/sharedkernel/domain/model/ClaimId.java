package com.pla.sharedkernel.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by Admin on 9/22/2015.
 */
@Getter
@EqualsAndHashCode(of = "claimId")
@Embeddable
@ValueObject
@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
public class ClaimId  implements Serializable{

    private String claimId;

    public ClaimId(String claimId) {
        this.claimId = claimId;
    }

   @Override
    public String toString() {
        return this.claimId;
    }

}
