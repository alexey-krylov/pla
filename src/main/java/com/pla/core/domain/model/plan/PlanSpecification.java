package com.pla.core.domain.model.plan;


import com.pla.sharedkernel.specification.ISpecification;
import org.nthdimenzion.utils.UtilValidator;

import java.util.Comparator;
import java.util.Set;

/**
 * Premium terms cannot be greater than policy terms
 *
 * @author: pradyumna
 * @since 1.0 13/03/2015
 */
public class PlanSpecification implements ISpecification<Plan> {

    @Override
    public boolean isSatisfiedBy(Plan plan) {

        Term policyTerm = plan.getPlanDetail().getPolicyTerm();
        Term premiumPaymentTerm = plan.getPlanDetail().getPremiumTerm();
        if (policyTerm != null && premiumPaymentTerm != null) {
            Set<Integer> policyTerms = policyTerm.getValidTerms();
            Set<Integer> premiumTerms = premiumPaymentTerm.getValidTerms();
            if (UtilValidator.isNotEmpty(premiumTerms) && UtilValidator.isNotEmpty(policyTerms)) {
                int maxPolicyTerm = policyTerms.stream().max(Comparator.comparingInt(term -> term.intValue())).get();
                int maxPremiumTerm = premiumTerms.stream().max(Comparator.comparingInt(term -> term.intValue())).get();
                return maxPremiumTerm <= maxPolicyTerm;
            }
        }
        return true;
    }

    public boolean checkCoverageTerm(Plan plan, Set<Integer> coverageTerms) {
        Term policyTerm = plan.getPlanDetail().getPolicyTerm();
        if (policyTerm != null && coverageTerms != null) {
            Set<Integer> policyTerms = policyTerm.getValidTerms();
            if (UtilValidator.isNotEmpty(coverageTerms) && UtilValidator.isNotEmpty(policyTerms)) {
                int maxPolicyTerm = policyTerms.stream().max(Comparator.comparingInt(term -> term.intValue())).get();
                int maxCoverageTerm = coverageTerms.stream().max(Comparator.comparingInt(term -> term.intValue())).get();
                return maxCoverageTerm <= maxPolicyTerm;
            }
        }
        return true;
    }
}
