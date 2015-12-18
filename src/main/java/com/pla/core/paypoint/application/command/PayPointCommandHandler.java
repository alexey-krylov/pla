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

    //private static final Logger LOGGER = LoggerFactory.getLogger(PayPointCommandHandler.class);
    @Autowired
    public PayPointCommandHandler(PayPointService payPointService){
        this.payPointService=payPointService;
    }

    @CommandHandler
    public void createPayPoint(PayPointCommand paypointCommand){
        PayPoint payPoint = payPointService.createPaypoint(paypointCommand);
    }

}
