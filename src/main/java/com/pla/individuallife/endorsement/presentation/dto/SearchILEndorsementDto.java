package com.pla.individuallife.endorsement.presentation.dto;

import com.pla.individuallife.sharedresource.model.ILEndorsementType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Created by Prasant on 28-Oct-15.
 */
@Getter
@Setter
public class SearchILEndorsementDto {

    private String policyHolderSurname;
    private String policyHolderFirstName;
    private String policyHolderNrc;
    private String lifeAssuredSurName;
    private String lifeAssuredFirstName;
    private String lifeAssuredNRC;
    private String policyNumber;
    private String endorsementId;
    private ILEndorsementType endorsementType;
    private String endorsementRequestNumber;
    private String endorsementNumber;
    private String policyHolderName;
    private List<Map<String,String>> endorsementTypes;
}
