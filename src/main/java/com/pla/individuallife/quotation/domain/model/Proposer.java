package com.pla.individuallife.quotation.domain.model;

import com.pla.sharedkernel.domain.model.Gender;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Karunakar on 5/18/2015.
 */
@Embeddable
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
public class Proposer {

    private String proposerTitle;

    private String proposerFName;

    private String proposerSurname;

    private String proposerNRC;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    private LocalDate proposerDateOfBirth;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String proposerMobileNumber;

    private String proposerEmailId;



    Proposer(ProposerBuilder proposerBuilder) {
        checkArgument(proposerBuilder != null);
        this.proposerTitle = proposerBuilder.getProposerTitle();
        this.proposerFName = proposerBuilder.getProposerFName();
        this.proposerSurname = proposerBuilder.getProposerSurname();
        this.proposerNRC = proposerBuilder.getProposerNRC();
        this.proposerDateOfBirth = proposerBuilder.getDateOfBirth();
        this.gender = proposerBuilder.getGender();
        this.proposerMobileNumber = proposerBuilder.getMobileNumber();
        this.proposerEmailId = proposerBuilder.getEmailId();
    }

    public static ProposerBuilder proposerBuilder() {
        return new ProposerBuilder();
    }


}
