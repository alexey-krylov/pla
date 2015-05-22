package com.pla.individuallife.domain.model.proposal;

import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.TitleEnum;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

/**
 * Created by pradyumna on 22-05-2015.
 */
public class Beneficiary {

    private TitleEnum title;
    private String firstName;
    private String surname;
    private String nrc;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String relationshipId;
    private BigDecimal share;
}
