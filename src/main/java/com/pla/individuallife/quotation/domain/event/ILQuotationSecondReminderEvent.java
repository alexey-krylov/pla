package com.pla.individuallife.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.Getter;

import java.io.Serializable;

/**
 * Created by Admin on 10/9/2015.
 */
@Getter
public class ILQuotationSecondReminderEvent  implements Serializable {

    private QuotationId quotationId;

    public ILQuotationSecondReminderEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
