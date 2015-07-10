package com.pla.core.domain.model.notification;

import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Admin on 6/24/2015.
 */
@Document(collection = "notification_template")
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "notificationTemplateId")
@ToString(of = {"lineOfBusiness","processType","waitingFor","reminderType"})
public class NotificationTemplate {

    @Id
    private NotificationTemplateId notificationTemplateId;

    private LineOfBusinessEnum lineOfBusiness;

    private ProcessType processType;

    private WaitingForEnum waitingFor;

    private ReminderTypeEnum reminderType;

    private byte[] reminderFile;

    private NotificationTemplate(NotificationTemplateId notificationTemplateId, LineOfBusinessEnum lineOfBusiness, ProcessType processType, WaitingForEnum waitingFor, ReminderTypeEnum reminderType){
        this.notificationTemplateId = notificationTemplateId;
        this.lineOfBusiness = lineOfBusiness;
        this.processType = processType;
        this.waitingFor = waitingFor;
        this.reminderType = reminderType;
    }

    public static NotificationTemplate createNotification(NotificationTemplateId notificationTemplateId,LineOfBusinessEnum lineOfBusiness,ProcessType processType,WaitingForEnum waitingFor,ReminderTypeEnum reminderType){
        return new NotificationTemplate(notificationTemplateId,lineOfBusiness,processType,waitingFor,reminderType);
    }

    public NotificationTemplate withReminderFile(byte[] reminderFile){
        this.reminderFile = reminderFile;
        return this;
    }

    public String getFileName(){
        return (this.lineOfBusiness.name()+"_"+this.processType.name()+"_"+this.waitingFor.name()+"_"+this.reminderType.name()).toLowerCase();
    }
}
