/*
 * Copyright (c) 3/26/15 8:13 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.application.service.plan.premium;

import com.pla.core.domain.model.plan.Plan;
import com.pla.core.query.MasterFinder;
import com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor;
import com.pla.sharedkernel.identifier.CoverageId;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author: Samir
 * @since 1.0 26/03/2015
 */
@Service
public class PremiumTemplateExcelGenerator {

    private HSSFWorkbook premiumTemplateWorkbook;

    private HSSFSheet premiumSheet;

    private MasterFinder masterFinder;

    @Autowired
    public PremiumTemplateExcelGenerator(MasterFinder masterFinder) {
        premiumTemplateWorkbook = new HSSFWorkbook();
        this.masterFinder = masterFinder;

    }

    public HSSFWorkbook generatePremiumTemplate(List<PremiumInfluencingFactor> premiumInfluencingFactors, Plan plan, CoverageId coverageId) throws IOException {
        int noOfExcelRow = getTotalNoOfPremiumCombination(premiumInfluencingFactors, coverageId, plan);
        premiumSheet = premiumTemplateWorkbook.createSheet(plan.getPlanDetail().getPlanName());
        HSSFRow headerRow = createHeaderRowWithCellData(0, convertToStringArray(premiumInfluencingFactors));
        HSSFCell premiumCell = headerRow.createCell(premiumInfluencingFactors.size());
        premiumCell.setCellType(Cell.CELL_TYPE_NUMERIC);
        premiumCell.setCellValue(AppConstants.PREMIUM_CELL_HEADER_NAME);
        createRowWithDvConstraintCellData(noOfExcelRow, premiumInfluencingFactors, plan, coverageId);
        return premiumTemplateWorkbook;
    }

    private int getTotalNoOfPremiumCombination(List<PremiumInfluencingFactor> premiumInfluencingFactors, CoverageId coverageId, Plan plan) {
        Integer noOfRow = 1;
        for (PremiumInfluencingFactor premiumInfluencingFactor : premiumInfluencingFactors) {
            Integer lengthOfAllowedValues = premiumInfluencingFactor.getAllowedValues(plan, coverageId, masterFinder).length == 0 ? 1 : premiumInfluencingFactor.getAllowedValues(plan, coverageId, masterFinder).length;
            noOfRow = noOfRow * lengthOfAllowedValues;
        }
        return noOfRow;
    }

    private void createRowWithDvConstraintCellData(int lastRowNumber, List<PremiumInfluencingFactor> premiumInfluencingFactors, Plan plan, CoverageId coverageId) {
        for (int cellNumber = 0; cellNumber < premiumInfluencingFactors.size(); cellNumber++) {
            if (!PremiumInfluencingFactor.BMI.equals(premiumInfluencingFactors.get(cellNumber))) {
                String columnIndex = String.valueOf((char) (65 + cellNumber));
                PremiumInfluencingFactor premiumInfluencingFactor = premiumInfluencingFactors.get(cellNumber);
                HSSFSheet hiddenSheetForNamedCell = premiumTemplateWorkbook.createSheet(premiumInfluencingFactor.name());
                String[] planData = premiumInfluencingFactor.getAllowedValues(plan, coverageId, masterFinder);
                createNamedRowWithCell(planData, hiddenSheetForNamedCell, cellNumber);
                HSSFName namedCell = premiumTemplateWorkbook.createName();
                namedCell.setNameName(premiumInfluencingFactor.name());
                String formula = premiumInfluencingFactor.name() + "!$" + columnIndex + "$1:$" + columnIndex + "$";
                namedCell.setRefersToFormula(formula + (planData.length == 0 ? 1 : planData.length));
                DVConstraint constraint = DVConstraint.createFormulaListConstraint(premiumInfluencingFactor.name());
                CellRangeAddressList addressList = new CellRangeAddressList(1, lastRowNumber, cellNumber, cellNumber);
                HSSFDataValidation dataValidation = new HSSFDataValidation(addressList, constraint);
                dataValidation.setErrorStyle(DataValidation.ErrorStyle.INFO);
                dataValidation.createErrorBox("Error", "Provide proper " + premiumInfluencingFactor.getDescription() + " value");
                premiumTemplateWorkbook.setSheetHidden(premiumTemplateWorkbook.getSheetIndex(hiddenSheetForNamedCell), true);
                premiumSheet.addValidationData(dataValidation);
            }
        }
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

    private HSSFRow createHeaderRowWithCellData(int rowNumber, String[] data) {
        HSSFRow row = premiumSheet.createRow(rowNumber);
        for (int cellNumber = 0; cellNumber < data.length; cellNumber++) {
            HSSFCell cell = row.createCell(cellNumber);
            cell.setCellValue(data[cellNumber]);
        }
        return row;
    }
}
