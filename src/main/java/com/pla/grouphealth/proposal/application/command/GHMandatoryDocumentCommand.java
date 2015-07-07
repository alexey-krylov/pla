package com.pla.grouphealth.proposal.application.command;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by Samir on 7/7/2015.
 */
@Getter
@Setter
public class GHMandatoryDocumentCommand {

    private String proposalId;

    private UserDetails userDetails;

    private List<MultipartFile> files;
}
