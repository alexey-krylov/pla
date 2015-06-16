package com.pla.individuallife.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.Getter;

import java.io.Serializable;

/**
 * Created by pradyumna on 16-06-2015.
 */
@Getter
public class ILQuotationVersionEvent implements Serializable {

    private static final long serialVersionUID = -8183046577107008783L;
    private String quotationARId;
    private QuotationId quotationId;

    public ILQuotationVersionEvent(String quotationARId, QuotationId quotationId) {
        this.quotationARId = quotationARId;
        this.quotationId = quotationId;
    }
}
