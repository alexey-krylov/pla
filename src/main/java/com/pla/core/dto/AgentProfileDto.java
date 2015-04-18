/*
 * Copyright (c) 3/16/15 7:38 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;
import org.nthdimenzion.presentation.LocalJodaDateDeserializer;
import org.nthdimenzion.presentation.LocalJodaDateSerializer;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author: Samir
 * @since 1.0 13/03/2015
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @JsonDeserialize(using = LocalJodaDateDeserializer.class)
    @JsonSerialize(using = LocalJodaDateSerializer.class)
    private LocalDate trainingCompleteOn;

    private DesignationDto designationDto = new DesignationDto();

    private String nrcNumberInString;

    public AgentProfileDto(String title, String firstName, String lastName, Integer nrcNumber, String employeeId, LocalDate trainingCompleteOn, DesignationDto designationDto) {
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nrcNumber = nrcNumber;
        this.employeeId = employeeId;
        this.trainingCompleteOn = trainingCompleteOn;
        this.designationDto = designationDto;

    }

    public static AgentProfileDto transFormToAgentProfileDto(Map<String, Object> agentDetail) {
        AgentProfileDto agentProfileDto = new AgentProfileDto();
        agentProfileDto.setTitle(agentDetail.get("title") != null ? (String) agentDetail.get("title") : null);
        agentProfileDto.setEmployeeId(agentDetail.get("employeeId") != null ? (String) agentDetail.get("employeeId") : null);
        agentProfileDto.setFirstName(agentDetail.get("firstName") != null ? (String) agentDetail.get("firstName") : null);
        agentProfileDto.setLastName(agentDetail.get("lastName") != null ? (String) agentDetail.get("lastName") : null);
        agentProfileDto.setNrcNumber(agentDetail.get("nrcNumber") != null ? ((Integer) agentDetail.get("nrcNumber")) : null);
        agentProfileDto.setTrainingCompleteOn(agentDetail.get("trainingCompletedOn") != null ? new LocalDate(agentDetail.get("trainingCompletedOn")) : null);
        DesignationDto designationDto = agentDetail.get("designationCode") != null ? new DesignationDto(((String) agentDetail.get("designationCode")), ((String) agentDetail.get("designationName"))) : null;
        agentProfileDto.setDesignationDto(designationDto);
        return agentProfileDto;
    }

    public void setNrcNumberInString(String nrcNumberInString) {
        nrcNumberInString = nrcNumberInString.replaceAll("/", "").trim();
        this.nrcNumber = Integer.valueOf(nrcNumberInString);
        this.nrcNumberInString = nrcNumberInString;
    }

    public String getNrcNumberInString() {
        String nrc = String.valueOf(getNrcNumber());
        String part1 = nrc.substring(0, 6);
        String part2 = nrc.substring(6, 8);
        String part3 = nrc.substring(8, 9);
        nrc = part1.concat("/").concat(part2).concat("/").concat(part3);
        return nrc;
    }

    public Integer getNrcNumber() {
        return nrcNumber;
    }


}
