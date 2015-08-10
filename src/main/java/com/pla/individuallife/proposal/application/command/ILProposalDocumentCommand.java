package com.pla.individuallife.proposal.application.command;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Karunakar on 7/14/2015.
 */
@Getter
@Setter
public class ILProposalDocumentCommand {

    private String proposalId;

    private UserDetails userDetails;

    private String documentId;

    private String filename;

    private MultipartFile file;

    private boolean mandatory;

//    private Boolean isApproved;
}
