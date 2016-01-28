package com.pla.individuallife.endorsement.application.command;

import com.pla.individuallife.endorsement.presentation.dto.ILEndorsementDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Created by Raghu Bandi on 25-Jan-2016.
 */

@Getter
@Setter
@AllArgsConstructor
public class ILUpdateEndorsementCommand {

    private UserDetails userDetails;
    private ILEndorsementDto iLEndorsementDto;
}
