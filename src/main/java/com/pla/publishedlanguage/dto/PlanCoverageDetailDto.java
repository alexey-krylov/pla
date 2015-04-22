package com.pla.publishedlanguage.dto;

import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Samir on 4/22/2015.
 */
@Getter
public class PlanCoverageDetailDto {

    private PlanId planId;

    private String planCode;

    private String planName;

    private SumAssuredDto sumAssuredDto;

    private List<CoverageDto> coverageDtoList;

    private List<String> relationTypes;


    public PlanCoverageDetailDto(PlanId planId, String planName, String planCode) {
        this.planId = planId;
        this.planCode = planCode;
        this.planName = planName;
    }

    public PlanCoverageDetailDto addCoverage(List<CoverageDto> coverageDtoList) {
        this.coverageDtoList = coverageDtoList;
        return this;
    }

    public PlanCoverageDetailDto addSumAssured(SumAssuredDto sumAssuredDto) {
        this.sumAssuredDto = sumAssuredDto;
        return this;
    }

    public PlanCoverageDetailDto addRelationTypes(List<String> relationTypes) {
        this.relationTypes = relationTypes;
        return this;
    }

    @Getter
    public class SumAssuredDto {

        private BigDecimal minimumSumAssured;

        private BigDecimal maximumSumAssured;

        private Integer multiplesOf;

        private List<BigDecimal> sumAssureds;

        public SumAssuredDto(BigDecimal minimumSumAssured, BigDecimal maximumSumAssured, Integer multiplesOf) {
            this.minimumSumAssured = minimumSumAssured;
            this.maximumSumAssured = maximumSumAssured;
            this.multiplesOf = multiplesOf;
        }

        public SumAssuredDto(List<BigDecimal> sumAssureds) {
            this.sumAssureds = sumAssureds;
        }
    }

    @Getter
    public class CoverageDto {

        private SumAssuredDto sumAssuredDto;

        private String coverageCode;

        private CoverageId coverageId;

        private String coverageName;

        public CoverageDto(String coverageCode, String coverageName, CoverageId coverageId) {
            this.coverageCode = coverageCode;
            this.coverageName = coverageName;
            this.coverageId = coverageId;
        }

        public CoverageDto addSumAssured(SumAssuredDto sumAssuredDto) {
            this.sumAssuredDto = sumAssuredDto;
            return this;
        }

    }
}
