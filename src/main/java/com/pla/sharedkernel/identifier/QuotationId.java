package com.pla.sharedkernel.identifier;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by Samir on 4/7/2015.
 */

@Getter
@ValueObject
@EqualsAndHashCode(of = "quotationId")
@Embeddable
public class QuotationId implements Serializable {

    private String quotationId;

    private QuotationId() {
    }

    public QuotationId(String s) {
        this.quotationId = s;
    }

    @Override
    public String toString() {
        return quotationId;
    }
}
