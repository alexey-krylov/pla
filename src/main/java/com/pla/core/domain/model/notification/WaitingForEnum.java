package com.pla.core.domain.model.notification;

import java.util.EnumSet;

/**
 * Created by pradyumna on 18-06-2015.
 */
public enum WaitingForEnum {

    QUOTATION_RESPONSE("Response", EnumSet.of(ReminderTypeEnum.REMINDER_1, ReminderTypeEnum.REMINDER_2));
    private String displayName;
    private EnumSet<ReminderTypeEnum> reminderTypes;

    WaitingForEnum(String displayName, EnumSet<ReminderTypeEnum> reminderTypes) {
        this.displayName = displayName;
        this.reminderTypes = reminderTypes;
    }

    public EnumSet<ReminderTypeEnum> getReminderTypes() {
        return reminderTypes;
    }

    public String toString() {
        return displayName;
    }
}
