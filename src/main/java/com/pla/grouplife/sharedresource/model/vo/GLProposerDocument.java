package com.pla.grouplife.sharedresource.model.vo;

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

    private boolean mandatory;

    private boolean requireForSubmission;

    private boolean isApproved;

    public GLProposerDocument(){
    }

    public GLProposerDocument(String documentId, String documentName, String gridFsDocId, String contentType, boolean mandatory) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.gridFsDocId = gridFsDocId;
        this.contentType = contentType;
        this.mandatory = mandatory;
        this.requireForSubmission = true;
    }

    public GLProposerDocument(String documentId, boolean mandatory, boolean isApproved){
        this.documentId = documentId;
        this.mandatory = mandatory;
        this.isApproved = isApproved;
    }

    public GLProposerDocument updateWithNameAndContent(String documentName, String gridFsDocId, String contentType) {
        this.documentName = documentName;
        this.gridFsDocId = gridFsDocId;
        this.contentType = contentType;
        return this;
    }
}
