package com.pla.core.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by User on 4/6/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommissionTermDto {

    private Integer startYear;

    private Integer endYear;

    private BigDecimal commissionPercentage;
}
