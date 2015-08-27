package com.pla.grouplife.endorsement.presentation.dto;

import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Created by Samir on 8/27/2015.
 */
@Getter
@Setter
public class SearchGLEndorsementDto {

    private GLEndorsementType endorsementType;

    private String endorsementNumber;

    private String policyHolderName;

    private String policyNumber;

    private String endorsementId;

    private List<Map<String,Object>> endorsementTypes;

}
