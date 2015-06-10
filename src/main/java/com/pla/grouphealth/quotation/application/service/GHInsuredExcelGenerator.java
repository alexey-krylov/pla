package com.pla.grouphealth.quotation.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.query.MasterFinder;
import com.pla.grouphealth.quotation.query.GHInsuredDto;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.OccupationCategory;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.util.ExcelGeneratorUtil;
import lombok.NoArgsConstructor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 5/4/2015.
 */
@Component(value = "ghInsuredExcelGenerator")
@NoArgsConstructor
public class GHInsuredExcelGenerator {

    private MasterFinder masterFinder;

    private IPlanAdapter planAdapter;

    @Autowired
    public GHInsuredExcelGenerator(IPlanAdapter planAdapter, MasterFinder masterFinder) {
        this.planAdapter = planAdapter;
        this.masterFinder = masterFinder;
    }

    public HSSFWorkbook generateInsuredExcel(List<GHInsuredDto> insureds, List<PlanId> planIds) throws IOException {
        final List<String> headers = GHInsuredExcelHeader.getAllowedHeaders(planAdapter, planIds);
        List<Map<Integer, String>> excelData = Lists.newArrayList();
        for (GHInsuredDto insuredDto : insureds) {
            List<Map<Integer, String>> tranformedList = transformInsuredDtoToExcelData(insuredDto, GHInsuredExcelHeader.getAllowedHeaderForParser(planAdapter, planIds));
            for (Map<Integer, String> insuredMap : tranformedList) {
                excelData.add(insuredMap);
            }
        }
        Map<Integer, List<String>> constraintCellDataMap = Maps.newHashMap();
        constraintCellDataMap.put(headers.indexOf("Gender"), Gender.getAllGender());
        constraintCellDataMap.put(headers.indexOf("Relationship"), Relationship.getAllRelation());
        constraintCellDataMap.put(headers.indexOf("Occupation"), getAllOccupationClassification());
        constraintCellDataMap.put(headers.indexOf("Category"), OccupationCategory.getAllCategory());
        HSSFWorkbook workbook = ExcelGeneratorUtil.generateExcelWithDvConstraintCell(headers, excelData, constraintCellDataMap);
        return workbook;
    }

    private List<Map<Integer, String>> transformInsuredDtoToExcelData(GHInsuredDto insuredDto, List<String> headers) {
        List<Map<Integer, String>> excelRowData = Lists.newArrayList();
        Map<Integer, String> excelDataMap = Maps.newHashMap();
        headers.forEach(header -> {
            if (!header.contains(AppConstants.OPTIONAL_COVERAGE_HEADER)) {
                excelDataMap.put(headers.indexOf(header), GHInsuredExcelHeader.valueOf(header).getAllowedValue(insuredDto));
            }
        });
        List<GHInsuredDto.GHCoveragePremiumDetailDto> coveragePremiumDetailDtoList = insuredDto.getCoveragePremiumDetails();
        coveragePremiumDetailDtoList.forEach(coveragePremiumDetail -> {
            int indexOfCoveragePremiumDetail = coveragePremiumDetailDtoList.indexOf(coveragePremiumDetail) + 1;
            int indexOfOptionalCoverage = headers.indexOf(AppConstants.OPTIONAL_COVERAGE_HEADER + indexOfCoveragePremiumDetail);
            int indexOfOptionalCoverageSA = headers.indexOf((AppConstants.OPTIONAL_COVERAGE_HEADER + indexOfCoveragePremiumDetail) + " " + AppConstants.OPTIONAL_COVERAGE_SA_HEADER);
            int indexOfOptionalCoveragePremium = headers.indexOf((AppConstants.OPTIONAL_COVERAGE_HEADER + indexOfCoveragePremiumDetail) + " " + AppConstants.PREMIUM_CELL_HEADER_NAME);
            int indexOfOptionalCoverageVisibility = headers.indexOf((AppConstants.OPTIONAL_COVERAGE_HEADER + indexOfCoveragePremiumDetail) + " " + AppConstants.OPTIONAL_COVERAGE_PREMIUM_VISIBILITY_HEADER);
            excelDataMap.put(indexOfOptionalCoverage, coveragePremiumDetail.getCoverageCode());
            excelDataMap.put(indexOfOptionalCoverageSA, coveragePremiumDetail.getSumAssured() != null ? coveragePremiumDetail.getSumAssured().toString() : "");
            excelDataMap.put(indexOfOptionalCoveragePremium, coveragePremiumDetail.getPremium() != null ? coveragePremiumDetail.getPremium().toString() : "");
            excelDataMap.put(indexOfOptionalCoverageVisibility, coveragePremiumDetail.getPremiumVisibility() != null ? coveragePremiumDetail.getPremiumVisibility() : "");
            List<GHInsuredDto.GHCoveragePremiumDetailDto.GHCoverageBenefitDetailDto> benefitDetailList = coveragePremiumDetail.getBenefitDetails();
            if (isNotEmpty(benefitDetailList)) {
                benefitDetailList.forEach(benefitDetail -> {
                    int indexOfBenefitDetail = benefitDetailList.indexOf(benefitDetail) + 1;
                    int indexOfBenefitCode = headers.indexOf((AppConstants.OPTIONAL_COVERAGE_HEADER + indexOfCoveragePremiumDetail) + " " + (AppConstants.OPTIONAL_COVERAGE_BENEFIT_HEADER + indexOfBenefitDetail));
                    int indexOfBenefitLimit = headers.indexOf((AppConstants.OPTIONAL_COVERAGE_HEADER + indexOfCoveragePremiumDetail) + " " + (AppConstants.OPTIONAL_COVERAGE_BENEFIT_HEADER + indexOfBenefitDetail + " Limit"));
                    excelDataMap.put(indexOfBenefitCode, benefitDetail.getBenefitCode());
                    excelDataMap.put(indexOfBenefitLimit, benefitDetail.getBenefitLimit() != null ? benefitDetail.getBenefitLimit().toPlainString() : "");
                });
            }
        });
        List<Map<Integer, String>> dependentDetailExcelRowData = insuredDto.getInsuredDependents().stream().map(new Function<GHInsuredDto.GHInsuredDependentDto, Map<Integer, String>>() {
            @Override
            public Map<Integer, String> apply(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
                return transformInsuredDependentDtoToExcelData(insuredDependentDto, headers);
            }
        }).collect(Collectors.toList());
        excelRowData.add(excelDataMap);
        excelRowData.addAll(dependentDetailExcelRowData);
        return excelRowData;
    }

    private Map<Integer, String> transformInsuredDependentDtoToExcelData(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, List<String> headers) {
        Map<Integer, String> excelDataMap = Maps.newHashMap();
        headers.forEach(header -> {
            if (!header.contains(AppConstants.OPTIONAL_COVERAGE_HEADER)) {
                excelDataMap.put(headers.indexOf(header), GHInsuredExcelHeader.valueOf(header).getAllowedValue(insuredDependentDto));
            }
        });
        List<GHInsuredDto.GHCoveragePremiumDetailDto> coveragePremiumDetailDtoList = insuredDependentDto.getCoveragePremiumDetails();
        coveragePremiumDetailDtoList.forEach(coveragePremiumDetail -> {
            int indexOfCoveragePremiumDetail = coveragePremiumDetailDtoList.indexOf(coveragePremiumDetail) + 1;
            int indexOfOptionalCoverage = headers.indexOf(AppConstants.OPTIONAL_COVERAGE_HEADER + indexOfCoveragePremiumDetail);
            int indexOfOptionalCoverageSA = headers.indexOf((AppConstants.OPTIONAL_COVERAGE_HEADER + indexOfCoveragePremiumDetail) + " " + AppConstants.OPTIONAL_COVERAGE_SA_HEADER);
            int indexOfOptionalCoveragePremium = headers.indexOf((AppConstants.OPTIONAL_COVERAGE_HEADER + indexOfCoveragePremiumDetail) + " " + AppConstants.PREMIUM_CELL_HEADER_NAME);
            int indexOfOptionalCoverageVisibility = headers.indexOf((AppConstants.OPTIONAL_COVERAGE_HEADER + indexOfCoveragePremiumDetail) + " " + AppConstants.OPTIONAL_COVERAGE_PREMIUM_VISIBILITY_HEADER);
            excelDataMap.put(indexOfOptionalCoverage, coveragePremiumDetail.getCoverageCode());
            excelDataMap.put(indexOfOptionalCoverageSA, coveragePremiumDetail.getSumAssured() != null ? coveragePremiumDetail.getSumAssured().toString() : "");
            excelDataMap.put(indexOfOptionalCoveragePremium, coveragePremiumDetail.getPremium() != null ? coveragePremiumDetail.getPremium().toString() : "");
            excelDataMap.put(indexOfOptionalCoverageVisibility, coveragePremiumDetail.getPremiumVisibility() != null ? coveragePremiumDetail.getPremiumVisibility() : "");
            List<GHInsuredDto.GHCoveragePremiumDetailDto.GHCoverageBenefitDetailDto> benefitDetailList = coveragePremiumDetail.getBenefitDetails();
            if (isNotEmpty(benefitDetailList)) {
                benefitDetailList.forEach(benefitDetail -> {
                    int indexOfBenefitDetail = benefitDetailList.indexOf(benefitDetail) + 1;
                    int indexOfBenefitCode = headers.indexOf((AppConstants.OPTIONAL_COVERAGE_HEADER + indexOfCoveragePremiumDetail) + " " + (AppConstants.OPTIONAL_COVERAGE_BENEFIT_HEADER + indexOfBenefitDetail));
                    int indexOfBenefitLimit = headers.indexOf((AppConstants.OPTIONAL_COVERAGE_HEADER + indexOfCoveragePremiumDetail) + " " + (AppConstants.OPTIONAL_COVERAGE_BENEFIT_HEADER + indexOfBenefitDetail + " Limit"));
                    excelDataMap.put(indexOfBenefitCode, benefitDetail.getBenefitCode());
                    excelDataMap.put(indexOfBenefitLimit, benefitDetail.getBenefitLimit() != null ? benefitDetail.getBenefitLimit().toPlainString() : "");
                });
            }
        });
        return excelDataMap;
    }

    private List<String> getAllOccupationClassification() {
        List<Map<String, Object>> occupationClassList = masterFinder.getAllOccupationClassification();
        List<String> occupationClasses = occupationClassList.stream().map(new Function<Map<String, Object>, String>() {
            @Override
            public String apply(Map<String, Object> stringObjectMap) {
                return (String) stringObjectMap.get("description");
            }
        }).collect(Collectors.toList());
        return occupationClasses;
    }
}
