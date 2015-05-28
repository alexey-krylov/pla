package com.pla.individuallife.quotation.presentation.dto;

import com.pla.individuallife.quotation.domain.model.ProposedAssured;
import com.pla.sharedkernel.domain.model.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;

/**
 * Created by Karunakar on 5/20/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProposedAssuredDto {

    private String assuredTitle;

    private String assuredFName;

    private String assuredSurname;

    private String assuredNRC;

    private LocalDate dateOfBirth;

    private Number ageNextBirthDay;

    private Gender gender;

    private String mobileNumber;

    private String emailId;

    private String occupation;


    public ProposedAssuredDto(ProposedAssured proposedAssured) {
        this.assuredTitle = proposedAssured.getAssuredTitle();
        this.assuredFName = proposedAssured.getAssuredFName();
        this.assuredSurname = proposedAssured.getAssuredSurname();
        this.assuredNRC = proposedAssured.getAssuredNRC();
        this.dateOfBirth = proposedAssured.getAssuredDateOfBirth();
        this.ageNextBirthDay = proposedAssured.getAgeNextBirthDay();
        this.gender = proposedAssured.getAssuredGender();
        this.mobileNumber = proposedAssured.getAssuredMobileNumber();
        this.emailId = proposedAssured.getAssuredEmailId() != null ? proposedAssured.getAssuredEmailId() : "";
        this.occupation = proposedAssured.getOccupation();
    }
}
