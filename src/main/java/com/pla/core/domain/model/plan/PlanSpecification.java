package com.pla.core.domain.model.plan;


import com.pla.sharedkernel.specification.ISpecification;
import org.nthdimenzion.utils.UtilValidator;

import java.util.Comparator;
import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 13/03/2015
 */
public class PlanSpecification implements ISpecification<Plan> {


    @Override
    public boolean isSatisfiedBy(Plan plan) {
        Set<Integer> policyTerms = plan.getPolicyTerm().getValidTerms();
        Set<Integer> premiumTerms = plan.getPlanPayment().getPremiumPayment().getValidTerms();
        if (UtilValidator.isNotEmpty(premiumTerms) && UtilValidator.isNotEmpty(policyTerms)) {
            int maxPolicyTerm = policyTerms.stream().max(Comparator.comparingInt(term -> term.intValue())).get();
            int maxPremiumTerm = premiumTerms.stream().max(Comparator.comparingInt(term -> term.intValue())).get();
            return maxPremiumTerm <= maxPolicyTerm;
        }
        return true;
    }
}
