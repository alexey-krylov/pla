package com.pla.publishedlanguage.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created by Admin on 6/10/2015.
 */

@Getter
@Setter
@EqualsAndHashCode(of = {"documentCode"})
@ToString
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

    public ClientDocumentDto(String documentCode, String documentName,boolean hasSubmitted) {
        this(documentCode,hasSubmitted);
        this.documentName = documentName;
    }

}
