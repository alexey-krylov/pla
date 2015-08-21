package com.pla.core.domain.model.generalinformation;

import com.pla.core.domain.exception.GeneralInformationException;
import com.pla.sharedkernel.domain.model.ProductLineProcessType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Admin on 4/1/2015.
 */

@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
class EndorsementProcessInformation {

    private Set<ProductLineProcessItem> endorsementProcessItems;

      EndorsementProcessInformation(Set<ProductLineProcessItem> endorsementProcessItems) {
        this.endorsementProcessItems = endorsementProcessItems;
    }

    public static EndorsementProcessInformation create(List<Map<ProductLineProcessType,Integer>> endorsementProcessItems) {
        Set<ProductLineProcessItem> productLineProcessItems = endorsementProcessItems.stream().map(new EndorsementProcessInformationTransformer()).collect(Collectors.toSet());
        return new EndorsementProcessInformation(productLineProcessItems);
    }

    private static class EndorsementProcessInformationTransformer implements Function<Map<ProductLineProcessType,Integer>,ProductLineProcessItem> {
        @Override
        public ProductLineProcessItem apply(Map<ProductLineProcessType,Integer> productLineProcessItemMap) {
            Map.Entry<ProductLineProcessType,Integer> entry = productLineProcessItemMap.entrySet().iterator().next();
            if(ProductLineProcessType.EARLY_DEATH_CRITERIA.equals(entry.getKey())){
                throw new GeneralInformationException("Early Death Criteria is applicable only for claim request");
            }
            ProductLineProcessItem productLineProcessItem = new ProductLineProcessItem(entry.getKey(), entry.getValue());
            return productLineProcessItem;
        }
    }

}
