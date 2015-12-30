package com.pla.core.SBCM.repository;

import ch.qos.logback.core.status.Status;
import com.pla.core.SBCM.domain.model.ServiceBenefitCoverageMapping;
import com.pla.core.SBCM.domain.model.ServiceBenefitCoverageMappingId;
import com.pla.sharedkernel.identifier.BenefitId;
import com.pla.sharedkernel.identifier.CoverageId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by Mohan Sharma on 12/24/2015.
 */
public interface SBCMRepository extends MongoRepository<ServiceBenefitCoverageMapping, ServiceBenefitCoverageMappingId> {
    @Query("{'planCode' : ?0,'coverageId':?1, 'benefitId' :?2 ,'service' : ?3, 'status' : ?4}")
    ServiceBenefitCoverageMapping findDistinctByPlanCodeAndCoverageIdAndBenefitIdAndService(String planCode, CoverageId coverageId, BenefitId benefitId, String service, ServiceBenefitCoverageMapping.Status status);

    @Query("{'status' : ?0}")
    List<ServiceBenefitCoverageMapping> findAllByStatus(ServiceBenefitCoverageMapping.Status status);
}
