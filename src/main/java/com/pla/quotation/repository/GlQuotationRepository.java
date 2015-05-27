package com.pla.quotation.repository;

import com.pla.quotation.domain.model.grouplife.GroupLifeQuotation;
import com.pla.sharedkernel.identifier.QuotationId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Samir on 5/26/2015.
 */
public interface GlQuotationRepository extends MongoRepository<GroupLifeQuotation, QuotationId> {
}
