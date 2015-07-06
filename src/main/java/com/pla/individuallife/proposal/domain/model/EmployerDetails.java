package com.pla.individuallife.proposal.domain.model;

import lombok.Getter;

/**
 * Created by Karunakar on 7/6/2015.
 */
@Getter
public class EmployerDetails {

    private String employeeId;
    private String manNumber;
    private String companyNameAndPostalAddress;
    private String basicSalary;
    private SalaryPer salaryPer;
}
