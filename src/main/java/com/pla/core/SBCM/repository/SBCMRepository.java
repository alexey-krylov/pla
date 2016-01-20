package com.pla.core.SBCM.repository;

import com.pla.core.SBCM.domain.model.ServiceBenefitCoverageMapping;
import com.pla.core.SBCM.domain.model.ServiceBenefitCoverageMappingId;
import com.pla.sharedkernel.identifier.BenefitId;
import com.pla.sharedkernel.identifier.CoverageId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

/**
 * Author - Mohan Sharma Created on 12/24/2015.
 */
public interface SBCMRepository extends MongoRepository<ServiceBenefitCoverageMapping, ServiceBenefitCoverageMappingId> {
    @Query("{'planCode' : ?0,'coverageId':?1, 'benefitId' :?2 ,'service' : ?3, 'status' : ?4}")
    ServiceBenefitCoverageMapping findDistinctByPlanCodeAndCoverageIdAndBenefitIdAndService(String planCode, CoverageId coverageId, BenefitId benefitId, String service, ServiceBenefitCoverageMapping.Status status);

    @Query("{'status' : ?0}")
    List<ServiceBenefitCoverageMapping> findAllByStatus(ServiceBenefitCoverageMapping.Status status);

    @Query("{'planCode':?0}")
    List<ServiceBenefitCoverageMapping> findAllByPlanCode(String planCode);

    @Query("{'planCode':?0, 'service' :?1}")
    List<ServiceBenefitCoverageMapping> findAllByPlanCodeAndService(String planCode, String service);

    @Query("{'coverageId':?0, 'benefitCode' : ?1, 'service' :?2}")
    List<ServiceBenefitCoverageMapping> findAllByCoverageIdAndBenefitCodeAndService(CoverageId coverageId, String benefitCode, String service);
}
