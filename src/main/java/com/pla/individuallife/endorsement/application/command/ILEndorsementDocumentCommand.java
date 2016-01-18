package com.pla.individuallife.endorsement.application.command;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Samir on 7/14/2015.
 */
@Getter
@Setter
public class ILEndorsementDocumentCommand {

    private String endorsementId;

    private UserDetails userDetails;

    private String documentId;

    private String filename;

    private MultipartFile file;

    private boolean additional;

    private boolean mandatory;

}
