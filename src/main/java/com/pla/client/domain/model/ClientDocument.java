package com.pla.client.domain.model;

import com.pla.underwriter.domain.model.RoutingLevel;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;

/**
 * Created by Admin on 5/28/2015.
 */
@ValueObject
@Getter(value = AccessLevel.PACKAGE)
@Setter(value = AccessLevel.PACKAGE)
@EqualsAndHashCode
public class ClientDocument {

    private String documentId;

    private String documentCode;

    private DocumentType documentType;

    private RoutingLevel routingLevel;

    private String documentContent;

    public ClientDocument(ClientBuilder.ClientDocumentBuilder clientDocumentBuilder){
        this.documentId = clientDocumentBuilder.getDocumentId();
        this.documentCode = clientDocumentBuilder.getDocumentCode();
        this.documentType = DocumentType.valueOf(clientDocumentBuilder.getDocumentType());
        this.routingLevel  = clientDocumentBuilder.getDocumentType()!=null?RoutingLevel.valueOf(clientDocumentBuilder.getRoutingLevel()):null;
        this.documentContent = clientDocumentBuilder.getDocumentContent();
    }

}
