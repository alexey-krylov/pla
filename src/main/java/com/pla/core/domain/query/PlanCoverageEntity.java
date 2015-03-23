package com.pla.core.domain.query;

import com.pla.sharedkernel.domain.model.SumAssuredType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 22/03/2015
 */
@Entity(name = "plan_coverage")
@Getter
@Setter
public class PlanCoverageEntity {

    @Embedded
    @Id
    private PlanCoverageCompositeKey id;

    @Enumerated(EnumType.STRING)
    private SumAssuredType sumAssuredType;
    private BigDecimal minSumAssured;
    private BigDecimal maxSumAssured;
    private int multiplesOf;
    private int percentage;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "plan_coverage_sum_assured",
            joinColumns = {@JoinColumn(name = "plan_id"), @JoinColumn(name = "coverage_id")})
    private Set<BigDecimal> sumAssured;

    public PlanCoverageEntity() {
    }

    public PlanCoverageEntity(String planId, String coverageId, SumAssuredType sumAssuredType, BigDecimal minSumAssured,
                              BigDecimal maxSumAssured, int multiplesOf, Set<BigDecimal> sumAssured, int percentage) {
        this.id = new PlanCoverageCompositeKey(planId, coverageId);
        this.sumAssuredType = sumAssuredType;
        this.minSumAssured = minSumAssured;
        this.maxSumAssured = maxSumAssured;
        this.multiplesOf = multiplesOf;
        this.sumAssured = sumAssured;
        this.percentage = percentage;
    }

}
