/*
 * Copyright (c) 3/10/15 9:28 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.google.common.base.Preconditions;
import lombok.*;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.crud.ICrudEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

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
    private List<TeamLeaderFulfillment> teamLeaders = new ArrayList<>();

    private Boolean active = Boolean.FALSE;

    Team(String teamId, TeamCode teamCode, TeamName teamName, String currentTeamLeader, TeamLeaderFulfillment teamLeaderFulfillment, Boolean active) {
        Preconditions.checkNotNull(teamId);
        Preconditions.checkNotNull(teamCode);
        Preconditions.checkNotNull(teamName);
        Preconditions.checkNotNull(currentTeamLeader);
        Preconditions.checkNotNull(teamLeaderFulfillment);
        Preconditions.checkState(active);
        this.teamId = teamId;
        this.teamCode = teamCode;
        this.teamName = teamName;
        this.currentTeamLeader = currentTeamLeader;
        this.teamLeaders.add(teamLeaderFulfillment);
        this.active = active;
    }

    public Team assignTeamLeader(String employeeId, String firstName, String lastName, LocalDate effectiveFrom) {
        if (!currentTeamLeader.equals(employeeId)) {
            if(expireCurrentTeamLeaderFullFillment())
                LOGGER.debug("Expired Current Team Leader Fulfillment ");
            updateTeamLeaderFullFillment(employeeId, firstName, lastName, effectiveFrom);
        }
        return this;
    }

    public boolean expireCurrentTeamLeaderFullFillment()
    {
        List teamLeaders = this.teamLeaders;
        long count = teamLeaders.stream().filter(new Predicate<TeamLeaderFulfillment>() {
            @Override
            public boolean test(TeamLeaderFulfillment teamLeaderFulfillment) {
                if (currentTeamLeader.equals(teamLeaderFulfillment.getTeamLeader().getEmployeeId())) {
                    teamLeaderFulfillment.expireFulfillment(new LocalDate(LocalDate.now().getYear(), LocalDate.now().getMonthOfYear(), (LocalDate.now().getDayOfMonth() - 1)));
                }
                return currentTeamLeader.equals(teamLeaderFulfillment.getTeamLeader().getEmployeeId());
            }
        }).count();
        if(count == 1)
        return true;
        else
            return false;
    }
    public void updateTeamLeaderFullFillment(String employeeId,String firstName,String lastName, LocalDate effectiveFrom)
    {
        this.currentTeamLeader = employeeId;
        TeamLeader teamLeader = new TeamLeader(employeeId, firstName, lastName);
        TeamLeaderFulfillment teamLeaderFulfillment = new TeamLeaderFulfillment(teamLeader, effectiveFrom);
        this.teamLeaders.add(teamLeaderFulfillment);

    }

}
