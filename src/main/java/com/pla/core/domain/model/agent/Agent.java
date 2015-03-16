/*
 * Copyright (c) 3/13/15 8:03 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.agent;

import com.pla.sharedkernel.domain.model.OverrideCommissionApplicable;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.*;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.*;
import java.util.Set;

/**
 * @author: Samir
 * @since 1.0 13/03/2015
 */

@Entity
@EqualsAndHashCode(of = {"licenseNumber","agentProfile"})
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PACKAGE)
public class Agent implements ICrudEntity {

    @EmbeddedId
    private AgentId agentId;

    @Enumerated(EnumType.STRING)
    private AgentStatus agentStatus;

    @Embedded
    private AgentProfile agentProfile;

    @Embedded
    private LicenseNumber licenseNumber;

    @Embedded
    private TeamDetail teamDetail;

    @Embedded
    private ContactDetail contactDetail;

    @Embedded
    private PhysicalAddress physicalAddress;

    @ElementCollection
    @JoinTable(name = "AGENT_AUTHORIZED_PLAN", joinColumns = @JoinColumn(name = "agentId"))
    private Set<PlanId> authorizePlansToSell;

    @Enumerated(EnumType.STRING)
    private OverrideCommissionApplicable overrideCommissionApplicable;

    private String channelType;
}
