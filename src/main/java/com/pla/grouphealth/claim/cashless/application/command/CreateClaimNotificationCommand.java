package com.pla.grouphealth.claim.cashless.application.command;

import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequestId;
import com.pla.sharedkernel.application.CreateNotificationCommand;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.identifier.ProposalId;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created by Mohan Sharma on 1/14/2016.
 */
@NoArgsConstructor
@Getter
public class CreateClaimNotificationCommand extends CreateNotificationCommand{

    private PreAuthorizationRequestId preAuthorizationRequestId;
    private List<String> pendingDocumentList;

    public CreateClaimNotificationCommand(PreAuthorizationRequestId preAuthorizationRequestId, String roleType, LineOfBusinessEnum lineOfBusiness, ProcessType processType, WaitingForEnum waitingFor, ReminderTypeEnum reminderType, List<String> pendingDocumentList){
        super(roleType,lineOfBusiness,processType,waitingFor,reminderType);
        this.preAuthorizationRequestId = preAuthorizationRequestId;
        this.pendingDocumentList = pendingDocumentList;
    }
}
