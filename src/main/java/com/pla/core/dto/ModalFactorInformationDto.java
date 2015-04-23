package com.pla.core.dto;

import com.pla.sharedkernel.domain.model.ModalFactorItem;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 4/22/2015.
 */
@Getter
@Setter
public class ModalFactorInformationDto {
    private ModalFactorItem modalFactorItem;
    private BigDecimal value;


}
