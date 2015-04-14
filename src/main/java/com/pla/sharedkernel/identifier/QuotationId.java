package com.pla.sharedkernel.identifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.io.Serializable;

/**
 * Created by Samir on 4/7/2015.
 */
@ValueObject
@Getter
@EqualsAndHashCode(of = "quotationId")
public class QuotationId implements Serializable {

    private String quotationId;

    public QuotationId(String quotationId) {
        this.quotationId = quotationId;
    }

    @Override
    public String toString() {
        return quotationId;
    }
}
