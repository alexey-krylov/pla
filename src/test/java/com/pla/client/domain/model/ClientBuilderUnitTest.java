package com.pla.client.domain.model;

import com.google.common.collect.Lists;
import com.pla.publishedlanguage.dto.ClientDetailDto;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created by Admin on 5/28/2015.
 */
public class ClientBuilderUnitTest {

    ClientBuilder clientBuilder;

    @Before
    public void setUp(){
        String clientName = "Test One";
        clientBuilder =   new ClientBuilder(clientName);
    }

    @Test
    public void givenAClientName_thenItShouldCreateTheClientWithGivenName(){
        String clientName = "Test the client Name";
        ClientBuilder clientBuilder = new ClientBuilder(clientName);
        assertThat("Test the client Name" ,is(clientBuilder.getClientName()));
    }

    @Test
    public void givenEmailAddress_thenItShouldAddTheEmailAddressToCreatedClientBuilder(){
        String emailAddress = "sometest@gmail.com";
        clientBuilder = clientBuilder.withEmailAddress(emailAddress);
        assertThat("sometest@gmail.com" ,is(clientBuilder.getEmailAddress()));
    }

    @Test
    public void givenClientAddressDetails_thenItShouldAddTheDetailsToTheCreatedClientBuilder(){
        String expectedAddress1  ="test Address One";
        String expectedAddress2 = "test Address Two";
        String town = "Bengaluru";
        String provience = "IND";
        clientBuilder = clientBuilder.withClientAddress("test Address One","test Address Two","IND","590062","Bengaluru");
        assertThat(expectedAddress1,is(clientBuilder.getAddress1()));
        assertThat(expectedAddress2,is(clientBuilder.getAddress2()));
        assertThat(town,is(clientBuilder.getTown()));
        assertThat(provience,is(clientBuilder.getProvience()));
    }

    @Test
    public void givenClientDocumentDetail_thenItShouldAddTheDocumentDetailToTheCreatedClientBuilder(){
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

        clientBuilder = clientBuilder.withClientDocument(clientDocument);
        assertThat(clientBuilder.getClientDocuments().size(),is(2));
    }

}

