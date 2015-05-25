package com.pla.individuallife.query;

import com.pla.individuallife.domain.model.quotation.ProposedAssured;
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

    private String assuredId;

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
        this.assuredId = proposedAssured.getAssuredId();
        this.assuredTitle = proposedAssured.getAssuredTitle();
        this.assuredFName = proposedAssured.getAssuredFName();
        this.assuredSurname = proposedAssured.getAssuredSurname();
        this.assuredNRC = proposedAssured.getAssuredNRC();
        this.dateOfBirth = proposedAssured.getDateOfBirth();
        this.ageNextBirthDay = proposedAssured.getAgeNextBirthDay();
        this.gender = proposedAssured.getGender();
        this.mobileNumber = proposedAssured.getMobileNumber();
        this.emailId = proposedAssured.getEmailId() != null ? proposedAssured.getEmailId() : "";
        this.occupation = proposedAssured.getOccupation();
    }
}
