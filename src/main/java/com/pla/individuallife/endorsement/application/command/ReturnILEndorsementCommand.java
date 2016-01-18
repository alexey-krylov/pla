package com.pla.individuallife.endorsement.application.command;

import com.pla.sharedkernel.domain.model.EndorsementStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Admin on 10/19/2015.
 */
@Getter
@Setter
public class ReturnILEndorsementCommand {
    private String endorsementId;
    private UserDetails userDetails;
    private EndorsementStatus status;
    private String comment;
}
