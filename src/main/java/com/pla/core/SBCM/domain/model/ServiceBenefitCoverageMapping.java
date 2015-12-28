package com.pla.core.SBCM.domain.model;

import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.BenefitId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by Mohan Sharma on 12/24/2015.
 */
@Document(collection = "service_benefit_coverage_mapping")
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class ServiceBenefitCoverageMapping {
    @Id
    private ServiceBenefitCoverageMappingId serviceBenefitCoverageMappingId;
    private String planCode;
    private String planName;
    private BenefitId benefitId;
    private String benefitName;
    private CoverageId coverageId;
    private String coverageName;
    private String service;
    private Status status;

    public ServiceBenefitCoverageMapping updateWithId(ServiceBenefitCoverageMappingId serviceBenefitCoverageMappingId) {
        this.serviceBenefitCoverageMappingId = serviceBenefitCoverageMappingId;
        return this;
    }

    public enum Status {
        ACTIVE, INACTIVE;
    }

    public ServiceBenefitCoverageMapping updateWithPlanCode(String planCode){
        this.planCode = planCode;
        return this;
    }

    public ServiceBenefitCoverageMapping updateWithPlanName(String planName){
        this.planName = planName;
        return this;
    }

    public ServiceBenefitCoverageMapping updateWithBenefitId(BenefitId benefitId){
        this.benefitId = benefitId;
        return this;
    }

    public ServiceBenefitCoverageMapping updateWithBenefitName(String benefitName){
        this.benefitName = benefitName;
        return this;
    }

    public ServiceBenefitCoverageMapping updateWithCoverageId(CoverageId coverageId){
        this.coverageId = coverageId;
        return this;
    }

    public ServiceBenefitCoverageMapping updateWithCoverageName(String coverageName){
        this.coverageName = coverageName;
        return this;
    }

    public ServiceBenefitCoverageMapping updateWithService(String service){
        this.service = service;
        return this;
    }

    public ServiceBenefitCoverageMapping updateWithStatus(Status status){
        this.status = status;
        return this;
    }
}
