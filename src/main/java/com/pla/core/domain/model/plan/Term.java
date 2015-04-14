package com.pla.core.domain.model.plan;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.nthdimenzion.utils.UtilValidator;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Term is used to denoted the Plan Policy Term as well
 * as the Premium Payment Term.
 *
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
@Getter
@ToString
@EqualsAndHashCode
public class Term {

    Set<Integer> validTerms = new HashSet<Integer>();
    Set<Integer> maturityAges = new HashSet<Integer>();
    int maxMaturityAge;

    Term() {

    }

    public Term(Set<Integer> maturityAges) {
        checkArgument(UtilValidator.isNotEmpty(maturityAges));
        this.maturityAges = maturityAges;
    }

    public Term(Term term) {
        this.validTerms = term.getValidTerms();
        this.maxMaturityAge = term.getMaxMaturityAge();
        this.maturityAges = term.getMaturityAges();
    }

    /**
     * Term with valid terms needs to have a Maximum Age.
     * The maximum age for a Premium Term is known as Premium Payment Cut Off Age
     * and in case of Policy Term it is Maximum Maturity Age.
     *
     * @param validTerms
     * @param maxMaturityAge
     */
    public Term(Set<Integer> validTerms, int maxMaturityAge) {
        checkArgument(UtilValidator.isNotEmpty(validTerms));
        checkArgument(maxMaturityAge > 0);
        long termsGreaterThanMaxMaturityAge = validTerms.stream().filter(term -> term.intValue() > maxMaturityAge).count();
        checkArgument(termsGreaterThanMaxMaturityAge == 0, " Invalid Terms. The values cannot be greater than Max Maturity Age.");
        this.validTerms = validTerms;
        this.maxMaturityAge = maxMaturityAge;
    }
}
