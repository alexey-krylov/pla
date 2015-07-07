package com.pla.grouphealth.proposal.presentation.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Samir on 7/7/2015.
 */
@Getter
@Setter
public class GHProposalMandatoryDocumentDto {

    private String documentId;

    private String documentName;

    private MultipartFile file;
}
