package com.pla.sharedkernel.application;

import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.*;

/**
 * Created by Admin on 6/30/2015.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateNotificationCommand {

    private String roleType;
    private LineOfBusinessEnum lineOfBusiness;
    private ProcessType processType;
    private WaitingForEnum waitingFor;
    private ReminderTypeEnum reminderType;
}
