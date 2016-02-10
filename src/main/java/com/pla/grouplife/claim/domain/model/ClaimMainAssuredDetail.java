package com.pla.grouplife.claim.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by nthdimensioncompany on 20/1/2016.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ClaimMainAssuredDetail {

    private String fullName;

    private String relationship;

    private String nrcNumber;

    private String manNumber;

    private BigDecimal lastSalary;
}
