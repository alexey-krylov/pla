package com.pla.grouplife.sharedresource.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.PremiumType;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.identifier.PlanId;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.nthdimenzion.common.AppConstants;
import org.nthdimenzion.presentation.AppUtils;

import java.math.BigDecimal;
import java.util.List;

import static com.pla.grouplife.sharedresource.exception.GLInsuredTemplateExcelParseException.raiseNotValidValueException;
import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Created by Samir on 5/4/2015.
 */
public enum GLInsuredExcelHeader {

    CLIENT_ID("Client ID") {
        @Override
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getFamilyId();
        }

        @Override
        public InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setFamilyId(cellValue);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setFamilyId(cellValue);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getFamilyId();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            return "";
        }
    },
    PROPOSER_NAME("Proposer Name") {
        @Override
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getCompanyName();
        }

        @Override
        public InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers) {
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
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getManNumber();
        }

        @Override
        public InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers) {
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
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getNrcNumber();
        }

        @Override
        public InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers) {
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
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getAnnualIncome() != null ? insuredDto.getAnnualIncome().toString() : "";
        }

        @Override
        public InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers) {
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
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getSalutation();
        }

        @Override
        public InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers) {
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
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getFirstName();
        }

        @Override
        public InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers) {
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
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getLastName();
        }

        @Override
        public InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers) {
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
        public String getAllowedValue(InsuredDto insuredDto) {
            return AppUtils.toString(insuredDto.getDateOfBirth());
        }

        @Override
        public InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers) {
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
            if(isNotEmpty(value)) {
                Cell planCell = row.getCell(excelHeaders.indexOf(PLAN.name()));
                String planCode = String.valueOf(Double.valueOf(getCellValue(planCell)).intValue());
                int age = AppUtils.getAgeOnNextBirthDate(LocalDate.parse(value, DateTimeFormat.forPattern(AppConstants.DD_MM_YYY_FORMAT)));
                if (!planAdapter.isValidPlanAge(planCode, age)) {
                    errorMessage = errorMessage + " Age is not valid for plan " + planCode + ".";
                }
            }
            return errorMessage;
        }
    },
    GENDER("Gender") {
        @Override
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getGender() != null ? insuredDto.getGender().name() : "";
        }

        @Override
        public InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setGender(isNotEmpty(cellValue) ? Gender.valueOf(cellValue) : null);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setGender(isNotEmpty(cellValue) ? Gender.valueOf(cellValue) : null);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getGender() != null ? insuredDependentDto.getGender().name() : "";
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            Cell noOfSumAssuredCell = row.getCell(excelHeaders.indexOf(NO_OF_ASSURED.name()));
            String noOfSumAssured = getCellValue(noOfSumAssuredCell);
            if (isNotEmpty(noOfSumAssured) && isEmpty(value)) {
                return "";
            }
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
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getOccupationClass();
        }

        @Override
        public InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers) {
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
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getOccupationCategory();
        }

        @Override
        public InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers) {
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
        public String getAllowedValue(InsuredDto insuredDto) {
            return "Self";
        }

        @Override
        public InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers) {
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
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getNoOfAssured() != null ? insuredDto.getNoOfAssured().toString() : "";
        }

        @Override
        public InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setNoOfAssured(isNotEmpty(cellValue) ? Double.valueOf(cellValue).intValue() : null);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setNoOfAssured(isNotEmpty(cellValue) ? Double.valueOf(cellValue).intValue() : null);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getNoOfAssured() != null ? insuredDependentDto.getNoOfAssured().toString() : "";
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            if (isNotEmpty(value) && Double.valueOf(value) < 0) {
                return "No of assured cannot be negative.";
            }
            return "";
        }
    }, PLAN("Plan") {
        @Override
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getPlanPremiumDetail().getPlanCode();
        }

        @Override
        public InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers) {
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
            boolean isValidPlanForGL = planAdapter.isValidPlanCodeForBusinessLine(planCode, LineOfBusinessEnum.GROUP_LIFE);
            if (!isValidPlanForGL) {
                errorMessage = errorMessage + "Plan code is not a Group Life Plan.";
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
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getPlanPremiumDetail().getIncomeMultiplier() != null ? insuredDto.getPlanPremiumDetail().getIncomeMultiplier().toString() : "";
        }

        @Override
        public InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers) {
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
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getPlanPremiumDetail().getSumAssured() != null ? insuredDto.getPlanPremiumDetail().getSumAssured().toString() : "";
        }

        @Override
        public InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            BigDecimal sumAssured = null;
            Cell incomeMultiplierCell = row.getCell(headers.indexOf(INCOME_MULTIPLIER.getDescription()));
            String incomeMultiplierCellValue = getCellValue(incomeMultiplierCell);
            Cell annualIncomeCell = row.getCell(headers.indexOf(ANNUAL_INCOME.getDescription()));
            String annualIncomeCellValue = getCellValue(annualIncomeCell);
            if (isNotEmpty(cellValue)) {
                sumAssured = BigDecimal.valueOf(Double.valueOf(cellValue).intValue());
            } else if (isNotEmpty(incomeMultiplierCellValue) && isNotEmpty(annualIncomeCellValue)) {
                try {
                    Double incomeMultiplierValue = Double.parseDouble(incomeMultiplierCellValue);
                    Double annualIncomeValue = Double.parseDouble(annualIncomeCellValue);
                    sumAssured = BigDecimal.valueOf(incomeMultiplierValue.intValue()).multiply(BigDecimal.valueOf(annualIncomeValue.intValue()));
                } catch (Exception e) {
                    raiseNotValidValueException("Income multiplier and Annual Income should be numeric");
                }
            }
            InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = insuredDto.getPlanPremiumDetail() != null ? insuredDto.getPlanPremiumDetail() : new InsuredDto.PlanPremiumDetailDto();
            planPremiumDetailDto.setSumAssured(sumAssured);

            insuredDto.setPlanPremiumDetail(planPremiumDetailDto);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = insuredDependentDto.getPlanPremiumDetail() != null ? insuredDependentDto.getPlanPremiumDetail() : new InsuredDto.PlanPremiumDetailDto();
            planPremiumDetailDto.setSumAssured(isNotEmpty(cellValue) ? BigDecimal.valueOf(Double.valueOf(cellValue).intValue()) : null);
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
            if (isNotEmpty(value) && Double.valueOf(value) < 0) {
                errorMessage = errorMessage + "Plan Sum assured cannot be negative.";
            }
            return errorMessage;
        }
    }, PREMIUM_TYPE("Premium Type") {
        @Override
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getPremiumType() != null ? insuredDto.getPremiumType().toString() : "";
        }

        @Override
        public InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setPremiumType(isNotEmpty(cellValue) ? PremiumType.valueOf(cellValue.toUpperCase()) : PremiumType.AMOUNT);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setPremiumType(isNotEmpty(cellValue) ? PremiumType.valueOf(cellValue.toUpperCase()) : PremiumType.AMOUNT);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getPremiumType() != null ? insuredDependentDto.getPremiumType().toString() : "";
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            try {
                Cell planPremiumCell = row.getCell(excelHeaders.indexOf(PLAN_PREMIUM.name()));
                String planPremium = getCellValue(planPremiumCell);
                Cell noOfAssuredCell = row.getCell(excelHeaders.indexOf(NO_OF_ASSURED.name()));
                String noOfAssured = getCellValue(noOfAssuredCell);

                if(PremiumType.RATE.toString().equalsIgnoreCase(value) && isEmpty(planPremium)){
                    errorMessage = errorMessage + "Premium Rate cannot be empty.";
                    return errorMessage;
                }
                if(PremiumType.AMOUNT.toString().equalsIgnoreCase(value) && isEmpty(planPremium)){
                    errorMessage = errorMessage + "Premium Amount cannot be empty.";
                    return errorMessage;
                }
                if(PremiumType.RATE.toString().equalsIgnoreCase(value) && isNotEmpty(planPremium)){
                    if(new BigDecimal(planPremium).signum() == -1) {
                        errorMessage = errorMessage + "Premium Rate cannot be negative value.";
                    }
                    if(!(new BigDecimal(1000).compareTo(new BigDecimal(planPremium)) >= 0)) {
                        errorMessage = errorMessage + "Premium Rate cannot be greater than 1000.";
                    }
                    if(isNotEmpty(errorMessage))
                        return errorMessage;
                }
                if(isNotEmpty(noOfAssured) && isEmpty(value)){
                    errorMessage = errorMessage + "Premium Type is empty for given number of assured.";
                    return errorMessage;
                }
                if(isEmpty(noOfAssured) && isEmpty(value) && isNotEmpty(planPremium)){
                    errorMessage = errorMessage + "Premium Type cannot be empty.";
                    return errorMessage;
                }
                if(!PremiumType.checkIfValidConstant(value)){
                    errorMessage = errorMessage + "Premium Type is not valid.";
                    return errorMessage;
                }
            } catch (Exception e) {
                errorMessage = errorMessage + e.getMessage();
                return errorMessage;
            }
            return errorMessage;
        }
    }, PLAN_PREMIUM("Plan Premium") {
        @Override
        public String getAllowedValue(InsuredDto insuredDto) {
            String premium = insuredDto.getPlanPremiumDetail().getPremiumAmount() != null ? insuredDto.getPlanPremiumDetail().getPremiumAmount().toString() : "";
            if (insuredDto.getPlanPremiumDetail().getPremiumAmount() != null && insuredDto.getNoOfAssured() != null) {
                BigDecimal premiumAmount = insuredDto.getPlanPremiumDetail().getPremiumAmount().divide(new BigDecimal(insuredDto.getNoOfAssured()));
                premium = premiumAmount.toPlainString();
            }
            if(PremiumType.RATE.equals(insuredDto.getPremiumType() != null ? insuredDto.getPremiumType() : "")){
                premium = insuredDto.getRateOfPremium();
            }
            return premium;
        }

        @Override
        public InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            BigDecimal planPremium = null;
            Cell noOfAssuredCell = row.getCell(headers.indexOf(NO_OF_ASSURED.getDescription()));
            String noOfAssuredCellValue = getCellValue(noOfAssuredCell);
            Cell firstNameCell = row.getCell(headers.indexOf(FIRST_NAME.getDescription()));
            String firstName = getCellValue(firstNameCell);
            Cell dateOfBirthCell = row.getCell(headers.indexOf(DATE_OF_BIRTH.getDescription()));
            String dateOfBirth = getCellValue(dateOfBirthCell);
            Cell annualLimitCell = row.getCell(headers.indexOf(SUM_ASSURED.getDescription()));
            String annualLimit = getCellValue(annualLimitCell);
            Cell premiumTypeCell = row.getCell(headers.indexOf(PREMIUM_TYPE.getDescription()));
            String premiumType = getCellValue(premiumTypeCell);
            if(premiumType.equalsIgnoreCase(PremiumType.RATE.toString())){
                planPremium = calculatePlanPremiumForPremiumTypeRate(cellValue, annualLimit, noOfAssuredCellValue);
                insuredDto.setRateOfPremium(cellValue);
            } else {
                if (isNotEmpty(noOfAssuredCellValue) && isNotEmpty(cellValue)) {
                    planPremium = BigDecimal.valueOf(Double.valueOf(noOfAssuredCellValue)).multiply(BigDecimal.valueOf(Double.valueOf(cellValue)));
                }
                if (isNotEmpty(firstName) && isNotEmpty(dateOfBirth) && isNotEmpty(cellValue)) {
                    planPremium = BigDecimal.valueOf(Double.valueOf(cellValue)).multiply(new BigDecimal(1));
                }
            }
            InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = insuredDto.getPlanPremiumDetail() != null ? insuredDto.getPlanPremiumDetail() : new InsuredDto.PlanPremiumDetailDto();
            planPremiumDetailDto.setPremiumAmount(planPremium);
            insuredDto.setPlanPremiumDetail(planPremiumDetailDto);
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            Cell noOfAssuredCell = row.getCell(headers.indexOf(NO_OF_ASSURED.getDescription()));
            String noOfAssuredCellValue = getCellValue(noOfAssuredCell);
            Cell firstNameCell = row.getCell(headers.indexOf(FIRST_NAME.getDescription()));
            String firstName = getCellValue(firstNameCell);
            Cell dateOfBirthCell = row.getCell(headers.indexOf(DATE_OF_BIRTH.getDescription()));
            String dateOfBirth = getCellValue(dateOfBirthCell);
            BigDecimal planPremium = null;
            if (isNotEmpty(noOfAssuredCellValue) && isNotEmpty(cellValue)) {
                planPremium = BigDecimal.valueOf(Double.valueOf(noOfAssuredCellValue)).multiply(BigDecimal.valueOf(Double.valueOf(cellValue)));
            }
            else if (isNotEmpty(firstName) && isNotEmpty(dateOfBirth) && isNotEmpty(cellValue)) {
                planPremium = BigDecimal.valueOf(Double.valueOf(cellValue)).multiply(new BigDecimal(1));
            }
            InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = insuredDependentDto.getPlanPremiumDetail() != null ? insuredDependentDto.getPlanPremiumDetail() : new InsuredDto.PlanPremiumDetailDto();
            planPremiumDetailDto.setPremiumAmount(planPremium);
            insuredDependentDto.setPlanPremiumDetail(planPremiumDetailDto);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            String premium = insuredDependentDto.getPlanPremiumDetail().getPremiumAmount() != null ? insuredDependentDto.getPlanPremiumDetail().getPremiumAmount().toString() : "";
            if (insuredDependentDto.getPlanPremiumDetail().getPremiumAmount() != null && insuredDependentDto.getNoOfAssured() != null) {
                BigDecimal premiumAmount = insuredDependentDto.getPlanPremiumDetail().getPremiumAmount().divide(new BigDecimal(insuredDependentDto.getNoOfAssured()));
                premium = premiumAmount.toPlainString();
            }
            return premium;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            Cell noOfSumAssuredCell = row.getCell(excelHeaders.indexOf(NO_OF_ASSURED.name()));
            String noOfSumAssured = getCellValue(noOfSumAssuredCell);
            if (isNotEmpty(noOfSumAssured) && isEmpty(value)) {
                errorMessage = errorMessage + "Plan premium cannot be empty.";
            }
            if (isNotEmpty(value) && Double.valueOf(value) < 0) {
                errorMessage = errorMessage + "Plan premium cannot be negative.";
            }
            return errorMessage;
        }
    };

    private String description;

    GLInsuredExcelHeader(String description) {
        this.description = description;
    }

    private static BigDecimal calculatePlanPremiumForPremiumTypeRate(String cellValue, String annualLimit, String noOfAssuredCellValue) {
        if(isNotEmpty(noOfAssuredCellValue))
            return ((new BigDecimal(cellValue).multiply(new BigDecimal(annualLimit))).divide(new BigDecimal(1000))).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(noOfAssuredCellValue));
        else
            return (new BigDecimal(cellValue).multiply(new BigDecimal(annualLimit))).divide(new BigDecimal(1000));
    }

    public static List<String> getAllHeader() {
        List<String> headers = Lists.newArrayList();
        for (GLInsuredExcelHeader glInsuredExcelHeader : GLInsuredExcelHeader.values()) {
            if (!CLIENT_ID.equals(glInsuredExcelHeader)) {
                headers.add(glInsuredExcelHeader.getDescription());
            }
        }
        return headers;
    }


    public static List<String> getAllHeaderForParser() {
        List<String> headers = Lists.newArrayList();
        for (GLInsuredExcelHeader glInsuredExcelHeader : GLInsuredExcelHeader.values()) {
            if (!CLIENT_ID.equals(glInsuredExcelHeader)) {
                headers.add(glInsuredExcelHeader.name());
            }
        }
        return headers;
    }

    public static List<String> getAllowedHeaders(IPlanAdapter planAdapter, List<PlanId> planIds) {
        List<PlanCoverageDetailDto> planCoverageDetailDtoList = planAdapter.getPlanAndCoverageDetail(planIds);

        int noOfOptionalCoverage = PlanCoverageDetailDto.getNoOfOptionalCoverage(planCoverageDetailDtoList);
        List<String> headers = GLInsuredExcelHeader.getAllHeader();
        for (int count = 1; count <= noOfOptionalCoverage; count++) {
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count));
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.OPTIONAL_COVERAGE_SA_HEADER);
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.PREMIUM_CELL_HEADER_NAME);
        }
        return ImmutableList.copyOf(headers);
    }

    public static List<String> getAllowedHeaderForParser(IPlanAdapter planAdapter, List<PlanId> planIds) {
        List<PlanCoverageDetailDto> planCoverageDetailDtoList = planAdapter.getPlanAndCoverageDetail(planIds);
        int noOfOptionalCoverage = PlanCoverageDetailDto.getNoOfOptionalCoverage(planCoverageDetailDtoList);
        List<String> headers = GLInsuredExcelHeader.getAllHeaderForParser();
        for (int count = 1; count <= noOfOptionalCoverage; count++) {
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count));
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.OPTIONAL_COVERAGE_SA_HEADER);
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.PREMIUM_CELL_HEADER_NAME);
        }
        return ImmutableList.copyOf(headers);
    }


    public static List<String> getAllowedHeadersForEndorsement(IPlanAdapter planAdapter, List<PlanId> planIds) {
        List<PlanCoverageDetailDto> planCoverageDetailDtoList = planAdapter.getPlanAndCoverageDetail(planIds);
        int noOfOptionalCoverage = PlanCoverageDetailDto.getNoOfOptionalCoverage(planCoverageDetailDtoList);
        List<String> headers = GLInsuredExcelHeader.getAllHeaderForEndorsement();
        for (int count = 1; count <= noOfOptionalCoverage; count++) {
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count));
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.OPTIONAL_COVERAGE_SA_HEADER);
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.PREMIUM_CELL_HEADER_NAME);
        }
        return ImmutableList.copyOf(headers);
    }


    public static List<String> getAllowedHeaderForParserEndorsement(IPlanAdapter planAdapter, List<PlanId> planIds) {
        List<PlanCoverageDetailDto> planCoverageDetailDtoList = planAdapter.getPlanAndCoverageDetail(planIds);
        int noOfOptionalCoverage = PlanCoverageDetailDto.getNoOfOptionalCoverage(planCoverageDetailDtoList);
        List<String> headers = GLInsuredExcelHeader.getAllHeaderForParserEndorsement();
        for (int count = 1; count <= noOfOptionalCoverage; count++) {
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count));
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.OPTIONAL_COVERAGE_SA_HEADER);
            headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.PREMIUM_CELL_HEADER_NAME);
        }
        return ImmutableList.copyOf(headers);
    }

    public static List<String> getAllHeaderForParserEndorsement() {
        List<String> headers = Lists.newArrayList();
        for (GLInsuredExcelHeader glInsuredExcelHeader : GLInsuredExcelHeader.values()) {
            headers.add(glInsuredExcelHeader.name());
        }
        return headers;
    }

    public static List<String> getAllHeaderForEndorsement() {
        List<String> headers = Lists.newArrayList();
        for (GLInsuredExcelHeader glInsuredExcelHeader : GLInsuredExcelHeader.values()) {
            headers.add(glInsuredExcelHeader.getDescription());
        }
        return headers;
    }

    public String getDescription() {
        return description;
    }

    public abstract String getAllowedValue(InsuredDto insuredDto);

    public abstract InsuredDto populateInsuredDetail(InsuredDto insuredDto, Row row, List<String> headers);

    public abstract InsuredDto.InsuredDependentDto populateInsuredDependentDetail(InsuredDto.InsuredDependentDto insuredDependentDto, Row row, List<String> headers);

    public abstract String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto);

    public abstract String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders);

}
