package com.pla.individuallife.proposal.domain.model;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import org.joda.time.DateTime;

public class ProposedAssuredBuilder {
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
    private ResidentialAddress residentialAddress;
    private EmploymentDetail employmentDetail;
    private boolean isProposer;

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

    public ProposedAssuredBuilder withNrc(String nrc) {
        this.nrc = nrc;
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

    public ProposedAssuredBuilder withMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
        return this;
    }

    public ProposedAssuredBuilder withSpouseFirstName(String spouseFirstName) {
        this.spouseFirstName = spouseFirstName;
        return this;
    }

    public ProposedAssuredBuilder withSpouseLastName(String spouseLastName) {
        this.spouseLastName = spouseLastName;
        return this;
    }

    public ProposedAssuredBuilder withSpouseEmailAddress(String spouseEmailAddress) {
        this.spouseEmailAddress = spouseEmailAddress;
        return this;
    }

    public ProposedAssuredBuilder withEmploymentDetail(EmploymentDetail employmentDetail) {
        this.employmentDetail = employmentDetail;
        return this;
    }

    public ProposedAssuredBuilder withResidentialAddress(ResidentialAddress residentialAddress) {
        this.residentialAddress = residentialAddress;
        return this;
    }

    public ProposedAssuredBuilder withIsProposer(boolean isProposer) {
        this.isProposer = isProposer;
        return this;
    }

    public ProposedAssured createProposedAssured() {

        return new ProposedAssured(title, firstName, surname, nrc, dateOfBirth, gender, mobileNumber, emailAddress, maritalStatus, spouseFirstName, spouseLastName, spouseEmailAddress, employmentDetail, residentialAddress, isProposer);
    }
}
