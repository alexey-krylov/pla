package com.pla.grouplife.endorsement.repository;

import com.pla.grouplife.endorsement.domain.model.GroupLifeEndorsementStatusAudit;
import com.pla.sharedkernel.identifier.EndorsementId;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by Admin on 10/20/2015.
 */
public interface GLEndorsementStatusAuditRepository extends MongoRepository<GroupLifeEndorsementStatusAudit, ObjectId> {

    public List<GroupLifeEndorsementStatusAudit> findByEndorsementId(EndorsementId endorsementId);
}
