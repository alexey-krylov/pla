package com.pla.underwriter.repository;

import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.UnderWriterDocumentId;
import com.pla.underwriter.domain.model.UnderWriterDocument;
import org.joda.time.LocalDate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Created by Admin on 5/13/2015.
 */

public interface UnderWriterDocumentRepository extends MongoRepository<UnderWriterDocument,UnderWriterDocumentId> {

    @Query(value = "{'planCode' : ?0,'validTill':?1, 'processType' :?2 }")
    public UnderWriterDocument findByPlanCodeAndValidityDate(String planCode, LocalDate validTill,String processType);

    @Query(value = "{'planCode' : ?0,'coverageId' : ?1 ,'validTill':?2 ,'processType' :?3}")
    public UnderWriterDocument findByPlanCodeAndCoverageIdAndValidityDate(String planCode, CoverageId coverageId, LocalDate validTill,String processType);

    @Query(value = "{'planCode' : ?0,'coverageId' : ?1, 'effectiveFrom' :?2, 'validTill' :?3, 'processType' :?4 }")
    public List<UnderWriterDocument> findUnderWriterDocument(String planCode,CoverageId coverageId,LocalDate effectiveFrom,LocalDate validTill,String processType);

    @Query(value = "{'validTill':?0}")
    public List<UnderWriterDocument> findEffectiveUnderWriterDocument(LocalDate validTill);

}
