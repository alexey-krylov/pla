package com.pla.grouphealth.quotation.repository;

import com.pla.grouphealth.quotation.domain.model.GroupHealthQuotation;
import com.pla.sharedkernel.identifier.QuotationId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Samir on 5/26/2015.
 */
public interface GHQuotationRepository extends MongoRepository<GroupHealthQuotation, QuotationId> {
}
