package com.pla.individuallife.endorsement.repository;

import com.pla.individuallife.endorsement.domain.model.IndividualLifeEndorsementStatusAudit;
import com.pla.sharedkernel.identifier.EndorsementId;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by Raghu on 10/20/2015.
 */
public interface ILEndorsementStatusAuditRepository extends MongoRepository<IndividualLifeEndorsementStatusAudit, ObjectId> {

    public List<IndividualLifeEndorsementStatusAudit> findByEndorsementId(EndorsementId endorsementId);
}
