package com.pla.sharedkernel.application;

import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Admin on 7/13/2015.
 */

@Getter
@Setter
@NoArgsConstructor
public class CreateNotificationHistoryCommand extends CreateNotificationCommand{

    private String requestNumber;
    private byte[] template;
    private String notificationId;
    private String[] recipientMailAddress;

    public CreateNotificationHistoryCommand(String requestNumber,String roleType, LineOfBusinessEnum lineOfBusiness,
                                            ProcessType processType, WaitingForEnum waitingFor,ReminderTypeEnum reminderType,String[] recipientMailAddress,byte[] template,String notificationId){
        super(roleType,lineOfBusiness,processType,waitingFor,reminderType);
        this.requestNumber = requestNumber;
        this.recipientMailAddress = recipientMailAddress;
        this.template = template;
        this.notificationId = notificationId;
    }
}
