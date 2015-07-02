/*
 * Copyright (c) 3/16/15 8:10 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.service;

import com.pla.core.domain.model.agent.*;
import com.pla.core.dto.*;
import com.pla.core.specification.AgentLicenseNumberIsUnique;
import com.pla.core.specification.NrcNumberIsUnique;
import com.pla.sharedkernel.domain.model.OverrideCommissionApplicable;
import com.pla.sharedkernel.identifier.PlanId;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.pla.core.domain.exception.AgentException.raiseAgentLicenseNumberUniqueException;
import static com.pla.core.domain.exception.AgentException.raiseAgentNrcNumberUniqueException;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

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

    public void createAgent(String agentId, AgentProfileDto agentProfileDto, LicenseNumberDto licenseNumberDto, TeamDetailDto teamDetailDto, ContactDetailDto contactDetailDto, PhysicalAddressDto physicalAddressDto, ChannelTypeDto channelTypeDto, Set<PlanId> authorizedPlans,
                            OverrideCommissionApplicable overrideCommissionApplicable, List<AgentContactPersonDetailDto> agentContactPersonDetailDtos) {
        isNotEmpty("");
        boolean isLicenseNumberUnique = isNotEmpty(licenseNumberDto.getLicenseNumber()) ? agentLicenseNumberIsUnique.isSatisfiedBy(new LicenseNumber(licenseNumberDto.getLicenseNumber())) : true;
        AgentDto agentDto = new AgentDto(agentProfileDto.getNrcNumber(), agentId);
        boolean isNrcNumberIsUnique = agentProfileDto.getNrcNumber() != null ? nrcNumberIsUnique.isSatisfiedBy(agentDto) : true;
        if (!isLicenseNumberUnique) {
            raiseAgentLicenseNumberUniqueException();
        }
        if (!isNrcNumberIsUnique) {
            raiseAgentNrcNumberUniqueException();
        }
        Agent agent = Agent.createAgent(new AgentId(agentId));
        Agent agentDetail = populateAgentDetail(agent, agentProfileDto, licenseNumberDto, teamDetailDto, contactDetailDto, physicalAddressDto, channelTypeDto, overrideCommissionApplicable);
        Agent agentWithPlans = agentDetail.withPlans(authorizedPlans);
        if (isNotEmpty(agentContactPersonDetailDtos)) {
            agentWithPlans = agentWithPlans.withContactPersonDetail(getAgentContactPersonDetails(agentContactPersonDetailDtos));
        }
        JpaRepository<Agent, AgentId> agentRepository = jpaRepositoryFactory.getCrudRepository(Agent.class);
        agentRepository.save(agentWithPlans);
    }

    public void updateAgent(String agentId, AgentProfileDto agentProfileDto, LicenseNumberDto licenseNumberDto, TeamDetailDto teamDetailDto, ContactDetailDto contactDetailDto, PhysicalAddressDto physicalAddressDto, ChannelTypeDto channelTypeDto, Set<PlanId> authorizedPlans,
                            AgentStatus agentStatus, OverrideCommissionApplicable overrideCommissionApplicable, List<AgentContactPersonDetailDto> agentContactPersonDetailDtos) {
        boolean isLicenseNumberUnique = true;
        JpaRepository<Agent, AgentId> agentRepository = jpaRepositoryFactory.getCrudRepository(Agent.class);
        Agent agent = agentRepository.getOne(new AgentId(agentId));
        isLicenseNumberUnique = (isNotEmpty(licenseNumberDto.getLicenseNumber()) && !(agent.getLicenseNumber().getLicenseNumber().equals(licenseNumberDto.getLicenseNumber()))) ? agentLicenseNumberIsUnique.isSatisfiedBy(new LicenseNumber(licenseNumberDto.getLicenseNumber())) : true;
        if (!isLicenseNumberUnique) {
            raiseAgentLicenseNumberUniqueException();
        }
        AgentDto agentDto = new AgentDto(agentProfileDto.getNrcNumber(), agentId);
        boolean isNrcNumberIsUnique = agentProfileDto.getNrcNumber() != null ? nrcNumberIsUnique.isSatisfiedBy(agentDto) : true;
        if (!isNrcNumberIsUnique) {
            raiseAgentNrcNumberUniqueException();
        }
        Agent updatedAgent = populateAgentDetail(agent, agentProfileDto, licenseNumberDto, teamDetailDto, contactDetailDto, physicalAddressDto, channelTypeDto, overrideCommissionApplicable);
        Agent agentWithPlans = updatedAgent.withPlans(authorizedPlans);
        agentWithPlans = agentWithPlans.updateStatus(agentStatus);
        if (isNotEmpty(agentContactPersonDetailDtos)) {
            agentWithPlans = agentWithPlans.withContactPersonDetail(getAgentContactPersonDetails(agentContactPersonDetailDtos));
        }
        agentRepository.save(agentWithPlans);
    }


    private List<AgentContactPersonDetail> getAgentContactPersonDetails(List<AgentContactPersonDetailDto> agentContactPersonDetailDtos) {
        if (isEmpty(agentContactPersonDetailDtos)) {
            return null;
        }
        List<AgentContactPersonDetail> agentContactPersonDetails = agentContactPersonDetailDtos.stream().map(new Function<AgentContactPersonDetailDto, AgentContactPersonDetail>() {
            @Override
            public AgentContactPersonDetail apply(AgentContactPersonDetailDto agentContactPersonDetailDto) {
                AgentContactPersonDetail agentContactPersonDetail = new AgentContactPersonDetail(agentContactPersonDetailDto.getLineOfBusinessId(), agentContactPersonDetailDto.getTitle(), agentContactPersonDetailDto.getFullName(), agentContactPersonDetailDto.getEmailId(), agentContactPersonDetailDto.getWorkPhone(), agentContactPersonDetailDto.getFax());
                return agentContactPersonDetail;
            }
        }).collect(Collectors.toList());
        return agentContactPersonDetails;
    }

    private Agent populateAgentDetail(Agent agent, AgentProfileDto agentProfileDto, LicenseNumberDto licenseNumberDto, TeamDetailDto teamDetailDto, ContactDetailDto contactDetailDto, PhysicalAddressDto physicalAddressDto, ChannelTypeDto channelTypeDto, OverrideCommissionApplicable overrideCommissionApplicable
    ) {
        Agent agentWithProfile = agent.createWithAgentProfile(agentProfileDto.getFirstName(), agentProfileDto.getLastName(), agentProfileDto.getTrainingCompleteOn(), agentProfileDto.getDesignationDto().getCode(), agentProfileDto.getDesignationDto().getDescription(), overrideCommissionApplicable);
        Agent updatedAgentWithProfile = agentWithProfile.updateAgentProfileWithEmployeeId(agentProfileDto.getEmployeeId());
        updatedAgentWithProfile = updatedAgentWithProfile.updateAgentProfileWithNrcNumber(agentProfileDto.getNrcNumber());
        updatedAgentWithProfile = updatedAgentWithProfile.updateAgentProfileWithTitle(agentProfileDto.getTitle());
        Agent agentWithLicenseNumber = updatedAgentWithProfile.withLicenseNumber(licenseNumberDto.getLicenseNumber());
        if (teamDetailDto != null) {
            agentWithLicenseNumber = agentWithLicenseNumber.withTeamDetail(teamDetailDto.getTeamId());
        }
        GeoDetailDto geoDetailDto = contactDetailDto.getGeoDetail();
        Agent agentWithContactDetail = agentWithLicenseNumber.withContactDetail(contactDetailDto.getMobileNumber(), contactDetailDto.getHomePhoneNumber(), contactDetailDto.getWorkPhoneNumber(), contactDetailDto.getEmailAddress(), contactDetailDto.getAddressLine1(), contactDetailDto.getAddressLine2(), geoDetailDto.getPostalCode(), geoDetailDto.getProvinceCode(), geoDetailDto.getCityCode());
        GeoDetailDto physicalGeoDetailDto = physicalAddressDto.getPhysicalGeoDetail();
        Agent agentWithPhysicalAddress = agentWithContactDetail.withPhysicalAddress(physicalAddressDto.getPhysicalAddressLine1(), physicalAddressDto.getPhysicalAddressLine2(), physicalGeoDetailDto.getPostalCode(), physicalGeoDetailDto.getProvinceCode(), physicalGeoDetailDto.getCityCode());
        Agent agentWithChannelType = agentWithPhysicalAddress.withChannelType(channelTypeDto.getChannelCode(), channelTypeDto.getChannelName());
        return agentWithChannelType;
    }
}
