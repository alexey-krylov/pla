package com.pla.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by Admin on 5/28/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientDetailDto {

    private String clientName;

    private String address1;

    private String address2;

    private String postalCode;

    private String provience;

    private String town;

    private String emailAddress;

    private List<ClientDocumentDetailDto> clientDocumentDetailDto;

    @Getter
    @Setter
    public   class ClientDocumentDetailDto{

        private String documentId;

        private String documentName;

        private String documentType;

        private String routingLevel;

        private String documentContent;
    }
}
