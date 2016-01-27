package com.pla.underwriter.repository;

import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.UnderWriterRoutingLevelId;
import com.pla.underwriter.domain.model.UnderWriterRoutingLevel;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Created by Admin on 5/13/2015.
 */
public interface UnderWriterRoutingLevelRepository  extends MongoRepository<UnderWriterRoutingLevel,UnderWriterRoutingLevelId> {

    @Query("{'planId' : ?0,'validTill':?1 , 'processType' : ?2,'coverageId' : null}")
    public UnderWriterRoutingLevel findByPlanCodeAndValidTillAndProcessType(PlanId planId, LocalDate validTill, String processType);

    @Query("{'planId' : ?0,'coverageId' : ?1 ,'validTill':?2,'processType' :?3}")
    public UnderWriterRoutingLevel findByPlanCodeAndCoverageIdAndValidityTillAndProcessType(PlanId planId, CoverageId coverageId, LocalDate validTill, String processType);

    @Query("{'planId' : ?0,'coverageId' : ?1,'validTill' :?2, 'processType' :?3 }")
    public List<UnderWriterRoutingLevel> findByPlanCodeAndCoverageIdAndValidTillAndProcessType(PlanId planId, CoverageId coverageId, LocalDate validTill, String processType);

    public List<UnderWriterRoutingLevel> findAllByPlanIdAndProcessType(PlanId planId, String processType);

}