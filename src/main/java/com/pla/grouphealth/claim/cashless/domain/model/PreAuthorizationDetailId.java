package com.pla.grouphealth.claim.cashless.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by Mohan Sharma on 12/30/2015.
 */
@Getter
@ValueObject
@EqualsAndHashCode(of = "preAuthorizationDetailId")
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class PreAuthorizationDetailId implements Serializable{
    private String preAuthorizationDetailId;
}
