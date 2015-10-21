package com.pla.sharedkernel.identifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by Samir on 8/3/2015.
 */
@Getter
@ValueObject
@EqualsAndHashCode(of = "endorsementId")
@Embeddable
public class EndorsementId implements Serializable {

    private String endorsementId;

    public EndorsementId(String endorsementId) {
        this.endorsementId = endorsementId;
    }

    public String toString() {
        return this.endorsementId;
    }
}
