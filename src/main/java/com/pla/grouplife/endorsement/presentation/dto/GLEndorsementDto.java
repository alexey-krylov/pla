package com.pla.grouplife.endorsement.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * Created by Samir on 8/27/2015.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GLEndorsementDto {

    private String endorsementId;

    private String endorsementNumber;

    private String policyNumber;

    private String endorsementType;

    private String endorsementCode;

    private DateTime effectiveDate;

    private String policyHolderName;

    private Integer aging;

    private String status;
}
