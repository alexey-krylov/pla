package com.pla.grouplife.policy.application.command;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by ASUS on 17-Nov-15.
 */
@Getter
@Setter
public class GLPolicyDocumentCommand {


    private String policyId;

    private UserDetails userDetails;

    private String documentId;

    private String filename;

    private MultipartFile file;

    private boolean additional;

    private boolean mandatory;
}
