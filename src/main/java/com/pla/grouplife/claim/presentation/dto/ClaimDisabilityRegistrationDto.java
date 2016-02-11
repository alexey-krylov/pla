package com.pla.grouplife.claim.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by nthdimensioncompany on 7/1/2016.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ClaimDisabilityRegistrationDto {
    private DateTime dateOfDisability;
    private String natureOfDisability;
    private String extendOfDisability;
    private DateTime dateOfDiagnosis;
    private String  exactDiagnosis;
    private String nameOfDoctorAndHospitalAddress;
    private String contactNumberOfHospital;
    private DateTime  dateOfFirstConsultation;
    private String treatmentTaken;
    private List<String> capabilityOfAssuredDailyLiving;
    private String assuredGainfulActivities;
    private String detailsOfWorkActivities;
    private DateTime fromActivitiesDate;
    private String assuredConfinedToIndoor;
    private DateTime fromIndoorDate;
    private String assuredIndoorDetails;
    private String assuredAbleToGetOutdoor;
    private DateTime fromOutdoorDate;
    private String assuredOutdoorDetails;
    private String visitingMedicalOfficerDetails;
}
