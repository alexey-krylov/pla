package com.pla.grouplife.proposal.presentation.dto;

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

        public CoverageDetailDto(String coverageName, String sumAssured) {
            this.coverageName = coverageName;
            this.sumAssured = sumAssured;
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
                    String coverageName = planCoverageDto.getCoverageName() + " (" + planCoverageDto.getCoverageCode() + ")";
                    CoverageDetailDto coverageDetailDto = new CoverageDetailDto(coverageName, planCoverageDto.getSumAssuredDto().getSumAssuredInString());
                    planDetailDto.addCoverageDetail(coverageDetailDto);
                });
                return planDetailDto;
            }
        }).collect(Collectors.toList());
        return planDetailDtoList;
    }
}
