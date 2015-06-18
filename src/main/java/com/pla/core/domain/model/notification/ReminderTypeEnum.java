package com.pla.core.domain.model.notification;

/**
 * Created by pradyumna on 18-06-2015.
 */
public enum ReminderTypeEnum {

    REMINDER_1("Reminder 1"), REMINDER_2("Reminder 2"), CANCELLATION("Cancellation"), LAPSE("Lapse");


    private String description;

    ReminderTypeEnum(String displayName) {
        this.description = displayName;
    }

    public String toString() {
        return description;
    }
}
