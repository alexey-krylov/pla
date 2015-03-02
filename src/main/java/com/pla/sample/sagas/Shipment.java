package com.pla.sample.sagas;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.domain.AbstractAggregateRoot;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
* Author: Nthdimenzion
*/
@AllArgsConstructor
@Entity
class Shipment extends AbstractAggregateRoot<String> {

    @Id
    private String id;

    @Override
    public String getIdentifier() {
        return id;
    }

    public void ship(){
        registerEvent(new ShippingArrivedEvent(id));
    }


    @ToString
    @AllArgsConstructor
    @Getter
    public static class ShippingArrivedEvent {
        public final String shipmentId;

    }
}
