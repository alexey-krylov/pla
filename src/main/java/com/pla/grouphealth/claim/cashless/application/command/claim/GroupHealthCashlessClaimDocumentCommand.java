package com.pla.grouphealth.claim.cashless.application.command.claim;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

/**
 * Author - Mohan Sharma Created on 1/12/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class GroupHealthCashlessClaimDocumentCommand {

    private String groupHealthCashlessClaimId;
    private UserDetails userDetails;
    private String documentId;
    private String filename;
    private boolean mandatory;
    private MultipartFile file;

}
