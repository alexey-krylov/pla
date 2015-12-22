package com.pla.grouplife.claim.application.command;

import com.pla.grouplife.proposal.presentation.dto.GLProposalMandatoryDocumentDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

/**
 * Created by ak on 16/12/2015.
 */

@Getter
@Setter

public class GLClaimWaiveMandatoryDocumentCommand {

        private UserDetails userDetails;
        private String ClaimId;
        private List<GLProposalMandatoryDocumentDto> waivedDocuments;
    }


