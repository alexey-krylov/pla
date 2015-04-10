/*
 * Copyright (c) 3/10/15 9:28 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.domain.model;

import com.google.common.collect.Sets;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.crud.ICrudEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * @author: Samir
 * @since 1.0 10/03/2015
 */

@Entity
@Table(name = "team", uniqueConstraints = {@UniqueConstraint(name = "UNQ_ACTIVE_TEAM_CODE_NAME", columnNames = {"teamCode", "teamName", "active"})})
@EqualsAndHashCode(of = {"teamName", "teamCode"})
@ToString(of = {"teamCode", "teamName"})
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(value = AccessLevel.PACKAGE)
public class Team implements ICrudEntity {

    private static final Logger LOGGER = LoggerFactory.getLogger(Team.class);

    @Id
    private String teamId;

    private String teamCode;

    private String teamName;

    private String currentTeamLeader;

    private String regionCode;

    private String branchCode;

    @ElementCollection(targetClass = TeamLeaderFulfillment.class, fetch = FetchType.EAGER)
    @OrderColumn
    @JoinTable(name = "TEAM_TEAM_LEADER_FUlFILLMENT", joinColumns = @JoinColumn(name = "TEAM_ID"))
    @Cascade(CascadeType.ALL)
    private Set<TeamLeaderFulfillment> teamLeaders = Sets.newHashSet();

    private Boolean active = Boolean.FALSE;

    Team(String teamId, String teamName, String teamCode, String regionCode, String branchCode, String currentTeamLeader, TeamLeaderFulfillment teamLeaderFulfillment, Boolean active) {
        checkArgument(isNotEmpty(teamId));
        checkArgument(teamName != null);
        checkArgument(teamCode != null);
        checkArgument(teamLeaderFulfillment != null);
        checkArgument(isNotEmpty(currentTeamLeader));
        checkArgument(active);
        this.teamId = teamId;
        this.teamCode = teamCode;
        this.teamName = teamName;
        this.regionCode = regionCode;
        this.branchCode = branchCode;
        this.currentTeamLeader = currentTeamLeader;
        this.teamLeaders.add(teamLeaderFulfillment);
        this.active = active;
    }

    public static TeamLeaderFulfillment createTeamLeaderFulfillment(String employeeId, String firstName, String lastName, LocalDate effectiveFrom) {
        TeamLeader teamLeader = new TeamLeader(employeeId, firstName, lastName);
        TeamLeaderFulfillment teamLeaderFulfillment = new TeamLeaderFulfillment(teamLeader, effectiveFrom);
        return teamLeaderFulfillment;
    }

    public Team assignTeamLeader(String employeeId, String firstName, String lastName, LocalDate effectiveFrom) {
        TeamLeaderFulfillment currentTeamFulfillment = getTeamLeaderFulfillmentForATeamLeader(this.currentTeamLeader);
        checkArgument(currentTeamFulfillment != null);
        this.teamLeaders = updateTeamLeaderFullfillment(this.teamLeaders, currentTeamFulfillment.getTeamLeader(), effectiveFrom.plusDays(-1));
        this.teamLeaders.add(createTeamLeaderFulfillment(employeeId, firstName, lastName, effectiveFrom));
        this.currentTeamLeader = employeeId;
        return this;
    }

    public Set<TeamLeaderFulfillment> updateTeamLeaderFullfillment(Set<TeamLeaderFulfillment> teamLeaderFulfillments, TeamLeader teamLeaderToBeExpired, LocalDate expireDate) {
        for (TeamLeaderFulfillment teamLeaderFulfillment : teamLeaderFulfillments) {
            if (teamLeaderFulfillment.getTeamLeader().equals(teamLeaderToBeExpired) && teamLeaderFulfillment.getThruDate() == null) {
                teamLeaderFulfillment.expireFulfillment(expireDate);
            }
        }
        return teamLeaderFulfillments;
    }

    public TeamLeaderFulfillment getTeamLeaderFulfillmentForATeamLeader(String currentTeamLeaderId) {
        for (TeamLeaderFulfillment teamLeaderFulfillment : teamLeaders) {
            if ((teamLeaderFulfillment.getTeamLeader().getEmployeeId()).equals(currentTeamLeaderId)) {
                return teamLeaderFulfillment;

            }
        }
        return null;
    }

    public Team inactivate() {
        this.active = Boolean.FALSE;
        return this;
    }


}
