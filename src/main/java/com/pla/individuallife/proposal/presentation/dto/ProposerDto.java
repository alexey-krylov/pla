package com.pla.individuallife.proposal.presentation.dto;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * Created by Prasant on 26-May-15.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProposerDto {
    private String title;
    private String firstName;
    private String surname;
    private String otherName;
    private String nrc;
    private DateTime dateOfBirth;
    private Gender gender;
    private Long mobileNumber;
    private String emailAddress;
    private MaritalStatus maritalStatus;
    private ResidentialAddressDto residentialAddress;
    private EmploymentDto employment;
    private SpouseDto spouse;
    private boolean isProposer;
}
