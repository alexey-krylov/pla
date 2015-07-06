package com.pla.grouphealth.proposal.domain.model;

import lombok.Getter;

import java.io.InputStream;

/**
 * Created by Samir on 6/24/2015.
 */
@Getter
public class GHProposerDocument {

    private String documentId;

    private String documentName;

    private boolean submitted;

    private InputStream content;

    public GHProposerDocument(String documentId, String documentName, boolean submitted, InputStream content) {
        this.documentId = documentId;
        this.documentName = documentName;
        this.submitted = submitted;
        this.content = content;
    }
}
