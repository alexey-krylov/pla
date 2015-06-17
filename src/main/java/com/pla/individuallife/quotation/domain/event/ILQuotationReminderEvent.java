package com.pla.individuallife.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.Getter;

import java.io.Serializable;

/**
 * Created by pradyumna on 16-06-2015.
 */
@Getter
public class ILQuotationReminderEvent implements Serializable {

    private static final long serialVersionUID = -7918572966035770882L;

    private QuotationId quotationId;
    public ILQuotationReminderEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
