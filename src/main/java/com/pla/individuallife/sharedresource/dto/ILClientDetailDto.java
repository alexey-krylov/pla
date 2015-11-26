package com.pla.individuallife.sharedresource.dto;

import com.pla.individuallife.sharedresource.model.vo.EmploymentDetail;
import com.pla.individuallife.sharedresource.model.vo.ResidentialAddress;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * Created by Admin on 26-Nov-15.
 */
@Getter
@Setter
public class ILClientDetailDto {
    private String title;
    private String firstName;
    private String surname;
    private String nrc;
    private DateTime dateOfBirth;
    private Gender gender;
    private String mobileNumber;
    private String emailAddress;
    private MaritalStatus maritalStatus;
    private String spouseFirstName;
    private String spouseLastName;
    private String spouseEmailAddress;
    private String spouseMobileNumber;
    private EmploymentDetail employmentDetail;
    private ResidentialAddress residentialAddress;
    private String otherName;

    public ILClientDetailDto(String title,String firstName,String surName , String otherName,String nrc,
                             DateTime dateOfBirth,Gender gender,String mobileNumber,
                             String emailAddress,MaritalStatus maritalStatus,String spouseFirstName,
                             String spouseLastName,String spouseEmailAddress,String spouseMobileNumber, EmploymentDetail employmentDetail,ResidentialAddress residentialAddress){
        this.title = title;
        this.firstName = firstName;
        this.surname = surName;
        this.otherName =otherName;
        this.nrc =nrc;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.mobileNumber = mobileNumber;
        this.emailAddress  =emailAddress;
        this.maritalStatus = maritalStatus;
        this.spouseFirstName = spouseFirstName;
        this.spouseLastName = spouseLastName;
        this.spouseEmailAddress = spouseEmailAddress;
        this.spouseMobileNumber = spouseMobileNumber;
        this.employmentDetail  = employmentDetail;
        this.residentialAddress  = residentialAddress;
    }
}
