package com.pla.sample.sagas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.domain.AbstractAggregateRoot;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Author: Nthdimenzion
 */

@AllArgsConstructor
@Entity
public class Invoice extends AbstractAggregateRoot<String> {

    @Id
    private String id;

    @Override
    public String getIdentifier() {
        return id;
    }

    public void paid(){
        registerEvent(new InvoicePaidEvent(id));
    }


    @AllArgsConstructor
    @ToString
    @Getter
    public static class InvoicePaidEvent  implements Serializable {
        public final String invoiceId;

    }
}
