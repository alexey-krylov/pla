package com.pla.core.SBCM.presentation.controller;

import com.pla.core.SBCM.application.service.SBCMService;
import com.pla.core.hcp.presentation.controller.HCPRateService;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Mohan Sharma on 12/24/2015.
 */
@RequestMapping(value = "/core/sbcm")
@RestController
public class ServiceBenefitCoverageMappingController {
    @Autowired
    CommandGateway commandGateway;
    @Autowired
    SBCMService sbcmService;
}
