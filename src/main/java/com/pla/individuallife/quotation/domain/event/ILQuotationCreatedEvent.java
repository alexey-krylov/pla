package com.pla.individuallife.quotation.domain.event;

import lombok.Getter;

import java.io.Serializable;

/**
 * Created by pradyumna on 16-06-2015.
 */
@Getter
public class ILQuotationCreatedEvent implements Serializable {


    private static final long serialVersionUID = 4401616796204536261L;
    private String quotationARId;

    public ILQuotationCreatedEvent(String quotationARId) {
        this.quotationARId = quotationARId;
    }
}
