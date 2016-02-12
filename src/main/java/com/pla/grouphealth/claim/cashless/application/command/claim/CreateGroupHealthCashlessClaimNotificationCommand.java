package com.pla.grouphealth.claim.cashless.application.command.claim;

import com.pla.sharedkernel.application.CreateNotificationCommand;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by Mohan Sharma on 1/14/2016.
 */
@NoArgsConstructor
@Getter
@Setter
public class CreateGroupHealthCashlessClaimNotificationCommand extends CreateNotificationCommand{

    private String groupHealthCashlessClaimId;
    private List<String> pendingDocumentList;

    public CreateGroupHealthCashlessClaimNotificationCommand(String groupHealthCashlessClaimId, String roleType, LineOfBusinessEnum lineOfBusiness, ProcessType processType, WaitingForEnum waitingFor, ReminderTypeEnum reminderType, List<String> pendingDocumentList){
        super(roleType,lineOfBusiness,processType,waitingFor,reminderType);
        this.groupHealthCashlessClaimId = groupHealthCashlessClaimId;
        this.pendingDocumentList = pendingDocumentList;
    }
}
