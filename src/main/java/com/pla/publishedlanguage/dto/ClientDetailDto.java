package com.pla.publishedlanguage.dto;

import com.pla.core.dto.ClientPolicyDetailDto;
import com.pla.sharedkernel.domain.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by Admin on 5/28/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientDetailDto {

    private String clientCode;

    private String clientName;

    private String address1;

    private String address2;

    private DateTime dateOfBirth;

    private Gender gender;

    private String nrcNumber;

    private String companyName;

    private String postalCode;

    private String province;

    private String town;

    private String emailAddress;

    private List<ClientDocumentDetailDto> clientDocumentDetailDtoList;

    private List<ClientPolicyDetailDto> clientPolicyDetailDtoList;

    private List<ClientPolicyDetailDto> clientProposalDetailDtoList;

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
    public  ClientDetailDto updateWithClientDocuments(List<ClientDocumentDetailDto> clientDocumentDetailDtoList){
        this.clientDocumentDetailDtoList=clientDocumentDetailDtoList;
        return this;
    }

   public ClientDetailDto updateWithClientProposalDetails(List<ClientPolicyDetailDto> clientProposalDetailDtoList){
       this.clientProposalDetailDtoList=clientProposalDetailDtoList;
       return this;
   }

    public ClientDetailDto updateWithClientPolicyDetails(List<ClientPolicyDetailDto> clientPolicyDetailDtoList){
        this.clientPolicyDetailDtoList=clientPolicyDetailDtoList;
        return this;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    public  class ClientDocumentDetailDto {

        private String documentId;

        private String documentName;

        private String documentType;

        private String routingLevel;

        private String documentContent;

        public  ClientDocumentDetailDto(String documentId,String documentName, String documentType,String routingLevel,String documentContent){
            this.documentId=documentId;
            this.documentName=documentName;
            this.documentType=documentType;
            this.routingLevel=routingLevel;
            this.documentContent=documentContent;

        }
    }

}
