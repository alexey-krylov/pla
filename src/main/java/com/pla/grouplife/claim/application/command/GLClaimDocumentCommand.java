package com.pla.grouplife.claim.application.command;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.File;

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

    private File file;

    //private MultipartFile file;

    private boolean additional;

    private boolean mandatory;
}




