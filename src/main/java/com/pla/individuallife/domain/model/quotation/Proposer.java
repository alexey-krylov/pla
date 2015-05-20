package com.pla.individuallife.domain.model.quotation;

import com.pla.core.domain.model.agent.AgentId;
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
public class Proposer {

    private String proposerTitle;

    private String proposerFName;

    private String proposerSurname;

    private String proposerNRC;

    private LocalDate dateOfBirth;

    private Number ageNextBirthDay;

    private Gender gender;

    private String mobileNumber;

    private String emailId;

    Proposer(ProposerBuilder proposerBuilder) {
        checkArgument(proposerBuilder != null);
        this.proposerTitle = proposerBuilder.getProposerTitle();
        this.proposerFName = proposerBuilder.getProposerFName();
        this.proposerSurname = proposerBuilder.getProposerSurname();
        this.proposerNRC = proposerBuilder.getProposerNRC();
        this.dateOfBirth = proposerBuilder.getDateOfBirth();
        this.gender = proposerBuilder.getGender();
        this.mobileNumber = proposerBuilder.getMobileNumber();
        this.emailId = proposerBuilder.getEmailId();
        this.ageNextBirthDay = proposerBuilder.getAgeNextBirthDay();
    }

    public static ProposerBuilder getProposerBuilder( String proposerTitle, String proposerFName, String proposerSurname, String proposerNRC, LocalDate dateOfBirth, Number ageNextBirthDay, Gender gender, String mobileNumber, String emailId) {
        return new ProposerBuilder(proposerTitle, proposerFName, proposerSurname, proposerNRC, dateOfBirth, ageNextBirthDay, gender, mobileNumber, emailId);
    }

}
