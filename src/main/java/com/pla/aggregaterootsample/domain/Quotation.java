package com.pla.aggregaterootsample.domain;

import org.axonframework.domain.AbstractAggregateRoot;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author: Samir
 * @since 1.0 10/02/2015
 */

@Entity
public class Quotation extends AbstractAggregateRoot<String> {

    @Id
    private String quotationId;

    private String quotationName;

    private String clientName;

    private String clientContactNumber;


    Quotation(){

    }

    public Quotation(String quotationId,String quotationName,String clientName,String clientContactNumber){
        this.quotationId = quotationId;
        this.clientContactNumber = clientContactNumber;
        this.clientName = clientName;
        this.quotationName = quotationName;
        registerEvent(new QuotationCreateEvent(quotationId,clientName));
    }

    @Override
    public String getIdentifier() {
        return quotationId;
    }
}
