package com.pla.sharedkernel.domain.model;

import lombok.Getter;

/**
 * Created by Samir on 7/9/2015.
 */
@Getter
public class BranchManager {

    private String employeeId;

    private String fullName;

    public BranchManager(String employeeId, String fullName) {
        this.employeeId = employeeId;
        this.fullName = fullName;
    }
}
