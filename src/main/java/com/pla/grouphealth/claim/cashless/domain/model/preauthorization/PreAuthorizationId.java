package com.pla.grouphealth.claim.cashless.domain.model.preauthorization;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */
@Getter
@ValueObject
@EqualsAndHashCode(of = "preAuthorizationId")
@Embeddable
@NoArgsConstructor
@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
public class PreAuthorizationId implements Serializable{
    private String preAuthorizationId;
    public PreAuthorizationId(String preAuthorizationId) {
        this.preAuthorizationId = preAuthorizationId;
    }

    @Override
    public String toString() {
        return preAuthorizationId;
    }
}
