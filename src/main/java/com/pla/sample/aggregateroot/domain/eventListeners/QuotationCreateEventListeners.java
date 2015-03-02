package com.pla.sample.aggregateroot.domain.eventListeners;

import com.pla.sample.aggregateroot.domain.QuotationCreateEvent;
import org.axonframework.eventhandling.annotation.EventHandler;
import org.springframework.stereotype.Component;

/**
 * @author: Samir
 * @since 1.0 10/02/2015
 */
@Component
public class QuotationCreateEventListeners {


    @EventHandler
    public void crateQuotationEventListener(QuotationCreateEvent quotationCreateEvent){
        System.out.println(quotationCreateEvent.getQuotationId()+"**"+quotationCreateEvent.getClientName());
    }
}
