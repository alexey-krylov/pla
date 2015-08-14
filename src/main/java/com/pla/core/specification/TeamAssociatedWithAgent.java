package com.pla.core.specification;

import com.pla.core.dto.TeamDto;
import com.pla.core.query.TeamFinder;
import com.pla.sharedkernel.specification.CompositeSpecification;
import org.nthdimenzion.ddd.domain.annotations.Specification;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by User on 5/1/2015.
 */
@Specification
public class TeamAssociatedWithAgent extends CompositeSpecification<TeamDto> {

    TeamFinder teamFinder;

    @Autowired
    public TeamAssociatedWithAgent(TeamFinder teamFinder) {
        this.teamFinder = teamFinder;

    }

    @Override
    public boolean isSatisfiedBy(TeamDto teamDto) {
        return (teamFinder.getActiveTeamCountByAgentAssociatedWithTeam(teamDto) != 0);
    }


}
