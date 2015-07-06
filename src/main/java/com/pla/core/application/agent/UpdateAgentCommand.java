/*
 * Copyright (c) 3/16/15 8:19 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application.agent;

import com.google.common.collect.Sets;
import com.pla.core.domain.model.agent.AgentStatus;
import com.pla.core.dto.*;
import com.pla.sharedkernel.domain.model.OverrideCommissionApplicable;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Set;

/**
 * @author: Samir
 * @since 1.0 16/03/2015
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateAgentCommand {

    private AgentProfileDto agentProfile = new AgentProfileDto();

    private LicenseNumberDto licenseNumber = new LicenseNumberDto();

    private TeamDetailDto teamDetail = new TeamDetailDto();

    private ContactDetailDto contactDetail = new ContactDetailDto();

    private PhysicalAddressDto physicalAddress = new PhysicalAddressDto();

    private Set<PlanId> authorizePlansToSell = Sets.newHashSet();

    private OverrideCommissionApplicable overrideCommissionApplicable;

    private ChannelTypeDto channelType = new ChannelTypeDto();

    private String agentId;

    private UserDetails userDetails;

    private AgentStatus agentStatus;

    private List<AgentContactPersonDetailDto> contactPersonDetails;

    private String registrationNumber;
}
