package com.pla.grouplife.quotation.repository;

import com.pla.grouplife.quotation.domain.model.GroupLifeQuotation;
import com.pla.sharedkernel.identifier.QuotationId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Samir on 5/26/2015.
 */
public interface GlQuotationRepository extends MongoRepository<GroupLifeQuotation, QuotationId> {
}
