package com.pla.grouplife.claim.application.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.pla.sharedkernel.domain.model.ClaimType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.LocalDate;
import org.nthdimenzion.presentation.LocalJodaDateDeserializer;
import org.nthdimenzion.presentation.LocalJodaDateSerializer;


/**
 * Created by Mirror on 8/12/2015.
 */
@Getter
@Setter
public class GLClaimIntimationCommand {

    private String memberId;

    private ClaimType claimType;

    @JsonSerialize(using = LocalJodaDateSerializer.class)
    @JsonDeserialize(using = LocalJodaDateDeserializer.class)
    private LocalDate claimIntimationDate;

    private String policyNumber;

    private String bankName;

    private String bankBranchName;

    private String bankAccountType;

    private String bankAccountNumber;

    private String policyId;

}
