package com.pla.grouplife.claim.application.command;

import com.pla.grouplife.claim.domain.model.AccidentTypes;
import com.pla.grouplife.claim.domain.model.CauseOfDeathAccidental;
import com.pla.grouplife.claim.domain.model.PoliceReportRegistered;
import com.pla.grouplife.claim.domain.model.PostMortemAutopsyDone;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 * Created by ak
 */
@Getter
@Setter
@NoArgsConstructor
public class GLClaimRegistrationCommand {

    private String claimNumber;
    private String claimId;
    private String causeOfDeath;
    private String placeOfDeath;
    private LocalDate dateOfDeath;
    private DateTime timeOfDeath;
    private String durationOfIllness;
    private String  nameOfDoctorAndHospitalAddress;
    private String contactNumber;
    private LocalDate firstConsultation;
    private String treatmentTaken;
    private CauseOfDeathAccidental causeOfDeathAccidental;
    private AccidentTypes typeOfAccident;
    private String placeOfAccident;
    private LocalDate dateOfAccident;
    private DateTime  timeOfAccident;
    private PostMortemAutopsyDone postMortemAutopsyDone;
    private PoliceReportRegistered policeReportRegistered;
    private String registrationNumber;
    private String policeStationName;
    private UserDetails userDetails;
    private String comments;
    private Set<GLClaimDocumentCommand> uploadedDocuments;
}

