package com.pla.grouplife.endorsement.application.command;

import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Samir on 8/27/2015.
 */
@Getter
@Setter
@AllArgsConstructor
public class GLCreateEndorsementCommand {

    private UserDetails userDetails;

    private GLEndorsementType endorsementType;

    private String policyId;
}
