package com.pla.core.dto;

import com.pla.sharedkernel.domain.model.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * Created by nthdimensioncompany on 22/9/2015.
 */

@Getter
@Setter
@NoArgsConstructor
public class ClientSummarySearchDto {
    private String firstName;
    private String clientId;
    private DateTime dateOfBirth;
    private Gender gender;
    private String nrc;
    private String companyName;
}
