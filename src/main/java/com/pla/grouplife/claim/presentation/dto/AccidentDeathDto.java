package com.pla.grouplife.claim.presentation.dto;

import com.pla.grouplife.claim.domain.model.AccidentTypes;
import com.pla.grouplife.claim.domain.model.PoliceReportRegistered;
import com.pla.grouplife.claim.domain.model.PostMortemAutopsyDone;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * Created by ak
 */

@Getter
@Setter
@NoArgsConstructor
public class AccidentDeathDto {

        private AccidentTypes typeOfAccident;
        private String placeOfAccident;
        private LocalDate dateOfAccident;
        private DateTime timeOfAccident;
        private PostMortemAutopsyDone postMortemAutopsyDone;
     // private ApplyConditionTypes postMortemAutopsyDone;
        private PoliceReportRegistered policeReportRegistered;
    //private ApplyConditionTypes  policeReportRegistered;
        private String registrationNumber;
        private String policeStationName;
}
