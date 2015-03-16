package com.pla.core.domain.model.plan;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;
import org.nthdimenzion.utils.UtilValidator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
@Getter(AccessLevel.PACKAGE)
@ToString
@Entity
class PolicyTerm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    int maxMaturityAge;

    @ElementCollection
    Set<Integer> validTerms = new HashSet<Integer>();

    protected PolicyTerm() {

    }

    PolicyTerm(int maxMaturityAge) {
        checkArgument(maxMaturityAge > 0, "Excepted maxMaturityAge>0 but %s > 0", maxMaturityAge);
        this.maxMaturityAge = maxMaturityAge;
    }

    PolicyTerm(Set<Integer> validTerms, int maxMaturityAge) {
        checkArgument(UtilValidator.isNotEmpty(validTerms));
        checkArgument(maxMaturityAge > 0);

        long termsGreaterThanMaxMaturityAge = validTerms.stream().filter(term -> term.intValue() > maxMaturityAge).count();

        checkArgument(termsGreaterThanMaxMaturityAge == 0, " Invalid Terms. The values cannot be greater than Max Maturity Age.");

        this.validTerms = validTerms;
        this.maxMaturityAge = maxMaturityAge;
    }
}
