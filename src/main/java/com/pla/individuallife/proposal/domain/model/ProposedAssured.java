package com.pla.individuallife.proposal.domain.model;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import lombok.Getter;
import lombok.ToString;
import org.joda.time.DateTime;
import org.joda.time.Years;

/**
 * Created by pradyumna on 22-05-2015.
 */
@Getter
@ToString
public class ProposedAssured {

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
    private String spouseMobilNumber;
    private EmploymentDetail employmentDetail;
    private ResidentialAddress residentialAddress;
    private Boolean isProposer;
    private String otherName;

    ProposedAssured(String title, String firstName, String surname, String nrc, DateTime dateOfBirth, Gender gender, String mobileNumber, String emailAddress,
                    MaritalStatus maritalStatus, String spouseFirstName, String spouseLastName, String spouseEmailAddress, String spouseMobilNumber,
                    EmploymentDetail employmentDetail, ResidentialAddress residentialAddress, boolean isProposer,String otherName) {
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
        this.spouseMobilNumber = spouseMobilNumber;
        this.employmentDetail = employmentDetail;
        this.residentialAddress = residentialAddress;
        this.isProposer = isProposer;
        this.otherName=otherName;
    }

    public int getAgeNextBirthday() {
        return Years.yearsBetween(dateOfBirth, DateTime.now()).getYears() + 1;
    }
}
