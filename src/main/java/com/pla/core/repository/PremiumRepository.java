package com.pla.core.repository;

import com.pla.core.domain.model.plan.premium.Premium;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PremiumId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * Created by Samir on 4/5/2015.
 */
public interface PremiumRepository extends MongoRepository<Premium, PremiumId> {

    @Query(value = "{'planId' : ?0 }")
    public Premium findByPlanId(PlanId planId);

    @Query(value = "{'planId' : ?0,'coverageId' : ?1 }")
    public Premium findByPlanAndCoverageId(PlanId planId, CoverageId coverageId);


}
