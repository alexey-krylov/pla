package com.pla.individuallife.proposal.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by Prasant on 26-May-15.
 */

@Getter
@Setter
@NoArgsConstructor
public class ResidentialAddressDto {
private String address1;
private String address2;
private String province;
private String town;
private int postalCode;
private long homePhone;
private String emailAddress;
        }
