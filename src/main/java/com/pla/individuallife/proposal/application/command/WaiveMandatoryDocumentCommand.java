package com.pla.individuallife.proposal.application.command;

import com.pla.individuallife.proposal.presentation.dto.ILProposalMandatoryDocumentDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Created by Admin on 8/31/2015.
 */
@Getter
@Setter
public class WaiveMandatoryDocumentCommand {
    private UserDetails userDetails;
    private String proposalId;
    private List<ILProposalMandatoryDocumentDto> waivedDocuments;
}
