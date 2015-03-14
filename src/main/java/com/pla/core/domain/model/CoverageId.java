package com.pla.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author: pradyumna
 * @since 1.0 13/03/2015
 */
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class CoverageId implements Serializable {

    String coverageId;

    protected CoverageId() {
    }
}
