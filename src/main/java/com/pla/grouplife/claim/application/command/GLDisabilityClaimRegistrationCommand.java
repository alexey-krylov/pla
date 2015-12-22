package com.pla.grouplife.claim.application.command;

import com.pla.grouplife.claim.domain.model.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.LocalDate;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 * Created by ak
 */
@Getter
@Setter
@NoArgsConstructor
public class GLDisabilityClaimRegistrationCommand {
   private String claimNumber;
    private String claimId;
    private LocalDate dateOfDisability;
    private DisabilityNature natureOfDisability;
    private DisabilityExtent extendOfDisability;
    private LocalDate dateOfDiagnosis;
    private String  exactDiagnosis;
    private String nameOfDoctorAndHospitalAddress;
    private String contactNumberOfHospital;
    private LocalDate  dateOfFirstConsultation;
    private String treatmentTaken;
    private AssuredTaskOfDailyLiving capabilityOfAssuredDailyLiving;
    private String assuredGainfulActivities;
    private String detailsOfWorkActivities;
    private LocalDate fromActivitiesDate;
    private AssuredConfinedToHouse AssuredConfinedToIndoor;
    private LocalDate fromIndoorDate;
    private String assuredIndoorDetails;
    private AssuredOutdoorActive assuredAbleToGetOutdoor;
    private LocalDate fromOutdoorDate;
    private String assuredOutdoorDetails;
    private String visitingMedicalOfficerDetails;
    private UserDetails userDetails;
    private Set<GLClaimDocumentCommand> uploadedDocuments;

}
