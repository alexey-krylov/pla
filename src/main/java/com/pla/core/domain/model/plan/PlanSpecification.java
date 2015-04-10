package com.pla.core.domain.model.plan;


import com.pla.sharedkernel.specification.ISpecification;
import org.nthdimenzion.utils.UtilValidator;

import java.util.Collection;
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

        Term policyTerm = plan.getPolicyTerm();
        Term premiumPaymentTerm = plan.getPremiumTerm();
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

    public boolean checkCoverageTerm(Plan plan, Collection<Term> coverageTerms) {
        Term policyTerm = plan.getPolicyTerm();
        if (policyTerm != null && UtilValidator.isNotEmpty(coverageTerms)) {
            Set<Integer> policyTerms = policyTerm.getValidTerms();
            for (Term term : coverageTerms) {
                if (UtilValidator.isNotEmpty(term.getValidTerms()) && UtilValidator.isNotEmpty(policyTerms)) {
                    int maxPolicyTerm = policyTerms.stream().max(Comparator.comparingInt(pTerm -> pTerm.intValue())).get();
                    int maxCoverageTerm = term.getValidTerms().stream().max(Comparator.comparingInt(each -> each.intValue())).get();
                    if (maxCoverageTerm > maxPolicyTerm) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
