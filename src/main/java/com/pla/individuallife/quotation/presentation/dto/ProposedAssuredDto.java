package com.pla.individuallife.quotation.presentation.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pla.sharedkernel.domain.model.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.nthdimenzion.presentation.LocalJodaDateDeserializer;
import org.nthdimenzion.presentation.LocalJodaDateSerializer;

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

    @JsonDeserialize(using = LocalJodaDateDeserializer.class)
    @JsonSerialize(using = LocalJodaDateSerializer.class)
    private LocalDate dateOfBirth;

    private Gender gender;

    private String mobileNumber;

    private String emailAddress;

    private String occupation;

}
