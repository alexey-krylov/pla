package com.pla.core.domain.model.plan;

import lombok.Getter;
import lombok.ToString;
import org.nthdimenzion.utils.UtilValidator;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author: pradyumna
 * @since 1.0 12/03/2015
 */
@ToString
@Getter
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
@DiscriminatorValue(value = "SUM_ASSURED_VALUE")
class SumAssured {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ElementCollection
    @CollectionTable(name = "sum_insured_values", joinColumns = @JoinColumn(name = "sum_assured_id"))
    //TODO change to AssuredValues
    private Set<BigDecimal> sumInsuredValues;

    protected SumAssured() {
    }

    SumAssured(Set<BigDecimal> sumInsuredValues) {
        checkArgument(UtilValidator.isNotEmpty(sumInsuredValues));
        this.sumInsuredValues = sumInsuredValues;
    }
}
