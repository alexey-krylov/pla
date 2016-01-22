package com.pla.grouphealth.claim.cashless.application.command;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Mohan Sharma on 1/22/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class PreAuthorizationRemoveAdditionalCommand {
    private String preAuthorizationId;
    private String gridFsDocId;
}
