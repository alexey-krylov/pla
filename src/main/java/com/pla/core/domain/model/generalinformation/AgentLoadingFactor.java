package com.pla.core.domain.model.generalinformation;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 6/23/2015.
 */
@Getter
@Setter
public class AgentLoadingFactor {

    private int age;
    private BigDecimal loadingFactor;

    public AgentLoadingFactor(int age, BigDecimal loadingFactor) {
        this.age =  age;
        this.loadingFactor = loadingFactor;
    }
}
