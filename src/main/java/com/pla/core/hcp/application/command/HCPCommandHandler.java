package com.pla.core.hcp.application.command;

import com.pla.core.hcp.application.service.HCPService;
import com.pla.core.hcp.domain.model.HCP;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Author - Mohan Sharma Created on 12/17/2015.
 */
@Component
public class HCPCommandHandler {
    @Autowired
    HCPService hcpService;

    @CommandHandler
    public HCP createHCP(CreateOrUpdateHCPCommand createOrUpdateHCPCommand){
        return hcpService.createOrUpdateHCP(createOrUpdateHCPCommand);
    }
}
