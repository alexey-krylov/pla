package com.pla.client.domain.model;

import com.google.common.collect.Maps;
import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.identifier.ClientId;
import lombok.*;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 5/28/2015.
 */
@Document(collection = "client")
@Getter
@Setter(value = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode
public class Client {

    @Id
    private ClientId clientCode;

    private FamilyId clientId;

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

    private Set<ClientDocument> clientDocuments;

    private Client(ClientBuilder clientBuilder,String clientCode){
        this.clientCode = new ClientId(clientCode);
        this.clientName = clientBuilder.getClientName();
        this.address1  = clientBuilder.getAddress1();
        this.address2 = clientBuilder.getAddress2();
        this.postalCode = clientBuilder.getPostalCode();
        this.province = clientBuilder.getProvience();
        this.town = clientBuilder.getTown();
            this.emailAddress = clientBuilder.getEmailAddress();
    }

    public static Client createClient(ClientBuilder clientBuilder,String clientCode){
        return new Client(clientBuilder,clientCode);
    }


    public Client withClientDocument(List<ClientBuilder.ClientDocumentBuilder> clientDocumentsBuilder){
        this.clientDocuments = clientDocumentsBuilder.stream().map(new Function<ClientBuilder.ClientDocumentBuilder, ClientDocument>() {
            @Override
            public ClientDocument apply(ClientBuilder.ClientDocumentBuilder clientDocumentBuilder) {
                return new ClientDocument(clientDocumentBuilder);
            }
        }).collect(Collectors.toSet());
        return this;
    }

    public List<Map<String,Object>> findClientDocument(){
        if (isNotEmpty(this.clientDocuments)) {
            return this.clientDocuments.stream().map(new Function<ClientDocument, Map<String, Object>>() {
                @Override
                public Map<String, Object> apply(ClientDocument clientDocument) {
                    Map<String, Object> clientDocumentMap = Maps.newLinkedHashMap();
                    clientDocumentMap.put(clientDocument.getDocumentCode(), clientDocument.getRoutingLevel());
                    return clientDocumentMap;
                }
            }).collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

    /*
    * create builder and set that to client
    * */
public Client updateClient(ClientBuilder clientBuilder){
   // this.clientName = clientBuilder.getClientName();
    this.address1  = clientBuilder.getAddress1();
    this.address2 = clientBuilder.getAddress2();
    this.postalCode = clientBuilder.getPostalCode();
    this.province = clientBuilder.getProvience();
    this.town = clientBuilder.getTown();
    this.emailAddress = clientBuilder.getEmailAddress();
    this.withClientDocument(clientBuilder.getClientDocuments());

    return this;
}
 }
