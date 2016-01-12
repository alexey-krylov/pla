package com.pla.grouphealth.claim.cashless.application.command;

import com.pla.grouphealth.claim.cashless.application.service.PreAuthorizationRequestService;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequestId;
import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationClaimantDetailCommand;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Author - Mohan Sharma Created on 1/4/2016.
 */
@Component
public class PreAuthorizationRequestCommandHandler {
    @Autowired
    PreAuthorizationRequestService preAuthorizationRequestService;

    @CommandHandler
    public PreAuthorizationRequestId createUpdate(PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand){
        return preAuthorizationRequestService.createUpdatePreAuthorizationRequest(preAuthorizationClaimantDetailCommand);
    }
}
