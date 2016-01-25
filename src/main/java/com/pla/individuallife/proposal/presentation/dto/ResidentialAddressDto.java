package com.pla.individuallife.proposal.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Prasant on 26-May-15.
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResidentialAddressDto {
private String address1;
private String address2;
private String province;
private String town;
private String postalCode;
private String homePhone;
private String emailAddress;
        }
