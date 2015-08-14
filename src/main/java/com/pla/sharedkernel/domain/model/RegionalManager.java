package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Samir on 7/9/2015.
 */
@Getter
public class RegionalManager {

    private String employeeId;

    private String fullName;

    public RegionalManager(String employeeId, String fullName) {
        this.employeeId = employeeId;
        this.fullName = fullName;
    }
}
