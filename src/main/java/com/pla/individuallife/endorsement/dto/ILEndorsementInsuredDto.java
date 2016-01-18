package com.pla.individuallife.endorsement.dto;

import com.pla.individuallife.sharedresource.dto.ProposerDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Raghu on 8/10/2015.
 */
@Getter
@Setter
public class ILEndorsementInsuredDto {

    private List<ProposerDto> insureds;

}
