/*
 * Copyright (c) 3/13/15 8:03 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model.agent;

import com.pla.core.application.exception.AgentApplicationException;
import com.pla.sharedkernel.domain.model.EmailAddress;
import com.pla.sharedkernel.domain.model.OverrideCommissionApplicable;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.*;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.*;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.pla.core.domain.exception.AgentException.raiseAgentUpdateNotAllowedException;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * @author: Samir
 * @since 1.0 13/03/2015
 */

@Entity
@EqualsAndHashCode(of = {"licenseNumber", "agentProfile"})
@ToString
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter(value = AccessLevel.PACKAGE)
public class Agent implements ICrudEntity {

    @EmbeddedId
    private AgentId agentId;

    @Enumerated(EnumType.STRING)
    private AgentStatus agentStatus;

    @Embedded
    private AgentProfile agentProfile;

    @Embedded
    @Getter
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

    @Embedded
    private ChannelType channelType;

    private Agent(AgentId agentId, AgentStatus agentStatus) {
        this.agentStatus = agentStatus;
        this.agentId = agentId;
    }

    public static Agent createAgent(AgentId agentId) {
        return new Agent(agentId, AgentStatus.ACTIVE);
    }

    public Agent createWithAgentProfile(String firstName, String lastName, LocalDate trainingCompleteOn, String designationCode, String designationDescription, OverrideCommissionApplicable overrideCommissionApplicable) {
        Designation designation = new Designation(designationCode, designationDescription);
        applyOverrideCommissionEligibility(designationCode, designationDescription, overrideCommissionApplicable);
        this.agentProfile = new AgentProfile(firstName, lastName, trainingCompleteOn, designation);
        return this;
    }

    public Agent updateAgentProfileWithTitle(String title) {
        AgentProfile agentProfile = this.getAgentProfile();
        AgentProfile updatedAgentProfile = agentProfile.withTitle(title);
        this.agentProfile = updatedAgentProfile;
        return this;
    }


    public Agent updateAgentProfileWithEmployeeId(String employeeId) {
        AgentProfile agentProfile = this.getAgentProfile();
        AgentProfile updatedAgentProfile = agentProfile.withEmployeeId(employeeId);
        this.agentProfile = updatedAgentProfile;
        return this;
    }

    public Agent updateAgentProfileWithNrcNumber(Integer nrcNumber) {
        AgentProfile agentProfile = this.getAgentProfile();
        AgentProfile updatedAgentProfile = agentProfile.withNrcNumber(nrcNumber);
        this.agentProfile = updatedAgentProfile;
        return this;
    }

    public Agent withLicenseNumber(String licenseNumber) {
        this.licenseNumber = new LicenseNumber(licenseNumber);
        return this;
    }

    public Agent withTeamDetail(String teamId) {
        TeamDetail teamDetail = new TeamDetail(teamId);
        this.teamDetail = teamDetail;
        return this;
    }

    public Agent withContactDetail(String mobileNumber, String homePhoneNumber, String workPhoneNumber,
                                   String emailAddress, String addressLine1, String addressLine2, Integer postalCode, String province, String city) {
        if (AgentStatus.TERMINATED.equals(this.agentStatus)) {
            raiseAgentUpdateNotAllowedException("Agent detail cannot be updated as it is terminated");
        }
        EmailAddress email = new EmailAddress(emailAddress);
        GeoDetail geoDetail = new GeoDetail(postalCode, province, city);
        ContactDetail contactDetail = new ContactDetail(mobileNumber, email, addressLine1, geoDetail).addHomePhoneNumber(homePhoneNumber).addWorkPhoneNumber(workPhoneNumber);
        checkArgument(contactDetail != null);
        ContactDetail updatedContactDetail = contactDetail.addAddressLine2(addressLine2);
        this.contactDetail = updatedContactDetail;
        return this;
    }

    public Agent withPhysicalAddress(String addressLine1, String addressLine2, Integer postalCode, String province, String city) {
        if (AgentStatus.TERMINATED.equals(this.agentStatus)) {
            raiseAgentUpdateNotAllowedException("Agent detail cannot be updated as it is terminated");
        }
        GeoDetail geoDetail = new GeoDetail(postalCode, province, city);
        PhysicalAddress physicalAddress = new PhysicalAddress(addressLine1, geoDetail);
        PhysicalAddress updatedPhysicalAddress = physicalAddress.addAddressLine2(addressLine2);
        this.physicalAddress = updatedPhysicalAddress;
        return this;
    }

    public Agent withPlans(Set<PlanId> planIds) {
        if (AgentStatus.TERMINATED.equals(this.agentStatus)) {
            raiseAgentUpdateNotAllowedException("Agent detail cannot be updated as it is terminated");
        }
        checkArgument(isNotEmpty(planIds));
        this.authorizePlansToSell = planIds;
        return this;
    }

    public Agent withChannelType(String channelCode, String channelDescription) {
        if (AgentStatus.TERMINATED.equals(this.agentStatus)) {
            raiseAgentUpdateNotAllowedException("Agent detail cannot be updated as it is terminated");
        }
        ChannelType channelType = new ChannelType(channelCode, channelDescription);
        this.channelType = channelType;
        return this;
    }

    public Agent applyOverrideCommissionEligibility(String designationCode, String designationDescription, OverrideCommissionApplicable overrideCommissionApplicable) {
        Designation designationOb = new Designation(designationCode, designationDescription);
        if (OverrideCommissionApplicable.YES.equals(overrideCommissionApplicable) && OverrideCommissionApplicable.NO.equals(designationOb.getOverrideCommissionApplicable()) && (!designationCode.equals("BRANCH_BDE"))) {
            throw new AgentApplicationException("Employee not a Branch BDE");
        }
        this.overrideCommissionApplicable = overrideCommissionApplicable;
        return this;
    }


    public Agent updateStatus(AgentStatus agentStatus) {
        if (AgentStatus.TERMINATED.equals(this.agentStatus)) {
            raiseAgentUpdateNotAllowedException("Agent detail cannot be updated as it is terminated");
        }
        this.agentStatus = agentStatus;
        return this;
    }


}
