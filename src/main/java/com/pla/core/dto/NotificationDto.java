package com.pla.core.dto;

import com.pla.core.domain.model.notification.NotificationStatusEnum;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 7/10/2015.
 */
@Getter
@Setter
public class NotificationDto {

    private String notificationId;

    private String requestNumber;

    private String roleType;

    private LineOfBusinessEnum lineOfBusiness;

    private ProcessType processType;

    private WaitingForEnum waitingFor;

    private ReminderTypeEnum reminderType;

    private byte[] reminderTemplate;

    private String generatedOn;

    private NotificationStatusEnum notificationStatus;

    private String emailAddress;
}
