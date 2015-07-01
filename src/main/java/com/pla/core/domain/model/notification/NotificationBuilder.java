package com.pla.core.domain.model.notification;

import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.Getter;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Admin on 6/26/2015.
 */
@Getter
public class NotificationBuilder {

    private String requestNumber;

    private String roleType;

    private LineOfBusinessEnum lineOfBusiness;

    private ProcessType processType;

    private WaitingForEnum waitingFor;

    private ReminderTypeEnum reminderType;

    private byte[] reminderTemplate;

    private String emailAddress;

    public NotificationBuilder() {
    }

    public NotificationBuilder withLineOfBusiness(LineOfBusinessEnum lineOfBusiness){
        checkArgument(lineOfBusiness!=null,"Product Line cannot be empty");
        this.lineOfBusiness = lineOfBusiness;
        return this;
    }

    public NotificationBuilder withProcessType(ProcessType processType){
        checkArgument(processType != null, "Process cannot be empty");
        checkArgument(this.lineOfBusiness.isValidProcess(processType), "The process " + processType.toString() + " is not associated with " + lineOfBusiness.toString());
        this.processType = processType;
        return this;
    }

    public NotificationBuilder withWaitingFor(WaitingForEnum waitingFor){
        checkArgument(waitingFor != null, "Waiting For cannot be empty");
        checkArgument(this.processType.isValidWaitingFor(waitingFor),"The "+waitingFor.toString()+" is not associated with "+this.processType.toString());
        this.waitingFor = waitingFor;
        return this;
    }

    public NotificationBuilder withReminderType(ReminderTypeEnum reminderType){
        checkArgument(reminderType!=null,"Reminder Type cannot be empty");
        checkArgument(this.waitingFor.isValidReminderType(reminderType),"The "+reminderType.toString()+" is not associated with "+this.waitingFor.toString());
        this.reminderType = reminderType;
        return this;
    }

    public NotificationBuilder withRoleType(String roleType){
        checkArgument(roleType!=null,"");
        this.roleType = roleType;
        return this;
    }

    public NotificationBuilder withReminderTemplate(byte[] reminderTemplate){
        checkArgument(reminderTemplate!=null,"Reminder Template cannot be empty");
        this.reminderTemplate  = reminderTemplate;
        return this;
    }

    public NotificationBuilder withRequestNumber(String requestNumber){
        checkArgument(requestNumber!=null,"Request Number cannot be empty");
        this.requestNumber = requestNumber;
        return this;
    }

    public NotificationBuilder withEmailAddress(String emailAddress){
        checkArgument(emailAddress!=null,"Email Address cannot be empty");
        this.emailAddress = emailAddress;
        return this;
    }


    public Notification createNotification(NotificationId notificationId){
        return new Notification(notificationId,this);
    }
}
