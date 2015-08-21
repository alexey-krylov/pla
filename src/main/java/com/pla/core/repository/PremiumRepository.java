package com.pla.core.repository;

import com.pla.core.domain.model.plan.premium.Premium;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PremiumId;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * Created by Samir on 4/5/2015.
 */
public interface PremiumRepository extends MongoRepository<Premium, PremiumId> {

    @Query(value = "{'planId' : ?0,'validTill':?1 }")
    public Premium findByPlanIdAndValidityDate(PlanId planId, LocalDate validTill);

    @Query(value = "{'planId' : ?0,'coverageId' : ?1 }")
    public Premium findByPlanAndCoverageIdAndValidityDate(PlanId planId, CoverageId coverageId,LocalDate validTill);


}
