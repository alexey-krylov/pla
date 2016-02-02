package com.pla.sharedkernel.identifier;

import lombok.*;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.io.Serializable;
@Getter
@Setter
@NoArgsConstructor
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
