package com.pla.individuallife.sharedresource.model.vo;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.DateTime;
import org.joda.time.Years;

/**
 * Created by pradyumna on 22-05-2015.
 */
@Getter
@Setter
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
    private String spouseMobileNumber;
    private EmploymentDetail employmentDetail;
    private ResidentialAddress residentialAddress;
    private String otherName;
    private String relationshipId;

   public ProposedAssured(String title, String firstName, String surname, String nrc, DateTime dateOfBirth, Gender gender, String mobileNumber, String emailAddress,
                    MaritalStatus maritalStatus, String spouseFirstName, String spouseLastName, String spouseEmailAddress, String spouseMobileNumber,
                    EmploymentDetail employmentDetail, ResidentialAddress residentialAddress,String otherName) {
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
        this.otherName=otherName;
    }

    public ProposedAssured(){

    }
    public int getAgeNextBirthday() {
        return Years.yearsBetween(dateOfBirth, DateTime.now()).getYears() + 1;
    }
}
