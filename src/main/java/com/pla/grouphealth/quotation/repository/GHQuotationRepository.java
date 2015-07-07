package com.pla.grouphealth.quotation.repository;

import com.pla.grouphealth.quotation.domain.model.GroupHealthQuotation;
import com.pla.sharedkernel.identifier.QuotationId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Created by Samir on 5/26/2015.
 */
public interface GHQuotationRepository extends MongoRepository<GroupHealthQuotation, QuotationId> {

    @Query(value = "{'quotationNumber' : ?0,'quotationStatus' : ?2, 'quotationId' : {'$ne' : ?1}}")
    List<GroupHealthQuotation> findQuotationByQuotNumberAndStatusByExcludingGivenQuotId(String quotationNumber, QuotationId quotationId, String quotationStatus);

    @Query(value = "{'quotationNumber' : ?0")
    List<GroupHealthQuotation> findQuotationByQuotationNumber(String quotationNumber);

}
