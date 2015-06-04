package com.pla.grouphealth.quotation.application.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.pla.grouphealth.quotation.query.InsuredDto;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.PlanId;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.nthdimenzion.common.AppConstants;
import org.nthdimenzion.presentation.AppUtils;

import java.math.BigDecimal;
import java.util.List;

import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Created by Samir on 5/4/2015.
 */
public enum GLInsuredExcelHeader {

    PROPOSER_NAME("Proposer Name") {
        @Override
        public String getAllowedValue(com.pla.grouphealth.quotation.query.InsuredDto insuredDto) {
            return insuredDto.getCompanyName();
        }

        @Override
        public com.pla.grouphealth.quotation.query.InsuredDto populateInsuredDetail(com.pla.grouphealth.quotation.query.InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setCompanyName(cellValue);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setCompanyName(cellValue);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getCompanyName();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            return "";
        }
    }, MAN_NUMBER("MAN Number") {
        @Override
        public String getAllowedValue(com.pla.grouphealth.quotation.query.InsuredDto insuredDto) {
            return insuredDto.getManNumber();
        }

        @Override
        public com.pla.grouphealth.quotation.query.InsuredDto populateInsuredDetail(com.pla.grouphealth.quotation.query.InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setManNumber(cellValue);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setManNumber(cellValue);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getManNumber();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            return "";
        }
    }, NRC_NUMBER("NRC Number") {
        @Override
        public String getAllowedValue(com.pla.grouphealth.quotation.query.InsuredDto insuredDto) {
            return insuredDto.getNrcNumber();
        }

        @Override
        public com.pla.grouphealth.quotation.query.InsuredDto populateInsuredDetail(com.pla.grouphealth.quotation.query.InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setNrcNumber(cellValue);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setNrcNumber(cellValue);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getNrcNumber();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            if (!isValidNrcNumber(value) && isNotEmpty(value)) {
                errorMessage = errorMessage + "NRC Number is not in valid format [0-9]{6}/[0-9]{2}/[0-9].";
            }
            return errorMessage;
        }
    }, ANNUAL_INCOME("Annual Income") {
        @Override
        public String getAllowedValue(com.pla.grouphealth.quotation.query.InsuredDto insuredDto) {
            return insuredDto.getAnnualIncome() != null ? insuredDto.getAnnualIncome().toString() : "";
        }

        @Override
        public com.pla.grouphealth.quotation.query.InsuredDto populateInsuredDetail(com.pla.grouphealth.quotation.query.InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setAnnualIncome(isNotEmpty(cellValue) ? BigDecimal.valueOf(Double.valueOf(cellValue)) : null);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return "";
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            Cell incomeMultiplierCell = row.getCell(excelHeaders.indexOf(INCOME_MULTIPLIER.name()));
            Cell annualIncomeCell = row.getCell(excelHeaders.indexOf(ANNUAL_INCOME.name()));
            String incomeMultiplier = getCellValue(incomeMultiplierCell);
            String annualIncome = getCellValue(annualIncomeCell);
            if (isEmpty(annualIncome) && isNotEmpty(incomeMultiplier)) {
                errorMessage = errorMessage + "Annual income cannot be blank.";
                return errorMessage;
            }
            if (isEmpty(value)) {
                return errorMessage;
            }
            try {
                Double.parseDouble(annualIncome);
            } catch (Exception e) {
                errorMessage = errorMessage + "Annual income should be numeric.";
            }
            return errorMessage;
        }

    },
    SALUTATION("Salutation") {
        @Override
        public String getAllowedValue(com.pla.grouphealth.quotation.query.InsuredDto insuredDto) {
            return insuredDto.getSalutation();
        }

        @Override
        public com.pla.grouphealth.quotation.query.InsuredDto populateInsuredDetail(com.pla.grouphealth.quotation.query.InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setSalutation(cellValue);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setSalutation(cellValue);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getSalutation();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            return "";
        }
    }, FIRST_NAME("First Name") {
        @Override
        public String getAllowedValue(com.pla.grouphealth.quotation.query.InsuredDto insuredDto) {
            return insuredDto.getFirstName();
        }

        @Override
        public com.pla.grouphealth.quotation.query.InsuredDto populateInsuredDetail(com.pla.grouphealth.quotation.query.InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setFirstName(cellValue);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setFirstName(cellValue);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getFirstName();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            return "";
        }
    }, LAST_NAME("Last Name") {
        @Override
        public String getAllowedValue(com.pla.grouphealth.quotation.query.InsuredDto insuredDto) {
            return insuredDto.getLastName();
        }

        @Override
        public com.pla.grouphealth.quotation.query.InsuredDto populateInsuredDetail(com.pla.grouphealth.quotation.query.InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setLastName(cellValue);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setLastName(cellValue);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getLastName();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            return "";
        }
    }, DATE_OF_BIRTH("Date of Birth") {
        @Override
        public String getAllowedValue(com.pla.grouphealth.quotation.query.InsuredDto insuredDto) {
            return AppUtils.toString(insuredDto.getDateOfBirth());
        }

        @Override
        public com.pla.grouphealth.quotation.query.InsuredDto populateInsuredDetail(com.pla.grouphealth.quotation.query.InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setDateOfBirth(isNotEmpty(cellValue) ? LocalDate.parse(cellValue, DateTimeFormat.forPattern(AppConstants.DD_MM_YYY_FORMAT)) : null);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setDateOfBirth(isNotEmpty(cellValue) ? LocalDate.parse(cellValue, DateTimeFormat.forPattern(AppConstants.DD_MM_YYY_FORMAT)) : null);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return AppUtils.toString(insuredDependentDto.getDateOfBirth());
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            if (isEmpty(value)) {
                return "";
            }
            String errorMessage = "";
            if (!isValidDate(value)) {
                errorMessage = errorMessage + "Date of birth should be in format(dd/MM/yyyy).";
            }
            return errorMessage;
        }
    },
    GENDER("Gender") {
        @Override
        public String getAllowedValue(com.pla.grouphealth.quotation.query.InsuredDto insuredDto) {
            return insuredDto.getGender().name();
        }

        @Override
        public com.pla.grouphealth.quotation.query.InsuredDto populateInsuredDetail(com.pla.grouphealth.quotation.query.InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setGender(Gender.valueOf(cellValue));
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setGender(Gender.valueOf(cellValue));
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getGender().name();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                Gender.valueOf(value);
            } catch (Exception e) {
                errorMessage = errorMessage + "Gender is not valid.";
                return errorMessage;
            }
            return errorMessage;
        }
    }, OCCUPATION("Occupation") {
        @Override
        public String getAllowedValue(com.pla.grouphealth.quotation.query.InsuredDto insuredDto) {
            return insuredDto.getOccupationClass();
        }

        @Override
        public com.pla.grouphealth.quotation.query.InsuredDto populateInsuredDetail(com.pla.grouphealth.quotation.query.InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setOccupationClass(cellValue);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setOccupationClass(cellValue);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getOccupationClass();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            Cell relationshipCell = row.getCell(excelHeaders.indexOf(RELATIONSHIP.name()));
            String relationship = getCellValue(relationshipCell);
            if (isEmpty(relationship)) {
                errorMessage = errorMessage + "Relationship cannot be empty.";
            }
            if (Relationship.SELF.description.equals(relationship) && isEmpty(value)) {
                errorMessage = errorMessage + "Occupation cannot be empty.";
            }
            return errorMessage;
        }
    }, CATEGORY("Category") {
        @Override
        public String getAllowedValue(com.pla.grouphealth.quotation.query.InsuredDto insuredDto) {
            return insuredDto.getOccupationCategory();
        }

        @Override
        public com.pla.grouphealth.quotation.query.InsuredDto populateInsuredDetail(com.pla.grouphealth.quotation.query.InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setOccupationCategory(cellValue);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setOccupationCategory(cellValue);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getOccupationCategory();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            if (isEmpty(value)) {
                errorMessage = errorMessage + "Category cannot be empty.";
            }
            return errorMessage;
        }
    }, RELATIONSHIP("Relationship") {
        @Override
        public String getAllowedValue(com.pla.grouphealth.quotation.query.InsuredDto insuredDto) {
            return "Self";
        }

        @Override
        public com.pla.grouphealth.quotation.query.InsuredDto populateInsuredDetail(com.pla.grouphealth.quotation.query.InsuredDto insuredDto, Row row, List<String> headers) {
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setRelationship(Relationship.getRelationship(cellValue));
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getRelationship().description;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            Cell relationshipCell = row.getCell(excelHeaders.indexOf(RELATIONSHIP.name()));
            String relationship = getCellValue(relationshipCell);
            String errorMessage = "";
            if (isEmpty(relationship)) {
                errorMessage = errorMessage + "Relationship cannot be empty.";
            }
            return errorMessage;
        }
    },
    NO_OF_ASSURED("No Of Assured") {
        @Override
        public String getAllowedValue(com.pla.grouphealth.quotation.query.InsuredDto insuredDto) {
            return insuredDto.getNoOfAssured() != null ? insuredDto.getNoOfAssured().toString() : "";
        }

        @Override
        public com.pla.grouphealth.quotation.query.InsuredDto populateInsuredDetail(com.pla.grouphealth.quotation.query.InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setNoOfAssured(isNotEmpty(cellValue) ? Double.valueOf(cellValue).intValue() : null);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return "";
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            return "";
        }
    }, PLAN("Plan") {
        @Override
        public String getAllowedValue(com.pla.grouphealth.quotation.query.InsuredDto insuredDto) {
            return insuredDto.getPlanPremiumDetail().getPlanCode();
        }

        @Override
        public com.pla.grouphealth.quotation.query.InsuredDto populateInsuredDetail(com.pla.grouphealth.quotation.query.InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            String planCode = null;
            try {
                planCode = String.valueOf(Double.valueOf(cellValue).intValue());
            } catch (Exception e) {
                planCode = cellValue;
            }
            InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = insuredDto.getPlanPremiumDetail() == null ? new InsuredDto.PlanPremiumDetailDto() : insuredDto.getPlanPremiumDetail();
            planPremiumDetailDto.setPlanCode(planCode);
            insuredDto.setPlanPremiumDetail(planPremiumDetailDto);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            String planCode = null;
            try {
                planCode = String.valueOf(Double.valueOf(cellValue).intValue());
            } catch (Exception e) {
                planCode = cellValue;
            }
            InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = insuredDependentDto.getPlanPremiumDetail() != null ? insuredDependentDto.getPlanPremiumDetail() : new InsuredDto.PlanPremiumDetailDto();
            planPremiumDetailDto.setPlanCode(planCode);
            insuredDependentDto.setPlanPremiumDetail(planPremiumDetailDto);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getPlanPremiumDetail().getPlanCode();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            if (isEmpty(value)) {
                errorMessage = errorMessage + "Plan code cannot be empty.";
                return errorMessage;
            }
            String planCode = null;
            try {
                planCode = String.valueOf(Double.valueOf(value).intValue());
            } catch (Exception e) {
                planCode = value;
            }
            boolean isValidPlan = planAdapter.isValidPlanCode(planCode);
            if (!isValidPlan) {
                errorMessage = errorMessage + "Plan code does not exist.";
            }
            Cell relationshipCell = row.getCell(excelHeaders.indexOf(RELATIONSHIP.name()));
            String relationship = getCellValue(relationshipCell);
            boolean isValidPlanForRelationship = planAdapter.isValidPlanForRelationship(planCode, Relationship.getRelationship(relationship));
            if (!isValidPlanForRelationship) {
                errorMessage = errorMessage + "Plan code is not valid for the relationship " + relationship + ".";
            }
            return errorMessage;
        }
    }, INCOME_MULTIPLIER("Income Multiplier") {
        @Override
        public String getAllowedValue(com.pla.grouphealth.quotation.query.InsuredDto insuredDto) {
            return insuredDto.getPlanPremiumDetail().getIncomeMultiplier() != null ? insuredDto.getPlanPremiumDetail().getIncomeMultiplier().toString() : "";
        }

        @Override
        public com.pla.grouphealth.quotation.query.InsuredDto populateInsuredDetail(com.pla.grouphealth.quotation.query.InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = insuredDto.getPlanPremiumDetail() != null ? insuredDto.getPlanPremiumDetail() : new InsuredDto.PlanPremiumDetailDto();
            planPremiumDetailDto.setIncomeMultiplier(isNotEmpty(cellValue) ? BigDecimal.valueOf(Double.valueOf(cellValue)) : null);
            insuredDto.setPlanPremiumDetail(planPremiumDetailDto);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = insuredDependentDto.getPlanPremiumDetail() != null ? insuredDependentDto.getPlanPremiumDetail() : new InsuredDto.PlanPremiumDetailDto();
            planPremiumDetailDto.setIncomeMultiplier(isNotEmpty(cellValue) ? BigDecimal.valueOf(Double.valueOf(cellValue)) : null);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getPlanPremiumDetail().getIncomeMultiplier() != null ? insuredDependentDto.getPlanPremiumDetail().getIncomeMultiplier().toString() : "";
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            Cell planCell = row.getCell(excelHeaders.indexOf(PLAN.name()));
            Cell relationshipCell = row.getCell(excelHeaders.indexOf(RELATIONSHIP.name()));
            String relationship = getCellValue(relationshipCell);
            String planCode = getCellValue(planCell);
            if (isEmpty(planCode)) {
                errorMessage = errorMessage + "Plan code cannot be empty.";
                return errorMessage;
            }
            try {
                planCode = String.valueOf(Double.valueOf(planCode).intValue());
            } catch (Exception e) {
            }
            boolean isValidPlan = planAdapter.isValidPlanCode(planCode);
            if (!isValidPlan) {
                errorMessage = errorMessage + "Plan code does not exist.";
                return errorMessage;
            }
            boolean hasPlanIncomeMultiplierSumAssuredType = planAdapter.hasPlanContainsIncomeMultiplierSumAssured(planCode);
            if (hasPlanIncomeMultiplierSumAssuredType && isEmpty(value) && Relationship.SELF.description.equals(relationship.trim())) {
                errorMessage = errorMessage + "Income multiplier cannot be empty as selected plan  has sum assured type as Income multiplier.";
                return errorMessage;
            }
            return errorMessage;
        }
    }, SUM_ASSURED("Sum Assured") {
        @Override
        public String getAllowedValue(com.pla.grouphealth.quotation.query.InsuredDto insuredDto) {
            return insuredDto.getPlanPremiumDetail().getSumAssured() != null ? insuredDto.getPlanPremiumDetail().getSumAssured().toString() : "";
        }

        @Override
        public com.pla.grouphealth.quotation.query.InsuredDto populateInsuredDetail(com.pla.grouphealth.quotation.query.InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = insuredDto.getPlanPremiumDetail() != null ? insuredDto.getPlanPremiumDetail() : new InsuredDto.PlanPremiumDetailDto();
            planPremiumDetailDto.setSumAssured(isNotEmpty(cellValue) ? BigDecimal.valueOf(Double.valueOf(cellValue)) : null);
            insuredDto.setPlanPremiumDetail(planPremiumDetailDto);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = insuredDependentDto.getPlanPremiumDetail() != null ? insuredDependentDto.getPlanPremiumDetail() : new InsuredDto.PlanPremiumDetailDto();
            planPremiumDetailDto.setSumAssured(isNotEmpty(cellValue) ? BigDecimal.valueOf(Double.valueOf(cellValue)) : null);
            insuredDependentDto.setPlanPremiumDetail(planPremiumDetailDto);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getPlanPremiumDetail().getSumAssured() != null ? insuredDependentDto.getPlanPremiumDetail().getSumAssured().toString() : "";
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            Cell planCell = row.getCell(excelHeaders.indexOf(PLAN.name()));
            Cell relationshipCell = row.getCell(excelHeaders.indexOf(RELATIONSHIP.name()));
            String relationship = getCellValue(relationshipCell);
            String planCode = getCellValue(planCell);
            if (isEmpty(planCode)) {
                errorMessage = errorMessage + "Plan code cannot be empty.";
                return errorMessage;
            }
            try {
                planCode = String.valueOf(Double.valueOf(planCode).intValue());
            } catch (Exception e) {
            }
            boolean isValidPlan = planAdapter.isValidPlanCode(planCode);
            if (!isValidPlan) {
                errorMessage = errorMessage + "Plan code does not exist.";
                return errorMessage;
            }
            boolean hasPlanIncomeMultiplierSumAssuredType = planAdapter.hasPlanContainsIncomeMultiplierSumAssured(planCode);
            if (!hasPlanIncomeMultiplierSumAssuredType && Relationship.SELF.description.endsWith(relationship.trim()) && isEmpty(value)) {
                errorMessage = errorMessage + "Sum assured cannot be empty.";
                return errorMessage;
            }
            if (!hasPlanIncomeMultiplierSumAssuredType && !Relationship.SELF.description.equals(relationship.trim()) && isEmpty(value)) {
                errorMessage = errorMessage + "Sum assured cannot be empty.";
                return errorMessage;
            }
            boolean isValidSumAssured = isNotEmpty(value) ? planAdapter.isValidPlanSumAssured(planCode, BigDecimal.valueOf(Double.valueOf(value).intValue())) : true;
            if (!isValidSumAssured) {
                errorMessage = errorMessage + "Sum assured is not valid for selected plan.";
            }
            return errorMessage;
        }
    },
    PLAN_PREMIUM("Plan Premium") {
        @Override
        public String getAllowedValue(com.pla.grouphealth.quotation.query.InsuredDto insuredDto) {
            return insuredDto.getPlanPremiumDetail().getPremiumAmount() != null ? insuredDto.getPlanPremiumDetail().getPremiumAmount().toString() : "";
        }

        @Override
        public com.pla.grouphealth.quotation.query.InsuredDto populateInsuredDetail(com.pla.grouphealth.quotation.query.InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = insuredDto.getPlanPremiumDetail() != null ? insuredDto.getPlanPremiumDetail() : new InsuredDto.PlanPremiumDetailDto();
            planPremiumDetailDto.setPremiumAmount(isNotEmpty(cellValue) ? BigDecimal.valueOf(Double.valueOf(cellValue)) : null);
            insuredDto.setPlanPremiumDetail(planPremiumDetailDto);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = insuredDependentDto.getPlanPremiumDetail() != null ? insuredDependentDto.getPlanPremiumDetail() : new InsuredDto.PlanPremiumDetailDto();
            planPremiumDetailDto.setPremiumAmount(isNotEmpty(cellValue) ? BigDecimal.valueOf(Double.valueOf(cellValue)) : null);
            insuredDependentDto.setPlanPremiumDetail(planPremiumDetailDto);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getPlanPremiumDetail().getPremiumAmount() != null ? insuredDependentDto.getPlanPremiumDetail().getPremiumAmount().toString() : "";
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            Cell noOfSumAssuredCell = row.getCell(excelHeaders.indexOf(NO_OF_ASSURED.name()));
            String noOfSumAssured = getCellValue(noOfSumAssuredCell);
            if (isNotEmpty(noOfSumAssured) && isEmpty(value)) {
                errorMessage = errorMessage + "Plan premium cannot be empty.";
            }
            return errorMessage;
        }
    };

    private String description;

    GLInsuredExcelHeader(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static List<String> getAllHeader() {
        List<String> headers = Lists.newArrayList();
        for (GLInsuredExcelHeader glInsuredExcelHeader : GLInsuredExcelHeader.values()) {
            headers.add(glInsuredExcelHeader.getDescription());
        }
        return headers;
    }

    public static List<String> getAllHeaderForParser() {
        List<String> headers = Lists.newArrayList();
        for (com.pla.grouplife.quotation.application.service.GLInsuredExcelHeader glInsuredExcelHeader : com.pla.grouplife.quotation.application.service.GLInsuredExcelHeader.values()) {
            headers.add(glInsuredExcelHeader.name());
        }
        return headers;
    }


    public static List<String> getAllowedHeaders(IPlanAdapter planAdapter, List<PlanId> planIds) {
        List<PlanCoverageDetailDto> planCoverageDetailDtoList = planAdapter.getPlanAndCoverageDetail(planIds);
        int noOfOptionalCoverage = PlanCoverageDetailDto.getNoOfOptionalCoverage(planCoverageDetailDtoList);
        List<String> headers = com.pla.grouplife.quotation.application.service.GLInsuredExcelHeader.getAllHeader();
        for (int count = 1; count <= noOfOptionalCoverage; count++) {
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count));
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.PREMIUM_CELL_HEADER_NAME);
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.OPTIONAL_COVERAGE_SA_HEADER);
        }
        return ImmutableList.copyOf(headers);
    }

    public static List<String> getAllowedHeaderForParser(IPlanAdapter planAdapter, List<PlanId> planIds) {
        List<PlanCoverageDetailDto> planCoverageDetailDtoList = planAdapter.getPlanAndCoverageDetail(planIds);
        int noOfOptionalCoverage = PlanCoverageDetailDto.getNoOfOptionalCoverage(planCoverageDetailDtoList);
        List<String> headers = com.pla.grouplife.quotation.application.service.GLInsuredExcelHeader.getAllHeaderForParser();
        for (int count = 1; count <= noOfOptionalCoverage; count++) {
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count));
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.PREMIUM_CELL_HEADER_NAME);
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.OPTIONAL_COVERAGE_SA_HEADER);
        }
        return ImmutableList.copyOf(headers);
    }

    public abstract String getAllowedValue(InsuredDto insuredDto);

    public abstract InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers);

    public abstract InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers);

    public abstract String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto);

    public abstract String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders);

}
