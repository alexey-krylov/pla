package com.pla.grouphealth.policy.application.command;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Admin on 11/12/2015.
 */
@Getter
@Setter
public class GHPolicyDocumentCommand {

    private String policyId;

    private UserDetails userDetails;

    private String documentId;

    private String filename;

    private boolean mandatory;

    private MultipartFile file;
}
