package com.pla.individuallife.endorsement.repository;

import com.pla.individuallife.endorsement.domain.model.IndividualLifeEndorsement;
import com.pla.sharedkernel.identifier.EndorsementId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Admin on 10/27/2015.
 */
public interface ILEndorsementRepository extends MongoRepository<IndividualLifeEndorsement, EndorsementId>{
}
