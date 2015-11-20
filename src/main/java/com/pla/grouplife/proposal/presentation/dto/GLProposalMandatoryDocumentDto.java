package com.pla.grouplife.proposal.presentation.dto;

import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(of = {"documentId","documentName"})
public class GLProposalMandatoryDocumentDto {

    private String documentId;

    private String documentName;

    private MultipartFile file;

    private byte[] content;

    private boolean submitted;

    private String fileName;

    private String contentType;

    private String gridFsDocId;

    private boolean isRequireForSubmission;

    private Boolean isApproved  = Boolean.FALSE;

    private Boolean mandatory;

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
