package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by User on 4/7/2015.
 */
@Getter
public enum CommissionDesignation {

    AGENT("Agent"), BROKER("Broker"), TEAM_LEADER("Team Leader"), SALES_SUPERVISOR_BDE("Sales Supervisor BDE"), BRANCH_MANAGER("Branch Manager"),
    REGIONAL_MANAGER("Regional Manager"),NATIONAL_SALES_MANAGER("National Sales Manager"),GENERAL_MANAGER("General Manager"),
    MANAGING_DIRECTOR("Managing Director"),STAFF("Staff");

    private String description;

    CommissionDesignation(String description) {
        this.description = description;
    }

}
