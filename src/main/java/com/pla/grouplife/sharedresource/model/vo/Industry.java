package com.pla.grouplife.sharedresource.model.vo;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * Created by Samir on 6/30/2015.
 */
@Getter
public class Industry {

    private String industryId;

    private String industryName;

    private BigDecimal loadingFactor;

    public Industry(String industryId, String industryName, BigDecimal loadingFactor) {
        this.industryId = industryId;
        this.industryName = industryName;
        this.loadingFactor = loadingFactor;
    }

}
