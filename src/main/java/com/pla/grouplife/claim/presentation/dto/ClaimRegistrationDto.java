package com.pla.grouplife.claim.presentation.dto;

import com.pla.grouplife.claim.domain.model.AccidentTypes;
import com.pla.grouplife.claim.domain.model.CauseOfDeathAccidental;
import com.pla.grouplife.claim.domain.model.PoliceReportRegistered;
import com.pla.grouplife.claim.domain.model.PostMortemAutopsyDone;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClaimRegistrationDto {
    private String causeOfDeath;
    private String placeOfDeath;
    private DateTime dateOfDeath;
    private DateTime timeOfDeath;
    private String durationOfIllness;
    private String  nameOfDocterAndHospialAddress;
    private String contactNumber;
    private DateTime firstConsultation;
    private String treatementTaken;
    private CauseOfDeathAccidental causeOfDeathAccidental;
    private AccidentTypes typeOfAccident;
    private String placeOfAccident;
    private DateTime dateOfAccident;
    private DateTime  timeOfAccident;
    private PostMortemAutopsyDone postMortemAutopsyDone;
    private PoliceReportRegistered policeReportRegistered;
    private String registrationNumber;
    private String policeStationName;

}
