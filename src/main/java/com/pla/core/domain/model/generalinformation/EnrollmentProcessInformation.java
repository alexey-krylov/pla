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
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
class EnrollmentProcessInformation {

    private Set<ProductLineProcessItem> enrollmentProcessItems;

    private EnrollmentProcessInformation(Set<ProductLineProcessItem> enrollmentProcessItems) {
        this.enrollmentProcessItems = enrollmentProcessItems;
    }

    public static EnrollmentProcessInformation create(List<Map<ProductLineProcessType,Integer>> enrollmentProcessItems) {
        Set<ProductLineProcessItem> productLineProcessItems = enrollmentProcessItems.stream().map(new EnrollmentProcessInformationTransformer()).collect(Collectors.toSet());
        return new EnrollmentProcessInformation(productLineProcessItems);
    }

    private static class EnrollmentProcessInformationTransformer implements Function<Map<ProductLineProcessType,Integer>,ProductLineProcessItem>{
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

    public int getTheProductLineProcessTypeValue(ProductLineProcessType productLineProcessType){
        ProductLineProcessItem productLineProcessItem = enrollmentProcessItems.stream().filter(new FilterProductLineProcessItem(productLineProcessType)).findAny().get();
        return productLineProcessItem.getValue();
    }

    private class FilterProductLineProcessItem implements Predicate<ProductLineProcessItem> {
        ProductLineProcessType productLineProcessType;
        public FilterProductLineProcessItem(ProductLineProcessType productLineProcessType) {
            this.productLineProcessType =  productLineProcessType;
        }

        @Override
        public boolean test(ProductLineProcessItem productLineProcessItem) {
            if (productLineProcessType.equals(productLineProcessItem.getProductLineProcessItem()))
                return true;
            return false;
        }
    }
}
