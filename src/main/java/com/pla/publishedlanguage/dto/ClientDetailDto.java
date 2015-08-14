package com.pla.publishedlanguage.dto;

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
public class ClientDetailDto {

    private String clientCode;

    private String clientName;

    private String address1;

    private String address2;

    private String postalCode;

    private String province;

    private String town;

    private String emailAddress;

    private List<ClientDocumentDetailDto> clientDocumentDetailDtoList;


    public ClientDetailDto(String clientCode, String clientName, String address1, String address2, String postalCode, String province, String town, String emailAddress) {
        this.clientCode = clientCode;
        this.clientName = clientName;
        this.address1 = address1;
        this.address2 = address2;
        this.postalCode = postalCode;
        this.province = province;
        this.town = town;
        this.emailAddress = emailAddress;
    }

    @Getter
    @Setter
    public class ClientDocumentDetailDto {

        private String documentId;

        private String documentName;

        private String documentType;

        private String routingLevel;

        private String documentContent;
    }
}
