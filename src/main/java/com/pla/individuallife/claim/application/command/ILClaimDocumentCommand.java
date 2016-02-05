package com.pla.individuallife.claim.application.command;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by ak on 21/1/2016.
 */
@Getter
@Setter
public class ILClaimDocumentCommand {
    private String claimId;

    private UserDetails userDetails;

    private String documentId;

    private String filename;

    private MultipartFile file;

    private boolean additional;

    private boolean mandatory;
}

