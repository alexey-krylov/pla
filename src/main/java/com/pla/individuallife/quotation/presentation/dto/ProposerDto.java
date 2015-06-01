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

    private String title;

    private String firstName;

    private String surname;

    private String nrcNumber;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String mobileNumber;

    private String emailAddress;

    public ProposerDto(Proposer proposer) {
        this.title = proposer.getProposerTitle();
        this.firstName = proposer.getProposerFName();
        this.surname = proposer.getProposerSurname();
        this.nrcNumber = proposer.getProposerNRC();
        this.dateOfBirth = proposer.getProposerDateOfBirth();
        this.gender = proposer.getGender();
        this.mobileNumber = proposer.getProposerMobileNumber();
        this.emailAddress = proposer.getProposerEmailId() != null ? proposer.getProposerEmailId() : "";
    }
}
