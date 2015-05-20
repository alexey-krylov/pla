package com.pla.individuallife.domain.model.quotation;

import com.pla.sharedkernel.domain.model.Gender;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Karunakar on 5/18/2015.
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class ProposedAssured {

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



    ProposedAssured(ProposedAssuredBuilder proposedAssuredBuilder) {
        checkArgument(proposedAssuredBuilder != null);
        this.assuredTitle = proposedAssuredBuilder.getAssuredTitle();
        this.assuredFName = proposedAssuredBuilder.getAssuredFName();
        this.assuredSurname = proposedAssuredBuilder.getAssuredSurname();
        this.assuredNRC = proposedAssuredBuilder.getAssuredNRC();
        this.dateOfBirth = proposedAssuredBuilder.getDateOfBirth();
        this.gender = proposedAssuredBuilder.getGender();
        this.mobileNumber = proposedAssuredBuilder.getMobileNumber();
        this.emailId = proposedAssuredBuilder.getEmailId();
        this.occupation = proposedAssuredBuilder.getOccupation();
        this.ageNextBirthDay = proposedAssuredBuilder.getAgeNextBirthDay();
    }


    public static ProposedAssuredBuilder getAssuredBuilder( String title, String firstName, String surname, String nrc ) {
        return new ProposedAssuredBuilder( title,firstName,  surname,  nrc);
    }

    public static ProposedAssuredBuilder getAssuredBuilder( String title, String firstName, String surname, String nrc, LocalDate dateOfBirth, Number ageNextBirthDay, Gender gender, String mobileNumber, String emailId, String occupation ) {
        return new ProposedAssuredBuilder( title,firstName,  surname,  nrc, dateOfBirth, ageNextBirthDay, gender, mobileNumber, emailId,occupation );
    }
}
