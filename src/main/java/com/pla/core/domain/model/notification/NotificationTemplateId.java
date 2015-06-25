package com.pla.core.domain.model.notification;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Created by Admin on 6/25/2015.
 */
@AllArgsConstructor
@EqualsAndHashCode(of = "notificationTemplateId")
@Embeddable
@NoArgsConstructor
@Getter
@JsonSerialize(using = com.fasterxml.jackson.databind.ser.std.ToStringSerializer.class)
public class NotificationTemplateId implements Serializable {

    private String notificationTemplateId;

    public String toString() {
        return notificationTemplateId;
    }
}

