package com.pla.individuallife.proposal.domain.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Karunakar on 7/13/2015.
 */
@Getter
@EqualsAndHashCode(of = "documentId")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ILProposerDocument {

    private String documentId;

    private String documentName;

    private String contentType;

    private String gridFsDocId;

    public ILProposerDocument(String documentId, String documentName, String gridFsDocId, String contentType) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.gridFsDocId = gridFsDocId;
        this.contentType = contentType;
    }

    public ILProposerDocument updateWithNameAndContent(String documentName, String gridFsDocId, String contentType) {
        this.documentName = documentName;
        this.gridFsDocId = gridFsDocId;
        this.contentType = contentType;
        return this;
    }
}
