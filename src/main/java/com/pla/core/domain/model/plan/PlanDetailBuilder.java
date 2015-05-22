package com.pla.core.domain.model.plan;

import com.pla.sharedkernel.domain.model.ClientType;
import com.pla.sharedkernel.domain.model.EndorsementType;
import com.pla.sharedkernel.domain.model.PlanType;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.joda.time.LocalDate;

import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 21/03/2015
 */
public class PlanDetailBuilder {

    boolean taxApplicable;
    String planName;
    String planCode;
    LocalDate launchDate;
    LocalDate withdrawalDate;
    int freeLookPeriod;
    int minEntryAge;
    int maxEntryAge;
    int surrenderAfterYears;
    Set<Relationship> applicableRelationships;
    Set<EndorsementType> endorsementTypes;
    LineOfBusinessEnum lineOfBusinessId;
    PlanType planType;
    ClientType clientType;
    boolean funeralCover;

    public PlanDetailBuilder withPlanName(String planName) {
        this.planName = planName;
        return this;
    }

    public PlanDetailBuilder withPlanCode(String planCode) {
        this.planCode = planCode;
        return this;
    }

    public PlanDetailBuilder withLaunchDate(LocalDate launchDate) {
        this.launchDate = launchDate;
        return this;
    }

    public PlanDetailBuilder withWithdrawalDate(LocalDate withdrawalDate) {
        this.withdrawalDate = withdrawalDate;
        return this;
    }

    public PlanDetailBuilder withFreeLookPeriod(int freeLookPeriod) {
        this.freeLookPeriod = freeLookPeriod;
        return this;
    }

    public PlanDetailBuilder withMinEntryAge(int minEntryAge) {
        this.minEntryAge = minEntryAge;
        return this;
    }

    public PlanDetailBuilder withMaxEntryAge(int maxEntryAge) {
        this.maxEntryAge = maxEntryAge;
        return this;
    }

    public PlanDetailBuilder withTaxApplicable(boolean taxApplicable) {
        this.taxApplicable = taxApplicable;
        return this;
    }

    public PlanDetailBuilder withApplicableRelationships(Set<Relationship> relationships) {
        this.applicableRelationships = relationships;
        return this;
    }

    public PlanDetailBuilder withEndorsementTypes(Set<EndorsementType> endorsementTypes) {
        this.endorsementTypes = endorsementTypes;
        return this;
    }

    public PlanDetailBuilder withSurrenderAfter(int surrenderAfterYears) {
        this.surrenderAfterYears = surrenderAfterYears;
        return this;
    }

    public PlanDetailBuilder withLineOfBusinessId(LineOfBusinessEnum lineOfBusinessId) {
        this.lineOfBusinessId = lineOfBusinessId;
        return this;
    }

    public PlanDetailBuilder withPlanType(PlanType planType) {
        this.planType = planType;
        return this;
    }

    public PlanDetailBuilder withClientType(ClientType clientType) {
        this.clientType = clientType;
        return this;
    }

    public PlanDetailBuilder withFuneralCover(boolean funeralCover) {
        this.funeralCover = funeralCover;
        return this;
    }
    public PlanDetail build() {
        return new PlanDetail(this, true);
    }

    public PlanDetail build(boolean isNewPlan) {
        return new PlanDetail(this, isNewPlan);
    }
}
