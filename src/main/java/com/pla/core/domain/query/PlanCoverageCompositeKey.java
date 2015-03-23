package com.pla.core.domain.query;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author: pradyumna
 * @since 1.0 22/03/2015
 */
@Embeddable
public class PlanCoverageCompositeKey implements Serializable {

    @Column(name = "plan_id")
    private String planId;
    @Column(name = "coverage_id")
    private String coverageId;

    protected PlanCoverageCompositeKey() {

    }

    public PlanCoverageCompositeKey(String planId, String coverageId) {
        this.planId = planId;
        this.coverageId = coverageId;
    }
}
