package com.pla.quotation.query;

import com.pla.quotation.application.service.GLInsuredExcelHeader;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.util.ExcelGeneratorUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.AppConstants;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Samir on 4/29/2015.
 */
@Getter
@Setter
public class InsuredDto {

    private String companyName;

    private String manNumber;

    private String nrcNumber;

    private String salutation;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;

    private Gender gender;

    private String category;

    private BigDecimal annualIncome;

    private String occupationClass;

    private String occupationCategory;

    private Integer noOfAssured;

    private Set<InsuredDependentDto> insuredDependents;

    private PlanPremiumDetailDto planPremiumDetail;

    private List<CoveragePremiumDetailDto> coveragePremiumDetails;


    public InsuredDto addInsuredDependent(Set<InsuredDependentDto> insuredDependentDtos){
        this.insuredDependents=insuredDependentDtos;
        return this;
    }


    public InsuredDto addPlanPremiumDetail(PlanPremiumDetailDto planPremiumDetailDto) {
        this.planPremiumDetail = planPremiumDetailDto;
        return this;
    }

    public InsuredDto addCoveragePremiumDetails(List<CoveragePremiumDetailDto> coveragePremiumDetailDtos) {
        this.coveragePremiumDetails = coveragePremiumDetailDtos;
        return this;
    }

    public static InsuredDto createInsuredDto(Row row, List<String> excelHeaders, List<String> headers, List<Cell> optionalCoverageCell) {
        InsuredDto insuredDto = new InsuredDto();
        for (String header : headers) {
            if (!header.contains(AppConstants.OPTIONAL_COVERAGE_HEADER)) {
                insuredDto = GLInsuredExcelHeader.valueOf(header).populateInsuredDetail(insuredDto, row, excelHeaders);
            }
        }
        List<CoveragePremiumDetailDto> coveragePremiumDetails = optionalCoverageCell.stream().map(new Function<Cell, CoveragePremiumDetailDto>() {
            @Override
            public CoveragePremiumDetailDto apply(Cell cell) {
                CoveragePremiumDetailDto coveragePremiumDetailDto = new CoveragePremiumDetailDto();
                coveragePremiumDetailDto.setCoverageCode(ExcelGeneratorUtil.getCellValue(cell));
                return coveragePremiumDetailDto;
            }
        }).collect(Collectors.toList());
        insuredDto = insuredDto.addCoveragePremiumDetails(coveragePremiumDetails);
        return insuredDto;
    }

    @Getter
    @Setter
    public static class InsuredDependentDto {

        private PlanId insuredDependentPlan;

        private Set<CoverageId> insuredDependentCoverages;

        private String companyName;

        private String manNumber;

        private String nrcNumber;

        private String salutation;

        private String firstName;

        private String lastName;

        private LocalDate dateOfBirth;

        private Gender gender;

        private String category;

        private Relationship relationship;

        private String occupationClass;

        private String occupationCategory;

        private PlanPremiumDetailDto planPremiumDetail;

        private List<CoveragePremiumDetailDto> coveragePremiumDetails;

        public InsuredDependentDto addPlanPremiumDetail(PlanPremiumDetailDto planPremiumDetailDto) {
            this.planPremiumDetail = planPremiumDetailDto;
            return this;
        }

        public InsuredDependentDto addCoveragePremiumDetails(List<CoveragePremiumDetailDto> coveragePremiumDetailDtos) {
            this.coveragePremiumDetails = coveragePremiumDetailDtos;
            return this;
        }

        public static InsuredDependentDto createInsuredDependentDto(Row dependentRow, List<String> excelHeaders, List<String> headers, List<Cell> optionalCoverageCells) {
            InsuredDependentDto insuredDependentDto = new InsuredDependentDto();
            for (String header : headers) {
                if (!header.contains(AppConstants.OPTIONAL_COVERAGE_HEADER)) {
                    insuredDependentDto = GLInsuredExcelHeader.valueOf(header).populateInsuredDependentDetail(insuredDependentDto, dependentRow, excelHeaders);
                }
            }
            List<CoveragePremiumDetailDto> coveragePremiumDetails = optionalCoverageCells.stream().map(new Function<Cell, CoveragePremiumDetailDto>() {
                @Override
                public CoveragePremiumDetailDto apply(Cell cell) {
                    CoveragePremiumDetailDto coveragePremiumDetailDto = new CoveragePremiumDetailDto();
                    coveragePremiumDetailDto.setCoverageCode(ExcelGeneratorUtil.getCellValue(cell));
                    return coveragePremiumDetailDto;
                }
            }).collect(Collectors.toList());
            insuredDependentDto = insuredDependentDto.addCoveragePremiumDetails(coveragePremiumDetails);
            return insuredDependentDto;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PlanPremiumDetailDto {

        private String planId;

        private String planCode;

        private BigDecimal premiumAmount;

        private BigDecimal incomeMultiplier;

        private BigDecimal sumAssured;

        public PlanPremiumDetailDto(String planId, String planCode, BigDecimal premiumAmount, BigDecimal sumAssured) {
            this.planId = planId;
            this.planCode = planCode;
            this.premiumAmount = premiumAmount;
            this.sumAssured = sumAssured;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class CoveragePremiumDetailDto {

        private String coverageCode;

        private String coverageId;

        private BigDecimal premium;

        private String coverageName;

        private BigDecimal sumAssured;


        public CoveragePremiumDetailDto(String coverageCode, String coverageId, BigDecimal premium, BigDecimal sumAssured) {
            this.coverageCode = coverageCode;
            this.coverageId = coverageId;
            this.premium = premium;
            this.sumAssured = sumAssured;
        }



    }
}
