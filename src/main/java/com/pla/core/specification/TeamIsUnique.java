/*
 * Copyright (c) 3/5/15 3:49 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.specification;

import com.pla.core.dto.TeamDto;
import com.pla.core.query.TeamFinder;
import com.pla.sharedkernel.specification.ISpecification;
import org.nthdimenzion.ddd.domain.annotations.Specification;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: Nischitha
 * @since 1.0 18/03/2015
 */
@Specification
public class TeamIsUnique implements ISpecification<TeamDto> {

    private TeamFinder teamFinder;

    @Autowired
    public TeamIsUnique(TeamFinder teamFinder) {
        this.teamFinder = teamFinder;

    }

    @Override
    public boolean isSatisfiedBy(TeamDto teamDto) {
        return ((teamFinder.getTeamCountByTeamName(teamDto.getTeamName()) == 0) && (teamFinder.getTeamCountByTeamName(teamDto.getTeamCode()) == 0));
    }

}
