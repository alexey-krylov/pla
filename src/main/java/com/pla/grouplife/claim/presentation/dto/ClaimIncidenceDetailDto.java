package com.pla.grouplife.claim.presentation.dto;

import com.pla.grouplife.claim.domain.model.CauseOfDeathAccidental;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * Created by ak on 6/1/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class ClaimIncidenceDetailDto {

    private String causeOfDeath;
    private String placeOfDeath;
    private LocalDate dateOfDeath;
    private DateTime timeOfDeath;
    private String durationOfIllness;
    private String  nameOfDocterAndHospialAddress;
    private String contactNumber;
    private LocalDate firstConsultation;
    private String treatementTaken;
    private CauseOfDeathAccidental causeOfDeathAccidental;
    private AccidentDeathDto accidentDeath;
}
