package com.pla.core.domain.model.notification;

import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.*;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Admin on 6/26/2015.
 */
@Entity
@Table(name = "notification")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = {"notificationId"})
@ToString
public class Notification implements ICrudEntity,Serializable {

    @EmbeddedId
    private NotificationId notificationId;

    private String requestNumber;

    private String roleType;

    @Enumerated(EnumType.STRING)
    private LineOfBusinessEnum lineOfBusiness;

    @Enumerated(EnumType.STRING)
    private ProcessType processType;

    @Enumerated(EnumType.STRING)
    private WaitingForEnum waitingFor;

    @Enumerated(EnumType.STRING)
    private ReminderTypeEnum reminderType;

    @Lob
    private byte[] reminderTemplate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate generatedOn;

    @Enumerated(EnumType.STRING)
    private NotificationStatusEnum notificationStatus;

    private String emailAddress;

    public Notification(NotificationId notificationId,NotificationBuilder notificationBuilder) {
        this.notificationId = notificationId;
        this.requestNumber = notificationBuilder.getRequestNumber();
        this.roleType = notificationBuilder.getRoleType();
        this.lineOfBusiness = notificationBuilder.getLineOfBusiness();
        this.processType = notificationBuilder.getProcessType();
        this.waitingFor = notificationBuilder.getWaitingFor();
        this.reminderType = notificationBuilder.getReminderType();
        this.reminderTemplate = notificationBuilder.getReminderTemplate();
        this.generatedOn = LocalDate.now();
        this.emailAddress = notificationBuilder.getEmailAddress();
        this.notificationStatus = NotificationStatusEnum.CREATED;
    }

    public static NotificationBuilder builder(){
        return new NotificationBuilder();
    }

    public Notification markAsActionTaken(){
        this.notificationStatus = NotificationStatusEnum.ACTION_TAKEN;
        return this;
    }



}
