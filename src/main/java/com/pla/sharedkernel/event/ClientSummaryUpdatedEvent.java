package com.pla.sharedkernel.event;

import com.pla.publishedlanguage.dto.ClientDetailDto;
import com.pla.sharedkernel.domain.model.Gender;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.util.List;

/**
 *
 *
 */

@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class ClientSummaryUpdatedEvent {
    private String clientName;
    private String clientCode;
    private String address1;
    private String address2;
    private DateTime dateOfBirth;
    private Gender gender;
    private String postalCode;
    private String nrcNumber;
    private String companyName;
    private String province;
    private String town;
    private String emailAddress;
    private List<ClientDetailDto.ClientDocumentDetailDto> clientDocuments;

    public ClientSummaryUpdatedEvent(String clientName,String address1,String address2,DateTime dateOfBirth,Gender gender,String postalCode,String nrcNumber,
                                     String companyName,String province,String town,String emailAddress,List<ClientDetailDto.ClientDocumentDetailDto>clientDocuments){

        this.clientName=clientName;
        this.address1=address1;
        this.address2=address2;
        this.dateOfBirth=dateOfBirth;
        this.gender=gender;
        this.postalCode=postalCode;
        this.nrcNumber=nrcNumber;
        this.companyName=companyName;
        this.province=province;
        this.town=town;
        this.emailAddress=emailAddress;
        this.clientDocuments=clientDocuments;

    }
}
