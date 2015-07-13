package com.pla.sharedkernel.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Samir on 7/9/2015.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PolicyCommissionHierarchy {

    private Agent agent;

    private TeamLeader teamLeader;

    private BranchManager branchManager;

    private BranchBDE branchBDE;

    private RegionalManager regionalManager;

    public PolicyCommissionHierarchy(Agent agent, TeamLeader teamLeader, BranchManager branchManager, BranchBDE branchBDE, RegionalManager regionalManager) {
        this.agent = agent;
        this.teamLeader = teamLeader;
        this.branchManager = branchManager;
        this.branchBDE = branchBDE;
        this.regionalManager = regionalManager;
    }
}
