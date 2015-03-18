/*
 * Copyright (c) 3/10/15 9:28 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import lombok.*;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.crud.ICrudEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * @author: Samir
 * @since 1.0 10/03/2015
 */

@Entity
@Table(name = "team", uniqueConstraints = {@UniqueConstraint(name = "UNQ_TEAM_CODE_NAME", columnNames = {"teamCode", "teamName"})})
@EqualsAndHashCode(of = {"teamName", "teamCode"})
@ToString(of = {"teamCode", "teamName"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PACKAGE)
public class Team implements ICrudEntity {

    private static final Logger LOGGER = LoggerFactory.getLogger(Team.class);

    @Id
    private String teamId;

    @Embedded
    private TeamCode teamCode;

    @Embedded
    private TeamName teamName;

    private String currentTeamLeader;

    @ElementCollection(targetClass = TeamLeaderFulfillment.class, fetch = FetchType.EAGER)
    @OrderColumn
    @JoinTable(name = "TEAM_TEAM_LEADER_FUlFILLMENT", joinColumns = @JoinColumn(name = "TEAM_ID"))
    private Set<TeamLeaderFulfillment> teamLeaders = Sets.newHashSet();

    private Boolean active = Boolean.FALSE;

    Team(String teamId, TeamCode teamCode, TeamName teamName, String currentTeamLeader, TeamLeaderFulfillment teamLeaderFulfillment, Boolean active) {
        checkArgument(isNotEmpty(teamId));
        checkArgument(teamName != null);
        checkArgument(teamCode != null);
        checkArgument(teamLeaderFulfillment != null);
        checkArgument(isNotEmpty(currentTeamLeader));
        checkArgument(active);
        this.teamId = teamId;
        this.teamCode = teamCode;
        this.teamName = teamName;
        this.currentTeamLeader = currentTeamLeader;
        this.teamLeaders.add(teamLeaderFulfillment);
        this.active = active;
    }

    public Team assignTeamLeader(String employeeId, String firstName, String lastName, LocalDate effectiveFrom) {
        TeamLeaderFulfillment currentTeamFulfillment = getCurrentTeamLeaderFulfillment(this.currentTeamLeader);
        checkArgument(currentTeamFulfillment != null);
        TeamLeaderFulfillment expiredTeamLeaderFulfillment = currentTeamFulfillment.expireFulfillment(effectiveFrom.plusDays(-1));
        this.teamLeaders = updateTeamLeaderFullfillment(this.teamLeaders, expiredTeamLeaderFulfillment);
        this.teamLeaders.add(createTeamLeaderFulfillment(employeeId, firstName, lastName, effectiveFrom));
        this.currentTeamLeader = employeeId;
        return this;
    }

    public Set<TeamLeaderFulfillment> updateTeamLeaderFullfillment(Set<TeamLeaderFulfillment> teamLeaderFulfillments, TeamLeaderFulfillment expiredTeamLeaderFulfillment) {
        for(Iterator<TeamLeaderFulfillment> i = teamLeaderFulfillments.iterator(); i.hasNext();)
        {
            TeamLeaderFulfillment local_teamLeaderFulfillment = (TeamLeaderFulfillment)i.next();
            if((local_teamLeaderFulfillment.getTeamLeader()).equals(expiredTeamLeaderFulfillment.getTeamLeader()))
            {
                teamLeaderFulfillments.remove(local_teamLeaderFulfillment);
                teamLeaderFulfillments.add(expiredTeamLeaderFulfillment);
            }
        }
        return teamLeaderFulfillments;
    }
    public TeamLeaderFulfillment getCurrentTeamLeaderFulfillment(String currentTeamLeaderId) {
        TeamLeaderFulfillment local_teamLeaderFulfillment = new TeamLeaderFulfillment(null,null);
        for(Iterator<TeamLeaderFulfillment> i = this.teamLeaders.iterator(); i.hasNext();)
        {
             local_teamLeaderFulfillment = (TeamLeaderFulfillment)i.next();
            if((local_teamLeaderFulfillment.getTeamLeader()).equals(currentTeamLeaderId))
            {
               break;
            }
        }
        return local_teamLeaderFulfillment;
    }

    public TeamLeaderFulfillment createTeamLeaderFulfillment(String employeeId, String firstName, String lastName, LocalDate effectiveFrom) {
        TeamLeader teamLeader = new TeamLeader(employeeId, firstName, lastName);
        TeamLeaderFulfillment teamLeaderFulfillment = new TeamLeaderFulfillment(teamLeader, effectiveFrom);
        return teamLeaderFulfillment;
    }

    public Team inactivate(){
        this.active=Boolean.FALSE;
        return this;
    }

}
