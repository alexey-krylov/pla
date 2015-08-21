package com.pla.individuallife.proposal.presentation.dto;

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
    private String mobileNumber;
    private String emailAddress;
}
