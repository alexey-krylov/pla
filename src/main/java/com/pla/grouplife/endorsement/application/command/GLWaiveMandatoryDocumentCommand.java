package com.pla.grouplife.endorsement.application.command;

import com.pla.grouplife.proposal.presentation.dto.GLProposalMandatoryDocumentDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Created by Admin on 8/31/2015.
 */
@Getter
@Setter
public class GLWaiveMandatoryDocumentCommand {
    private UserDetails userDetails;
    private String endorsementId;
    private List<GLProposalMandatoryDocumentDto> waivedDocuments;
}
