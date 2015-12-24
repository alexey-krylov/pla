package com.pla.core.SBCM.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by Mohan Sharma on 12/24/2015.
 */
@Getter
@ValueObject
@EqualsAndHashCode(of = "serviceBenefitCoverageMappingId")
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ServiceBenefitCoverageMappingId implements Serializable{
    private String serviceBenefitCoverageMappingId;
}
