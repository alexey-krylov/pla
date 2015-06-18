package com.pla.grouplife.quotation.repository;

import com.pla.grouplife.quotation.domain.model.GroupLifeQuotation;
import com.pla.sharedkernel.identifier.QuotationId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Created by Samir on 5/26/2015.
 */
public interface GlQuotationRepository extends MongoRepository<GroupLifeQuotation, QuotationId> {

    @Query(value = "{'quotationNumber' : ?0,'quotationStatus' : ?2, 'quotationId' : {'$ne' : ?1}}")
    List<GroupLifeQuotation> findQuotationByQuotNumberAndStatusByExcludingGivenQuotId(String quotationNumber, QuotationId quotationId, String quotationStatus);
}
