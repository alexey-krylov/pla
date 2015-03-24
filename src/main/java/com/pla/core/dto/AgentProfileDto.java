/*
 * Copyright (c) 3/16/15 7:38 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author: Samir
 * @since 1.0 13/03/2015
 */
@Getter
@Setter
public class AgentProfileDto {

    private String title;

    @NotNull(message = "{First name cannot be null}")
    @NotEmpty(message = "{First name cannot be empty}")
    private String firstName;

    @NotNull(message = "{Last name cannot be null}")
    @NotEmpty(message = "{Last name cannot be empty}")
    private String lastName;

    private Integer nrcNumber;

    private String employeeId;

    private LocalDate trainingCompleteOn=LocalDate.now();

    private DesignationDto designationDto = new DesignationDto();


    public static AgentProfileDto transFormToAgentProfileDto(Map<String, Object> agentDetail) {
        AgentProfileDto agentProfileDto = new AgentProfileDto();
        agentProfileDto.setTitle(agentDetail.get("title") != null ? (String) agentDetail.get("title") : null);
        agentProfileDto.setEmployeeId(agentDetail.get("employeeId") != null ? (String) agentDetail.get("employeeId") : null);
        agentProfileDto.setFirstName(agentDetail.get("firstName") != null ? (String) agentDetail.get("firstName") : null);
        agentProfileDto.setLastName(agentDetail.get("lastName") != null ? (String) agentDetail.get("lastName") : null);
        agentProfileDto.setNrcNumber(agentDetail.get("nrcNumber") != null ? ((Long) agentDetail.get("nrcNumber")).intValue() : null);
        DesignationDto designationDto = agentDetail.get("designationCode") != null ? new DesignationDto(((String) agentDetail.get("designationCode")), ((String) agentDetail.get("designationName"))) : null;
        agentProfileDto.setDesignationDto(designationDto);
        return agentProfileDto;
    }

}
