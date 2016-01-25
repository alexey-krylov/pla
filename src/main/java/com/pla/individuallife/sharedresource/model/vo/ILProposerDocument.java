package com.pla.individuallife.sharedresource.model.vo;

import lombok.*;

/**
 * Created by Karunakar on 7/13/2015.
 */
@Getter
@Setter
@EqualsAndHashCode(of = "documentId")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
public class ILProposerDocument {

    private String documentId;

    private String documentName;

    private String contentType;

    private String gridFsDocId;

    private boolean mandatory;

    @Setter
    private boolean isApproved;

    private boolean requireForSubmission;

    public ILProposerDocument(String documentId, String documentName, String gridFsDocId, String contentType,boolean mandatory,boolean isApproved) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.gridFsDocId = gridFsDocId;
        this.contentType = contentType;
        this.mandatory = mandatory;
        this.isApproved = isApproved;
        this.requireForSubmission=true;
    }

    public ILProposerDocument(String documentId,boolean mandatory,boolean isApproved){
        this.documentId = documentId;
        this.mandatory = mandatory;
        this.isApproved = isApproved;
        this.requireForSubmission=true;
    }

    public ILProposerDocument updateWithNameAndContent(String documentName, String gridFsDocId, String contentType) {
        this.documentName = documentName;
        this.gridFsDocId = gridFsDocId;
        this.contentType = contentType;
        return this;
    }
}
