package com.pla.core.domain.model.generalinformation;

import com.pla.sharedkernel.domain.model.PolicyFeeProcessType;
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

@Getter
@Setter(AccessLevel.PACKAGE)
public class PolicyFeeProcessInformation {

    private Set<PolicyFeeProcessItem> policyFeeProcessItems;

    public PolicyFeeProcessInformation(Set<PolicyFeeProcessItem> policyFeeProcessItems) {
        this.policyFeeProcessItems = policyFeeProcessItems;
    }

    public static PolicyFeeProcessInformation create(List<Map<PolicyFeeProcessType,Integer>> policyFeeProcessItems) {
        Set<PolicyFeeProcessItem> policyFeeProcessItem = policyFeeProcessItems.stream().map(new PolicyFeeProcessInformationTransformer()).collect(Collectors.toSet());
        return new PolicyFeeProcessInformation(policyFeeProcessItem);
    }

    private static class PolicyFeeProcessInformationTransformer implements Function<Map<PolicyFeeProcessType,Integer>,PolicyFeeProcessItem> {
        @Override
        public PolicyFeeProcessItem apply(Map<PolicyFeeProcessType,Integer> productLineProcessItemMap) {
            Map.Entry<PolicyFeeProcessType,Integer> entry = productLineProcessItemMap.entrySet().iterator().next();
            PolicyFeeProcessItem productLineProcessItem = new PolicyFeeProcessItem(entry.getKey(), entry.getValue());
            return productLineProcessItem;
        }
    }

}
