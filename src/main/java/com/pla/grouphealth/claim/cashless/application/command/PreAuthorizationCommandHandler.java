package com.pla.grouphealth.claim.cashless.application.command;

import com.pla.grouphealth.claim.cashless.application.service.PreAuthorizationService;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Mohan Sharma on 1/4/2016.
 */
@Component
public class PreAuthorizationCommandHandler {
    @Autowired
    PreAuthorizationService preAuthorizationService;

    @CommandHandler
    public int uploadPreAuthorizationDetails(UploadPreAuthorizationCommand uploadPreAuthorizationCommand){
        return preAuthorizationService.uploadPreAuthorizationDetails(uploadPreAuthorizationCommand);
    }
}
