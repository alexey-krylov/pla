package com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization;

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
public class GHClaimDocumentCommand {

    private String preAuthorizationRequestId;
    private UserDetails userDetails;
    private String documentId;
    private String filename;
    private boolean mandatory;
    private MultipartFile file;

}
