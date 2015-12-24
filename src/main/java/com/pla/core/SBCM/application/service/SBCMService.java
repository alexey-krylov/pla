package com.pla.core.SBCM.application.service;

import com.pla.core.SBCM.domain.model.ServiceBenefitCoverageMapping;
import com.pla.core.SBCM.query.SBCMFinder;
import com.pla.core.SBCM.repository.SBCMRepository;
import com.pla.core.repository.PlanRepository;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Mohan Sharma on 12/24/2015.
 */
@DomainService
public class SBCMService {
    private PlanRepository planRepository;
    private SBCMFinder sbcmFinder;
    private SBCMRepository sbcmRepository;

    @Autowired
    public SBCMService(PlanRepository planRepository, SBCMFinder sbcmFinder, SBCMRepository sbcmRepository){
        this.planRepository = planRepository;
        this.sbcmRepository = sbcmRepository;
        this.sbcmFinder = sbcmFinder;
    }
}
