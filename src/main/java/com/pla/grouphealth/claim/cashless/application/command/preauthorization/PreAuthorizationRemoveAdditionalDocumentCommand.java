package com.pla.grouphealth.claim.cashless.application.command.preauthorization;

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
public class PreAuthorizationRemoveAdditionalDocumentCommand {
    @NotNull(message = "preAuthorizationId must not be null")
    @NotEmpty(message = "preAuthorizationId must not be null")
    private String preAuthorizationId;
    @NotNull(message = "gridFsDocId must not be null")
    @NotEmpty(message = "gridFsDocId must not be null")
    private String gridFsDocId;
}
