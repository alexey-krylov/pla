package com.pla.sharedkernel.domain.model;

import lombok.Getter;

import java.util.EnumSet;

/**
 * Created by pradyumna on 18-06-2015.
 */
@Getter
public enum WaitingForEnum {

    QUOTATION_RESPONSE("Response", EnumSet.of(ReminderTypeEnum.REMINDER_1, ReminderTypeEnum.REMINDER_2)) {
        @Override
        public boolean isValidReminderType(ReminderTypeEnum reminderType) {
            return getReminderTypes().contains(reminderType);
        }
    },

    MANDATORY_DOCUMENTS("Mandatory Documents",EnumSet.of(ReminderTypeEnum.REMINDER_1,ReminderTypeEnum.REMINDER_2,ReminderTypeEnum.CANCELLATION)) {
        @Override
        public boolean isValidReminderType(ReminderTypeEnum reminderType) {
            return getReminderTypes().contains(reminderType);
        }
    },

    CONSENT_LETTER("Consent Letter",EnumSet.of(ReminderTypeEnum.REMINDER_1,ReminderTypeEnum.REMINDER_2,ReminderTypeEnum.CANCELLATION)) {
        @Override
        public boolean isValidReminderType(ReminderTypeEnum reminderType) {
            return getReminderTypes().contains(reminderType);
        }
    },

    MEDICALS("Medicals",EnumSet.of(ReminderTypeEnum.REMINDER_1,ReminderTypeEnum.REMINDER_2,ReminderTypeEnum.CANCELLATION)) {
        @Override
        public boolean isValidReminderType(ReminderTypeEnum reminderType) {
            return getReminderTypes().contains(reminderType);
        }
    },

    INITIAL_PREMIUM("Initial Premium",EnumSet.of(ReminderTypeEnum.REMINDER_1,ReminderTypeEnum.REMINDER_2,ReminderTypeEnum.CANCELLATION)) {
        @Override
        public boolean isValidReminderType(ReminderTypeEnum reminderType) {
            return getReminderTypes().contains(reminderType);
        }
    },

    SUBSEQUENT_PREMIUM("Subsequent Premium",EnumSet.of(ReminderTypeEnum.REMINDER_1,ReminderTypeEnum.REMINDER_2,ReminderTypeEnum.LAPSE)) {
        @Override
        public boolean isValidReminderType(ReminderTypeEnum reminderType) {
            return getReminderTypes().contains(reminderType);
        }
    },

    PREMIUM("Premium",EnumSet.of(ReminderTypeEnum.REMINDER_1,ReminderTypeEnum.REMINDER_2,ReminderTypeEnum.CANCELLATION)) {
        @Override
        public boolean isValidReminderType(ReminderTypeEnum reminderType) {
            return getReminderTypes().contains(reminderType);
        }
    };

    private String displayName;
    private EnumSet<ReminderTypeEnum> reminderTypes;

    WaitingForEnum(String displayName, EnumSet<ReminderTypeEnum> reminderTypes) {
        this.displayName = displayName;
        this.reminderTypes = reminderTypes;
    }

    public String toString() {
        return displayName;
    }
    public abstract boolean isValidReminderType(ReminderTypeEnum reminderType);
}
