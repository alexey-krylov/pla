package com.pla.core.domain.model.notification;

import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.*;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Admin on 7/8/2015.
 */

@Document(collection = "notification_history")
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "notificationTemplateId")
@ToString(of = {"lineOfBusiness","processType","waitingFor","reminderType"})
public class NotificationHistory {

    @Id
    private String notificationHistoryId;

    private String requestNumber;

    private String roleType;

    private LineOfBusinessEnum lineOfBusiness;

    private ProcessType processType;

    private WaitingForEnum waitingFor;

    private ReminderTypeEnum reminderType;

    private byte[] reminderTemplate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate generatedOn;

    private NotificationStatusEnum notificationStatus;

    private String emailAddress;

    public NotificationHistory(String notificationHistoryId,NotificationBuilder notificationBuilder) {
        this.notificationHistoryId = notificationHistoryId;
        this.requestNumber = notificationBuilder.getRequestNumber();
        this.roleType = notificationBuilder.getRoleType();
        this.lineOfBusiness = notificationBuilder.getLineOfBusiness();
        this.processType = notificationBuilder.getProcessType();
        this.waitingFor = notificationBuilder.getWaitingFor();
        this.reminderType = notificationBuilder.getReminderType();
        this.reminderTemplate = notificationBuilder.getReminderTemplate();
        this.generatedOn = notificationBuilder.getGeneratedOn();
        this.emailAddress = notificationBuilder.getEmailAddress();
        this.notificationStatus = NotificationStatusEnum.ACTION_TAKEN;
    }

    public static NotificationBuilder builder(){
        return new NotificationBuilder();
    }

}
