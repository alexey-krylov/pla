package com.pla.sample.sagas;

import com.pla.sample.sagas.OrderManagementSaga.CreateInvoiceCommand;
import com.pla.sample.sagas.OrderManagementSaga.PrepareShippingCommand;
import com.pla.sample.sagas.SagaController.CreateOrderCommand;
import lombok.*;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.axonframework.domain.AbstractAggregateRoot;
import org.axonframework.repository.Repository;
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

    @Autowired
    private Repository<OrderHeader> orderHeaderRepository;

    @Autowired
    private Repository<Shipment> shipmentRepository;

    @Autowired
    private Repository<Invoice> invoiceRepository;



    @CommandHandler
    public void createOrder(CreateOrderCommand createOrderCommand){
        System.out.println("CreateOrderCommand");
        OrderHeader orderHeader = new OrderHeader(idGenerator.nextId());
        orderHeader.approve();
        orderHeaderRepository.add(orderHeader);
        System.out.println("Command over");
    }

    @CommandHandler
    public void prepareShipping(PrepareShippingCommand prepareShippingCommand) {
        System.out.println(prepareShippingCommand);
        Shipment shipment = new Shipment(prepareShippingCommand.shipmentId);
        shipment.ship();
        shipmentRepository.add(shipment);

    }

    @CommandHandler
    public void createInvoice(CreateInvoiceCommand createInvoiceCommand) {
        System.out.println(createInvoiceCommand);
        Invoice invoice = new Invoice(createInvoiceCommand.invoiceId);
        invoice.paid();
        invoiceRepository.add(invoice);
    }



}
