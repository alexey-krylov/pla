package com.pla.core.domain.model.generalinformation;

import com.pla.sharedkernel.domain.model.ModalFactorItem;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by Admin on 4/1/2015.
 */

@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode(callSuper = false)
class ModelFactorOrganizationInformation {

    private ModalFactorItem modalFactorItem;

    private BigDecimal value;

    ModelFactorOrganizationInformation(ModalFactorItem modalFactorItem, BigDecimal value) {
        this.modalFactorItem = modalFactorItem;
        this.value = value.setScale(4,BigDecimal.ROUND_HALF_UP);
    }
}
