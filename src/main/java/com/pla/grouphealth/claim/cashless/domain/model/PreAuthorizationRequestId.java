package com.pla.grouphealth.claim.cashless.domain.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by Mohan Sharma on 1/9/2016.
 */
@EqualsAndHashCode(of = "preAuthorizationRequestId")
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PreAuthorizationRequestId implements Serializable {
    private String preAuthorizationRequestId;
}
