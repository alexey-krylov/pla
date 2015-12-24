package com.pla.core.SBCM.domain.model;

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
}
