package com.pla.core.domain.model.generalinformation;

import com.pla.sharedkernel.domain.model.ProductLineProcessType;
import com.pla.sharedkernel.exception.ProcessInfoException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.pla.sharedkernel.exception.ProcessInfoException.raiseProductLineItemNotFoundException;

/**
 * Created by Admin on 4/1/2015.
 */
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
public class ClaimProcessInformation {

    private Set<ProductLineProcessItem> claimProcessItems;


    public ClaimProcessInformation(Set<ProductLineProcessItem> claimProcessItems) {
        this.claimProcessItems = claimProcessItems;
    }

    public static ClaimProcessInformation create(List<Map<ProductLineProcessType,Integer>> claimProcessItems) {
        Set<ProductLineProcessItem> productLineProcessItems = claimProcessItems.stream().map(new ClaimProcessInformationTransformer()).collect(Collectors.toSet());
        return new ClaimProcessInformation(productLineProcessItems);
    }

    public int getTheProductLineProcessTypeValue(ProductLineProcessType productLineProcessType) throws ProcessInfoException {
        Optional<ProductLineProcessItem> optionalProductLineProcessItem = claimProcessItems.stream().filter(new FilterProductLineProcessItem(productLineProcessType)).findAny();
        if (!optionalProductLineProcessItem.isPresent()) {
            raiseProductLineItemNotFoundException();
        }
        return optionalProductLineProcessItem.get().getValue();
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
    private static class ClaimProcessInformationTransformer implements Function<Map<ProductLineProcessType,Integer>,ProductLineProcessItem> {
        @Override
        public ProductLineProcessItem apply(Map<ProductLineProcessType,Integer> productLineProcessItemMap) {
            Map.Entry<ProductLineProcessType,Integer> entry = productLineProcessItemMap.entrySet().iterator().next();
            ProductLineProcessItem productLineProcessItem = new ProductLineProcessItem(entry.getKey(), entry.getValue());
            return productLineProcessItem;
        }
    }



}
