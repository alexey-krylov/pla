package com.pla.client.service;

import com.pla.client.domain.model.Client;
import com.pla.client.repository.ClientRepository;
import com.pla.publishedlanguage.contract.IClientProvider;
import com.pla.publishedlanguage.dto.ClientDetailDto;
import com.pla.sharedkernel.identifier.ClientId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Samir on 6/22/2015.
 */
@Service(value = "clientProvider")
public class ClientProviderImpl implements IClientProvider {

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public ClientDetailDto getClientDetail(String clientCode) {
        Client client = clientRepository.findOne(new ClientId(clientCode));
        if (client != null) {
            ClientDetailDto clientDetailDto = new ClientDetailDto(client.getClientCode().getClientId(), client.getClientName(), client.getAddress1(), client.getAddress2(), client.getPostalCode(), client.getProvince(), client.getTown(), client.getEmailAddress());
            return clientDetailDto;
        }
        return null;
    }
}
