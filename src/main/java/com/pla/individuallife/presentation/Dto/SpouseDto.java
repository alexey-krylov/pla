package com.pla.individuallife.presentation.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Prasant on 26-May-15.
 */

@Getter
@Setter
@NoArgsConstructor
public class SpouseDto {

    private String firstName ;
    private String surname;// "Proposed Spouse Surname",
    private Long mobileNumber;
    private String emailAddress;
}
