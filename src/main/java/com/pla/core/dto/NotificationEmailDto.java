package com.pla.core.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 7/10/2015.
 */
@Getter
@Setter
public class NotificationEmailDto {

    private String notificationId;

    private String subject;

    private String emailBody;

    private String[] recipientMailAddress;

}
