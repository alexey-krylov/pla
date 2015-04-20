package com.pla.sharedkernel.domain.model;

import com.pla.core.dto.ProductLineProcessDto;
import lombok.Getter;

import java.util.List;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter
public enum PolicyFeeProcessType {
    ANNUAL("Annual"),
    SEMI_ANNUAL("Semi-Annual"),
    QUARTERLY("Quarterly"),
    MONTHLY("Monthly");

    private String description;
    private String fullDescription;

    PolicyFeeProcessType(String description) {
        this.description = description;
    }


    public static List<ProductLineProcessDto> getPolicyFeeProcessType(List<ProductLineProcessDto> productLineProcessList ){
        for(PolicyFeeProcessType policyFeeProcessType : values()){
            ProductLineProcessDto productLineProcessDto = new ProductLineProcessDto();
            productLineProcessDto.setType(policyFeeProcessType.name());
            productLineProcessDto.setDescription(policyFeeProcessType.getDescription());
            productLineProcessDto.setFullDescription(policyFeeProcessType.getFullDescription());
            productLineProcessList.add(productLineProcessDto);
        }
        return productLineProcessList;
    }
}
