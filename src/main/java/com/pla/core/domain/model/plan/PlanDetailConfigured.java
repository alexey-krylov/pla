package com.pla.core.domain.model.plan;

import com.pla.sharedkernel.domain.model.ClientType;
import com.pla.sharedkernel.domain.model.PlanType;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 18/03/2015
 */
@Getter
public class PlanDetailConfigured implements Serializable {

    String planId;
    String planName;
    String planCode;
    LocalDate launchDate;
    LocalDate withdrawalDate;
    int freeLookPeriod = 15;
    int minEntryAge;
    int maxEntryAge;
    boolean taxApplicable;
    int surrenderAfter;
    Set<Relationship> applicableRelationships;
    Set<EndorsementType> endorsementTypes;
    LineOfBusinessId lineOfBusinessId;
    PlanType planType;
    ClientType clientType;

    public PlanDetailConfigured(PlanId planId, String planName, String planCode, LocalDate launchDate, LocalDate withdrawalDate,
                                int freeLookPeriod, int minEntryAge, int maxEntryAge, boolean taxApplicable, int surrenderAfter,
                                Set<Relationship> applicableRelationships, Set<EndorsementType> endorsementTypes, LineOfBusinessId lineOfBusinessId, PlanType planType, ClientType clientType) {
        this.planId = planId.toString();
        this.planName = planName;
        this.planCode = planCode;
        this.launchDate = launchDate;
        this.withdrawalDate = withdrawalDate;
        this.freeLookPeriod = freeLookPeriod;
        this.minEntryAge = minEntryAge;
        this.maxEntryAge = maxEntryAge;
        this.taxApplicable = taxApplicable;
        this.surrenderAfter = surrenderAfter;
        this.applicableRelationships = applicableRelationships;
        this.endorsementTypes = endorsementTypes;
        this.lineOfBusinessId = lineOfBusinessId;
        this.planType = planType;
        this.clientType = clientType;
    }
}
