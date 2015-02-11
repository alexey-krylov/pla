package com.pla.sample.sagas;

import com.pla.sample.sagas.OrderManagementSaga.CreateInvoiceCommand;
import com.pla.sample.sagas.OrderManagementSaga.PrepareShippingCommand;
import com.pla.sample.sagas.SagaController.CreateOrderCommand;
import lombok.*;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.domain.AbstractAggregateRoot;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.Id;

/**
 * Author: Nthdimenzion
 */

@Component
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class OrderManagementCommandHandler {

    @Autowired
    private IIdGenerator idGenerator;

    @CommandHandler
    public void createOrder(CreateOrderCommand createOrderCommand){
        System.out.println("CreateOrderCommand");
        Order order = new Order(idGenerator.nextId());
        order.approve();
        System.out.println("Command over");

    }

    @CommandHandler
    public void prepareShipping(PrepareShippingCommand prepareShippingCommand) {
        System.out.println(prepareShippingCommand);

    }

    @CommandHandler
    public void createInvoice(CreateInvoiceCommand createInvoiceCommand) {
        System.out.println(createInvoiceCommand);
    }


    @AllArgsConstructor
    private class Shipment extends AbstractAggregateRoot<String>{

        @Id
        private String id;

        @Override
        public String getIdentifier() {
            return id;
        }

        public void ship(){
            registerEvent(new ShippingArrivedEvent(id));
        }
    }


    @AllArgsConstructor
    @ToString
    @Getter
    public static class ShippingArrivedEvent {
        public final String shipmentId;

    }
}
