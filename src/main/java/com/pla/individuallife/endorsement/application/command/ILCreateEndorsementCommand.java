package com.pla.individuallife.endorsement.application.command;

import com.pla.individuallife.policy.presentation.dto.ILPolicyDto;
import com.pla.individuallife.sharedresource.model.ILEndorsementType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Raghu Bandi on 8/27/2015.
 */
@Getter
@Setter
@AllArgsConstructor
public class ILCreateEndorsementCommand {

    private UserDetails userDetails;
    private ILPolicyDto ilPolicyDto;
}
