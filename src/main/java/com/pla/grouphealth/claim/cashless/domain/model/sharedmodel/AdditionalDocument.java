package com.pla.grouphealth.claim.cashless.domain.model.sharedmodel;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Author - Mohan Sharma Created on 1/28/2016.
 */
@Getter
@Setter
@EqualsAndHashCode(of = {"documentCode"})
@ToString
public class AdditionalDocument {
    private String documentCode;
    private boolean hasSubmitted;
    private String documentName;

    public AdditionalDocument(String documentCode, String documentName) {
        this.documentCode = documentCode;
        this.documentName = documentName;
    }
}
