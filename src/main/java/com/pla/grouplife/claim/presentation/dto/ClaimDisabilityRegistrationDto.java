package com.pla.grouplife.claim.presentation.dto;

import com.pla.grouplife.claim.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * Created by nthdimensioncompany on 7/1/2016.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class ClaimDisabilityRegistrationDto {
    private LocalDate dateOfDisability;
    private DisabilityNature natureOfDisability;
    private DisabilityExtent extendOfDisability;
    private LocalDate dateOfDiagnosis;
    private String  exactDiagnosis;
    private String nameOfDoctorAndHospitalAddress;
    private String contactNumberOfHospital;
    private LocalDate  dateOfFirstConsultation;
    private String treatmentTaken;
    private List<AssuredTaskOfDailyLiving> capabilityOfAssuredDailyLiving;
    private String assuredGainfulActivities;
    private String detailsOfWorkActivities;
    private LocalDate fromActivitiesDate;
    private AssuredConfinedToHouse assuredConfinedToIndoor;
    private LocalDate fromIndoorDate;
    private String assuredIndoorDetails;
    private AssuredOutdoorActive assuredAbleToGetOutdoor;
    private LocalDate fromOutdoorDate;
    private String assuredOutdoorDetails;
    private String visitingMedicalOfficerDetails;
}
