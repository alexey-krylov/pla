package com.pla.grouphealth.quotation.presentation.dto;

import com.google.common.collect.Lists;
import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by Samir on 5/7/2015.
 */
@Getter
public class PlanDetailDto {

    private String product;

    private String sumAssured;

    private String coverages;

    private String relationships;

    private List<CoverageDetailDto> coverageDetails;

    public PlanDetailDto(String product, String sumAssured, String coverages, String relationships) {
        this.product = product;
        this.sumAssured = sumAssured;
        this.coverages = coverages;
        this.relationships = relationships;
    }

    public void addCoverageDetail(CoverageDetailDto coverageDetailDto) {
        if (isEmpty(coverageDetails)) {
            coverageDetails = Lists.newArrayList();
        }
        this.coverageDetails.add(coverageDetailDto);
    }

    @Getter
    public static class CoverageDetailDto {

        private String coverageName;
        private String sumAssured;
        private List<BenefitDetailDto> benefits;

        public CoverageDetailDto(String coverageName, String sumAssured) {
            this.coverageName = coverageName;
            this.sumAssured = sumAssured;
        }

        public CoverageDetailDto addBenefit(BenefitDetailDto benefitDetailDto) {
            if (isEmpty(this.benefits)) {
                this.benefits = Lists.newArrayList();
            }
            this.benefits.add(benefitDetailDto);
            return this;
        }

    }


    @Getter
    public static class BenefitDetailDto {

        private String benefitName;

        private String benefitLimit;

        public BenefitDetailDto(String benefitName, String benefitLimit) {
            this.benefitName = benefitName;
            this.benefitLimit = benefitLimit;
        }
    }

    public static List<PlanDetailDto> transformToPlanDetail(List<PlanCoverageDetailDto> planCoverageDetailDtoList) {
        List<PlanDetailDto> planDetailDtoList = planCoverageDetailDtoList.stream().map(new Function<PlanCoverageDetailDto, PlanDetailDto>() {
            @Override
            public PlanDetailDto apply(PlanCoverageDetailDto planCoverageDetailDto) {
                String product = planCoverageDetailDto.getPlanName() + " (" + planCoverageDetailDto.getPlanCode() + ")";
                String sumAssured = planCoverageDetailDto.getSumAssuredDto().getSumAssuredInString();
                String relationship = planCoverageDetailDto.getRelations();
                String coverages = planCoverageDetailDto.coveragesInString();
                PlanDetailDto planDetailDto = new PlanDetailDto(product, sumAssured, coverages, relationship);
                planCoverageDetailDto.getCoverageDtoList().forEach(planCoverageDto -> {
                    String coverageName = planCoverageDto.getCoverageName() + "(" + planCoverageDto.getCoverageCode() + ")";
                    final CoverageDetailDto[] coverageDetailDto = {new CoverageDetailDto(coverageName, planCoverageDto.getSumAssuredDto().getSumAssuredInString())};
                    planCoverageDto.getBenefits().forEach(coverageBenefit -> {
                        String benefitName = coverageBenefit.getBenefitName() + "(" + coverageBenefit.getBenefitCode() + ")";
                        BenefitDetailDto benefitDetailDto = new BenefitDetailDto(benefitName, coverageBenefit.getBenefitLimit() != null ? coverageBenefit.getBenefitLimit().toPlainString() : "");
                        coverageDetailDto[0] = coverageDetailDto[0].addBenefit(benefitDetailDto);
                    });
                    planDetailDto.addCoverageDetail(coverageDetailDto[0]);
                });
                return planDetailDto;
            }
        }).collect(Collectors.toList());
        return planDetailDtoList;
    }
}