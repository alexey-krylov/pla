package com.pla.individuallife.proposal.presentation.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Karunakar on 7/13/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ILProposalMandatoryDocumentDto {


    private String documentId;

    private String documentName;

    private MultipartFile file;

    private byte[] content;

    private boolean submitted;

    public ILProposalMandatoryDocumentDto(String documentId, String documentName) {
        this.documentId = documentId;
        this.documentName = documentName;
    }

    public ILProposalMandatoryDocumentDto updateWithContent(byte[] content){
        this.content=content;
        this.submitted=true;
        return this;
    }


}
