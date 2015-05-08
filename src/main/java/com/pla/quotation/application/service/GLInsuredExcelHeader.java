package com.pla.quotation.application.service;

import com.google.common.collect.Lists;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.quotation.query.InsuredDto;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.Relationship;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.nthdimenzion.presentation.AppUtils;

import java.math.BigDecimal;
import java.util.List;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Created by Samir on 5/4/2015.
 */
public enum GLInsuredExcelHeader {

    PROPOSER_NAME("Proposer Name") {
        @Override
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getCompanyName();
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getCompanyName();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            if (isEmpty(value)) {
                errorMessage = errorMessage + "Proposer name cannot be empty.";
            }
            return errorMessage;
        }
    }, MAN_NUMBER("MAN Number") {
        @Override
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getManNumber();
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
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getNrcNumber();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            if (!isValidNrcNumber(value)) {
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
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return "";
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            Cell incomeMultiplierCell = row.getCell(excelHeaders.indexOf(INCOME_MULTIPLIER.getDescription()));
            Cell annualIncomeCell = row.getCell(excelHeaders.indexOf(ANNUAL_INCOME.getDescription()));
            String incomeMultiplier = getCellValue(incomeMultiplierCell);
            String annualIncome = getCellValue(annualIncomeCell);
            if (isEmpty(annualIncome) && isNotEmpty(incomeMultiplier)) {
                errorMessage = errorMessage + "Annual income cannot be blank.";
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
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getGender().name();
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
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getOccupationClass();
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getOccupationClass();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            Cell relationshipCell = row.getCell(excelHeaders.indexOf(RELATIONSHIP.getDescription()));
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
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getRelationship().description;
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            Cell relationshipCell = row.getCell(excelHeaders.indexOf(RELATIONSHIP.getDescription()));
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
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return "";
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            return "";
        }
    }, PLAN("Plan") {
        @Override
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getPlanPremiumDetail().getPlanCode();
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getPlanPremiumDetail().getPlanCode();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            if (isEmpty(value)) {
                errorMessage = errorMessage + "Plan cannot be empty.";
                return errorMessage;
            }
            boolean isValidPlan = planAdapter.isValidPlanCode(value);
            if (!isValidPlan) {
                errorMessage = errorMessage + "Plan code does not exist.";
            }
            return errorMessage;
        }
    }, INCOME_MULTIPLIER("Income Multiplier") {
        @Override
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getPlanPremiumDetail().getIncomeMultiplier() != null ? insuredDto.getPlanPremiumDetail().getIncomeMultiplier().toString() : "";
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getPlanPremiumDetail().getIncomeMultiplier() != null ? insuredDependentDto.getPlanPremiumDetail().getIncomeMultiplier().toString() : "";
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            Cell planCell = row.getCell(excelHeaders.indexOf(PLAN.getDescription()));
            Cell relationshipCell = row.getCell(excelHeaders.indexOf(RELATIONSHIP.getDescription()));
            String relationship = getCellValue(relationshipCell);
            String planCode = getCellValue(planCell);
            if (isEmpty(planCode)) {
                errorMessage = errorMessage + "Plan cannot be empty.";
                return errorMessage;
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
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getPlanPremiumDetail().getSumAssured() != null ? insuredDependentDto.getPlanPremiumDetail().getSumAssured().toString() : "";
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            Cell planCell = row.getCell(excelHeaders.indexOf(PLAN.getDescription()));
            Cell relationshipCell = row.getCell(excelHeaders.indexOf(RELATIONSHIP.getDescription()));
            String relationship = getCellValue(relationshipCell);
            String planCode = getCellValue(planCell);
            if (isEmpty(planCode)) {
                errorMessage = errorMessage + "Plan code cannot be empty.";
                return errorMessage;
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
            boolean isValidSumAssured = isNotEmpty(value) ? planAdapter.isValidPlanSumAssured(planCode, BigDecimal.valueOf(Double.valueOf(value))) : true;
            if (!isValidSumAssured) {
                errorMessage = errorMessage + "Sum assured is not valid for selected plan.";
            }
            return errorMessage;
        }
    },
    PLAN_PREMIUM("Plan Premium") {
        @Override
        public String getAllowedValue(InsuredDto insuredDto) {
            return insuredDto.getPlanPremiumDetail().getPremiumAmount() != null ? insuredDto.getPlanPremiumDetail().getPremiumAmount().toString() : "";
        }

        @Override
        public String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getPlanPremiumDetail().getPremiumAmount() != null ? insuredDependentDto.getPlanPremiumDetail().getPremiumAmount().toString() : "";
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            Cell noOfSumAssuredCell = row.getCell(excelHeaders.indexOf(NO_OF_ASSURED.description));
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
            headers.add(glInsuredExcelHeader.description);
        }
        return headers;
    }

    String getCellValue(Cell cell) {
        if (Cell.CELL_TYPE_BLANK == cell.getCellType()) {
            return "";
        } else if (Cell.CELL_TYPE_NUMERIC == cell.getCellType()) {
            return Double.valueOf(cell.getNumericCellValue()).toString().trim();
        }
        return cell.getStringCellValue().trim();
    }

    public abstract String getAllowedValue(InsuredDto insuredDto);

    public abstract String getAllowedValue(InsuredDto.InsuredDependentDto insuredDependentDto);

    public abstract String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders);
}
