package com.pla.sample.sagas;

import lombok.AllArgsConstructor;
import lombok.ToString;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Author: Nthdimenzion
 */

public class OrderManagementSaga extends AbstractAnnotatedSaga {

    private boolean paid = false;
    private boolean delivered = false;

    @Autowired
    private transient CommandGateway commandGateway;
    @Autowired
    private transient IIdGenerator idGenerator;

    //@StartSaga
    //@SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent event) {
        // client generated identifiers (1)
        System.out.println("StartSaga " + event);
        String shipmentId = idGenerator.nextId();
        String invoiceId = idGenerator.nextId();
        // associate the Saga with these values, before sending the commands (2)
        associateWith("shipmentId", shipmentId);
        associateWith("invoiceId", invoiceId);
        // send the commands
        commandGateway.send(new PrepareShippingCommand(shipmentId));
        commandGateway.send(new CreateInvoiceCommand(invoiceId));
    }


    @AllArgsConstructor
    @ToString
    public static class PrepareShippingCommand {
        public final String shipmentId;
    }

    @AllArgsConstructor
    @ToString
    public static class CreateInvoiceCommand {
        public final String invoiceId;
    }
}
