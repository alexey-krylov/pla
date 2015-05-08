package com.pla.publishedlanguage.dto;

import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

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


        public String getSumAssuredInString() {
            String sumAssuredInString = "";
            if (isNotEmpty(sumAssureds)) {
                for (BigDecimal sumAssured : sumAssureds) {
                    sumAssuredInString = sumAssuredInString + sumAssured.toString() + ",";
                }
            } else if (minimumSumAssured != null && maximumSumAssured != null) {
                sumAssuredInString = minimumSumAssured.toString() + " to " + maximumSumAssured.toString() + " multiples of" + multiplesOf.toString();
            }
            return sumAssuredInString;
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

        public List<String> getCoverageName(List<CoverageDto> coverageDtoList) {
            List<String> coverageNames = coverageDtoList.stream().map(new Function<CoverageDto, String>() {
                @Override
                public String apply(CoverageDto coverageDto) {
                    return coverageDto.coverageName;
                }
            }).collect(Collectors.toList());
            return coverageNames;

        }
    }

    public static int getNoOfOptionalCoverage(List<PlanCoverageDetailDto> planCoverageDetailDtoList) {
        int noOfCoverage = 0;
        for (PlanCoverageDetailDto planCoverageDetailDto : planCoverageDetailDtoList) {
            noOfCoverage = noOfCoverage + planCoverageDetailDto.getCoverageDtoList().size();
        }
        return noOfCoverage;
    }

    public String getRelations() {
        String relations = "";
        for (String relationship : relationTypes) {
            relations = relations + relationship + ",";
        }
        return relations;
    }

    public String coveragesInString() {
        String coveragesInString = "";
        for (CoverageDto coverageDto : coverageDtoList) {
            coveragesInString = coveragesInString + coverageDto.getCoverageName() + " (" + coverageDto.getCoverageCode() + " )" + ",";
        }
        return coveragesInString;
    }
}
