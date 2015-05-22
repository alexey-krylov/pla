package com.pla.individuallife.domain.model.proposal;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import com.pla.sharedkernel.domain.model.TitleEnum;
import lombok.Getter;
import org.joda.time.LocalDate;
import org.joda.time.Years;

/**
 * Created by pradyumna on 22-05-2015.
 */
@Getter
public class ProposedAssured {

    private TitleEnum title;
    private String firstName;
    private String surname;
    private String nrc;
    private LocalDate dateOfBirth;
    private Gender gender;
    private long mobileNumber;
    private String emailAddress;
    private MaritalStatus maritalStatus;
    private String spouseFirstName;
    private String spouseLastName;
    private String spouseEmailAddress;
    private EmploymentDetail employmentDetail;
    private Address residentialAddress;
    private boolean isProposer;

    ProposedAssured(TitleEnum title, String firstName, String surname, String nrc, LocalDate dateOfBirth, Gender gender, long mobileNumber, String emailAddress, MaritalStatus maritalStatus, String spouseFirstName, String spouseLastName, String spouseEmailAddress, EmploymentDetail employmentDetail, Address residentialAddress, boolean isProposer) {
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
        this.employmentDetail = employmentDetail;
        this.residentialAddress = residentialAddress;
        this.isProposer = isProposer;
    }

    public int getAgeNextBirthday() {
        return Years.yearsBetween(dateOfBirth, LocalDate.now()).getYears() + 1;
    }

}
