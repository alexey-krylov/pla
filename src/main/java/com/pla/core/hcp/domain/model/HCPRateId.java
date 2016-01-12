package com.pla.core.hcp.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Author - Mohan Sharma Created on 12/21/2015.
 */
@Getter
@ValueObject
@EqualsAndHashCode(of = "hcpRateId")
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class HCPRateId implements Serializable {
    private String hcpRateId;
}
