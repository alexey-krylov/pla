package com.pla.grouplife.claim.presentation.dto;

import com.pla.sharedkernel.domain.model.ClaimType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by ak on 3/2/2016.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ClaimantClaimInformationDto {

    private String claimId;
    private String claimNumber;
    private String firstName;
    private String lastName;
    private BigDecimal claimAmount;
    private ClaimType claimType;

}
