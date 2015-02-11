package com.pla.sample.sagas;

import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: Nthdimenzion
 */

@RestController
@RequestMapping(value = "/saga")
public class SagaController {

    @Autowired
    private CommandGateway commandGateway;

    @RequestMapping(value = "/conventionalExample", method = RequestMethod.POST)
    public ResponseEntity conventionalExample(){
        commandGateway.sendAndWait(new CreateOrderCommand());
        System.out.println("Out of the request");
        return ResponseEntity.ok().build();
    }

    public static class CreateOrderCommand {
    }
}
