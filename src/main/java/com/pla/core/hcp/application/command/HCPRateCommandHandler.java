package com.pla.core.hcp.application.command;

import com.pla.core.hcp.application.service.HCPService;
import com.pla.core.hcp.domain.model.HCP;
import com.pla.core.hcp.domain.model.HCPRate;
import com.pla.core.hcp.presentation.controller.HCPRateService;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Mohan Sharma on 12/17/2015.
 */
@Component
public class HCPRateCommandHandler {
    @Autowired
    HCPRateService hcpRateService;

    @CommandHandler
    public HCPRate uploadHCPServiceRates(UploadHCPServiceRatesCommand uploadHCPServiceRatesCommand){
        return hcpRateService.uploadHCPServiceRates(uploadHCPServiceRatesCommand);
    }
}
