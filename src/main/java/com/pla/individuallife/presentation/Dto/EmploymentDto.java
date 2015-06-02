package com.pla.individuallife.presentation.Dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.nthdimenzion.presentation.LocalJodaDateDeserializer;
import org.nthdimenzion.presentation.LocalJodaDateSerializer;

/**
 * Created by ASUS on 26-May-15.
 */

@Getter
@Setter
@NoArgsConstructor
public class EmploymentDto {

    private String occupation;
    private String employer;

    @JsonDeserialize(using = LocalJodaDateDeserializer.class)
    @JsonSerialize(using = LocalJodaDateSerializer.class)
    LocalDate employmentDate;// "11/07/2008",
    private int employmentType;
    private String address1;
    private String province;
    private String town;
}
