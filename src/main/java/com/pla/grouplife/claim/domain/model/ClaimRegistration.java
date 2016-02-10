package com.pla.grouplife.claim.domain.model;

import lombok.*;
import org.joda.time.DateTime;
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
    private DateTime dateOfDeath;
    private DateTime timeOfDeath;
    private String durationOfIllness;
    private String  nameOfDoctorAndHospitalAddress;
    private String contactNumber;
    private DateTime dateOfFirstConsultation;
    private String treatmentTaken;
    private Boolean isCauseOfDeathAccidental;
    private String typeOfAccident;
    private String placeOfAccident;
    private DateTime dateOfAccident;
    private DateTime  timeOfAccident;
    private Boolean isPostMortemAutopsyDone;
    private Boolean isPoliceReportRegistered;
    private String registrationNumber;
    private String policeStationName;
}
