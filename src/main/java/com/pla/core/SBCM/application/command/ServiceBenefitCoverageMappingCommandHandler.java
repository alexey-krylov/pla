package com.pla.core.SBCM.application.command;

import com.pla.core.SBCM.application.service.SBCMService;
import com.pla.core.SBCM.domain.model.ServiceBenefitCoverageMapping;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Mohan Sharma on 12/28/2015.
 */
@Service
public class ServiceBenefitCoverageMappingCommandHandler {
    @Autowired
    private SBCMService sbcmService;

    @CommandHandler
    public ServiceBenefitCoverageMapping createServiceBenefitCoverageMapping(CreateSBCMCommand createSBCMCommand){
        return sbcmService.createServiceBenefitCoverageMapping(createSBCMCommand);
    }
}
