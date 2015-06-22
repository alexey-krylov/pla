package com.pla.client.service;

import com.pla.client.domain.model.Client;
import com.pla.client.domain.model.ClientBuilder;
import com.pla.client.repository.ClientRepository;
import com.pla.publishedlanguage.dto.ClientDetailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Admin on 5/28/2015.
 */
@Service
public class ClientApplicationService {

    private ClientRepository clientRepository;

    @Autowired
    public ClientApplicationService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public boolean createClient(ClientDetailDto clientDetailDto) {
        ClientBuilder clientBuilder = new ClientBuilder(clientDetailDto.getClientName());
        clientBuilder.withClientAddress(clientDetailDto.getAddress1(), clientDetailDto.getAddress2(), clientDetailDto.getProvince(), clientDetailDto.getPostalCode(), clientDetailDto.getTown());
        clientBuilder.withEmailAddress(clientDetailDto.getEmailAddress());
        clientBuilder.withClientDocument(clientDetailDto.getClientDocumentDetailDtoList());
        Client client = Client.createClient(clientBuilder, clientDetailDto.getClientCode());
        clientRepository.save(client);
        return true;
    }

}
