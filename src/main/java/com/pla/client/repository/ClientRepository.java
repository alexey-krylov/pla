package com.pla.client.repository;

import com.pla.client.domain.model.Client;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.identifier.ClientId;
import org.joda.time.DateTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by Admin on 5/28/2015.
 */
public interface ClientRepository extends MongoRepository<Client, ClientId> {

    /*
    * DOB,first name,gender and nrc number
    * */


     @Query( value = "{'clientName' : ?0,'dateOfBirth' : ?1,'gender' : ?2,'nrcNumber' : ?3 }")
    public List<Client> findByClientNameAndDateOfBirthAndGenderAndNrcNumber(String clientName,DateTime date,Gender gender,String nrcNumber);

    @Query( value = "{'clientName' : ?0,'address1' : ?1,'town' : ?2}")
    public List<Client> findByClientNameAndAddress1AndTownAndEmailAddress(String clientName,String address1,String town,String email);
}



