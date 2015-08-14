package com.pla.core.domain.model.notification;

import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

/**
 * Created by Admin on 7/1/2015.
 */
public class NotificationBuilderUnitTest {

    @Test
    public void givenLineOfBusiness_whenTheLineOfBusinessIsValidAndNotNull_thenItShouldCreateTheNotificationWithLineOfBusiness(){
        NotificationBuilder notificationBuilder = Notification.builder();
        notificationBuilder.withLineOfBusiness(LineOfBusinessEnum.GROUP_HEALTH);
        assertThat(LineOfBusinessEnum.GROUP_HEALTH,is(notificationBuilder.getLineOfBusiness()));
        assertNotEquals(LineOfBusinessEnum.GROUP_LIFE,notificationBuilder.getLineOfBusiness());
    }


    /*
    * When the Line of business is null then it should throw an exception with error message "Product Line cannot be empty"
    * */
    @Test(expected = IllegalArgumentException.class)
    public void givenLineOfBusiness_whenTheLineOfBusinessIsNotValidOrNull_thenItShouldAnException(){
        NotificationBuilder notificationBuilder = Notification.builder();
        notificationBuilder.withLineOfBusiness(null);
    }

    @Test
    public void givenProcessType_whenTheProcessTypeIsValidAndNotNull_thenItShouldCreateTheNotificationWithProcessType(){
        NotificationBuilder notificationBuilder = Notification.builder();
        notificationBuilder.withLineOfBusiness(LineOfBusinessEnum.GROUP_HEALTH);
        notificationBuilder.withProcessType(ProcessType.QUOTATION);
        assertThat(LineOfBusinessEnum.GROUP_HEALTH, is(notificationBuilder.getLineOfBusiness()));
        assertThat(ProcessType.QUOTATION, is(notificationBuilder.getProcessType()));
        assertNotEquals(ProcessType.PROPOSAL, notificationBuilder.getProcessType());
    }


    /*
    *
    * When the given process is not valid i.e, the process is not associated with the Line Of business,
    * then it throw an exception with error message "The process Reinstatement is not associated with Group Health"
    * */
    @Test(expected = IllegalArgumentException.class)
    public void givenProcessType_whenTheProcessTypeIsValidWithLineOfBusinessOrNull_thenItShouldThrowAnException(){
        NotificationBuilder notificationBuilder = Notification.builder();
        notificationBuilder.withLineOfBusiness(LineOfBusinessEnum.GROUP_HEALTH);
        notificationBuilder.withProcessType(ProcessType.REINSTATEMENT);

    }

    @Test
    public void givenWaitingFor_whenTheWaitingForIsValidAndNotNull_thenItShouldCreateTheNotificationWithWaitingFor(){
        NotificationBuilder notificationBuilder = Notification.builder();
        notificationBuilder.withLineOfBusiness(LineOfBusinessEnum.GROUP_HEALTH);
        notificationBuilder.withProcessType(ProcessType.QUOTATION);
        notificationBuilder.withWaitingFor(WaitingForEnum.QUOTATION_RESPONSE);
        assertThat(WaitingForEnum.QUOTATION_RESPONSE, is(notificationBuilder.getWaitingFor()));
        assertThat(ProcessType.QUOTATION, is(notificationBuilder.getProcessType()));
        assertNotEquals(WaitingForEnum.CONSENT_LETTER, is(notificationBuilder.getWaitingFor()));
    }

    /*
    *
    * When the given waiting for is not valid i.e, the waiting for is not associated with the process,
    * then it throw an exception with error message " The Consent Letter is not associated with Quotation"
    * */
    @Test(expected = IllegalArgumentException.class)
    public void  givenWaitingFor_whenTheWaitingForIsNotValidWithProcessTypeOrNull_thenItShouldThrowAnException(){
        NotificationBuilder notificationBuilder = Notification.builder();
        notificationBuilder.withLineOfBusiness(LineOfBusinessEnum.GROUP_HEALTH);
        notificationBuilder.withProcessType(ProcessType.QUOTATION);
        notificationBuilder.withWaitingFor(WaitingForEnum.CONSENT_LETTER);

    }

    @Test
    public void givenReminderType_whenTheReminderTypeIsValidAndNotNull_thenItShouldCreateTheNotificationWithReminderType(){
        NotificationBuilder notificationBuilder = Notification.builder();
        notificationBuilder.withLineOfBusiness(LineOfBusinessEnum.GROUP_HEALTH);
        notificationBuilder.withProcessType(ProcessType.QUOTATION);
        notificationBuilder.withWaitingFor(WaitingForEnum.QUOTATION_RESPONSE);
        notificationBuilder.withReminderType(ReminderTypeEnum.REMINDER_1);
        assertThat(WaitingForEnum.QUOTATION_RESPONSE, is(notificationBuilder.getWaitingFor()));
        assertThat(ProcessType.QUOTATION, is(notificationBuilder.getProcessType()));
        assertThat(ReminderTypeEnum.REMINDER_1, is(notificationBuilder.getReminderType()));
        assertNotEquals(ReminderTypeEnum.REMINDER_2, is(notificationBuilder.getReminderType()));
    }



    /*
    *
    * When the given Reminder type is not valid i.e, the Reminder type is not associated with the waiting for,
    * then it throw an exception with error message " The Cancellation is not associated with Response"
    * */
    @Test(expected = IllegalArgumentException.class)
    public void  givenReminderType_whenTheReminderTypeIsNotValidWithWaitingForOrNull_thenItShouldThrowAnException(){
        NotificationBuilder notificationBuilder = Notification.builder();
        notificationBuilder.withLineOfBusiness(LineOfBusinessEnum.GROUP_HEALTH);
        notificationBuilder.withProcessType(ProcessType.QUOTATION);
        notificationBuilder.withWaitingFor(WaitingForEnum.QUOTATION_RESPONSE);
        notificationBuilder.withReminderType(ReminderTypeEnum.CANCELLATION);
    }


    @Test
    public void givenTheNotificationBuilder_thenItShouldCreateTheNotification(){
        NotificationBuilder notificationBuilder = Notification.builder();
        notificationBuilder.withLineOfBusiness(LineOfBusinessEnum.GROUP_HEALTH);
        notificationBuilder.withProcessType(ProcessType.QUOTATION);
        notificationBuilder.withWaitingFor(WaitingForEnum.QUOTATION_RESPONSE);
        notificationBuilder.withReminderType(ReminderTypeEnum.REMINDER_1);
        notificationBuilder.withEmailAddress("test@gmail.com");
        notificationBuilder.withRoleType("ROLE_GROUP_HEALTH_QUOTATION_PROCESSOR");
        notificationBuilder.withRequestNumber("R001");
        Notification notification  = notificationBuilder.createNotification(new NotificationId("N001"));
        assertNotNull(notification);
        assertThat(NotificationStatusEnum.CREATED,is(notification.getNotificationStatus()));
        assertThat(LineOfBusinessEnum.GROUP_HEALTH,is(notification.getLineOfBusiness()));
        assertThat(ProcessType.QUOTATION,is(notification.getProcessType()));
        assertThat(WaitingForEnum.QUOTATION_RESPONSE,is(notification.getWaitingFor()));
        assertThat(ReminderTypeEnum.REMINDER_1,is(notification.getReminderType()));
    }

    @Test
    public void givenTheNotification_whenTheNotificationHasBeenSent_thenTheStatusShouldMarkAsActionTaken(){
        NotificationBuilder notificationBuilder = Notification.builder();
        notificationBuilder.withLineOfBusiness(LineOfBusinessEnum.GROUP_HEALTH);
        notificationBuilder.withProcessType(ProcessType.QUOTATION);
        notificationBuilder.withWaitingFor(WaitingForEnum.QUOTATION_RESPONSE);
        notificationBuilder.withReminderType(ReminderTypeEnum.REMINDER_1);
        notificationBuilder.withEmailAddress("test@gmail.com");
        notificationBuilder.withRoleType("ROLE_GROUP_HEALTH_QUOTATION_PROCESSOR");
        notificationBuilder.withRequestNumber("R001");
        Notification notification  = notificationBuilder.createNotification(new NotificationId("N001"));
        notification.markAsActionTaken();

        assertThat(NotificationStatusEnum.ACTION_TAKEN,is(notification.getNotificationStatus()));
    }

}
