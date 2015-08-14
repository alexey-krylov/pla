package com.pla.core.domain.model.notification;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by Admin on 6/26/2015.
 */
@EqualsAndHashCode(of = "notificationId")
@Embeddable
@NoArgsConstructor
@Getter
@Setter
@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
public class NotificationId implements Serializable {

    private String notificationId;

    public NotificationId(String s) {
        this.notificationId = s;
    }

    public String toString() {
        return notificationId;
    }
}

