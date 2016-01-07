package com.pla.grouplife.endorsement.application.command;

import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.model.vo.GLEndorsementInsured;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 28-Dec-15.
 */
@Getter
@Setter
@AllArgsConstructor
public class GLCreateFLCEndorsementCommand {
    private GLEndorsementType endorsementType;
    private String policyId;
    private GLEndorsementInsured glEndorsementInsured;
}
