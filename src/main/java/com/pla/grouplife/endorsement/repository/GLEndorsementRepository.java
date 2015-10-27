package com.pla.grouplife.endorsement.repository;

import com.pla.grouplife.endorsement.domain.model.GroupLifeEndorsement;
import com.pla.sharedkernel.identifier.EndorsementId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Admin on 10/27/2015.
 */
public interface GLEndorsementRepository extends MongoRepository<GroupLifeEndorsement, EndorsementId>{
}
