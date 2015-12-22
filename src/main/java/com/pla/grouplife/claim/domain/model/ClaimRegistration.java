package com.pla.grouplife.claim.domain.model;

import lombok.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;
/**
 * Created by ak
 */
@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
@AllArgsConstructor
public class ClaimRegistration {

    private String causeOfDeath;
    private String placeOfDeath;
    private LocalDate dateOfDeath;
    private DateTime timeOfDeath;
    private String durationOfIllness;
    private String  nameOfDoctorAndHospialAddress;
    private String contactNumber;
    private LocalDate firstConsultation;
    private String treatementTaken;
    private CauseOfDeathAccidental causeOfDeathAccidental;
    private AccidentTypes typeOfAccident;
    private String placeOfAccident;
    private LocalDate dateOfAccident;
    private DateTime  timeOfAccident;
    private PostMortemAutopsyDone postMortemAutopsyDone;
    private PoliceReportRegistered policeReportRegistered;
    private String registrationNumber;
    private String policeStationName;
  // private Set<GLClaimDocument> claimDocuments;
}
