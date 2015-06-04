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

    @JsonSerialize(using = LocalJodaDateSerializer.class)
    @JsonDeserialize(using = LocalJodaDateDeserializer.class)
    private LocalDate dateOfBirth;

    private Gender gender;

    private String mobileNumber;

    private String emailAddress;

}
