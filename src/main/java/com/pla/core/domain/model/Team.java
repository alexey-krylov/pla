/*
 * Copyright (c) 3/10/15 9:28 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.google.common.collect.Lists;
import com.pla.core.domain.exception.TeamException;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.nthdimenzion.common.crud.ICrudEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * @author: Samir
 * @since 1.0 10/03/2015
 */
@Entity
@Table(name = "team", uniqueConstraints = {@UniqueConstraint(name = "UNQ_TEAM_CODE_NAME", columnNames = {"teamCode", "teamName"})})
@EqualsAndHashCode(of = {"teamName", "teamCode", "teamId"})
@ToString(of = {"teamCode", "teamName"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Team implements ICrudEntity {

    @Id
    private String teamId;

    private String teamCode;

    private String teamName;

    @Embedded
    private TeamLeader currentTeamLeader;

    @ElementCollection
    @OrderColumn
    @JoinTable(name = "TEAM_TEAM_LEADER", joinColumns = @JoinColumn(name = "TEAM_ID"))
    private List<TeamLeader> teamLeaders = Lists.newArrayList();

    private Boolean active = Boolean.FALSE;

    Team(String teamId, String teamCode, String teamName, TeamLeader teamLeader) {
        this.teamId = teamId;
        this.teamCode = teamCode;
        this.teamName = teamName;
        this.currentTeamLeader = teamLeader;
        this.teamLeaders.add(teamLeader);
    }

    public Team updateTeamLeader(TeamLeader teamLeader) {
        if (this.currentTeamLeader != null) {
            TeamException.raiseTeamLeaderCannotBeAssociatedException();
        }
        this.currentTeamLeader = teamLeader;
        this.teamLeaders.add(teamLeader);
        return this;
    }


    public Team dissociateTeamLeader(String employeeId, LocalDate dissociatedOn) {
        return this;
    }
}
