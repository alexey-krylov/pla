package com.pla.client.repository;

import com.pla.client.domain.model.Client;
import com.pla.sharedkernel.identifier.ClientId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Admin on 5/28/2015.
 */
public interface ClientRepository extends MongoRepository<Client, ClientId> {

}
