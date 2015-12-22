package com.pla.client.service;

import com.pla.client.domain.model.Client;
import com.pla.client.domain.model.ClientBuilder;
import com.pla.client.repository.ClientRepository;
import com.pla.core.query.ClientSummaryFinder;
import com.pla.publishedlanguage.dto.ClientDetailDto;
import com.pla.sharedkernel.event.ClientSummaryUpdatedEvent;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;
/**
 * Created by Admin on 5/28/2015.
 */
@Service
public class ClientApplicationService {

    private ClientRepository clientRepository;
    private ClientSummaryFinder clientSummaryFinder;

    @Autowired
      public ClientApplicationService(ClientRepository clientRepository,ClientSummaryFinder clientSummaryFinder) {
        this.clientRepository = clientRepository;
        this.clientSummaryFinder = clientSummaryFinder;
    }
  /*
    @Autowired
    public ClientApplicationService(ClientSummaryFinder clientSummaryFinder) {
        this.clientSummaryFinder = clientSummaryFinder;
    }

   */

    public boolean createClient(ClientDetailDto clientDetailDto) {
        ClientBuilder clientBuilder = new ClientBuilder(clientDetailDto.getClientName());
        clientBuilder.withClientAddress(clientDetailDto.getAddress1(), clientDetailDto.getAddress2(), clientDetailDto.getProvince(), clientDetailDto.getPostalCode(), clientDetailDto.getTown());
        clientBuilder.withEmailAddress(clientDetailDto.getEmailAddress());
        Client client = Client.createClient(clientBuilder, clientDetailDto.getClientCode());
        clientRepository.save(client);
        return true;
    }


    /*
    * UPDATE THE CLIENT RECORD
    * */

    public boolean updateClient(ClientSummaryUpdatedEvent clientUpdatedEvent) {

        List<Client> clientList =clientRepository.findByClientNameAndAddress1AndTownAndEmailAddress(clientUpdatedEvent.getClientName(), clientUpdatedEvent.getAddress1(), clientUpdatedEvent.getTown(), clientUpdatedEvent.getEmailAddress());
        checkArgument(isNotEmpty(clientList), "Client Summary can not be null");
        checkArgument(clientList.size() == 1);
        Client client = clientList.get(0);
        List<ClientDetailDto.ClientDocumentDetailDto> addedDocumentList = clientUpdatedEvent.getClientDocuments();
        ClientBuilder clientBuilder = new ClientBuilder(clientUpdatedEvent.getClientName());
        clientBuilder.withClientAddress(clientUpdatedEvent.getAddress1(), clientUpdatedEvent.getAddress2(), clientUpdatedEvent.getProvince(), clientUpdatedEvent.getPostalCode(), clientUpdatedEvent.getTown());
        clientBuilder.withEmailAddress(clientUpdatedEvent.getEmailAddress());
        clientBuilder.withClientDocument(addedDocumentList);
        client = client.updateClient(clientBuilder);
        clientRepository.save(client);
        return true;

    }

     public ClientDetailDto getClientSummaryDetail(String clientId,LineOfBusinessEnum lineOfBusiness){
         return clientSummaryFinder.getClientDetail( clientId,lineOfBusiness.toString());


     }


}