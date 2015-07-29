package com.pla.grouphealth.proposal.presentation.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Samir on 7/7/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class GHProposalMandatoryDocumentDto {

    private String documentId;

    private String documentName;

    private MultipartFile file;

    private byte[] content;

    private boolean submitted;

    private String fileName;

    private String contentType;

    private String gridFsDocId;

    public GHProposalMandatoryDocumentDto(String documentId, String documentName) {
        this.documentId = documentId;
        this.documentName = documentName;
    }

    public GHProposalMandatoryDocumentDto updateWithContent(byte[] content) {
        this.content = content;
        this.submitted = true;
        return this;
    }
}
