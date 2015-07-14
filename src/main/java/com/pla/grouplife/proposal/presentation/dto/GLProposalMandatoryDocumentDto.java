package com.pla.grouplife.proposal.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Samir on 7/14/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class GLProposalMandatoryDocumentDto {

    private String documentId;

    private String documentName;

    private MultipartFile file;

    private byte[] content;

    private boolean submitted;

    public GLProposalMandatoryDocumentDto(String documentId, String documentName) {
        this.documentId = documentId;
        this.documentName = documentName;
    }

    public GLProposalMandatoryDocumentDto updateWithContent(byte[] content) {
        this.content = content;
        this.submitted = true;
        return this;
    }
}
