/*
 * Copyright (c) 3/3/15 7:55 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.*;
import java.util.Collection;

/**
 * @author: Samir
 * @since 1.0 03/03/2015
 */
@Entity
@Table(name = "coverage", uniqueConstraints = {@UniqueConstraint(name = "UNQ_COVERAGE_NAME", columnNames = "coverageName")})
@EqualsAndHashCode(of = "coverageName")
@ToString(of = "coverageName")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class Coverage implements ICrudEntity {

    @Id
    private String coverageId;

    @Column(length = 50)
    private String coverageName;

    @Column(length = 150)
    private String description;

    private Boolean active;

    @ElementCollection(targetClass = String.class)
    @OrderColumn
    private Collection<String> benefitIds;
    
    private Coverage(String coverageId, String coverageName, Collection<String> benefitIds) {
        this.coverageId = coverageId;
        this.coverageName = coverageName;
        this.benefitIds = benefitIds;
    }

    public static Coverage createCoverage(String coverageId, String coverageName, Collection<String> benefitIds) {
        Coverage coverage = new Coverage(coverageId, coverageName, benefitIds);
        return coverage;
    }

    public Coverage updateWithDescription(String description) {
        this.description = description;
        return this;
    }

    public Coverage updateWithCoverageNameAndBenefit(String coverageName,Collection<String> benefitIds) {
        this.coverageName = coverageName;
        this.benefitIds = benefitIds;
        return this;
    }

    public Coverage activate() {
        this.active = Boolean.TRUE;
        return this;
    }

    public Coverage deactivate() {
        this.active = Boolean.FALSE;
        return this;
    }
}
