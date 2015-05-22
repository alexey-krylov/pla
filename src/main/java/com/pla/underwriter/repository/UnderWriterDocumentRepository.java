package com.pla.underwriter.repository;

import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.UnderWriterDocumentId;
import com.pla.underwriter.domain.model.UnderWriterDocument;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * Created by Admin on 5/13/2015.
 */

public interface UnderWriterDocumentRepository extends MongoRepository<UnderWriterDocument,UnderWriterDocumentId> {

    @Query(value = "{'planCode' : ?0,'validTill':?1 }")
    public UnderWriterDocument findByPlanCodeAndValidityDate(String planCode, LocalDate validTill);

    @Query(value = "{'planCode' : ?0,'coverageId' : ?1 }")
    public UnderWriterDocument findByPlanCodeAndCoverageIdAndValidityDate(String planCode, CoverageId coverageId, LocalDate validTill);

}