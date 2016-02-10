package com.pla.grouplife.claim.presentation.dto;

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
    private String  nameOfDoctorAndHospitalAddress;
    private String contactNumber;
    private DateTime firstConsultation;
    private String treatmentTaken;
    private Boolean isCauseOfDeathAccidental;
    private String typeOfAccident;
    private String placeOfAccident;
    private DateTime dateOfAccident;
    private DateTime  timeOfAccident;
    private Boolean isPostMortemAutopsyDone;
    private Boolean IsPoliceReportRegistered;
    private String registrationNumber;
    private String policeStationName;

}
