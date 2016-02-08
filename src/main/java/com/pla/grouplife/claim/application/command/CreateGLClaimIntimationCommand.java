package com.pla.grouplife.claim.application.command;

import com.pla.grouplife.claim.domain.model.ClaimantDetail;
import com.pla.grouplife.claim.presentation.dto.BankDetailsDto;
import com.pla.grouplife.claim.presentation.dto.ClaimAssuredDetailDto;
import com.pla.grouplife.claim.presentation.dto.CoverageDetailDto;
import com.pla.grouplife.claim.presentation.dto.PlanDetailDto;
import com.pla.sharedkernel.domain.model.ClaimType;
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
    private String relationship;
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