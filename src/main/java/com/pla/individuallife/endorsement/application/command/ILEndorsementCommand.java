package com.pla.individuallife.endorsement.application.command;

import com.pla.individuallife.endorsement.dto.ILEndorsementInsuredDto;
import com.pla.individuallife.sharedresource.model.ILEndorsementType;
import com.pla.sharedkernel.identifier.EndorsementId;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Samir on 10/4/2015.
 */
@Getter
@Setter
public class ILEndorsementCommand {

    private ILEndorsementInsuredDto ilEndorsementInsuredDto;

    private EndorsementId endorsementId;

    private ILEndorsementType ilEndorsementType;

    private UserDetails userDetails;
}
