package com.pla.individuallife.claim.application.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 * Created by ak on 25/1/2016.
 */

@Setter
@Getter
@NoArgsConstructor

public class CIRegistrationCommand {

    private String claimId;
    private String criticalIllness;
    private DateTime dateOfFirstConsultation;
    private DateTime dateOfDiagnosis;
    private String exactDiagnosis;
    private String nameOfDoctorAndHospitalAddress;
    private String contactNumberOfHospital;
    private String HistoryOfPresentIllness;
    private String anyOtherPastMedicalHistory;
    private String treatmentTaken;
    private Set<ILClaimDocumentCommand> uploadedDocuments;
    private String comments;
    private UserDetails userDetails;
}
