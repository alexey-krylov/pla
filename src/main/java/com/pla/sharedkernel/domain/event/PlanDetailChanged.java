package com.pla.sharedkernel.domain.event;

import com.pla.sharedkernel.domain.model.ClientType;
import com.pla.sharedkernel.domain.model.EndorsementType;
import com.pla.sharedkernel.domain.model.PlanType;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import com.pla.sharedkernel.identifier.PlanId;
import org.joda.time.LocalDate;

import java.util.Set;

/**
 * @author: pradyumna
 * @since 1.0 19/03/2015
 */
public class PlanDetailChanged extends PlanDetailConfigured {

    public PlanDetailChanged(PlanId planId, String planName, String planCode, LocalDate launchDate,
                             LocalDate withdrawalDate, int freeLookPeriod, int minEntryAge, int maxEntryAge,
                             boolean taxApplicable, int surrenderAfter, Set<Relationship> applicableRelationships,
                             Set<EndorsementType> endorsementTypes, LineOfBusinessId lineOfBusinessId,
                             PlanType planType, ClientType clientType) {
        super(planId, planName, planCode, launchDate,
                withdrawalDate, freeLookPeriod, minEntryAge, maxEntryAge,
                taxApplicable, surrenderAfter, applicableRelationships,
                endorsementTypes, lineOfBusinessId,
                planType, clientType);
    }
}
