package com.pla.individuallife.quotation.presentation.dto;

import com.pla.individuallife.quotation.domain.model.ProposedAssured;
import com.pla.sharedkernel.domain.model.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

/**
 * Created by Karunakar on 5/20/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProposedAssuredDto {

    private String title;

    private String firstName;

    private String surname;

    private String nrcNumber;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate dateOfBirth;

    private Gender gender;

    private String mobileNumber;

    private String emailAddress;

    private String occupation;


    public ProposedAssuredDto(ProposedAssured proposedAssured) {
        this.title = proposedAssured.getAssuredTitle();
        this.firstName = proposedAssured.getAssuredFName();
        this.surname = proposedAssured.getAssuredSurname();
        this.nrcNumber = proposedAssured.getAssuredNRC();
        this.dateOfBirth = proposedAssured.getAssuredDateOfBirth();
        this.gender = proposedAssured.getAssuredGender();
        this.mobileNumber = proposedAssured.getAssuredMobileNumber();
        this.emailAddress = proposedAssured.getAssuredEmailId() != null ? proposedAssured.getAssuredEmailId() : "";
        this.occupation = proposedAssured.getOccupation();
    }
}
