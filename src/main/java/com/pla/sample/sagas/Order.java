package com.pla.sample.sagas;

import lombok.AllArgsConstructor;
import org.axonframework.domain.AbstractAggregateRoot;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
* Author: Nthdimenzion
*/
@Entity
@AllArgsConstructor
public class Order extends AbstractAggregateRoot<String> {

    @Id
    private String orderId;

    Order() {
    }

    @Override
    public String getIdentifier() {
        return orderId;
    }

    public void approve(){
        registerEvent(new OrderCreatedEvent(orderId));
    }

}
