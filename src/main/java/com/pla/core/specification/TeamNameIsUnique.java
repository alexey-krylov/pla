/*
 * Copyright (c) 3/5/15 3:49 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.specification;

import com.pla.core.domain.model.TeamName;
import com.pla.core.query.TeamFinder;
import com.pla.sharedkernel.specification.ISpecification;
import org.nthdimenzion.ddd.domain.annotations.Specification;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: Samir
 * @since 1.0 05/03/2015
 */
@Specification
public class TeamNameIsUnique implements ISpecification<TeamName> {

    private TeamFinder teamFinder;

    @Autowired
    public TeamNameIsUnique(TeamFinder teamFinder) {
        this.teamFinder = teamFinder;

    }

    @Override
    public boolean isSatisfiedBy(TeamName teamNameCode) {
        int teamCount = teamFinder.getTeamCountByTeamNameCode(teamNameCode.getTeamName(), teamNameCode.getTeamCode());
        return teamCount == 0;
    }

}
