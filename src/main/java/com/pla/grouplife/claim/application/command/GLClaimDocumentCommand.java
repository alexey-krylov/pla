package com.pla.grouplife.claim.application.command;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by ak
 */
@Getter
@Setter
public class GLClaimDocumentCommand {

    private String claimId;

    private UserDetails userDetails;

    private String documentId;

    private String filename;

    private MultipartFile file;

    private boolean additional;

    private boolean mandatory;
}




