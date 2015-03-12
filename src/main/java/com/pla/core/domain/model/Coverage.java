/*
 * Copyright (c) 3/3/15 7:55 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.google.common.base.Preconditions;
import com.pla.core.domain.exception.CoverageException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.*;
import java.util.List;

/**
 * @author: Samir
 * @since 1.0 03/03/2015
 */
@Entity
@Table(name = "coverage", uniqueConstraints = {@UniqueConstraint(name = "UNQ_COVERAGE_NAME", columnNames = "coverageName")})
@EqualsAndHashCode(of = {"coverageName", "coverageId"})
@ToString(of = "coverageName")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Coverage implements ICrudEntity {

    @Id
    private String coverageId;

    @Embedded
    private CoverageName coverageName;

    @Column(length = 150)
    private String description;

    @Enumerated(EnumType.STRING)
    private CoverageStatus status;

    @OneToMany(targetEntity = Benefit.class, fetch = FetchType.EAGER)
    @JoinTable(name = "coverage_benefit", joinColumns = @JoinColumn(name = "COVERAGE_ID"), inverseJoinColumns = @JoinColumn(name = "BENEFIT_ID"))
    private List<Benefit> benefits;

    Coverage(String coverageId, CoverageName coverageName, List<Benefit> benefits) {
        Preconditions.checkNotNull(coverageId);
        Preconditions.checkNotNull(coverageName);
        Preconditions.checkNotNull(benefits);
        this.coverageId = coverageId;
        this.coverageName = coverageName;
        this.benefits = benefits;
    }

    public Coverage updateCoverageName(String name) {
        if (CoverageStatus.INUSE.equals(this.status)) {
            throw new CoverageException("Coverage is in use;cannot be updated");
        }
        CoverageName updatedCoverageName = new CoverageName(name);
        this.coverageName = updatedCoverageName;
        return this;
    }

    public Coverage updateDescription(String description) {
        if (CoverageStatus.INUSE.equals(this.status)) {
            throw new CoverageException("Coverage is in use;cannot be updated");
        }
        this.description = description;
        return this;
    }

    public Coverage activate() {
        this.status = CoverageStatus.ACTIVE;
        return this;
    }

    public Coverage deactivate() {
        if (CoverageStatus.INUSE.equals(this.status)) {
            throw new CoverageException("Coverage is in use;cannot be deactivated");
        }
        this.status = CoverageStatus.INACTIVE;
        return this;
    }
}
