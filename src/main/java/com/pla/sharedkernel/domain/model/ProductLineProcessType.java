package com.pla.sharedkernel.domain.model;

import com.pla.core.dto.ProductLineProcessDto;
import lombok.Getter;

import java.util.List;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter
public enum ProductLineProcessType {

    PURGE_TIME_PERIOD("Purge Time Period"),
    FIRST_REMAINDER(""),
    NO_OF_REMAINDER("No. of Remainder"),
    GAP("Gap"),
    CLOSURE("Closure"),
    EARLY_DEATH_CRITERIA("Early Death Criteria");

    private String description;
    private String fullDescription;
    ProductLineProcessType(String description) {
        this.description = description;
    }

    public static List<ProductLineProcessDto> getProductLineProcessType(List<ProductLineProcessDto> productLineProcessList){
        for(ProductLineProcessType productLineProcessType : values()){
            ProductLineProcessDto productLineProcessDto = new ProductLineProcessDto();
            productLineProcessDto.setType(productLineProcessType.name());
            productLineProcessDto.setDescription(productLineProcessType.getDescription());
            productLineProcessDto.setFullDescription(productLineProcessType.getFullDescription());
            productLineProcessList.add(productLineProcessDto);
        }
        return productLineProcessList;
    }

}
