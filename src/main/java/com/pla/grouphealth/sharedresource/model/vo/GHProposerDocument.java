package com.pla.grouphealth.sharedresource.model.vo;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by Samir on 6/24/2015.
 */
@Getter
@EqualsAndHashCode(of = "documentId")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GHProposerDocument {

    private String documentId;

    private String documentName;

    private String contentType;

    private String gridFsDocId;

    private boolean mandatory;

    private boolean requireForSubmission;


    public GHProposerDocument(String documentId, String documentName, String gridFsDocId, String contentType, boolean mandatory) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.gridFsDocId = gridFsDocId;
        this.contentType = contentType;
        this.mandatory = mandatory;
        this.requireForSubmission = true;
    }

    public GHProposerDocument updateWithNameAndContent(String documentName, String gridFsDocId, String contentType) {
        this.documentName = documentName;
        this.gridFsDocId = gridFsDocId;
        this.contentType = contentType;
        return this;
    }
}
