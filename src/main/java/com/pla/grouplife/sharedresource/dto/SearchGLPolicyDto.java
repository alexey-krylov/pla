package com.pla.grouplife.sharedresource.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Samir on 7/9/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class SearchGLPolicyDto {

    private String policyNumber;

    private String policyHolderName;

    private String proposalNumber;

    private  String clientId;
}
