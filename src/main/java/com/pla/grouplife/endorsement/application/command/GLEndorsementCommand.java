package com.pla.grouplife.endorsement.application.command;

import com.pla.grouplife.endorsement.dto.GLEndorsementInsuredDto;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.sharedkernel.identifier.EndorsementId;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Samir on 10/4/2015.
 */
@Getter
@Setter
public class GLEndorsementCommand {

    private GLEndorsementInsuredDto glEndorsementInsuredDto;

    private EndorsementId endorsementId;

    private GLEndorsementType glEndorsementType;

    private UserDetails userDetails;
}
