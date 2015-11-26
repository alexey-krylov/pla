package com.pla.grouplife.sharedresource.model.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Samir on 6/24/2015.
 */
@Getter
@EqualsAndHashCode(of = {"documentId","gridFsDocId"})
public class GLProposerDocument {

    private String documentId;

    private String documentName;

    private String contentType;

    private String gridFsDocId;

    @Setter
    private boolean mandatory;

    private boolean requireForSubmission;

    @Setter
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
