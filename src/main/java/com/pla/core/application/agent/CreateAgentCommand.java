/*
 * Copyright (c) 3/16/15 7:36 PM .NthDimenzion,Inc - All Rights Reserved
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
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * @author: Samir
 * @since 1.0 13/03/2015
 */

@Getter
@Setter
@NoArgsConstructor
public class CreateAgentCommand {

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

    private List<PlanDetailDto> agentPlanDetails;


    public static CreateAgentCommand transformToAgentCommand(Map<String, Object> agentDetail, List<Map<String, Object>> allAgentPlans, List<Map> allMasterPlans) {
        CreateAgentCommand createAgentCommand = new CreateAgentCommand();
        createAgentCommand.setAgentId((String) agentDetail.get("agentId"));
        createAgentCommand.setAgentProfile(AgentProfileDto.transFormToAgentProfileDto(agentDetail));
        LicenseNumberDto licenseNumberDto = agentDetail.get("licenseNumber") != null ? new LicenseNumberDto((String) agentDetail.get("licenseNumber")) : null;
        createAgentCommand.setLicenseNumber(licenseNumberDto);
        createAgentCommand.setOverrideCommissionApplicable(agentDetail.get("overrideCommissionApplicable") != null ? OverrideCommissionApplicable.valueOf((String) agentDetail.get("overrideCommissionApplicable")) : null);
        createAgentCommand.setContactDetail(ContactDetailDto.transformToContactDetailDto(agentDetail));
        TeamDetailDto teamDetailDto = agentDetail.get("teamId") != null ? new TeamDetailDto((String) agentDetail.get("teamId"), (String) agentDetail.get("teamCode"), (String) agentDetail.get("teamName"), (String) agentDetail.get("teamLeaderFirstName"), (String) agentDetail.get("teamLeaderLastName"), (String) agentDetail.get("regionName"), (String) agentDetail.get("branchName")) : null;
        createAgentCommand.setTeamDetail(teamDetailDto);
        ChannelTypeDto channelTypeDto = agentDetail.get("channelCode") != null ? new ChannelTypeDto((String) agentDetail.get("channelCode"), (String) agentDetail.get("channelName")) : null;
        createAgentCommand.setChannelType(channelTypeDto);
        createAgentCommand.setPhysicalAddress(PhysicalAddressDto.transformToPhysicalAddressDto(agentDetail));
        List<Map<String, Object>> agentPlans = allAgentPlans.stream().filter(new FilterAgentPlanByAgentId((String) agentDetail.get("agentId"))).collect(Collectors.toList());
        Set<PlanId> authorizedPlanToSell = agentPlans.stream().map(new TransformAgentPlanToPlanId()).collect(Collectors.toSet());
        createAgentCommand.setAuthorizePlansToSell(authorizedPlanToSell);
        createAgentCommand.setAgentStatus(AgentStatus.valueOf((String) agentDetail.get("agentStatus")));
        if (isNotEmpty(allMasterPlans)) {
            List<PlanDetailDto> planDetailDtos = authorizedPlanToSell.stream().map(new TransformToPlanDetailDto(allMasterPlans)).collect(Collectors.toList());
            createAgentCommand.setAgentPlanDetails(planDetailDtos);
        }
        return createAgentCommand;
    }

    public static List<CreateAgentCommand> transformToAgentCommand(List<Map<String, Object>> nonTerminatedAgents, List<Map<String, Object>> allAgentPlans, List<Map> allMasterPlans) {
        List<CreateAgentCommand> createAgentCommands = nonTerminatedAgents.stream().map(new TransformAgentDetailToAgentCommand(allAgentPlans, allMasterPlans)).collect(Collectors.toList());
        return createAgentCommands;
    }


    private static class FilterAgentPlanByAgentId implements Predicate<Map<String, Object>> {

        private String agentId;

        FilterAgentPlanByAgentId(String agentId) {
            this.agentId = agentId;
        }

        @Override
        public boolean test(Map<String, Object> agentPlan) {
            return agentId.equals((String) agentPlan.get("agentId"));
        }
    }

    private static class TransformAgentPlanToPlanId implements Function<Map<String, Object>, PlanId> {

        @Override
        public PlanId apply(Map<String, Object> agentPlan) {
            return new PlanId((String) agentPlan.get("planId"));
        }
    }

    private static class TransformAgentDetailToAgentCommand implements Function<Map<String, Object>, CreateAgentCommand> {

        private List<Map<String, Object>> allAgentPlans;

        private List<Map> allMasterPlans;

        TransformAgentDetailToAgentCommand(List<Map<String, Object>> allAgentPlans, List<Map> allMasterPlans) {
            this.allAgentPlans = allAgentPlans;
            this.allMasterPlans = allMasterPlans;
        }

        @Override
        public CreateAgentCommand apply(Map<String, Object> agentDetail) {
            return transformToAgentCommand(agentDetail, allAgentPlans, allMasterPlans);
        }


    }

    private static class TransformToPlanDetailDto implements Function<PlanId, PlanDetailDto> {

        private List<Map> allMasterPlans;

        public TransformToPlanDetailDto(List<Map> allMasterPlans) {
            this.allMasterPlans = allMasterPlans;
        }

        @Override
        public PlanDetailDto apply(PlanId planId) {
            List<Map> planDetails = allMasterPlans.stream().filter(new Predicate<Map>() {
                @Override
                public boolean test(Map map) {
                    return planId.getPlanId().equals(((Map) map.get("planId")).get("planId"));
                }
            }).collect(Collectors.toList());
            String  planName =isNotEmpty(planDetails)? (String)((Map) planDetails.get(0).get("planDetail")).get("planName"):"";
            return new PlanDetailDto(planId, planName);
        }
    }
}
