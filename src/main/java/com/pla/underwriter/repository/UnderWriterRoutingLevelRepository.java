package com.pla.underwriter.repository;

import com.pla.sharedkernel.identifier.CoverageId;
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

    @Query(value = "{'planCode' : ?0,'validTill':?1 , 'processType' : ?2}")
    public UnderWriterRoutingLevel findByPlanCodeAndValidityDate(String planCode, LocalDate validTill,String processType);

    @Query(value = "{'planCode' : ?0,'coverageId' : ?1 ,'validTill':?2,'processType' :?3}")
    public UnderWriterRoutingLevel findByPlanCodeAndCoverageIdAndValidityDate(String planCode, CoverageId coverageId, LocalDate validTill,String processType);

    @Query(value = "{'validTill':?0}")
    public List<UnderWriterRoutingLevel> findEffectiveUnderWriterRoutingLevel(LocalDate validTill);

    @Query(value = "{'planCode' : ?0,'coverageId' : ?1, 'effectiveFrom' :?2, 'validTill' :?3, 'processType' :?4 }")
    public List<UnderWriterRoutingLevel> findUnderWriterRoutingLevel(String planCode,CoverageId coverageId,LocalDate effectiveFrom,LocalDate validTill,String processType);

}