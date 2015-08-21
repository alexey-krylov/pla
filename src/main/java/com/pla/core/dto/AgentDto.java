package com.pla.core.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 4/20/2015.
 */
@Getter
@Setter
@EqualsAndHashCode
public class AgentDto {
    private Integer nrcNumber;
    private String agentId;

    public AgentDto(Integer nrcNumber,String agentId) {
        this.nrcNumber = nrcNumber;
        this.agentId = agentId;
    }
}
