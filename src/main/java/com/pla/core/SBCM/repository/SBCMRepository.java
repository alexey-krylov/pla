package com.pla.core.SBCM.repository;

import com.pla.core.SBCM.domain.model.ServiceBenefitCoverageMapping;
import com.pla.core.SBCM.domain.model.ServiceBenefitCoverageMappingId;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Mohan Sharma on 12/24/2015.
 */
public interface SBCMRepository extends MongoRepository<ServiceBenefitCoverageMapping, ServiceBenefitCoverageMappingId>{
}
