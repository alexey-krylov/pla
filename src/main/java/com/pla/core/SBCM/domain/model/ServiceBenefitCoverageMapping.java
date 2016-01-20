package com.pla.core.SBCM.domain.model;

import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.BenefitId;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

import static org.nthdimenzion.utils.UtilValidator.*;

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
    private String coverageCode;
    private String service;
    private Status status;
    private String benefitCode;

    public ServiceBenefitCoverageMapping updateWithId(ServiceBenefitCoverageMappingId serviceBenefitCoverageMappingId) {
        this.serviceBenefitCoverageMappingId = serviceBenefitCoverageMappingId;
        return this;
    }

    public ServiceBenefitCoverageMapping updateWithBenefitCode(String benefitCode) {
        this.benefitCode = benefitCode;
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

    public ServiceBenefitCoverageMapping updateWithCoverageName(Map<String, Object> coverage){
        if(isNotEmpty(coverage)) {
            this.coverageCode = isNotEmpty(coverage.get("coverageCode")) ? coverage.get("coverageCode").toString() : StringUtils.EMPTY;
            this.coverageName = isNotEmpty(coverage.get("coverageName")) ? coverage.get("coverageName").toString() : StringUtils.EMPTY;
        }
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

    public static class CoverageBenefit{
        CoverageId coverageId;
        BenefitId benefitId;
        public CoverageBenefit(CoverageId coverageId, BenefitId benefitId){
            this.coverageId = coverageId;
            this.benefitId = benefitId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CoverageBenefit that = (CoverageBenefit) o;

            if (!benefitId.equals(that.benefitId)) return false;
            if (!coverageId.equals(that.coverageId)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = coverageId.hashCode();
            result = 31 * result + benefitId.hashCode();
            return result;
        }
    }
    public CoverageBenefit getCoverageBenefit(){
        return new CoverageBenefit(coverageId, benefitId);
    }
}
