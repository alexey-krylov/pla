package com.pla.individuallife.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;

import java.io.Serializable;

/**
 * Created by pradyumna on 16-06-2015.
 */
public class ILQuotationPurgeEvent implements Serializable {

    private static final long serialVersionUID = -3474893540180435648L;
    private QuotationId quotationId;
    public ILQuotationPurgeEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
