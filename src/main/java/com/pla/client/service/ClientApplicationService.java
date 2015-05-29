package com.pla.client.service;

import com.pla.client.domain.model.Client;
import com.pla.client.domain.model.ClientBuilder;
import com.pla.client.dto.ClientDetailDto;
import com.pla.client.repository.ClientRepository;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Admin on 5/28/2015.
 */
@Service
public class ClientApplicationService {

    private ClientRepository clientRepository;

    private IIdGenerator idGenerator;

    @Autowired
    public ClientApplicationService(ClientRepository clientRepository,IIdGenerator idGenerator){
        this.clientRepository = clientRepository;
        this.idGenerator = idGenerator;
    }

    @Transactional
    public boolean createClient(ClientDetailDto clientDetailDto) {
        ClientBuilder clientBuilder = new ClientBuilder(clientDetailDto.getClientName());
        clientBuilder.withClientAddress(clientDetailDto.getAddress1(),clientDetailDto.getAddress2(),clientDetailDto.getProvience(),clientDetailDto.getPostalCode(),clientDetailDto.getTown());
        clientBuilder.withEmailAddress(clientDetailDto.getEmailAddress());
        clientBuilder.withClientDocument(clientDetailDto.getClientDocumentDetailDto());
        Client client = Client.createClient(clientBuilder, idGenerator.nextId());
        clientRepository.save(client);
        return true;
    }

}
