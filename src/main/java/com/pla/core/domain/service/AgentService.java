/*
 * Copyright (c) 3/16/15 8:10 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.service;

import com.pla.core.domain.model.agent.Agent;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.core.domain.model.agent.AgentStatus;
import com.pla.core.domain.model.agent.LicenseNumber;
import com.pla.core.dto.*;
import com.pla.core.specification.AgentLicenseNumberIsUnique;
import com.pla.core.specification.NrcNumberIsUnique;
import com.pla.sharedkernel.identifier.PlanId;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

import static com.pla.core.domain.exception.AgentException.raiseAgentLicenseNumberUniqueException;
import static com.pla.core.domain.exception.AgentException.raiseAgentNrcNumberUniqueException;

/**
 * @author: Samir
 * @since 1.0 16/03/2015
 */
@DomainService
public class AgentService {

    private AgentLicenseNumberIsUnique agentLicenseNumberIsUnique;

    private NrcNumberIsUnique nrcNumberIsUnique;

    private JpaRepositoryFactory jpaRepositoryFactory;

    @Autowired
    public AgentService(AgentLicenseNumberIsUnique agentLicenseNumberIsUnique, NrcNumberIsUnique nrcNumberIsUnique,
                        JpaRepositoryFactory jpaRepositoryFactory) {
        this.agentLicenseNumberIsUnique = agentLicenseNumberIsUnique;
        this.jpaRepositoryFactory = jpaRepositoryFactory;
        this.nrcNumberIsUnique = nrcNumberIsUnique;
    }

    public void createAgent(String agentId, AgentProfileDto agentProfileDto, LicenseNumberDto licenseNumberDto, TeamDetailDto teamDetailDto, ContactDetailDto contactDetailDto, PhysicalAddressDto physicalAddressDto, ChannelTypeDto channelTypeDto, Set<PlanId> authorizedPlans) {
        UtilValidator.isNotEmpty("");
        boolean isLicenseNumberUnique = UtilValidator.isNotEmpty(licenseNumberDto.getLicenseNumber()) ? agentLicenseNumberIsUnique.isSatisfiedBy(new LicenseNumber(licenseNumberDto.getLicenseNumber())) : true;
        boolean isNrcNumberIsUnique = agentProfileDto.getNrcNumber() != 0 ? nrcNumberIsUnique.isSatisfiedBy(agentProfileDto.getNrcNumber()) : true;
        if (!isLicenseNumberUnique) {
            raiseAgentLicenseNumberUniqueException();
        }
        if (!isNrcNumberIsUnique) {
            raiseAgentNrcNumberUniqueException();
        }
        Agent agent = Agent.createAgent(new AgentId(agentId));
        Agent agentDetail = populateAgentDetail(agent, agentProfileDto, licenseNumberDto, teamDetailDto, contactDetailDto, physicalAddressDto, channelTypeDto);
        Agent agentWithPlans = agentDetail.withPlans(authorizedPlans);
        JpaRepository<Agent, AgentId> agentRepository = jpaRepositoryFactory.getCrudRepository(Agent.class);
        agentRepository.save(agentWithPlans);
    }

    public void updateAgent(String agentId, AgentProfileDto agentProfileDto, LicenseNumberDto licenseNumberDto, TeamDetailDto teamDetailDto, ContactDetailDto contactDetailDto, PhysicalAddressDto physicalAddressDto, ChannelTypeDto channelTypeDto, Set<PlanId> authorizedPlans, AgentStatus agentStatus) {
        boolean isLicenseNumberUnique = true;
        JpaRepository<Agent, AgentId> agentRepository = jpaRepositoryFactory.getCrudRepository(Agent.class);
        Agent agent = agentRepository.getOne(new AgentId(agentId));
        isLicenseNumberUnique = (UtilValidator.isNotEmpty(licenseNumberDto.getLicenseNumber()) && !(agent.getLicenseNumber().getLicenseNumber().equals(licenseNumberDto.getLicenseNumber()))) ? agentLicenseNumberIsUnique.isSatisfiedBy(new LicenseNumber(licenseNumberDto.getLicenseNumber())) : true;
        if (!isLicenseNumberUnique) {
            raiseAgentLicenseNumberUniqueException();
        }
        boolean isNrcNumberIsUnique = agentProfileDto.getNrcNumber() != 0 ? nrcNumberIsUnique.isSatisfiedBy(agentProfileDto.getNrcNumber()) : true;
        if (!isNrcNumberIsUnique) {
            raiseAgentNrcNumberUniqueException();
        }
        Agent updatedAgent = populateAgentDetail(agent, agentProfileDto, licenseNumberDto, teamDetailDto, contactDetailDto, physicalAddressDto, channelTypeDto);
        Agent agentWithPlans = updatedAgent.withPlans(authorizedPlans);
        Agent agentWithUpdatedStatus = agentWithPlans.updateStatus(agentStatus);
        agentRepository.save(agentWithUpdatedStatus);
    }


    private Agent populateAgentDetail(Agent agent, AgentProfileDto agentProfileDto, LicenseNumberDto licenseNumberDto, TeamDetailDto teamDetailDto, ContactDetailDto contactDetailDto, PhysicalAddressDto physicalAddressDto, ChannelTypeDto channelTypeDto) {
        Agent agentWithProfile = agent.createWithAgentProfile(agentProfileDto.getFirstName(), agentProfileDto.getLastName(), agentProfileDto.getTrainingCompleteOn(), agentProfileDto.getDesignationDto().getCode(), agentProfileDto.getDesignationDto().getDescription());
        Agent updatedAgentWithProfile = agentWithProfile.updateAgentProfileWithEmployeeId(agentProfileDto.getEmployeeId());
        updatedAgentWithProfile = updatedAgentWithProfile.updateAgentProfileWithNrcNumber(agentProfileDto.getNrcNumber());
        updatedAgentWithProfile = updatedAgentWithProfile.updateAgentProfileWithTitle(agentProfileDto.getTitle());
        Agent agentWithLicenseNumber = updatedAgentWithProfile.withLicenseNumber(licenseNumberDto.getLicenseNumber());
        Agent agentWithTeamDetail = agentWithLicenseNumber.withTeamDetail(teamDetailDto.getTeamId());
        GeoDetailDto geoDetailDto = contactDetailDto.getGeoDetail();
        Agent agentWithContactDetail = agentWithTeamDetail.withContactDetail(contactDetailDto.getMobileNumber(), contactDetailDto.getHomePhoneNumber(), contactDetailDto.getWorkPhoneNumber(), contactDetailDto.getEmailAddress(), contactDetailDto.getAddressLine1(), contactDetailDto.getAddressLine2(), geoDetailDto.getPostalCode(), geoDetailDto.getProvinceCode(), geoDetailDto.getCityCode());
        GeoDetailDto physicalGeoDetailDto = physicalAddressDto.getPhysicalGeoDetail();
        Agent agentWithPhysicalAddress = agentWithContactDetail.withPhysicalAddress(physicalAddressDto.getPhysicalAddressLine1(), physicalAddressDto.getPhysicalAddressLine2(), physicalGeoDetailDto.getPostalCode(), physicalGeoDetailDto.getProvinceCode(), physicalGeoDetailDto.getCityCode());
        Agent agentWithChannelType = agentWithPhysicalAddress.withChannelType(channelTypeDto.getChannelCode(), channelTypeDto.getChannelName());
        return agentWithChannelType;
    }
}
