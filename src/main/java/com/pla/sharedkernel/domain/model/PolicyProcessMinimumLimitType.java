package com.pla.sharedkernel.domain.model;

import com.pla.core.dto.ProductLineProcessDto;
import lombok.Getter;

import java.util.List;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter
public enum PolicyProcessMinimumLimitType {
    ANNUAL("Minimum Number of Persons per Policy"),
    SEMI_ANNUAL("Minimum Premium");

    private String description;
    private String fullDescription;

    PolicyProcessMinimumLimitType(String description) {
        this.description = description;
    }

    public static List<ProductLineProcessDto> getPolicyProcessMinimumLimitType(List<ProductLineProcessDto> productLineProcessList ){
        for(PolicyProcessMinimumLimitType minimumLimitType : values()){
            ProductLineProcessDto productLineProcessDto = new ProductLineProcessDto();
            productLineProcessDto.setType(minimumLimitType.name());
            productLineProcessDto.setDescription(minimumLimitType.getDescription());
            productLineProcessDto.setFullDescription(minimumLimitType.getFullDescription());
            productLineProcessList.add(productLineProcessDto);
        }
        return productLineProcessList;
    }
}
