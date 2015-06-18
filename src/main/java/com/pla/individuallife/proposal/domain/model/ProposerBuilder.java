package com.pla.individuallife.proposal.domain.model;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import org.joda.time.LocalDate;

public class ProposerBuilder {
    private String title;
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
    private ResidentialAddress residentialAddress;

    public ProposerBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public ProposerBuilder withFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public ProposerBuilder withSurname(String surname) {
        this.surname = surname;
        return this;
    }

    public ProposerBuilder withNrc(String nrc) {
        this.nrc = nrc;
        return this;
    }

    public ProposerBuilder withDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        return this;
    }

    public ProposerBuilder withGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public ProposerBuilder withMobileNumber(long mobileNumber) {
        this.mobileNumber = mobileNumber;
        return this;
    }

    public ProposerBuilder withEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }

    public ProposerBuilder withMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
        return this;
    }

    public ProposerBuilder withSpouseFirstName(String spouseFirstName) {
        this.spouseFirstName = spouseFirstName;
        return this;
    }

    public ProposerBuilder withSpouseLastName(String spouseLastName) {
        this.spouseLastName = spouseLastName;
        return this;
    }

    public ProposerBuilder withSpouseEmailAddress(String spouseEmailAddress) {
        this.spouseEmailAddress = spouseEmailAddress;
        return this;
    }

    public ProposerBuilder withEmploymentDetail(EmploymentDetail employmentDetail) {
        this.employmentDetail = employmentDetail;
        return this;
    }

    public ProposerBuilder withResidentialAddress(ResidentialAddress residentialAddress) {
        this.residentialAddress = residentialAddress;
        return this;
    }

    public Proposer createProposer() {
        return new Proposer(title, firstName, surname, nrc, dateOfBirth, gender, mobileNumber, emailAddress, maritalStatus, spouseFirstName, spouseLastName, spouseEmailAddress, employmentDetail, residentialAddress);
    }
}
