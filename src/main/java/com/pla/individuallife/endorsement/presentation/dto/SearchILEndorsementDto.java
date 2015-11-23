package com.pla.individuallife.endorsement.presentation.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Prasant on 28-Oct-15.
 */
@Getter
@Setter
public class SearchILEndorsementDto {
    private String policyHolderSurname;
    private String policyHolderFirstName;
    private String policyHolderNrc;
    private String policyNumber;
    private String lifeAssuredSurName;
    private String lifeAssuredFirstName;
    private String lifeAssuredNRC;
}
