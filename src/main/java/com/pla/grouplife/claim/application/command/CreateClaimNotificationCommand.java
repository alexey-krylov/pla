package com.pla.grouplife.claim.application.command;

import com.pla.sharedkernel.application.CreateNotificationCommand;
import com.pla.sharedkernel.domain.model.ClaimId;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by ak
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class CreateClaimNotificationCommand extends CreateNotificationCommand{

    private ClaimId claimId;

    public CreateClaimNotificationCommand(ClaimId claimId,String roleType, LineOfBusinessEnum lineOfBusiness,
                                             ProcessType processType, WaitingForEnum waitingFor,ReminderTypeEnum reminderType){
        super(roleType,lineOfBusiness,processType,waitingFor,reminderType);
        this.claimId = claimId;
    }

}

