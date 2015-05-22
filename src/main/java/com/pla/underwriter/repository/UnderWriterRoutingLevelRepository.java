package com.pla.underwriter.repository;

import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.UnderWriterRoutingLevelId;
import com.pla.underwriter.domain.model.UnderWriterRoutingLevel;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * Created by Admin on 5/13/2015.
 */
public interface UnderWriterRoutingLevelRepository  extends MongoRepository<UnderWriterRoutingLevel,UnderWriterRoutingLevelId> {

    @Query(value = "{'planCode' : ?0,'validTill':?1 }")
    public UnderWriterRoutingLevel findByPlanCodeAndValidityDate(String planCode, LocalDate validTill);

    @Query(value = "{'planCode' : ?0,'coverageId' : ?1 }")
    public UnderWriterRoutingLevel findByPlanCodeAndCoverageIdAndValidityDate(String planCode, CoverageId coverageId, LocalDate validTill);

}