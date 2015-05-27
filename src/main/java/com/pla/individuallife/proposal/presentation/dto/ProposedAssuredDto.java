package com.pla.individuallife.proposal.presentation.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;
import org.joda.time.LocalDate;
import org.nthdimenzion.presentation.LocalJodaDateDeserializer;
import org.nthdimenzion.presentation.LocalJodaDateSerializer;

import javax.validation.constraints.NotNull;

/**
 * Created by Prasant on 26-May-15.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProposedAssuredDto {

    private String title;

    private String firstName;

    private String surname;

    private String otherName;
    private String nrc;

    @JsonDeserialize(using = LocalJodaDateDeserializer.class)
    @JsonSerialize(using = LocalJodaDateSerializer.class)
    private LocalDate dateOfBirth;

    @NotNull(message = "{Assured gender cannot be null}")
    @NotEmpty(message = "{Assured gender cannot be empty}")
    private Gender gender;

    @NotNull(message = "{Assured MobileNumber cannot be null}")
    @NotEmpty(message = "{Assured MobileNumber cannot be empty}")
    private Long mobileNumber;

    @NotNull(message = "{Assured emailAddress cannot be null}")
    @NotEmpty(message = "{Assured emailAddress cannot be empty}")
    private String emailAddress;

    @NotNull(message = "{Assured marital cannot be null}")
    @NotEmpty(message = "{Assured marital cannot be empty}")
    private MaritalStatus maritalStatus;

    @NotNull(message = "{Assured residentialAddress cannot be null}")
    private ResidentialAddressDto residentialAddress;

    @NotNull(message = "{Assured employment cannot be null}")
    private EmploymentDto employment;

    private SpouseDto spouse;

}
