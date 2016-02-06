package com.pla.grouphealth.claim.cashless.application.command.claim;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by Mohan Sharma on 1/22/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class GroupHealthCashlessClaimRemoveAdditionalDocumentCommand {
    @NotNull(message = "groupHealthCashlessClaimId must not be null")
    @NotEmpty(message = "groupHealthCashlessClaimId must not be null")
    private String groupHealthCashlessClaimId;
    @NotNull(message = "gridFsDocId must not be null")
    @NotEmpty(message = "gridFsDocId must not be null")
    private String gridFsDocId;
}
