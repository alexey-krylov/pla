package com.pla.grouphealth.sharedresource.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Admin on 10-Dec-15.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactPersonDetailDto {

    private String contactPersonName;

    private String contactPersonEmail;

    private String contactPersonMobileNumber;

    private String contactPersonWorkPhoneNumber;

}
