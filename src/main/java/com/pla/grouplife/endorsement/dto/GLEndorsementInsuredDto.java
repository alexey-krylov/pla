package com.pla.grouplife.endorsement.dto;

import com.pla.grouplife.sharedresource.dto.InsuredDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by Samir on 8/10/2015.
 */
@Getter
@Setter
public class GLEndorsementInsuredDto {

    private List<InsuredDto> insureds;

}
