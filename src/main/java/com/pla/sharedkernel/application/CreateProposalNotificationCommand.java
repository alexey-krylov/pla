package com.pla.sharedkernel.application;

import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.identifier.ProposalId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Admin on 6/30/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateProposalNotificationCommand extends CreateNotificationCommand{

    private ProposalId proposalId;

    public CreateProposalNotificationCommand(ProposalId proposalId,String roleType, LineOfBusinessEnum lineOfBusiness,
                                             ProcessType processType, WaitingForEnum waitingFor,ReminderTypeEnum reminderType){
        super(roleType,lineOfBusiness,processType,waitingFor,reminderType);
        this.proposalId = proposalId;
    }
}
