package com.pla.grouphealth.claim.cashless.application.command;

import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationRequestProposerDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

/**
 * Created by Mohan Sharma on 1/8/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class CreatePreAuthorizationRequestCommand {
    private String policyNumber;
    private String schemeName;
    private DateTime claimIntimationDate;
    private String category;
    private String relationship;
    private String batchNumber;
    private String hcpCode;
    private DateTime preAuthorizationDate;
    private PreAuthorizationRequestProposerDetail preAuthorizationRequestProposerDetail;
}
