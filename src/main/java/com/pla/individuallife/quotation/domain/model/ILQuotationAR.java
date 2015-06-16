package com.pla.individuallife.quotation.domain.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.axonframework.eventsourcing.annotation.AbstractAnnotatedAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by pradyumna on 13-06-2015.
 */
@Entity(name = "individual_quotation_ar")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ILQuotationAR extends AbstractAnnotatedAggregateRoot<String> {

    @AggregateIdentifier
    @Id
    @Column(name = "quotation_ar_id")
    private String quotationARId;

    private int versionNumber;

    public ILQuotationAR(String quotationARId) {
        this.versionNumber = 0;
        this.quotationARId = quotationARId;
    }

    public int incrementVersion() {
        this.versionNumber++;
        return this.versionNumber;
    }


}
