package com.pla.grouphealth.claim.cashless.domain.model.preauthorization;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Author - Mohan Sharma Created on 1/9/2016.
 */
@EqualsAndHashCode(of = "preAuthorizationRequestId")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
public class PreAuthorizationRequestId implements Serializable {
    private String preAuthorizationRequestId;
}