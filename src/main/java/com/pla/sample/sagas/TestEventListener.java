package com.pla.sample.sagas;

import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.stereotype.Component;

/**
 * Author: Nthdimenzion
 */

@Component
public class TestEventListener {

    @EventHandler
    public void handle(OrderCreatedEvent event) {
        // client generated identifiers (1)
        System.out.println("OrderManagementCommandHandler.OrderCreatedEvent " + event);
    }

}
