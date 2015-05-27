package com.pla.individuallife.proposal.presentation.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.nthdimenzion.presentation.LocalJodaDateDeserializer;
import org.nthdimenzion.presentation.LocalJodaDateSerializer;
import org.joda.time.LocalDate;

/**
 * Created by ASUS on 26-May-15.
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

    @JsonDeserialize(using = LocalJodaDateDeserializer.class)
    @JsonSerialize(using = LocalJodaDateSerializer.class)
     private LocalDate dateOfBirth;

     private Gender gender;
     private Long mobileNumber;
     private String emailAddress;
    private MaritalStatus maritalStatus;

    private ResidentialAddressDto residentialAddress;
    private EmploymentDto employment;
    private SpouseDto spouse;
}
