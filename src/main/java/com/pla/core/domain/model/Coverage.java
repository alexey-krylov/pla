
/*
 * Copyright (c) 3/3/15 7:55 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.pla.core.domain.exception.CoverageException;
import com.pla.sharedkernel.identifier.CoverageId;
import lombok.*;
import org.nthdimenzion.common.crud.ICrudEntity;
import org.nthdimenzion.utils.UtilValidator;

import javax.persistence.*;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author: Samir
 * @since 1.0 03/03/2015
 */

@Entity
@Table(name = "coverage")
@EqualsAndHashCode(of = {"coverageName","coverageCode"})
@ToString(of = {"coverageName","coverageCode"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Coverage implements ICrudEntity {

    @EmbeddedId
    private CoverageId coverageId;

    private String coverageCode;

    @Embedded
    private CoverageName coverageName;

    @Column(length = 150)
    private String description;

    @Enumerated(EnumType.STRING)
    private CoverageStatus status;

    @ManyToMany(targetEntity = Benefit.class, fetch = FetchType.EAGER)
    @JoinTable(name = "coverage_benefit", joinColumns = @JoinColumn(name = "COVERAGE_ID"), inverseJoinColumns = @JoinColumn(name = "BENEFIT_ID"))
    private Set<Benefit> benefits;

    Coverage(CoverageId coverageId, CoverageName coverageName,String coverageCode, Set<Benefit> benefits,CoverageStatus coverageStatus) {
        checkNotNull(coverageId == null);
        checkNotNull(coverageName == null);
        checkNotNull(coverageCode == null);
        checkState(UtilValidator.isNotEmpty(benefits));
        this.coverageId = coverageId;
        this.coverageName = coverageName;
        this.coverageCode = coverageCode;
        this.benefits = benefits;
        this.status = coverageStatus;
    }

    public Coverage updateCoverageName(String name) {
        if (CoverageStatus.INUSE.equals(this.status)) {
            throw new CoverageException("Coverage is in use;cannot be updated");
        }
        CoverageName updatedCoverageName = new CoverageName(name);
        this.coverageName = updatedCoverageName;
        return this;
    }

    public Coverage updateCoverageCode(String coverageCode) {
        if (CoverageStatus.INUSE.equals(this.status)) {
            throw new CoverageException("Coverage is in use;cannot be updated");
        }
        this.coverageCode = coverageCode;
        return this;
    }

    public Coverage updateDescription(String description) {
        if (CoverageStatus.INUSE.equals(this.status)) {
            throw new CoverageException("Coverage is in use;cannot be updated");
        }
        this.description = description;
        return this;
    }

    public Coverage markAsUsed() {
        if (CoverageStatus.INACTIVE.equals(this.status)) {
            throw new CoverageException("Coverage cannot be marked as used as it is in inactive state");
        }
        this.status = CoverageStatus.INUSE;
        return this;
    }

    public Coverage deactivate() {
        if (CoverageStatus.INUSE.equals(this.status)) {
            throw new CoverageException("Coverage is in use;cannot be deactivated");
        }
        this.status = CoverageStatus.INACTIVE;
        return this;
    }

    public Coverage updateBenefit(Set<Benefit> benefits) {
        if (CoverageStatus.INUSE.equals(this.status) || CoverageStatus.INACTIVE.equals(this.status)) {
            throw new CoverageException("Coverage is in use;cannot be updated");
        }
        this.benefits = benefits;
        return this;
    }

}
