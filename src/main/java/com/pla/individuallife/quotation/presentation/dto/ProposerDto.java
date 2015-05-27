package com.pla.individuallife.quotation.presentation.dto;

import com.pla.individuallife.quotation.domain.model.Proposer;
import com.pla.sharedkernel.domain.model.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;

/**
 * Created by Karunakar on 4/30/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProposerDto {

    private String proposerTitle;

    private String proposerFName;

    private String proposerSurname;

    private String proposerNRC;

    private LocalDate dateOfBirth;

    private Number ageNextBirthDay;

    private Gender gender;

    private String mobileNumber;

    private String emailId;

    public ProposerDto(Proposer proposer) {
        this.proposerTitle = proposer.getProposerTitle();
        this.proposerFName = proposer.getProposerFName();
        this.proposerSurname = proposer.getProposerSurname();
        this.proposerNRC = proposer.getProposerNRC();
        this.dateOfBirth = proposer.getDateOfBirth();
        this.ageNextBirthDay = proposer.getAgeNextBirthDay();
        this.gender = proposer.getGender();
        this.mobileNumber = proposer.getMobileNumber();
        this.emailId = proposer.getEmailId() != null ? proposer.getEmailId() : "";
    }
}
