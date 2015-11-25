package com.pla.grouplife.sharedresource.model;

import com.pla.grouplife.endorsement.application.service.excel.parser.GLEndorsementExcelValidator;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.Relationship;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.nthdimenzion.common.AppConstants;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(PROPOSER_NAME.getDescription()),"");
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(PROPOSER_NAME.getDescription()),"");
            return insuredDetailMap;
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(MAN_NUMBER.getDescription()),insured.getManNumber());
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(MAN_NUMBER.getDescription()),insuredDependentDto.getManNumber());
            return insuredDetailMap;
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(NRC_NUMBER.getDescription()),insured.getNrcNumber());
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(NRC_NUMBER.getDescription()),insuredDependentDto.getNrcNumber());
            return insuredDetailMap;
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(ANNUAL_INCOME.getDescription()),insured.getAnnualIncome()!=null?insured.getAnnualIncome().toPlainString():"");
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            return insuredDetailMap;
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(SALUTATION.getDescription()),insured.getSalutation());
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(SALUTATION.getDescription()),insuredDependentDto.getSalutation());
            return insuredDetailMap;
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(FIRST_NAME.getDescription()),insured.getFirstName());
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(FIRST_NAME.getDescription()),insuredDependentDto.getFirstName());
            return insuredDetailMap;
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(LAST_NAME.getDescription()),insured.getLastName());
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(LAST_NAME.getDescription()),insuredDependentDto.getLastName());
            return insuredDetailMap;
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(DATE_OF_BIRTH.getDescription()),insured.getDateOfBirth()!=null?insured.getDateOfBirth().toString(AppConstants.DD_MM_YYY_FORMAT):"");
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(DATE_OF_BIRTH.getDescription()),insuredDependentDto.getDateOfBirth()!=null?insuredDependentDto.getDateOfBirth().toString(AppConstants.DD_MM_YYY_FORMAT):"");
            return insuredDetailMap;
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(GENDER.getDescription()),insured.getGender()!=null?insured.getGender().name():"");
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(GENDER.getDescription()),insuredDependentDto.getGender()!=null?insuredDependentDto.getGender().name():"");
            return insuredDetailMap;
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(OCCUPATION.getDescription()),insured.getOccupationClass());
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            return null;
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(CATEGORY.getDescription()),insured.getCategory());
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(CATEGORY.getDescription()),insuredDependentDto.getCategory());
            return insuredDetailMap;
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(RELATIONSHIP.getDescription()),Relationship.SELF.description);
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(RELATIONSHIP.getDescription()),insuredDependentDto.getRelationship()!=null?insuredDependentDto.getRelationship().description:"");
            return insuredDetailMap;
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(NO_OF_ASSURED.getDescription()),String.valueOf(insured.getNoOfAssured()!=null?insured.getNoOfAssured():""));
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(NO_OF_ASSURED.getDescription()),String.valueOf(insuredDependentDto.getNoOfAssured()!=null?insuredDependentDto.getNoOfAssured():""));
            return insuredDetailMap;
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(PLAN.getDescription()),"");
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(PLAN.getDescription()),"");
            return insuredDetailMap;
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(INCOME_MULTIPLIER.getDescription()),"");
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(INCOME_MULTIPLIER.getDescription()),"");
            return insuredDetailMap;
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(SUM_ASSURED.getDescription()),"");
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(SUM_ASSURED.getDescription()),"");
            return insuredDetailMap;
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(PLAN_PREMIUM.getDescription()),"");
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(PLAN_PREMIUM.getDescription()),"");
            return insuredDetailMap;
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(OLD_CATEGORY.getDescription()),"");
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(OLD_CATEGORY.getDescription()),"");
            return insuredDetailMap;
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(NEW_CATEGORY.getDescription()),"");
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(NEW_CATEGORY.getDescription()),"");
            return insuredDetailMap;
        }
    }, OLD_ANNUAL_INCOME("Old Annual Income") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            String oldAnnualIncome = value;
            if (isNotEmpty(value)) {
                  oldAnnualIncome = String.valueOf(new BigDecimal(value).setScale(0, BigDecimal.ROUND_FLOOR));
            }
            boolean isValid = true;/*glEndorsementExcelValidator.isValidOldAnnualIncome(row, oldAnnualIncome, excelHeaders)*/;
            return isValid ? "" : "Old Annual Income cannot be blank/should be same as in policy for the client.";
        }

        @Override
        public InsuredDto populate(InsuredDto insuredDto, String value) {
            if (isNotEmpty(value)) {
                insuredDto.setOldAnnualIncome(BigDecimal.valueOf(Double.valueOf(value)));
            }
            return insuredDto;
        }

        @Override
        public InsuredDto.InsuredDependentDto populate(InsuredDto.InsuredDependentDto insuredDependentDto, String value) {
            return insuredDependentDto;
        }

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(OLD_ANNUAL_INCOME.getDescription()),insured.getOldAnnualIncome().toPlainString());
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(OLD_ANNUAL_INCOME.getDescription()),"");
            return insuredDetailMap;
        }
    }, NEW_ANNUAL_INCOME("New Annual Income") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            String newAnnualIncome = value;
            if (isNotEmpty(value)) {
                newAnnualIncome = String.valueOf(new BigDecimal(value).setScale(0, BigDecimal.ROUND_FLOOR));
            }
            boolean isValid = glEndorsementExcelValidator.isValidNewAnnualIncome(row, newAnnualIncome, excelHeaders);
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(NEW_ANNUAL_INCOME.getDescription()),insured.getAnnualIncome().toPlainString());
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(NEW_ANNUAL_INCOME.getDescription()),"");
            return insuredDetailMap;
        }
    },
    CLIENT_ID("Client ID") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            Cell cell = row.getCell(excelHeaders.indexOf(NO_OF_ASSURED.getDescription()));
            String cellValue = getCellValue(cell);
            if (isNotEmpty(cellValue)){
                return "";
            }
            String clientId = isNotEmpty(value)?String.valueOf(new BigDecimal(value).longValue()):"";
            boolean isValid = glEndorsementExcelValidator.isValidClientId(row, clientId, excelHeaders);
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(CLIENT_ID.getDescription()),insured.getFamilyId());
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(CLIENT_ID.getDescription()),insuredDependentDto.getFamilyId());
            return insuredDetailMap;
        }
    }, MAIN_ASSURED_CLIENT_ID("Main Assured Client ID") {
        @Override
        public String getErrorMessageIfNotValid(GLEndorsementExcelValidator glEndorsementExcelValidator, Row row, String value, List<String> excelHeaders) {
            Cell cell = row.getCell(excelHeaders.indexOf(NO_OF_ASSURED.getDescription()));
            String cellValue = getCellValue(cell);
            if (isNotEmpty(cellValue)){
                return "";
            }
            String clientId = isNotEmpty(value)?String.valueOf(new BigDecimal(value).longValue()):"";
            boolean isValid = glEndorsementExcelValidator.isValidMainAssuredClientId(row, clientId, excelHeaders);
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

        @Override
        public Map<Integer, String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(MAIN_ASSURED_CLIENT_ID.getDescription()),insured.getFamilyId());
            return insuredDetailMap;
        }

        @Override
        public Map<Integer, String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString) {
            insuredDetailMap.put(excelHeaderInString.indexOf(MAIN_ASSURED_CLIENT_ID.getDescription()),insuredDependentDto.getFamilyId());
            return insuredDetailMap;
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

    public abstract Map<Integer,String> getInsuredDetail(Map<Integer, String> insuredDetailMap, InsuredDto insured, List<String> excelHeaderInString);

    public abstract Map<Integer,String> getInsuredDependentDetail(Map<Integer, String> insuredDetailMap, InsuredDto.InsuredDependentDto insuredDependentDto, List<String> excelHeaderInString);

    public String getDescription() {
        return description;
    }
}
