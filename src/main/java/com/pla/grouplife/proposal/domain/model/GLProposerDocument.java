package com.pla.grouplife.proposal.domain.model;

import lombok.Getter;

/**
 * Created by Samir on 6/24/2015.
 */
@Getter
public class GLProposerDocument {

    private String documentId;

    private String documentName;

    private String contentType;

    private String gridFsDocId;

    public GLProposerDocument(String documentId, String documentName, String gridFsDocId, String contentType) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.gridFsDocId = gridFsDocId;
        this.contentType = contentType;
    }

    public GLProposerDocument updateWithNameAndContent(String documentName, String gridFsDocId, String contentType) {
        this.documentName = documentName;
        this.gridFsDocId = gridFsDocId;
        this.contentType = contentType;
        return this;
    }
}
