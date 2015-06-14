package com.pla.individuallife.quotation.query;

import com.pla.individuallife.quotation.domain.model.ILQuotation;
import com.pla.sharedkernel.identifier.QuotationId;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by pradyumna on 03-06-2015.
 */
public interface ILQuotationRepository extends PagingAndSortingRepository<ILQuotation, QuotationId> {

}
