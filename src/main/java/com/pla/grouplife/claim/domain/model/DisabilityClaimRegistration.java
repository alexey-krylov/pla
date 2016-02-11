package com.pla.grouplife.claim.domain.model;

import lombok.*;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by ak
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter(value = AccessLevel.PACKAGE)
@AllArgsConstructor

public class DisabilityClaimRegistration {

    private DateTime dateOfDisability;
    private String natureOfDisability;
    private String  extendOfDisability;
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
    private String  assuredAbleToGetOutdoor;
    private DateTime fromOutdoorDate;
    private String assuredOutdoorDetails;
    private String visitingMedicalOfficerDetails;

}
