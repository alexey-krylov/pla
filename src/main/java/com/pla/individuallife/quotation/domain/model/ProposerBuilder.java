package com.pla.individuallife.quotation.domain.model;

import com.pla.sharedkernel.domain.model.Gender;
import lombok.Getter;
import org.joda.time.LocalDate;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Getter
public class ProposerBuilder {

    private String proposerTitle;

    private String proposerFName;

    private String proposerSurname;

    private String proposerNRC;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String mobileNumber;

    private String emailId;

    public ProposerBuilder withProposerTitle(String proposerTitle) {
        this.proposerTitle = proposerTitle;
        return this;
    }

    public ProposerBuilder withProposerFName(String proposerFName) {
        this.proposerFName = proposerFName;
        return this;
    }

    public ProposerBuilder withProposerSurname(String proposerSurname) {
        this.proposerSurname = proposerSurname;
        return this;
    }

    public ProposerBuilder withProposerNRC(String proposerNRC) {
        this.proposerNRC = proposerNRC;
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

    public ProposerBuilder withMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
        return this;
    }

    public ProposerBuilder withEmailId(String emailId) {
        this.emailId = emailId;
        return this;
    }

    public Proposer build() {
        return new Proposer(this);
    }
}
