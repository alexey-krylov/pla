package com.pla.core.domain.model.generalinformation;

import com.pla.sharedkernel.domain.model.ModalFactorItem;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Admin on 4/27/2015.
 */
@Getter
@Setter(AccessLevel.PACKAGE)
@EqualsAndHashCode
public class ModalFactorProcessInformation {
    private Set<ModelFactorOrganizationInformation> modelFactorItems;

    public ModalFactorProcessInformation(Set<ModelFactorOrganizationInformation> modelFactorItems) {
        this.modelFactorItems = modelFactorItems;
    }

    public static ModalFactorProcessInformation create(List<Map<ModalFactorItem, BigDecimal>> listOfModalFactorItem) {
        Set<ModelFactorOrganizationInformation> modelFactorItem = listOfModalFactorItem.stream().map(new ModalFactorInformationTransformer()).collect(Collectors.toSet());
        return new ModalFactorProcessInformation(modelFactorItem);
    }

    private static class ModalFactorInformationTransformer implements Function<Map<ModalFactorItem, BigDecimal>, ModelFactorOrganizationInformation> {
        @Override
        public ModelFactorOrganizationInformation apply(Map<ModalFactorItem, BigDecimal> modalFactorItemMap) {
            Map.Entry<ModalFactorItem, BigDecimal> modelFactorItem = modalFactorItemMap.entrySet().iterator().next();
            ModelFactorOrganizationInformation modelFactorOrganizationInformation = new ModelFactorOrganizationInformation(modelFactorItem.getKey(), modelFactorItem.getValue());
            return modelFactorOrganizationInformation;
        }
    }
}
