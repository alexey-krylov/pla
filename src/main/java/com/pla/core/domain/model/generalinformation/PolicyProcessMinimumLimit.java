package com.pla.core.domain.model.generalinformation;

import com.pla.sharedkernel.domain.model.PolicyProcessMinimumLimitType;
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
public class PolicyProcessMinimumLimit {

   private Set<PolicyProcessMinimumLimitItem> policyProcessMinimumLimitItems;


   public PolicyProcessMinimumLimit(Set<PolicyProcessMinimumLimitItem> policyProcessMinimumLimitItems) {
      this.policyProcessMinimumLimitItems = policyProcessMinimumLimitItems;
   }

   public static PolicyProcessMinimumLimit create(List<Map<PolicyProcessMinimumLimitType,Integer>> policyProcessMinimumLimitItems) {
      Set<PolicyProcessMinimumLimitItem> policyFeeProcessItem = policyProcessMinimumLimitItems.stream().map(new PolicyProcessMinimumLimitInformationTransformer()).collect(Collectors.toSet());
      return new PolicyProcessMinimumLimit(policyFeeProcessItem);
   }


   private static class PolicyProcessMinimumLimitInformationTransformer implements Function<Map<PolicyProcessMinimumLimitType,Integer>,PolicyProcessMinimumLimitItem> {
      @Override
      public PolicyProcessMinimumLimitItem apply(Map<PolicyProcessMinimumLimitType,Integer> policyProcessMinimumLimitItemDto) {
         Map.Entry<PolicyProcessMinimumLimitType,Integer> entry = policyProcessMinimumLimitItemDto.entrySet().iterator().next();
         PolicyProcessMinimumLimitItem policyProcessMinimumLimitItem = new PolicyProcessMinimumLimitItem(entry.getKey(),entry.getValue());
         return policyProcessMinimumLimitItem;
      }
   }

   public Set<PolicyProcessMinimumLimitItem> getPolicyProcessMinimumLimitItemsByForce(){
      return this.policyProcessMinimumLimitItems;
   }
}
