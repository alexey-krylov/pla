package com.pla.grouplife.claim.domain.model;

import lombok.*;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

/**
 * Created by ak
 */

@ValueObject
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@EqualsAndHashCode(of = {"documentId","gridFsDocId"})

public class GLClaimDocument {

    private String documentId;

    private String documentName;

    private String contentType;

    private String gridFsDocId;

    @Setter
    private boolean mandatory;

    private boolean requireForSubmission;

    @Setter
    private boolean isApproved;

    public GLClaimDocument(String documentId, String documentName, String gridFsDocId, String contentType, boolean mandatory) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.gridFsDocId = gridFsDocId;
        this.contentType = contentType;
        this.mandatory = mandatory;
        this.requireForSubmission = true;
    }

    public GLClaimDocument(String documentId, boolean mandatory, boolean isApproved){
        this.documentId = documentId;
        this.mandatory = mandatory;
        this.isApproved = isApproved;
    }


    public GLClaimDocument updateWithNameAndContent(String documentName, String gridFsDocId, String contentType) {
        this.documentName = documentName;
        this.gridFsDocId = gridFsDocId;
        this.contentType = contentType;
        return this;
    }
}
