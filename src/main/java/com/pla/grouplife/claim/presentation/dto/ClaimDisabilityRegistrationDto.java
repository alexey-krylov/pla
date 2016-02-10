package com.pla.grouplife.claim.presentation.dto;

import com.pla.grouplife.claim.domain.model.DisabilityExtent;
import com.pla.grouplife.claim.domain.model.DisabilityNature;
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
    private DisabilityNature natureOfDisability;
    private DisabilityExtent extendOfDisability;
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
    private Boolean isAssuredConfinedToIndoor;
    private DateTime fromIndoorDate;
    private String assuredIndoorDetails;
    private Boolean isAssuredAbleToGetOutdoor;
    private DateTime fromOutdoorDate;
    private String assuredOutdoorDetails;
    private String visitingMedicalOfficerDetails;
}
