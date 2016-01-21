package com.pla.grouphealth.claim.cashless.application.command;

import com.pla.grouphealth.claim.cashless.application.service.PreAuthorizationRequestService;
import com.pla.grouphealth.claim.cashless.domain.exception.GenerateReminderFollowupException;
import com.pla.grouphealth.claim.cashless.domain.model.CommentDetail;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequestId;
import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationClaimantDetailCommand;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Author - Mohan Sharma Created on 1/4/2016.
 */
@Component
public class PreAuthorizationRequestCommandHandler {
    @Autowired
    PreAuthorizationRequestService preAuthorizationRequestService;

    @CommandHandler
    public PreAuthorizationRequestId createUpdate(PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand) throws GenerateReminderFollowupException {
        return preAuthorizationRequestService.createUpdatePreAuthorizationRequest(preAuthorizationClaimantDetailCommand, Boolean.FALSE);
    }

    @CommandHandler
    public Set<CommentDetail> updateComments(UpdateCommentCommand updateCommentCommand){
        return preAuthorizationRequestService.updateComments(updateCommentCommand);
    }
}
