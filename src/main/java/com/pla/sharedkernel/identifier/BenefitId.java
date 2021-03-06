/*
 * Copyright (c) 3/3/15 5:52 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.sharedkernel.identifier;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author: Samir
 * @since 1.0 03/03/2015
 */
@Embeddable
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode(of = "benefitId")
@JsonSerialize(using = ToStringSerializer.class)
public class BenefitId implements Serializable {

    private String benefitId;

    public BenefitId(String benefitId) {
        this.benefitId = benefitId;
    }

    public String toString() {
        return benefitId;
    }
}
