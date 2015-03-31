package com.pla.publishedlanguage.domain.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Admin on 3/26/2015.
 */

@Setter
@Getter
public class EmployeeDto implements Serializable{

    private String firstName;
    private String lastName;
    private String middleName;
    private String employeeId;
    private String nrcNumber;
    private String designation;
    private String designationDescription;
    private String departmentName;
    private String email;
    private Map<String, Object> primaryContactDetail;
    private Map<String, Object> physicalContactDetail;

    public EmployeeDto() {
    }
}