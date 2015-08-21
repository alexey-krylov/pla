package com.pla.individuallife.quotation.domain.model;

import com.pla.sharedkernel.domain.model.Gender;
import lombok.Getter;
import org.joda.time.DateTime;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Getter
public class ProposedAssuredBuilder {

    private String title;

    private String firstName;

    private String surname;

    private String nrcNumber;

    private DateTime dateOfBirth;

    private Gender gender;

    private String mobileNumber;

    private String emailAddress;

    private String occupation;

    public ProposedAssuredBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public ProposedAssuredBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public ProposedAssuredBuilder withSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public ProposedAssuredBuilder withNrcNumber(String nrcNumber) {
        this.nrcNumber = nrcNumber;
        return this;
    }

    public ProposedAssuredBuilder withDateOfBirth(DateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public ProposedAssuredBuilder withGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public ProposedAssuredBuilder withMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
        return this;
    }

    public ProposedAssuredBuilder withEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }

    public ProposedAssuredBuilder withOccupation(String occupation) {
        this.occupation = occupation;
        return this;
    }

    public ProposedAssured build() {
        return new ProposedAssured(this);
    }
}
