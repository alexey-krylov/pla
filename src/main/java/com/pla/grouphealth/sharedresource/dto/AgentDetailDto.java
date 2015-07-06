package com.pla.grouphealth.sharedresource.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Samir on 4/14/2015.
 */
@Getter
@Setter
public class AgentDetailDto {

    private String agentId;

    private String proposerName;

    private String agentName;

    private String teamName;

    private String branchName;

    private String agentSalutation;

    private String agentMobileNumber;

    private boolean active;

}
