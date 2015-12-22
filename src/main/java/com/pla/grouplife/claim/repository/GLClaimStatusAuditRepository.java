package com.pla.grouplife.claim.repository;

import com.pla.grouplife.claim.domain.model.GroupLifeClaimStatusAudit;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by ak.
 */

public interface GLClaimStatusAuditRepository extends MongoRepository<GroupLifeClaimStatusAudit, ObjectId> {

}
