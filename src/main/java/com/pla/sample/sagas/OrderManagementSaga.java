package com.pla.sample.sagas;

import com.pla.sample.sagas.Invoice.InvoicePaidEvent;
import com.pla.sample.sagas.OrderHeader.OrderCreatedEvent;
import com.pla.sample.sagas.Shipment.ShippingArrivedEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.scheduling.EventScheduler;
import org.axonframework.saga.annotation.AbstractAnnotatedSaga;
import org.axonframework.saga.annotation.EndSaga;
import org.axonframework.saga.annotation.SagaEventHandler;
import org.axonframework.saga.annotation.StartSaga;
import org.joda.money.Money;
import org.nthdimenzion.common.AppConstants;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import org.joda.time.Duration;

import java.math.BigDecimal;

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
    @Autowired
    private transient EventScheduler eventScheduler;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(OrderCreatedEvent event) {
        System.out.println("StartSaga " + event);
        String shipmentId = idGenerator.nextId();
        String invoiceId = idGenerator.nextId();
        associateWith("shipmentId", shipmentId);
        associateWith("invoiceId", invoiceId);
        associateWith("orderId", event.orderId);
        // send the commands
        commandGateway.send(new PrepareShippingCommand(shipmentId));
        commandGateway.send(new CreateInvoiceCommand(invoiceId));
        eventScheduler.schedule(Duration.standardMinutes(1L),new OrderPaymentInstallmentEvent(event.orderId,Money.of(AppConstants.DEFAULT_CURRENCY, BigDecimal.valueOf(100d))));
    }

    @SagaEventHandler(associationProperty = "shipmentId")
    public void handle(ShippingArrivedEvent event) {
        System.out.println("ShippingArrivedEvent Saga " + event);
        delivered = true;
        /*if (paid) {
            end();
        }*/
    }

    @SagaEventHandler(associationProperty = "invoiceId")
    public void handle(InvoicePaidEvent event) {
        System.out.println("InvoicePaidEvent Saga " + event);
        paid = true;
        /*if (delivered) {
            end();
        }*/
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void handle(OrderPaymentInstallmentEvent event) {
        System.out.println("OrderPaymentInstallment Saga " + event);
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

    @AllArgsConstructor
    @ToString
    @Getter
    public static class OrderPaymentInstallmentEvent {

        private String orderId;
        private Money installmentAmount;
    }
}
