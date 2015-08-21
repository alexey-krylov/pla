package com.pla.core.domain.model.notification;

/**
 * Created by Admin on 6/26/2015.
 */
public enum NotificationStatusEnum {

    CREATED("Created"),ACTION_TAKEN("Action Taken");

    private String description;

    NotificationStatusEnum(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
