package com.pla.sample.aggregateroot.application;

import com.pla.sample.aggregateroot.domain.Quotation;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.axonframework.repository.Repository;

/**
 * @author: Samir
 * @since 1.0 10/02/2015
 */
@Component
public class QuotationCommandHandler {

    private IIdGenerator idGenerator;

    private Repository<Quotation> quotationRepository;

    @Autowired
    public QuotationCommandHandler(IIdGenerator idGenerator,Repository<Quotation> quotationRepository){
        this.idGenerator=idGenerator;
        this.quotationRepository = quotationRepository;
    }


    @CommandHandler
    public void createQuotationCommandHandler(CreateQuotationCommand createQuotationCommand){
        String quotationId = idGenerator.nextId();
        Quotation quotation = new Quotation(quotationId,createQuotationCommand.getQuotationName(),createQuotationCommand.getClientName(),createQuotationCommand.getContactNumber());
        quotationRepository.add(quotation);
    }
}
