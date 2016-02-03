package com.pla.grouplife.claim.application.command;

import com.pla.grouplife.claim.domain.model.ClaimantDetail;
import com.pla.grouplife.claim.presentation.dto.*;
import com.pla.sharedkernel.domain.model.ClaimType;
import com.pla.sharedkernel.domain.model.Relationship;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 * Created by ak
 */

@Getter
@Setter
@NoArgsConstructor
public class CreateGLClaimIntimationCommand {

    private String policyNumber;
    private String schemeName;
    private Relationship relationship;
    private String category;
    private ClaimType claimType;
    private DateTime claimIntimationDate;
    private DateTime claimIncidenceDate;
    private ClaimantDetail claimantDetail;
    private PlanDetailDto planDetail;
    private Set<CoverageDetailDto> coverageDetails;
    private BankDetailsDto bankDetails;
    private ClaimAssuredDetailDto claimAssuredDetail;
    private Set<GLClaimDocumentCommand> uploadedDocuments;
    private UserDetails userDetails;
    //private String comment;
}