package com.pla.client.domain.model;

import com.pla.client.dto.ClientDetailDto;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Admin on 5/28/2015.
 */
@Getter
public class ClientBuilder {

    private String clientName;

    private String address1;

    private String address2;

    private String postalCode;

    private String provience;

    private String town;

    private String emailAddress;

    private List<ClientDocumentBuilder> clientDocuments;

    public ClientBuilder(String clientName){
        this.clientName = clientName;
    }

    public ClientBuilder withClientAddress(String address1,String address2,String provience,String postalCode,String town){
        this.address1 = address1;
        this.address2 = address2;
        this.provience = provience;
        this.postalCode = postalCode;
        this.town = town;
        return this;
    }


    public ClientBuilder withEmailAddress(String emailAddress){
        this.emailAddress = emailAddress;
        return this;
    }

    public ClientBuilder withClientDocument(List<ClientDetailDto.ClientDocumentDetailDto> clientDocument){
        List<ClientDocumentBuilder> clientDocumentBuilders  =  clientDocument.stream().map(new TransformClientDocument()).collect(Collectors.toList());
        this.clientDocuments = clientDocumentBuilders;
        return this;
    }

    private class TransformClientDocument implements Function<ClientDetailDto.ClientDocumentDetailDto, ClientDocumentBuilder> {
        @Override
        public ClientDocumentBuilder apply(ClientDetailDto.ClientDocumentDetailDto clientDocumentDetailDto) {
            ClientDocumentBuilder clientDocumentBuilder  = new ClientDocumentBuilder(clientDocumentDetailDto.getDocumentId(),clientDocumentDetailDto.getDocumentName(),clientDocumentDetailDto.getDocumentType(),clientDocumentDetailDto.getRoutingLevel());
            return clientDocumentBuilder.withDocumentContent(clientDocumentDetailDto.getDocumentContent());
        }
    }

    @Getter
    public static class ClientDocumentBuilder{
        private String documentId;

        private String documentCode;

        private String documentType;

        private String routingLevel;

        private String documentContent;

        public ClientDocumentBuilder(String documentId, String documentCode, String documentType, String routingLevel) {
            this.documentId = documentId;
            this.documentCode = documentCode;
            this.documentType = documentType;
            this.routingLevel = routingLevel;
        }

        public ClientDocumentBuilder withDocumentContent(String documentContent){
            this.documentContent = documentContent;
            return this;
        }
    }

}
