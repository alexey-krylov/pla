package com.pla.grouphealth.proposal.domain.model;

import lombok.Getter;

/**
 * Created by Samir on 6/24/2015.
 */
@Getter
public class GHProposerDocument {

    private String documentId;

    private String documentName;

    private boolean submitted;


    public GHProposerDocument(String documentId, String documentName, boolean submitted) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.submitted = submitted;
    }
}
