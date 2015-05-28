package com.pla.individuallife.quotation.domain.model;

import com.pla.sharedkernel.domain.model.Gender;
import lombok.Getter;
import org.joda.time.LocalDate;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Getter
public class ProposedAssuredBuilder {

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

    ProposedAssuredBuilder(String title, String firstName, String surname, String nrc) {
        checkArgument(isNotEmpty(title));
        checkArgument(isNotEmpty(firstName));
        checkArgument(isNotEmpty(surname));
        checkArgument(isNotEmpty(nrc));
        this.assuredTitle = title;
        this.assuredFName = firstName;
        this.assuredSurname = surname;
        this.assuredNRC = nrc;
    }

    public ProposedAssuredBuilder(String title, String firstName, String surname, String nrc, LocalDate dateOfBirth, Number ageNextBirthDay, Gender gender, String mobileNumber, String emailId, String occupation) {
        checkArgument(isNotEmpty(title));
        checkArgument(isNotEmpty(firstName));
        checkArgument(isNotEmpty(surname));
        checkArgument(isNotEmpty(nrc));
        checkArgument(dateOfBirth != null);
        checkArgument(gender != null);
        checkArgument(mobileNumber != null);
        checkArgument(emailId != null);
        checkArgument(isNotEmpty(occupation));
        checkArgument(ageNextBirthDay != null);
        this.assuredTitle = title;
        this.assuredFName = firstName;
        this.assuredSurname = surname;
        this.assuredNRC = nrc;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.mobileNumber = mobileNumber;
        this.emailId = emailId;
        this.occupation = occupation;
        this.ageNextBirthDay = ageNextBirthDay;
    }

    public ProposedAssured build() {
        return new ProposedAssured(this);
    }

    public static void main(String[] args) {
        try {
            return;
        }catch (Exception e){

        }finally {
            System.out.println("XX");
        }
    }
}
