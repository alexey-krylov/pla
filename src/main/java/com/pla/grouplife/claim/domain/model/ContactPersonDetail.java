package com.pla.grouplife.claim.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by on 20/1/2016.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class ContactPersonDetail {

    private String contactPersonName;

    private String contactPersonEmail;

    private String contactPersonMobileNumber;

    private String contactPersonWorkPhoneNumber;
}
