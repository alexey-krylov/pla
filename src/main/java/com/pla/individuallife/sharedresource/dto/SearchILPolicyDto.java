package com.pla.individuallife.sharedresource.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Raghu on 7/9/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class SearchILPolicyDto {

    private String policyNumber;

    private String policyHolderName;

    private String proposalNumber;

    private  String clientId;
}
