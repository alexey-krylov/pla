package com.pla.grouplife.claim.domain.model;

import com.pla.sharedkernel.identifier.CoverageId;
import lombok.*;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.math.BigDecimal;

/**
 * Created by ak on 31/12/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
@AllArgsConstructor
public class CoverageDetail {

    private String coverageCode;

    private CoverageId coverageId;

    private String coverageName;

    private BigDecimal sumAssured;

    private BigDecimal premium;
}
