package com.pla.core.domain.model.plan;


import com.pla.sharedkernel.specification.ISpecification;
import org.nthdimenzion.utils.UtilValidator;

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
            int maxPolicyTerm = policyTerms.stream().sorted().findFirst().get();
            System.out.println(maxPolicyTerm);
        }
        return true;
    }
}
