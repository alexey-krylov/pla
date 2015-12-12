/*
 * Copyright (c) 3/26/15 8:13 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application.service.plan.premium;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.query.MasterFinder;
import com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor;
import com.pla.sharedkernel.domain.model.PremiumTermType;
import com.pla.sharedkernel.identifier.CoverageId;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * @author: Samir
 * @since 1.0 26/03/2015
 */
@Service
public class PremiumTemplateExcelGenerator {


    private MasterFinder masterFinder;

    @Autowired
    public PremiumTemplateExcelGenerator(MasterFinder masterFinder) {
        this.masterFinder = masterFinder;

    }

    public HSSFWorkbook generatePremiumParseErrorExcel(Map<Integer, String> errorRowMessageMap, String planName) {
        HSSFWorkbook errorExcelWorkBook = new HSSFWorkbook();
        HSSFSheet premiumErrorSheet = errorExcelWorkBook.createSheet(planName);
        createRowWithCellData(0, new String[]{"Row Number", "Error Message"}, premiumErrorSheet);
        int rowNumber = 1;
        for (Map.Entry<Integer, String> entry : errorRowMessageMap.entrySet()) {
            createRowWithCellData(rowNumber, new String[]{entry.getKey().toString(), entry.getValue()}, premiumErrorSheet);
            rowNumber = rowNumber + 1;
        }
        return errorExcelWorkBook;
    }

    public HSSFWorkbook generatePremiumTemplate(List<PremiumInfluencingFactor> premiumInfluencingFactors, Plan plan, CoverageId coverageId) throws IOException {
        HSSFWorkbook premiumTemplateWorkbook = new HSSFWorkbook();
        PremiumTermType premiumTermType = plan.getPremiumTermType();
        Set<String> sheets = premiumTermType.getSheetNamesByPremiumTermType();
        int counter = 1;
        for(String sheet : sheets){
            HSSFSheet premiumSheet = premiumTemplateWorkbook.createSheet(sheet);
            int noOfExcelRow = getTotalNoOfPremiumCombination(premiumInfluencingFactors, coverageId, plan, premiumSheet);
            HSSFRow headerRow = createRowWithCellData(0, convertToStringArray(premiumInfluencingFactors), premiumSheet);
            HSSFCell premiumCell = headerRow.createCell(premiumInfluencingFactors.size());
            premiumCell.setCellType(Cell.CELL_TYPE_NUMERIC);
            premiumCell.setCellValue(AppConstants.PREMIUM_CELL_HEADER_NAME);
            createRowWithDvConstraintCellData(noOfExcelRow, premiumInfluencingFactors, plan, coverageId, premiumTemplateWorkbook, premiumSheet, counter);
            counter++;
        }
        return premiumTemplateWorkbook;
    }

    private Set<HSSFSheet> createSheetByPremiumTermType(Plan plan, HSSFWorkbook premiumTemplateWorkbook) {
        Set<HSSFSheet> sheets = Sets.newLinkedHashSet();
        PremiumTermType premiumTermType = plan.getPremiumTermType();
        Set<String> sheetNames = premiumTermType.getSheetNamesByPremiumTermType();
        for(String sheetName : sheetNames){
            HSSFSheet premiumSheet = premiumTemplateWorkbook.createSheet(sheetName);
            sheets.add(premiumSheet);
        }
        return sheets;
    }

    private int getTotalNoOfPremiumCombination(List<PremiumInfluencingFactor> premiumInfluencingFactors, CoverageId coverageId, Plan plan, HSSFSheet hssfSheet) {
        Integer noOfRow = 1;
        for (PremiumInfluencingFactor premiumInfluencingFactor : premiumInfluencingFactors) {
            String[] data = getAllowedValues(premiumInfluencingFactor, plan, coverageId, hssfSheet.getSheetName());
            Integer lengthOfAllowedValues = data.length == 0 ? 1 : data.length;
            noOfRow = noOfRow * lengthOfAllowedValues;
        }
        return noOfRow;
    }

    private void createRowWithDvConstraintCellData(int lastRowNumber, List<PremiumInfluencingFactor> premiumInfluencingFactors, Plan plan, CoverageId coverageId, HSSFWorkbook premiumTemplateWorkbook, HSSFSheet sheets, int counter) {
        for (int cellNumber = 0; cellNumber < premiumInfluencingFactors.size(); cellNumber++) {
            String columnIndex = String.valueOf((char) (65 + cellNumber));
            PremiumInfluencingFactor premiumInfluencingFactor = premiumInfluencingFactors.get(cellNumber);
            HSSFSheet hiddenSheetForNamedCell = premiumTemplateWorkbook.createSheet(premiumInfluencingFactor.name()+counter);
            String[] planData = getAllowedValues(premiumInfluencingFactor, plan, coverageId, sheets.getSheetName());
            createNamedRowWithCell(planData, hiddenSheetForNamedCell, cellNumber);
            HSSFName namedCell = premiumTemplateWorkbook.createName();
            namedCell.setNameName(premiumInfluencingFactor.name()+counter);
            String formula = premiumInfluencingFactor.name()+counter + "!$" + columnIndex + "$1:$" + columnIndex + "$";
            namedCell.setRefersToFormula(formula + (planData.length == 0 ? 1 : planData.length));
            DVConstraint constraint = DVConstraint.createFormulaListConstraint(premiumInfluencingFactor.name()+counter);
            CellRangeAddressList addressList = new CellRangeAddressList(1, lastRowNumber, cellNumber, cellNumber);
            HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, constraint);
            dataValidation.setErrorStyle(DataValidation.ErrorStyle.INFO);
            dataValidation.createErrorBox("Error", "Provide proper " + premiumInfluencingFactor.getDescription() + " value");
            premiumTemplateWorkbook.setSheetHidden(premiumTemplateWorkbook.getSheetIndex(hiddenSheetForNamedCell), false);
            sheets.addValidationData(dataValidation);
        }
    }

    private String[] getAllowedValues(PremiumInfluencingFactor premiumInfluencingFactor, Plan plan, CoverageId coverageId, String sheetName) {
        if (PremiumInfluencingFactor.OCCUPATION_CLASS.equals(premiumInfluencingFactor)) {
            List<Map<String, Object>> occupationCategories = masterFinder.getAllOccupationClass();
            occupationCategories = isNotEmpty(occupationCategories) ? occupationCategories : Lists.newArrayList();
            String[] categories = new String[occupationCategories.size()];
            for (int count = 0; count < occupationCategories.size(); count++) {
                Map<String, Object> occupationCategoryMap = occupationCategories.get(count);
                categories[count] = (String) occupationCategoryMap.get("code");
            }
            return categories;
        }
        if(premiumInfluencingFactor.equals(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM) &&  sheetName.equalsIgnoreCase(PremiumTermType.SINGLE.toString()))
            return new String[]{"1"};
        return premiumInfluencingFactor.getAllowedValues(plan, coverageId);
    }

    private void createNamedRowWithCell(String[] planData, HSSFSheet hiddenSheet, int cellNumber) {
        for (int count = 0; count < planData.length; count++) {
            String name = planData[count];
            HSSFRow row = hiddenSheet.createRow(count);
            HSSFCell cell = row.createCell(cellNumber);
            cell.setCellValue(name);
        }
    }

    private String[] convertToStringArray(List<PremiumInfluencingFactor> premiumInfluencingFactors) {
        String[] data = new String[premiumInfluencingFactors.size()];
        for (int count = 0; count < premiumInfluencingFactors.size(); count++) {
            data[count] = premiumInfluencingFactors.get(count).getDescription();
        }
        return data;
    }

    private HSSFRow createRowWithCellData(int rowNumber, String[] data, HSSFSheet hssfSheet) {
        HSSFRow row = hssfSheet.createRow(rowNumber);
        for (int cellNumber = 0; cellNumber < data.length; cellNumber++) {
            HSSFCell cell = row.createCell(cellNumber);
            cell.setCellValue(data[cellNumber]);
        }
        return row;
    }
}
