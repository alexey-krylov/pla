/*
 * Copyright (c) 3/10/15 9:28 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.google.common.base.Preconditions;
import com.pla.core.domain.exception.TeamException;
import lombok.*;
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
@Getter(value = AccessLevel.PRIVATE)
public class Team implements ICrudEntity {

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
    private List<TeamLeaderFulfillment> teamLeaders;

    private Boolean active = Boolean.FALSE;

    Team(String teamId, TeamCode teamCode, TeamName teamName, String currentTeamLeader, TeamLeaderFulfillment teamLeaderFulfillment) {
        Preconditions.checkNotNull(teamId);
        this.teamId = teamId;
        this.teamCode = teamCode;
        this.teamName = teamName;
        this.currentTeamLeader = currentTeamLeader;
        this.teamLeaders.add(teamLeaderFulfillment);
    }

    public Team updateTeamLeader(TeamLeader teamLeader, LocalDate effectiveFrom) {
        if (this.currentTeamLeader != null) {
            TeamException.raiseTeamLeaderCannotBeAssociatedException();
        }
        this.currentTeamLeader= teamLeader.getEmployeeId();
        return this;
    }

}
