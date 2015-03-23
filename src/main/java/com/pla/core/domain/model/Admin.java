/*
 * Copyright (c) 3/5/15 5:32 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.pla.core.application.agent.*;
import com.pla.core.domain.exception.BenefitDomainException;
import com.pla.core.domain.exception.TeamDomainException;
import com.pla.core.domain.model.agent.Agent;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.sharedkernel.domain.model.BenefitStatus;
import com.pla.sharedkernel.identifier.PlanId;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import java.util.Set;

import static com.pla.core.domain.exception.AgentException.raiseAgentLicenseNumberUniqueException;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@ValueObject
public class Admin {


    public Benefit createBenefit(boolean isUniqueBenefitName, String benefitId, String benefitName) {
        if (!isUniqueBenefitName) {
            throw new BenefitDomainException("Benefit name already satisfied");
        }
        return new Benefit(new BenefitId(benefitId), new BenefitName(benefitName), BenefitStatus.ACTIVE);
    }

    public Benefit updateBenefit(Benefit benefit, String newBenefitName, boolean benefitIsUpdatable) {
        if (!benefitIsUpdatable) {
            throw new BenefitDomainException("Benefit is associated with active coverage");
        }
        Benefit updatedBenefit = benefit.updateBenefitName(new BenefitName(newBenefitName));
        return updatedBenefit;
    }

    public Benefit inactivateBenefit(Benefit benefit) {
        Benefit updatedBenefit = benefit.inActivate();
        return updatedBenefit;
    }

    public Team createTeam(boolean isTeamUnique, String teamId, String teamName, String teamCode, String regionCode, String branchCode
            , String employeeId, LocalDate fromDate, String firstName, String lastName) {
        if (!isTeamUnique) {
            throw new TeamDomainException("Team name and Team Code already satisfied");
        }
        TeamLeader teamLeader = new TeamLeader(employeeId, firstName, lastName);
        TeamLeaderFulfillment teamLeaderFulfillment = new TeamLeaderFulfillment(teamLeader, fromDate);
        return new Team(teamId, teamName, teamCode, regionCode, branchCode, employeeId, teamLeaderFulfillment, Boolean.TRUE);
    }

    public Team updateTeamLead(Team team, String employeeId, String firstName, String lastName, LocalDate fromDate) {
        Team updatedTeam = team.assignTeamLeader(employeeId, firstName, lastName, fromDate);
        return updatedTeam;
    }

    public Agent createAgent(boolean isLicenseNumberUnique, CreateAgentCommand createAgentCommand) {
        if (!isLicenseNumberUnique) {
            raiseAgentLicenseNumberUniqueException("Agent cannot be created as license number is in use");
        }
        Agent agent = Agent.createAgent(new AgentId(createAgentCommand.getAgentId()));
        Agent agentDetail = populateAgentDetail(agent, createAgentCommand.getAgentProfile(), createAgentCommand.getLicenseNumber(), createAgentCommand.getTeamDetail(), createAgentCommand.getContactDetail(), createAgentCommand.getPhysicalAddress(), createAgentCommand.getChannelType());
        Agent agentWithPlans = agentDetail.withPlans(createAgentCommand.getAuthorizePlansToSell());
        return agentWithPlans;
    }

    public Agent updateAgent(Agent agent, boolean isLicenseNumberUnique, UpdateAgentCommand updateAgentCommand) {
        if (!isLicenseNumberUnique) {
            raiseAgentLicenseNumberUniqueException("Agent cannot be updated as license number is in use");
        }
        Agent agentDetail = populateAgentDetail(agent, updateAgentCommand.getAgentProfile(), updateAgentCommand.getLicenseNumber(), updateAgentCommand.getTeamDetail(), updateAgentCommand.getContactDetail(), updateAgentCommand.getPhysicalAddress(), updateAgentCommand.getChannelType());
        Agent agentWithPlans = agentDetail.withPlans(updateAgentCommand.getAuthorizePlansToSell());
        Agent agentWithUpdatedStatus = agentWithPlans.updateStatus(updateAgentCommand.getAgentStatus());
        return agentWithUpdatedStatus;
    }

    private Agent populateAgentDetail(Agent agent, AgentProfileDto agentProfileDto, LicenseNumberDto licenseNumberDto, TeamDetailDto teamDetailDto, ContactDetailDto contactDetailDto, PhysicalAddressDto physicalAddressDto, ChannelTypeDto channelTypeDto) {
        Agent agentWithProfile = agent.createWithAgentProfile(agentProfileDto.getFirstName(), agentProfileDto.getLastName(), agentProfileDto.getTrainingCompleteOn(), agentProfileDto.getDesignationDto().getCode(), agentProfileDto.getDesignationDto().getDescription());
        Agent updatedAgentWithProfile = agentWithProfile.updateAgentProfileWithEmployeeId(agentProfileDto.getEmployeeId());
        updatedAgentWithProfile = updatedAgentWithProfile.updateAgentProfileWithNrcNumber(agentProfileDto.getNrcNumber());
        updatedAgentWithProfile = updatedAgentWithProfile.updateAgentProfileWithTitle(agentProfileDto.getTitle());
        Agent agentWithLicenseNumber = updatedAgentWithProfile.withLicenseNumber(licenseNumberDto.getLicenseNumber());
        Agent agentWithTeamDetail = agentWithLicenseNumber.withTeamDetail(teamDetailDto.getTeamId());
        GeoDetailDto geoDetailDto = contactDetailDto.getGeoDetail();
        Agent agentWithContactDetail = agentWithTeamDetail.withContactDetail(contactDetailDto.getMobileNumber(), contactDetailDto.getHomePhoneNumber(), contactDetailDto.getWorkPhoneNumber(), contactDetailDto.getEmailAddress(), contactDetailDto.getAddressLine1(), contactDetailDto.getAddressLine2(), geoDetailDto.getPostalCode(), geoDetailDto.getProvince(), geoDetailDto.getCity());
        GeoDetailDto physicalGeoDetailDto = physicalAddressDto.getPhysicalGeoDetail();
        Agent agentWithPhysicalAddress = agentWithContactDetail.withPhysicalAddress(physicalAddressDto.getPhysicalAddressLine1(), physicalAddressDto.getPhysicalAddressLine2(), physicalGeoDetailDto.getPostalCode(), physicalGeoDetailDto.getProvince(), physicalGeoDetailDto.getCity());
        Agent agentWithChannelType = agentWithPhysicalAddress.withChannelType(channelTypeDto.getChannelCode(), channelTypeDto.getChannelName());
        return agentWithChannelType;
    }
}
