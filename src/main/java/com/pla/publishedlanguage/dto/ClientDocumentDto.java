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
    private boolean isOptional;

    public ClientDocumentDto() {
    }

    public ClientDocumentDto(String documentCode, boolean isOptional) {
        this.documentCode = documentCode;
        this.isOptional = isOptional;
    }
}
