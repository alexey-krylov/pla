package com.pla.grouplife.claim.application.command;

import com.pla.grouplife.claim.domain.model.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Set;

/**
 * Created by ak
 */
@Getter
@Setter
@NoArgsConstructor
public class GLDisabilityClaimRegistrationCommand {

    private String claimId;

    private DateTime dateOfDisability;
    private DisabilityNature natureOfDisability;
    private DisabilityExtent  extendOfDisability;
    private DateTime dateOfDiagnosis;
    private String  exactDiagnosis;
    private String nameOfDoctorAndHospitalAddress;
    private String contactNumberOfHospital;
    private DateTime  dateOfFirstConsultation;
    private String treatmentTaken;
    private List<AssuredTaskOfDailyLiving> capabilityOfAssuredDailyLiving;
    private String assuredGainfulActivities;
    private String detailsOfWorkActivities;
    private DateTime fromActivitiesDate;
    private AssuredConfinedToHouse assuredConfinedToIndoor;
    private DateTime fromIndoorDate;
    private String assuredIndoorDetails;
    private AssuredOutdoorActive  assuredAbleToGetOutdoor;
    private DateTime fromOutdoorDate;
    private String assuredOutdoorDetails;
    private String visitingMedicalOfficerDetails;
    private String comments;
    private UserDetails userDetails;
    private Set<GLClaimDocumentCommand> uploadedDocuments;

}
