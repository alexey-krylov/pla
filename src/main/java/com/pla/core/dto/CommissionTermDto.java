package com.pla.core.dto;

import com.pla.sharedkernel.domain.model.CommissionTermType;
import lombok.*;

import java.math.BigDecimal;

/**
 * Created by User on 4/6/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"startYear", "endYear", "commissionPercentage", "commissionTermType"})
public class CommissionTermDto {

    CommissionTermType commissionTermType;

    private Integer startYear;

    private Integer endYear;

    private BigDecimal commissionPercentage;
}
