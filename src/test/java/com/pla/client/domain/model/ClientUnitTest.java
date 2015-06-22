package com.pla.client.domain.model;

import com.google.common.collect.Lists;
import com.pla.publishedlanguage.dto.ClientDetailDto;
import com.pla.sharedkernel.identifier.ClientId;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Admin on 5/28/2015.
 */
public class ClientUnitTest {


    @Test
    public void givenClientDetail_thenItShouldCreateTheClient(){
        String clientName = "Test One";
        String emailAddress = "sometest@gmail.com";
        String expectedAddress1  ="test Address One";
        String expectedAddress2 = "test Address Two";
        String provience = "IND";

        ClientDetailDto clientDetailDto = new ClientDetailDto();
        List<ClientDetailDto.ClientDocumentDetailDto> clientDocument = Lists.newArrayList();
        ClientDetailDto.ClientDocumentDetailDto clientDocumentDetail  = clientDetailDto.new ClientDocumentDetailDto();
        clientDocumentDetail.setDocumentId("D001");
        clientDocumentDetail.setDocumentName("Document One");
        clientDocumentDetail.setDocumentType("UNDERWRITER");
        clientDocumentDetail.setRoutingLevel("UNDERWRITING_LEVEL_ONE");
        clientDocument.add(clientDocumentDetail);

        clientDocumentDetail  = clientDetailDto.new ClientDocumentDetailDto();
        clientDocumentDetail.setDocumentId("D002");
        clientDocumentDetail.setDocumentName("Document Two");
        clientDocumentDetail.setDocumentType("MANDATORY");
        clientDocumentDetail.setRoutingLevel("UNDERWRITING_LEVEL_TWO");
        clientDocument.add(clientDocumentDetail);

        ClientBuilder clientBuilder = new ClientBuilder(clientName);
        clientBuilder = clientBuilder.withEmailAddress(emailAddress);
        clientBuilder = clientBuilder.withClientAddress("test Address One", "test Address Two", "IND", "590062", "Bengaluru");
        clientBuilder = clientBuilder.withClientDocument(clientDocument);
        Client client = Client.createClient(clientBuilder, "C001");

        assertNotNull(client);
        assertThat(client.getClientCode(), is(new ClientId("C001")));
        assertThat(client.getClientDocuments().size(), is(2));
        assertThat(expectedAddress1, is(client.getAddress1()));
        assertThat(expectedAddress2,is(client.getAddress2()));
        assertThat(provience,is(client.getProvience()));

    }
}
