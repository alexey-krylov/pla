package com.pla.client.domain.model;

import com.google.common.collect.Maps;
import com.pla.sharedkernel.identifier.ClientId;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Admin on 5/28/2015.
 */
@Document(collection = "client")
@Getter
@Setter(value = AccessLevel.PACKAGE)
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(of = "clientCode")
public class Client {

    @Id
    private ClientId clientCode;

    private String clientName;

    private String address1;

    private String address2;

    private String postalCode;

    private String provience;

    private String town;

    private String emailAddress;

    private Set<ClientDocument> clientDocuments;

    private Client(ClientBuilder clientBuilder,String clientCode){
        this.clientCode = new ClientId(clientCode);
        this.clientName = clientBuilder.getClientName();
        this.address1  = clientBuilder.getAddress1();
        this.address2 = clientBuilder.getAddress2();
        this.postalCode = clientBuilder.getPostalCode();
        this.provience = clientBuilder.getProvience();
        this.town = clientBuilder.getTown();
        this.emailAddress = clientBuilder.getEmailAddress();
    }

    public static Client createClient(ClientBuilder clientBuilder,String clientCode){
        return new Client(clientBuilder,clientCode);
    }


    private Set<ClientDocument> withClientDocument(List<ClientBuilder.ClientDocumentBuilder> clientDocumentsBuilder){
        Set<ClientDocument> clientDocuments = clientDocumentsBuilder.stream().map(new Function<ClientBuilder.ClientDocumentBuilder, ClientDocument>() {
            @Override
            public ClientDocument apply(ClientBuilder.ClientDocumentBuilder clientDocumentBuilder) {
                return new ClientDocument(clientDocumentBuilder);
            }
        }).collect(Collectors.toSet());
        return clientDocuments;
    }

    public List<Map<String,Object>> findClientDocument(){
        return this.clientDocuments.stream().map(new Function<ClientDocument, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(ClientDocument clientDocument) {
                Map<String,Object> clientDocumentMap = Maps.newLinkedHashMap();
                clientDocumentMap.put(clientDocument.getDocumentCode(), clientDocument.getRoutingLevel());
                return clientDocumentMap;
            }
        }).collect(Collectors.toList());
    }
}
