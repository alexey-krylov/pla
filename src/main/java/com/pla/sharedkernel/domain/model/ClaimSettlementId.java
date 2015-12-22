package com.pla.sharedkernel.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by ak on 21/12/2015.
 */

@Getter
@EqualsAndHashCode(of = "claimSettlementId")
@Embeddable
@ValueObject
@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)

public class ClaimSettlementId implements Serializable {
    private String claimSettlementId;

    public ClaimSettlementId(String claimSettlementId) {
        this.claimSettlementId = claimSettlementId;
    }

    @Override
    public String toString() {
        return this.claimSettlementId;
    }
}
