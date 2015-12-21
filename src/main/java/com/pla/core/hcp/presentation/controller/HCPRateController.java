package com.pla.core.hcp.presentation.controller;

import com.pla.core.hcp.application.service.HCPService;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Mohan Sharma on 12/17/2015.
 */
@RequestMapping(value = "/core/hcp")
@RestController
public class HCPRateController {
    @Autowired
    CommandGateway commandGateway;
    @Autowired
    HCPService hcpService;
}
