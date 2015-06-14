package com.pla.individuallife.quotation.presentation.dto;

import com.pla.sharedkernel.domain.model.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * Created by Karunakar on 5/20/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProposedAssuredDto {

    private String title;

    private String firstName;

    private String surname;

    private String nrcNumber;

    private DateTime dateOfBirth;

    private Gender gender;

    private String mobileNumber;

    private String emailAddress;

    private String occupation;

    public DateTime getDateOfBirth() {
        return dateOfBirth;
    }

}
