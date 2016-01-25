package com.pla.individuallife.sharedresource.model.vo;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * Created by pradyumna on 22-05-2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Proposer {

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
    private Boolean isProposedAssured;
    private String clientId;

    Proposer(String title, String firstName, String surname, String nrc, DateTime dateOfBirth, Gender gender, String mobileNumber, String emailAddress,
                    MaritalStatus maritalStatus, String spouseFirstName, String spouseLastName, String spouseEmailAddress, String spouseMobileNumber,
                    EmploymentDetail employmentDetail, ResidentialAddress residentialAddress, boolean isProposedAssured,String otherName,String clientId) {
        this.title = title;
        this.firstName = firstName;
        this.surname = surname;
        this.nrc = nrc;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.mobileNumber = mobileNumber;
        this.emailAddress = emailAddress;
        this.maritalStatus = maritalStatus;
        this.spouseFirstName = spouseFirstName;
        this.spouseLastName = spouseLastName;
        this.spouseEmailAddress = spouseEmailAddress;
        this.spouseMobileNumber = spouseMobileNumber;
        this.employmentDetail = employmentDetail;
        this.residentialAddress = residentialAddress;
        this.isProposedAssured = isProposedAssured;
        this.otherName=otherName;
        this.clientId = clientId;
    }

    public Proposer(String title, String firstName, String surname, DateTime dateOfBirth, Gender gender, String mobileNumber, String emailAddress,boolean isProposedAssured) {
        this.title = title;
        this.firstName = firstName;
        this.surname = surname;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.mobileNumber = mobileNumber;
        this.emailAddress = emailAddress;
        this.isProposedAssured = isProposedAssured;
    }
}
