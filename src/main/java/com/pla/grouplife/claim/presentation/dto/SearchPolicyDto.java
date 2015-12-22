package com.pla.grouplife.claim.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by nthdimensioncompany
 */

@Getter
@Setter
@NoArgsConstructor

public class SearchPolicyDto {

    private String policyNumber;

    private String policyHolderName;

    private  String clientId;


    public SearchPolicyDto(String policyNumber,String policyHolderName,String clientId){
        this.policyNumber=policyNumber;
        this.policyHolderName=policyHolderName;
        this.clientId=clientId;

    }

}

