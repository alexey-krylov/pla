package com.pla.core.dto;

import com.pla.sharedkernel.domain.model.ProductLineProcessType;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 4/22/2015.
 */
@Getter
@Setter
public class ProductLineProcessItemDto {
    private ProductLineProcessType productLineProcessItem;
    private int value;

    public ProductLineProcessItemDto() {
    }
}
