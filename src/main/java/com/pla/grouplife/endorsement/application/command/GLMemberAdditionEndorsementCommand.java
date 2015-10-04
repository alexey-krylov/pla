package com.pla.grouplife.endorsement.application.command;

import com.pla.grouplife.endorsement.dto.GLEndorsementInsuredDto;
import com.pla.sharedkernel.identifier.EndorsementId;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Samir on 10/4/2015.
 */
@Getter
@Setter
public class GLMemberAdditionEndorsementCommand {

    private GLEndorsementInsuredDto glEndorsementInsuredDto;

    private EndorsementId endorsementId;
}
