package com.pla.core.domain.model.plan;

import lombok.Getter;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedEntity;

/**
 * @author: pradyumna
 * @since 1.0 18/03/2015
 */
@Getter
public class SumAssuredConfigured extends AbstractAnnotatedEntity {
    private SumAssured sumAssured;

    public SumAssuredConfigured(SumAssured sumAssured) {
        this.sumAssured = sumAssured;
    }
}
