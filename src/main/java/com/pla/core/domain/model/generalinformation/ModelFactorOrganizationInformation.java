package com.pla.core.domain.model.generalinformation;

import com.pla.sharedkernel.domain.model.ModalFactorItem;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 4/1/2015.
 */

@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode
public class ModelFactorOrganizationInformation {

    private ModalFactorItem modalFactorItem;

    private BigDecimal value;

    public ModelFactorOrganizationInformation(ModalFactorItem modalFactorItem, BigDecimal value) {
        this.modalFactorItem = modalFactorItem;
        this.value = value;
    }

    public static BigDecimal getMonthlyModalFactor(Set<ModelFactorOrganizationInformation> modelFactorOrganizationInformations) {
        ModelFactorOrganizationInformation modelFactorOrganizationInformation = findModalFactorItem(ModalFactorItem.MONTHLY, modelFactorOrganizationInformations);
        return modelFactorOrganizationInformation.getValue();
    }

    public static BigDecimal getQuarterlyModalFactor(Set<ModelFactorOrganizationInformation> modelFactorOrganizationInformations) {
        ModelFactorOrganizationInformation modelFactorOrganizationInformation = findModalFactorItem(ModalFactorItem.QUARTERLY, modelFactorOrganizationInformations);
        return modelFactorOrganizationInformation.getValue();
    }

    public static BigDecimal getSemiAnnualModalFactor(Set<ModelFactorOrganizationInformation> modelFactorOrganizationInformations) {
        ModelFactorOrganizationInformation modelFactorOrganizationInformation = findModalFactorItem(ModalFactorItem.SEMI_ANNUAL, modelFactorOrganizationInformations);
        return modelFactorOrganizationInformation.getValue();
    }

    private static ModelFactorOrganizationInformation findModalFactorItem(ModalFactorItem modalFactorItem, Set<ModelFactorOrganizationInformation> modelFactorOrganizationInformations) {
        List<ModelFactorOrganizationInformation> modelFactorOrganizationInformationList = modelFactorOrganizationInformations.stream().filter(new Predicate<ModelFactorOrganizationInformation>() {
            @Override
            public boolean test(ModelFactorOrganizationInformation modelFactorOrganizationInformation) {
                return modalFactorItem.equals(modelFactorOrganizationInformation.modalFactorItem);
            }
        }).collect(Collectors.toList());
        checkArgument(isNotEmpty(modelFactorOrganizationInformationList), modalFactorItem.getDescription() + " discount factor is not found");
        return modelFactorOrganizationInformationList.get(0);
    }
}
