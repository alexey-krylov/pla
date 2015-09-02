package com.pla.individuallife.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.Getter;

import java.io.Serializable;

/**
 * Created by Admin on 8/27/2015.
 */
@Getter
public class ILQuotationSharedEvent implements Serializable {

    private QuotationId quotationId;

    public ILQuotationSharedEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }

}
