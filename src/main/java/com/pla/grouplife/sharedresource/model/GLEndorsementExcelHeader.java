package com.pla.grouplife.sharedresource.model;

import com.pla.grouplife.endorsement.application.service.excel.parser.GLEndorsementExcelValidator;
import org.apache.poi.ss.usermodel.Row;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Created by Samir on 8/5/2015.
 */
public enum GLEndorsementExcelHeader {


    PROPOSER_NAME("Proposer Name") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidProposerName(row, value, excelHeaders);
            return isValid ? "" : "Proposer name is not valid.";
        }
    }, MAN_NUMBER("MAN Number") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidMANNumber(row, value, excelHeaders);
            return isValid ? "" : "MAN Number is not valid.";
        }
    }, NRC_NUMBER("NRC Number") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidNRCNumber(row, value, excelHeaders);
            return isValid ? "" : "NRC Number is not valid.";
        }
    }, ANNUAL_INCOME("Annual Income") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidAnnualIncome(row, value, excelHeaders);
            return isValid ? "" : "Annual Income cannot be blank/negative.";
        }
    },
    SALUTATION("Salutation") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidSalutation(row, value, excelHeaders);
            return isValid ? "" : "Salutation cannot be blank.";
        }
    }, FIRST_NAME("First Name") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidFirstName(row, value, excelHeaders);
            return isValid ? "" : "First Name cannot be blank.";
        }
    }, LAST_NAME("Last Name") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidLastName(row, value, excelHeaders);
            return isValid ? "" : "Last Name cannot be blank.";
        }
    }, DATE_OF_BIRTH("Date of Birth") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidDateOfBirth(row, value, excelHeaders);
            return isValid ? "" : "Date of birth cannot be blank/should be in (DD/MM/YYYY)format";
        }
    },
    GENDER("Gender") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidGender(row, value, excelHeaders);
            return isValid ? "" : "Gender is not valid.";
        }
    }, OCCUPATION("Occupation") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidOccupation(row, value, excelHeaders);
            return isValid ? "" : "Occupation is not valid.";
        }
    }, CATEGORY("Category") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidCategory(row, value, excelHeaders);
            return isValid ? "" : "Category is not valid.";
        }
    }, RELATIONSHIP("Relationship") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidRelationship(row, value, excelHeaders);
            return isValid ? "" : "Relationship is not valid.";
        }
    },
    NO_OF_ASSURED("No Of Assured") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidNumberOfAssured(row, value, excelHeaders);
            return isValid ? "" : "No of Assured cannot be blank.";
        }
    }, PLAN("Plan") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidPlan(row, value, excelHeaders);
            return isValid ? "" : "Plan cannot be blank/not valid.";
        }
    }, INCOME_MULTIPLIER("Income Multiplier") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidIncomeMultiplier(row, value, excelHeaders);
            return isValid ? "" : "Income Multiplier cannot be blank.";
        }
    }, SUM_ASSURED("Sum Assured") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidSumAssured(row, value, excelHeaders);
            return isValid ? "" : "Sum Assured is not valid.";
        }
    },
    PLAN_PREMIUM("Plan Premium") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidPlanPremium(row, value, excelHeaders);
            return isValid ? "" : "Plan Premium cannot be blank";
        }
    }, OLD_CATEGORY("Old Category") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidOldCategory(row, value, excelHeaders);
            return isValid ? "" : "Old Category cannot be blank/should be same as in policy for the client.";
        }
    }, NEW_CATEGORY("New Category") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidNewCategory(row, value, excelHeaders);
            return isValid ? "" : "New Category cannot be blank/should not be same as Old Category.";
        }
    }, OLD_ANNUAL_INCOME("Old Annual Income") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidOldAnnualIncome(row, value, excelHeaders);
            return isValid ? "" : "Old Annual Income cannot be blank/should be same as in policy for the client.";
        }
    }, NEW_ANNUAL_INCOME("New Annual Income") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidNewAnnualIncome(row, value, excelHeaders);
            return isValid ? "" : "New Annual Income cannot be blank/should not be same as Old Annual Income.";
        }
    },
    CLIENT_ID("Client ID") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidClientId(row, value, excelHeaders);
            return isValid ? "" : "Client ID is not valid.";
        }
    }, MAIN_ASSURED_CLIENT_ID("Main Assured Client ID") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidMainAssuredClientId(row, value, excelHeaders);
            return isValid ? "" : "Main Assured Client ID is not valid.";
        }
    };

    private String description;

    public static GLEndorsementExcelHeader findGLEndorsementExcelHeaderTypeFromDescription(String description) {
        Optional<GLEndorsementExcelHeader> glEndorsementExcelHeaderOptional = Arrays.asList(GLEndorsementExcelHeader.values()).stream().filter(glEndorsementExcelHeader -> description.equals(glEndorsementExcelHeader.description)).findAny();
        return glEndorsementExcelHeaderOptional.isPresent() ? glEndorsementExcelHeaderOptional.get() : null;
    }

    GLEndorsementExcelHeader(String description) {
        this.description = description;
    }

    public abstract String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders);

    public String getDescription() {
        return description;
    }
}
