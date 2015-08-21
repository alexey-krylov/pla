package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Samir on 7/9/2015.
 */
@Getter
public class Agent {

    private String agentCode;

    private String agentName;

    public Agent(String agentCode, String agentName) {
        this.agentCode = agentCode;
        this.agentName = agentName;
    }
}
