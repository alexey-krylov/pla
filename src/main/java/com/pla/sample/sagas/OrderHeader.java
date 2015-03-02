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
@Entity
@AllArgsConstructor
public class OrderHeader extends AbstractAggregateRoot<String> {

    @Id
    private String orderId;

    OrderHeader() {
    }

    @Override
    public String getIdentifier() {
        return orderId;
    }

    public void approve(){
        registerEvent(new OrderCreatedEvent(orderId));
    }

    @ToString
    @AllArgsConstructor
    @Getter
    public static class OrderCreatedEvent  implements Serializable {
        public final String orderId;
    }


}
