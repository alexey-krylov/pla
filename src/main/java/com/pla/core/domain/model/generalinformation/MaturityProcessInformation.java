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
 * Created by Admin on 4/6/2015.
 */
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class MaturityProcessInformation {
    private Set<ProductLineProcessItem> maturityProcessItems;

    public MaturityProcessInformation(Set<ProductLineProcessItem> maturityProcessItems) {
        this.maturityProcessItems = maturityProcessItems;
    }

    public static MaturityProcessInformation create(List<Map<ProductLineProcessType,Integer>> maturityProcessItems) {
        Set<ProductLineProcessItem> productLineProcessItems = maturityProcessItems.stream().map(new MaturityProcessInformationTransformer()).collect(Collectors.toSet());
        return new MaturityProcessInformation(productLineProcessItems);
    }

    private static class MaturityProcessInformationTransformer implements Function<Map<ProductLineProcessType,Integer>,ProductLineProcessItem> {
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
