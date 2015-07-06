package com.pla.publishedlanguage.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Admin on 6/10/2015.
 */

@Getter
@Setter
public class ClientDocumentDto {

    private String documentCode;
    private boolean hasSubmitted;
    private String documentName;

    public ClientDocumentDto() {
    }

    public ClientDocumentDto(String documentCode, boolean hasSubmitted) {
        this.documentCode = documentCode;
        this.hasSubmitted = hasSubmitted;
    }
}
