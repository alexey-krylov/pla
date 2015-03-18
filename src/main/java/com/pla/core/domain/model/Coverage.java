
/*
 * Copyright (c) 3/3/15 7:55 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.google.common.base.Preconditions;
import com.pla.core.domain.exception.CoverageException;
import com.pla.sharedkernel.identifier.CoverageId;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.nthdimenzion.common.crud.ICrudEntity;
import org.nthdimenzion.utils.UtilValidator;

import javax.persistence.*;
import java.util.Set;

/**
 * @author: Samir
 * @since 1.0 03/03/2015
 */
@Entity
@Table(name = "coverage", uniqueConstraints = {@UniqueConstraint(name = "UNQ_COVERAGE_NAME", columnNames = "coverageName")})
@EqualsAndHashCode(of = {"coverageName"})
@ToString(of = "coverageName")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Coverage implements ICrudEntity {

    @EmbeddedId
    private CoverageId coverageId;

    @Embedded
    private CoverageName coverageName;

    @Column(length = 150)
    private String description;

    @Enumerated(EnumType.STRING)
    private CoverageStatus status;

    @OneToMany(targetEntity = Benefit.class, fetch = FetchType.EAGER)
    @JoinTable(name = "coverage_benefit", joinColumns = @JoinColumn(name = "COVERAGE_ID"), inverseJoinColumns = @JoinColumn(name = "BENEFIT_ID"))
    private Set<Benefit> benefits;

    Coverage(CoverageId coverageId, CoverageName coverageName, Set<Benefit> benefits) {
        Preconditions.checkArgument(coverageId == null);
        Preconditions.checkArgument(coverageName == null);
        Preconditions.checkArgument(UtilValidator.isNotEmpty(benefits));
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
