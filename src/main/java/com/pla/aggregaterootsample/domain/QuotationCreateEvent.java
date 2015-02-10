package com.pla.aggregaterootsample.domain;

import lombok.Getter;

/**
 * @author: Samir
 * @since 1.0 10/02/2015
 */
@Getter
public class QuotationCreateEvent {


    private String quotationId;

    private String clientName;


    public QuotationCreateEvent(String quotationId, String clientName) {
        this.quotationId = quotationId;
        this.clientName = clientName;

    }
}
