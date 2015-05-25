package com.pla.individuallife.domain.model.quotation;

import com.pla.sharedkernel.domain.model.Gender;
import lombok.Getter;
import org.joda.time.LocalDate;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Getter
public class ProposerBuilder {

    private String proposerId;

    private String proposerTitle;

    private String proposerFName;

    private String proposerSurname;

    private String proposerNRC;

    private LocalDate dateOfBirth;

    private Number ageNextBirthDay;

    private Gender gender;

    private String mobileNumber;

    private String emailId;

    ProposerBuilder( String  proposerId, String proposerTitle, String proposerFName, String proposerSurname, String proposerNRC, LocalDate dateOfBirth, Number ageNextBirthDay, Gender gender, String mobileNumber, String emailId) {
        checkArgument(isNotEmpty(proposerTitle));
        checkArgument(isNotEmpty(proposerFName));
        checkArgument(isNotEmpty(proposerSurname));
        checkArgument(isNotEmpty(proposerNRC));
        checkArgument(dateOfBirth != null);
        checkArgument(gender != null);
        checkArgument(isNotEmpty(mobileNumber));
        this.proposerTitle = proposerTitle;
        this.proposerFName = proposerFName;
        this.proposerSurname = proposerSurname;
        this.proposerNRC = proposerNRC;
        this.dateOfBirth = dateOfBirth;
        this.ageNextBirthDay =  ageNextBirthDay;
        this.gender = gender;
        this.mobileNumber = mobileNumber;
        this.emailId = emailId;
    }

    public Proposer build() {
        return new Proposer(this);
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
