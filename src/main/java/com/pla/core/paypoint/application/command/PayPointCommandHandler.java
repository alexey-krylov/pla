package com.pla.core.paypoint.application.command;

import com.pla.core.paypoint.application.service.PayPointService;
import com.pla.core.paypoint.domain.model.PayPoint;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Rudra on 12/11/2015.
 */
@Component
public class PayPointCommandHandler {

    public PayPointService payPointService;

    @Autowired
    public PayPointCommandHandler(PayPointService payPointService){
        this.payPointService=payPointService;
    }

    @CommandHandler
    public PayPoint createPayPoint(PayPointCommand paypointCommand){
        return payPointService.createPaypoint(paypointCommand);
    }

}
