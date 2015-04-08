package com.pla.core.domain.model.generalinformation;

import com.pla.core.dto.PolicyProcessMinimumLimitItemDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
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

   public static PolicyProcessMinimumLimit create(List<PolicyProcessMinimumLimitItemDto> policyProcessMinimumLimitItems) {
      Set<PolicyProcessMinimumLimitItem> policyFeeProcessItem = policyProcessMinimumLimitItems.stream().map(new PolicyProcessMinimumLimitInformationTransformer()).collect(Collectors.toSet());
      return new PolicyProcessMinimumLimit(policyFeeProcessItem);
   }


   private static class PolicyProcessMinimumLimitInformationTransformer implements Function<PolicyProcessMinimumLimitItemDto,PolicyProcessMinimumLimitItem> {
      @Override
      public PolicyProcessMinimumLimitItem apply(PolicyProcessMinimumLimitItemDto policyProcessMinimumLimitItemDto) {
         PolicyProcessMinimumLimitItem policyProcessMinimumLimitItem = new PolicyProcessMinimumLimitItem(policyProcessMinimumLimitItemDto.getPolicyProcessMinimumLimitType(),policyProcessMinimumLimitItemDto.getNoOfPersonPerPolicy(),policyProcessMinimumLimitItemDto.getMinimumPremium());
         return policyProcessMinimumLimitItem;
      }
   }
}
