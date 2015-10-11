package com.pla.grouplife.sharedresource.model;

import com.pla.grouplife.endorsement.application.service.excel.parser.GLEndorsementExcelValidator;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.Relationship;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.nthdimenzion.common.AppConstants;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

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

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            if (isNotEmpty(value)) {
                insuredDto.setCompanyName(value);
            }
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            if (isNotEmpty(value)) {
                insuredDependentDto.setCompanyName(value);
            }
            return insuredDependentDto;
        }
    }, MAN_NUMBER("MAN Number") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidMANNumber(row, value, excelHeaders);
            return isValid ? "" : "MAN Number is not valid.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            if (isNotEmpty(value)) {
                insuredDto.setManNumber(value);
            }
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            if (isNotEmpty(value)) {
                insuredDependentDto.setManNumber(value);
            }
            return insuredDependentDto;
        }
    }, NRC_NUMBER("NRC Number") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidNRCNumber(row, value, excelHeaders);
            return isValid ? "" : "NRC Number is not valid.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            if (isNotEmpty(value)) {
                insuredDto.setNrcNumber(value);
            }
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            if (isNotEmpty(value)) {
                insuredDependentDto.setNrcNumber(value);
            }
            return insuredDependentDto;
        }
    }, ANNUAL_INCOME("Annual Income") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidAnnualIncome(row, value, excelHeaders);
            return isValid ? "" : "Annual Income cannot be blank/negative.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            if (isNotEmpty(value)) {
                insuredDto.setAnnualIncome(BigDecimal.valueOf(Double.valueOf(value)));
            }
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            return insuredDependentDto;
        }
    },
    SALUTATION("Salutation") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidSalutation(row, value, excelHeaders);
            return isValid ? "" : "Salutation cannot be blank.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            if (isNotEmpty(value)) {
                insuredDto.setSalutation(value);
            }
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            if (isNotEmpty(value)) {
                insuredDependentDto.setSalutation(value);
            }
            return insuredDependentDto;
        }
    }, FIRST_NAME("First Name") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidFirstName(row, value, excelHeaders);
            return isValid ? "" : "First Name cannot be blank.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            if (isNotEmpty(value)) {
                insuredDto.setFirstName(value);
            }
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            if (isNotEmpty(value)) {
                insuredDependentDto.setFirstName(value);
            }
            return insuredDependentDto;
        }
    }, LAST_NAME("Last Name") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidLastName(row, value, excelHeaders);
            return isValid ? "" : "Last Name cannot be blank.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            if (isNotEmpty(value)) {
                insuredDto.setLastName(value);
            }
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            if (isNotEmpty(value)) {
                insuredDependentDto.setLastName(value);
            }
            return insuredDependentDto;
        }
    }, DATE_OF_BIRTH("Date of Birth") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidDateOfBirth(row, value, excelHeaders);
            return isValid ? "" : "Date of birth cannot be blank/should be in (DD/MM/YYYY)format";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            if (isNotEmpty(value)) {
                insuredDto.setDateOfBirth(LocalDate.parse(value, DateTimeFormat.forPattern(AppConstants.DD_MM_YYY_FORMAT)));
            }
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            if (isNotEmpty(value)) {
                insuredDependentDto.setDateOfBirth(LocalDate.parse(value, DateTimeFormat.forPattern(AppConstants.DD_MM_YYY_FORMAT)));
            }
            return insuredDependentDto;
        }
    },
    GENDER("Gender") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidGender(row, value, excelHeaders);
            return isValid ? "" : "Gender is not valid.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            if (isNotEmpty(value)) {
                insuredDto.setGender(Gender.valueOf(value));
            }
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            if (isNotEmpty(value)) {
                insuredDependentDto.setGender(Gender.valueOf(value));
            }
            return insuredDependentDto;
        }
    }, OCCUPATION("Occupation") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidOccupation(row, value, excelHeaders);
            return isValid ? "" : "Occupation is not valid.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            if (isNotEmpty(value)) {
                insuredDto.setOccupationClass(value);
            }
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            if (isNotEmpty(value)) {
                insuredDependentDto.setOccupationClass(value);
            }
            return insuredDependentDto;
        }
    }, CATEGORY("Category") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidCategory(row, value, excelHeaders);
            return isValid ? "" : "Category is not valid.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            if (isNotEmpty(value)) {
                insuredDto.setCategory(value);
            }
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            if (isNotEmpty(value)) {
                insuredDependentDto.setCategory(value);
            }
            return insuredDependentDto;
        }
    }, RELATIONSHIP("Relationship") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidRelationship(row, value, excelHeaders);
            return isValid ? "" : "Relationship is not valid.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            if (isNotEmpty(value)) {
                insuredDto.setRelationship(value);
            }
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            if (isNotEmpty(value)) {
                insuredDependentDto.setRelationship(Relationship.getRelationship(value));
            }
            return insuredDependentDto;
        }
    },
    NO_OF_ASSURED("No Of Assured") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidNumberOfAssured(row, value, excelHeaders);
            return isValid ? "" : "No of Assured cannot be blank.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            if (isNotEmpty(value)) {
                insuredDto.setNoOfAssured(Double.valueOf(value).intValue());
            }
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            if (isNotEmpty(value)) {
                insuredDependentDto.setNoOfAssured(Double.valueOf(value).intValue());
            }
            return insuredDependentDto;
        }
    }, PLAN("Plan") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidPlan(row, value, excelHeaders);
            return isValid ? "" : "Plan cannot be blank/not valid.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            return insuredDependentDto;
        }
    }, INCOME_MULTIPLIER("Income Multiplier") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidIncomeMultiplier(row, value, excelHeaders);
            return isValid ? "" : "Income Multiplier cannot be blank.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            return insuredDependentDto;
        }
    }, SUM_ASSURED("Sum Assured") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidSumAssured(row, value, excelHeaders);
            return isValid ? "" : "Sum Assured is not valid.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            return insuredDependentDto;
        }
    },
    PLAN_PREMIUM("Plan Premium") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidPlanPremium(row, value, excelHeaders);
            return isValid ? "" : "Plan Premium cannot be blank";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            return insuredDependentDto;
        }
    }, OLD_CATEGORY("Old Category") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidOldCategory(row, value, excelHeaders);
            return isValid ? "" : "Old Category cannot be blank/should be same as in policy for the client.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            if (isNotEmpty(value)) {
                insuredDto.setCategory(value);
            }
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            if (isNotEmpty(value)) {
                insuredDependentDto.setCategory(value);
            }
            return insuredDependentDto;
        }
    }, NEW_CATEGORY("New Category") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidNewCategory(row, value, excelHeaders);
            return isValid ? "" : "New Category cannot be blank/should not be same as Old Category.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            if (isNotEmpty(value)) {
                insuredDto.setCategory(value);
            }
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            if (isNotEmpty(value)) {
                insuredDependentDto.setCategory(value);
            }
            return insuredDependentDto;
        }
    }, OLD_ANNUAL_INCOME("Old Annual Income") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidOldAnnualIncome(row, value, excelHeaders);
            return isValid ? "" : "Old Annual Income cannot be blank/should be same as in policy for the client.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            return insuredDependentDto;
        }
    }, NEW_ANNUAL_INCOME("New Annual Income") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidNewAnnualIncome(row, value, excelHeaders);
            return isValid ? "" : "New Annual Income cannot be blank/should not be same as Old Annual Income.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            if (isNotEmpty(value)) {
                insuredDto.setAnnualIncome(BigDecimal.valueOf(Double.valueOf(value)));
            }
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            return insuredDependentDto;
        }
    },
    CLIENT_ID("Client ID") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidClientId(row, value, excelHeaders);
            return isValid ? "" : "Client ID cannot be blank/is not valid.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            if (isNotEmpty(value)) {
                insuredDto.setFamilyId(String.valueOf(Double.valueOf(value).intValue()));
            }
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            if (isNotEmpty(value)) {
                insuredDependentDto.setFamilyId(String.valueOf(Double.valueOf(value).intValue()));
            }
            return insuredDependentDto;
        }
    }, MAIN_ASSURED_CLIENT_ID("Main Assured Client ID") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            boolean isValid = glEndorsementExcelValidator.isValidMainAssuredClientId(row, value, excelHeaders);
            return isValid ? "" : "Main Assured Client ID cannot be blank/is not valid.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            if (isNotEmpty(value)) {
                insuredDto.setFamilyId(String.valueOf(Double.valueOf(value).intValue()));
            }
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            if (isNotEmpty(value)) {
                insuredDependentDto.setFamilyId(String.valueOf(Double.valueOf(value).intValue()));
            }
            return insuredDependentDto;
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

    public abstract InsuredDto populate(InsuredDto insuredDto, String value);

    public abstract InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value);

    public String getDescription() {
        return description;
    }
}
