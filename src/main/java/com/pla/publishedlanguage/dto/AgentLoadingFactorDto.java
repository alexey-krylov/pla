package com.pla.publishedlanguage.dto;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * Created by Samir on 6/23/2015.
 */
@Getter
public class AgentLoadingFactorDto {

    private int age;

    private BigDecimal loadingFactor = BigDecimal.ZERO;

    public AgentLoadingFactorDto(int age, BigDecimal loadingFactor) {
        this.age = age;
        this.loadingFactor = loadingFactor;
    }
}
