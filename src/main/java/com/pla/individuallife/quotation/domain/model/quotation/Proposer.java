package com.pla.individuallife.quotation.domain.model.quotation;

import com.pla.sharedkernel.domain.model.Gender;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Karunakar on 5/18/2015.
 */
@Entity
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class Proposer {

    @Id
    private String proposerId;

    private String proposerTitle;

    private String proposerFName;

    private String proposerSurname;

    private String proposerNRC;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate dateOfBirth;

    private Number ageNextBirthDay;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String mobileNumber;

    private String emailId;

    Proposer(ProposerBuilder proposerBuilder) {
        checkArgument(proposerBuilder != null);
        this.proposerId = proposerBuilder.getProposerId();
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

    public static ProposerBuilder getProposerBuilder( String proposerId, String proposerTitle, String proposerFName, String proposerSurname, String proposerNRC, LocalDate dateOfBirth, Number ageNextBirthDay, Gender gender, String mobileNumber, String emailId) {
        return new ProposerBuilder(proposerId, proposerTitle, proposerFName, proposerSurname, proposerNRC, dateOfBirth, ageNextBirthDay, gender, mobileNumber, emailId);
    }

}
