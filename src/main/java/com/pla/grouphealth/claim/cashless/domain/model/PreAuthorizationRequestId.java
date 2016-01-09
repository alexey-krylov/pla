package com.pla.grouphealth.claim.cashless.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.persistence.Embeddable;

/**
 * Created by Mohan Sharma on 1/9/2016.
 */
@EqualsAndHashCode(of = "preAuthorizationRequestId")
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
public class PreAuthorizationRequestId {
    private String preAuthorizationRequestId;
}
