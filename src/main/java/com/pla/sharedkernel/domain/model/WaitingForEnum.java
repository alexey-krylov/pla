package com.pla.sharedkernel.domain.model;

import com.pla.core.domain.model.notification.ReminderTypeEnum;
import lombok.Getter;

import java.util.EnumSet;

/**
 * Created by pradyumna on 18-06-2015.
 */
@Getter
public enum WaitingForEnum {

    QUOTATION_RESPONSE("Response", EnumSet.of(ReminderTypeEnum.REMINDER_1, ReminderTypeEnum.REMINDER_2)),

    MANDATORY_DOCUMENTS("Mandatory Documents",EnumSet.of(ReminderTypeEnum.REMINDER_1,ReminderTypeEnum.REMINDER_2,ReminderTypeEnum.CANCELLATION)),

    CONSENT_LETTER("Consent Letter",EnumSet.of(ReminderTypeEnum.REMINDER_1,ReminderTypeEnum.REMINDER_2,ReminderTypeEnum.CANCELLATION)),

    MEDICALS("Medicals",EnumSet.of(ReminderTypeEnum.REMINDER_1,ReminderTypeEnum.REMINDER_2,ReminderTypeEnum.CANCELLATION)),

    INITIAL_PREMIUM("Initial Premium",EnumSet.of(ReminderTypeEnum.REMINDER_1,ReminderTypeEnum.REMINDER_2,ReminderTypeEnum.CANCELLATION)),

    SUBSEQUENT_PREMIUM("Subsequent Premium",EnumSet.of(ReminderTypeEnum.REMINDER_1,ReminderTypeEnum.REMINDER_2,ReminderTypeEnum.LAPSE)),

    PREMIUM("Premium",EnumSet.of(ReminderTypeEnum.REMINDER_1,ReminderTypeEnum.REMINDER_2,ReminderTypeEnum.CANCELLATION));

    private String displayName;
    private EnumSet<ReminderTypeEnum> reminderTypes;

    WaitingForEnum(String displayName, EnumSet<ReminderTypeEnum> reminderTypes) {
        this.displayName = displayName;
        this.reminderTypes = reminderTypes;
    }

    public String toString() {
        return displayName;
    }
}
