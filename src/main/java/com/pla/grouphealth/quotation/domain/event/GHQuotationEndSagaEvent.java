package com.pla.grouphealth.quotation.domain.event;

import com.pla.sharedkernel.identifier.QuotationId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by Samir on 6/18/2015.
 */
@EqualsAndHashCode
@Getter
@ToString
public class GHQuotationEndSagaEvent {

    private QuotationId quotationId;

    public GHQuotationEndSagaEvent(QuotationId quotationId) {
        this.quotationId = quotationId;
    }
}
