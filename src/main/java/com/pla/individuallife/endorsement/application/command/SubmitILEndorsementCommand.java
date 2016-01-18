package com.pla.individuallife.endorsement.application.command;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Samir on 7/14/2015.
 */
@Getter
@Setter
public class SubmitILEndorsementCommand {

    private String endorsementId;

    private UserDetails userDetails;

    private String comment;
}
