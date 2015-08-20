package com.pla.grouplife.sharedresource.model;

import com.pla.grouplife.endorsement.application.service.excel.parser.GLEndorsementExcelValidator;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;

/**
 * Created by Samir on 8/5/2015.
 */
public enum GLEndorsementExcelHeader {


    PROPOSER_NAME("Proposer Name") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidProposerName(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    }, MAN_NUMBER("MAN Number") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidMANNumber(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    }, NRC_NUMBER("NRC Number") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidNRCNumber(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    }, ANNUAL_INCOME("Annual Income") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidAnnualIncome(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    },
    SALUTATION("Salutation") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidSalutation(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    }, FIRST_NAME("First Name") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidFirstName(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    }, LAST_NAME("Last Name") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidLastName(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    }, DATE_OF_BIRTH("Date of Birth") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidDateOfBirth(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    },
    GENDER("Gender") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidGender(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    }, OCCUPATION("Occupation") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidOccupation(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    }, CATEGORY("Category") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidCategory(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    }, RELATIONSHIP("Relationship") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidRelationship(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    },
    NO_OF_ASSURED("No Of Assured") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidNumberOfAssured(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    }, PLAN("Plan") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidPlan(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    }, INCOME_MULTIPLIER("Income Multiplier") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidIncomeMultiplier(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    }, SUM_ASSURED("Sum Assured") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidSumAssured(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    },
    PLAN_PREMIUM("Plan Premium") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidPlanPremium(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    }, OLD_CATEGORY("Old Category") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidOldCategory(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    }, NEW_CATEGORY("New Category") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidNewCategory(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    }, OLD_ANNUAL_INCOME("Old Annual Income") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidOldAnnualIncome(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    }, NEW_ANNUAL_INCOME("New Annual Income") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidNewAnnualIncome(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    },
    CLIENT_ID("Client ID") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidClientId(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    }, MAIN_ASSURED_CLIENT_ID("Main Assured Client ID") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidMainAssuredClientId(row, value, excelHeaders);
            return isValid ? "" : "";
        }
    };

    private String description;

    GLEndorsementExcelHeader(String description) {
        this.description = description;
    }

    public abstract String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders);

    public String getDescription() {
        return description;
    }
}
