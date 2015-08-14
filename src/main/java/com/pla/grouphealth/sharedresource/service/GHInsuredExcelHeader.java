package com.pla.grouphealth.sharedresource.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.pla.grouphealth.sharedresource.dto.GHInsuredDto;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import com.pla.sharedkernel.domain.model.Gender;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Created by Samir on 5/4/2015.
 */
public enum GHInsuredExcelHeader {

    PROPOSER_NAME("Proposer Name") {
        @Override
        public String getAllowedValue(GHInsuredDto insuredDto) {
            return insuredDto.getCompanyName();
        }

        @Override
        public GHInsuredDto populateInsuredDetail(GHInsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setCompanyName(cellValue);
            return insuredDto;
        }

        @Override
        public GHInsuredDto.GHInsuredDependentDto populateInsuredDependentDetail(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setCompanyName(cellValue);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getCompanyName();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            return "";
        }
    }, MAN_NUMBER("MAN Number") {
        @Override
        public String getAllowedValue(GHInsuredDto insuredDto) {
            return insuredDto.getManNumber();
        }

        @Override
        public GHInsuredDto populateInsuredDetail(GHInsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setManNumber(cellValue);
            return insuredDto;
        }

        @Override
        public GHInsuredDto.GHInsuredDependentDto populateInsuredDependentDetail(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setManNumber(cellValue);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getManNumber();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            return "";
        }
    }, NRC_NUMBER("NRC Number") {
        @Override
        public String getAllowedValue(GHInsuredDto insuredDto) {
            return insuredDto.getNrcNumber();
        }

        @Override
        public GHInsuredDto populateInsuredDetail(GHInsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setNrcNumber(cellValue);
            return insuredDto;
        }

        @Override
        public GHInsuredDto.GHInsuredDependentDto populateInsuredDependentDetail(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setNrcNumber(cellValue);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
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
    },
    SALUTATION("Salutation") {
        @Override
        public String getAllowedValue(GHInsuredDto insuredDto) {
            return insuredDto.getSalutation();
        }

        @Override
        public GHInsuredDto populateInsuredDetail(GHInsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setSalutation(cellValue);
            return insuredDto;
        }

        @Override
        public GHInsuredDto.GHInsuredDependentDto populateInsuredDependentDetail(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setSalutation(cellValue);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getSalutation();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            return "";
        }
    }, FIRST_NAME("First Name") {
        @Override
        public String getAllowedValue(GHInsuredDto insuredDto) {
            return insuredDto.getFirstName();
        }

        @Override
        public GHInsuredDto populateInsuredDetail(GHInsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setFirstName(cellValue);
            return insuredDto;
        }

        @Override
        public GHInsuredDto.GHInsuredDependentDto populateInsuredDependentDetail(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setFirstName(cellValue);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getFirstName();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            return "";
        }
    }, LAST_NAME("Last Name") {
        @Override
        public String getAllowedValue(GHInsuredDto insuredDto) {
            return insuredDto.getLastName();
        }

        @Override
        public GHInsuredDto populateInsuredDetail(GHInsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setLastName(cellValue);
            return insuredDto;
        }

        @Override
        public GHInsuredDto.GHInsuredDependentDto populateInsuredDependentDetail(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setLastName(cellValue);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getLastName();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            return "";
        }
    }, DATE_OF_BIRTH("Date of Birth") {
        @Override
        public String getAllowedValue(GHInsuredDto insuredDto) {
            return AppUtils.toString(insuredDto.getDateOfBirth());
        }

        @Override
        public GHInsuredDto populateInsuredDetail(GHInsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setDateOfBirth(isNotEmpty(cellValue) ? LocalDate.parse(cellValue, DateTimeFormat.forPattern(AppConstants.DD_MM_YYY_FORMAT)) : null);
            return insuredDto;
        }

        @Override
        public GHInsuredDto.GHInsuredDependentDto populateInsuredDependentDetail(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setDateOfBirth(isNotEmpty(cellValue) ? LocalDate.parse(cellValue, DateTimeFormat.forPattern(AppConstants.DD_MM_YYY_FORMAT)) : null);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
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
                return errorMessage;
            }
            Cell minimumAgeCell = row.getCell(excelHeaders.indexOf(MIN_ENTRY_AGE.name()));
            String minimumAgeCellValue = getCellValue(minimumAgeCell);
            Cell maximumAgeCell = row.getCell(excelHeaders.indexOf(MAX_ENTRY_AGE.name()));
            String maximumAgeCellValue = getCellValue(maximumAgeCell);
            int age = AppUtils.getAgeOnNextBirthDate(LocalDate.parse(value, DateTimeFormat.forPattern(AppConstants.DD_MM_YYY_FORMAT)));
            if (isNotEmpty(minimumAgeCellValue) && isNotEmpty(maximumAgeCellValue) && isEmpty(MIN_ENTRY_AGE.validateAndIfNotBuildErrorMessage(planAdapter, row, minimumAgeCellValue, excelHeaders)) && isEmpty(MAX_ENTRY_AGE.validateAndIfNotBuildErrorMessage(planAdapter, row, maximumAgeCellValue, excelHeaders))) {
                int minimumAge = Double.valueOf(minimumAgeCellValue.trim()).intValue();
                int maximumAge = Double.valueOf(maximumAgeCellValue.trim()).intValue();
                if (!(age >= minimumAge && age <= maximumAge)) {
                    errorMessage = errorMessage + " Age should between " + minimumAgeCellValue + " and " + maximumAgeCellValue + ".";
                    return errorMessage;
                }
            } else {
                Cell planCell = row.getCell(excelHeaders.indexOf(PLAN.name()));
                String planCode = String.valueOf(Double.valueOf(getCellValue(planCell)).intValue());
                if (!planAdapter.isValidPlanAge(planCode, age)) {
                    errorMessage = errorMessage + " Age is not valid for plan " + planCode + ".";
                }
            }
            return errorMessage;
        }
    },
    GENDER("Gender") {
        @Override
        public String getAllowedValue(GHInsuredDto insuredDto) {
            return insuredDto.getGender() != null ? insuredDto.getGender().name() : "";
        }

        @Override
        public GHInsuredDto populateInsuredDetail(GHInsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setGender(isNotEmpty(cellValue) ? Gender.valueOf(cellValue) : null);
            return insuredDto;
        }

        @Override
        public GHInsuredDto.GHInsuredDependentDto populateInsuredDependentDetail(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setGender(isNotEmpty(cellValue) ? Gender.valueOf(cellValue) : null);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getGender()!=null?insuredDependentDto.getGender().name():"";
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
        public String getAllowedValue(GHInsuredDto insuredDto) {
            return insuredDto.getOccupationClass();
        }

        @Override
        public GHInsuredDto populateInsuredDetail(GHInsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setOccupationClass(cellValue);
            return insuredDto;
        }

        @Override
        public GHInsuredDto.GHInsuredDependentDto populateInsuredDependentDetail(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setOccupationClass(cellValue);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getOccupationClass();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            Cell relationshipCell = row.getCell(excelHeaders.indexOf(RELATIONSHIP.name()));
            String relationship = getCellValue(relationshipCell);
            Cell noOfSumAssuredCell = row.getCell(excelHeaders.indexOf(NO_OF_ASSURED.name()));
            String noOfSumAssured = getCellValue(noOfSumAssuredCell);
            if (isEmpty(relationship)) {
                errorMessage = errorMessage + "Relationship cannot be empty.";
            }
            if (isEmpty(noOfSumAssured) && isEmpty(value)) {
                errorMessage = errorMessage + "Occupation cannot be empty.";
            }
            return errorMessage;
        }
    }, CATEGORY("Category") {
        @Override
        public String getAllowedValue(GHInsuredDto insuredDto) {
            return insuredDto.getCategory();
        }

        @Override
        public GHInsuredDto populateInsuredDetail(GHInsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setOccupationCategory(cellValue);
            return insuredDto;
        }

        @Override
        public GHInsuredDto.GHInsuredDependentDto populateInsuredDependentDetail(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setOccupationCategory(cellValue);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getCategory();
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
        public String getAllowedValue(GHInsuredDto insuredDto) {
            return "Self";
        }

        @Override
        public GHInsuredDto populateInsuredDetail(GHInsuredDto insuredDto, Row row, List<String> headers) {
            return insuredDto;
        }

        @Override
        public GHInsuredDto.GHInsuredDependentDto populateInsuredDependentDetail(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setRelationship(Relationship.getRelationship(cellValue));
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
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
        public String getAllowedValue(GHInsuredDto insuredDto) {
            return insuredDto.getNoOfAssured() != null ? insuredDto.getNoOfAssured().toString() : "";
        }

        @Override
        public GHInsuredDto populateInsuredDetail(GHInsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setNoOfAssured(isNotEmpty(cellValue) ? Double.valueOf(cellValue).intValue() : null);
            return insuredDto;
        }

        @Override
        public GHInsuredDto.GHInsuredDependentDto populateInsuredDependentDetail(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setNoOfAssured(isNotEmpty(cellValue) ? Double.valueOf(cellValue).intValue() : null);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
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
        public String getAllowedValue(GHInsuredDto insuredDto) {
            return insuredDto.getPlanPremiumDetail().getPlanCode();
        }

        @Override
        public GHInsuredDto populateInsuredDetail(GHInsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            String planCode = null;
            try {
                planCode = String.valueOf(Double.valueOf(cellValue).intValue());
            } catch (Exception e) {
                planCode = cellValue;
            }
            GHInsuredDto.GHPlanPremiumDetailDto planPremiumDetailDto = insuredDto.getPlanPremiumDetail() == null ? new GHInsuredDto.GHPlanPremiumDetailDto() : insuredDto.getPlanPremiumDetail();
            planPremiumDetailDto.setPlanCode(planCode);
            insuredDto.setPlanPremiumDetail(planPremiumDetailDto);
            return insuredDto;
        }

        @Override
        public GHInsuredDto.GHInsuredDependentDto populateInsuredDependentDetail(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            String planCode = null;
            try {
                planCode = String.valueOf(Double.valueOf(cellValue).intValue());
            } catch (Exception e) {
                planCode = cellValue;
            }
            GHInsuredDto.GHPlanPremiumDetailDto planPremiumDetailDto = insuredDependentDto.getPlanPremiumDetail() != null ? insuredDependentDto.getPlanPremiumDetail() : new GHInsuredDto.GHPlanPremiumDetailDto();
            planPremiumDetailDto.setPlanCode(planCode);
            insuredDependentDto.setPlanPremiumDetail(planPremiumDetailDto);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
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
            boolean isValidPlanForGH = planAdapter.isValidPlanCodeForBusinessLine(planCode, LineOfBusinessEnum.GROUP_HEALTH);
            if (!isValidPlanForGH) {
                errorMessage = errorMessage + "Plan code is not a Group Health Plan.";
            }
            Cell relationshipCell = row.getCell(excelHeaders.indexOf(RELATIONSHIP.name()));
            String relationship = getCellValue(relationshipCell);
            boolean isValidPlanForRelationship = planAdapter.isValidPlanForRelationship(planCode, Relationship.getRelationship(relationship));
            if (!isValidPlanForRelationship) {
                errorMessage = errorMessage + "Plan code is not valid for the relationship " + relationship + ".";
            }
            return errorMessage;
        }
    },
    MIN_ENTRY_AGE("Minimum Entry Age") {
        @Override
        public String getAllowedValue(GHInsuredDto insuredDto) {
            return insuredDto.getMinAgeEntry() != null ? insuredDto.getMinAgeEntry().toString() : "";
        }

        @Override
        public GHInsuredDto populateInsuredDetail(GHInsuredDto insuredDto, Row row, List<String> headers) {
            Cell minAgeCell = row.getCell(headers.indexOf(this.getDescription()));
            String minAgeCellValue = getCellValue(minAgeCell);
            Integer minimumAge = isNotEmpty(minAgeCellValue) ? Double.valueOf(minAgeCellValue.trim()).intValue() : null;
            insuredDto.setMinAgeEntry(minimumAge);
            return insuredDto;
        }

        @Override
        public GHInsuredDto.GHInsuredDependentDto populateInsuredDependentDetail(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            Cell minAgeCell = row.getCell(headers.indexOf(this.getDescription()));
            String minAgeCellValue = getCellValue(minAgeCell);
            Integer minimumAge = isNotEmpty(minAgeCellValue) ? Double.valueOf(minAgeCellValue.trim()).intValue() : null;
            insuredDependentDto.setMaxAgeEntry(minimumAge);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getMinAgeEntry() != null ? insuredDependentDto.getMinAgeEntry().toString() : "";
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            if (isEmpty(value)) {
                return "";
            }
            String minAgeValue = String.valueOf(Double.valueOf(value.trim()).intValue());
            Pattern pattern = Pattern.compile("^[1-9]{1,2}");
            Matcher matcher = pattern.matcher(minAgeValue.trim());
            if (!matcher.find()) {
                return "Minimum Entry Age is not valid.";
            }
            return "";
        }
    }, MAX_ENTRY_AGE("Maximum Entry Age") {
        @Override
        public String getAllowedValue(GHInsuredDto insuredDto) {
            return insuredDto.getMaxAgeEntry() != null ? insuredDto.getMaxAgeEntry().toString() : "";
        }

        @Override
        public GHInsuredDto populateInsuredDetail(GHInsuredDto insuredDto, Row row, List<String> headers) {
            Cell maxAgeCell = row.getCell(headers.indexOf(this.getDescription()));
            String maxAgeCellValue = getCellValue(maxAgeCell);
            Integer maximumAge = isNotEmpty(maxAgeCellValue) ? Double.valueOf(maxAgeCellValue.trim()).intValue() : null;
            insuredDto.setMaxAgeEntry(maximumAge);
            return insuredDto;
        }

        @Override
        public GHInsuredDto.GHInsuredDependentDto populateInsuredDependentDetail(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            Cell maxAgeCell = row.getCell(headers.indexOf(this.getDescription()));
            String maxAgeCellValue = getCellValue(maxAgeCell);
            Integer maximumAge = isNotEmpty(maxAgeCellValue) ? Double.valueOf(maxAgeCellValue.trim()).intValue() : null;
            insuredDependentDto.setMaxAgeEntry(maximumAge);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getMaxAgeEntry() != null ? insuredDependentDto.getMaxAgeEntry().toString() : "";
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            if (isEmpty(value)) {
                return "";
            }
            String maxAgeValue = String.valueOf(Double.valueOf(value.trim()).intValue());
            Pattern pattern = Pattern.compile("^[1-9]{1,2}");
            Matcher matcher = pattern.matcher(maxAgeValue);
            if (!matcher.find()) {
                return "Maximum Entry Age is not valid.";
            }
            return "";
        }
    },
    ANNUAL_LIMIT("Annual Limit") {
        @Override
        public String getAllowedValue(GHInsuredDto insuredDto) {
            return insuredDto.getPlanPremiumDetail().getSumAssured() != null ? insuredDto.getPlanPremiumDetail().getSumAssured().toString() : "";
        }

        @Override
        public GHInsuredDto populateInsuredDetail(GHInsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            GHInsuredDto.GHPlanPremiumDetailDto planPremiumDetailDto = insuredDto.getPlanPremiumDetail() != null ? insuredDto.getPlanPremiumDetail() : new GHInsuredDto.GHPlanPremiumDetailDto();
            planPremiumDetailDto.setSumAssured(isNotEmpty(cellValue) ? BigDecimal.valueOf(Double.valueOf(cellValue).intValue()) : null);
            insuredDto.setPlanPremiumDetail(planPremiumDetailDto);
            return insuredDto;
        }

        @Override
        public GHInsuredDto.GHInsuredDependentDto populateInsuredDependentDetail(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            GHInsuredDto.GHPlanPremiumDetailDto planPremiumDetailDto = insuredDependentDto.getPlanPremiumDetail() != null ? insuredDependentDto.getPlanPremiumDetail() : new GHInsuredDto.GHPlanPremiumDetailDto();
            planPremiumDetailDto.setSumAssured(isNotEmpty(cellValue) ? BigDecimal.valueOf(Double.valueOf(cellValue).intValue()) : null);
            insuredDependentDto.setPlanPremiumDetail(planPremiumDetailDto);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getPlanPremiumDetail().getSumAssured() != null ? insuredDependentDto.getPlanPremiumDetail().getSumAssured().toString() : "";
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            String errorMessage = "";
            if (isEmpty(value)) {
                return "Annual Limit is blank.";
            }
            Cell planCell = row.getCell(excelHeaders.indexOf(PLAN.name()));
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
            boolean isValidSumAssured = isNotEmpty(value) ? planAdapter.isValidPlanSumAssured(planCode, BigDecimal.valueOf(Double.valueOf(value).intValue())) : true;
            if (!isValidSumAssured) {
                errorMessage = errorMessage + "Annual Limit is not valid for selected plan.";
            }
            if (isNotEmpty(value) && Double.valueOf(value) < 0) {
                errorMessage = errorMessage + "Annual limit cannot be negative.";
            }
            return errorMessage;
        }
    },
    PLAN_PREMIUM("Plan Premium") {
        @Override
        public String getAllowedValue(GHInsuredDto insuredDto) {
            String premium = insuredDto.getPlanPremiumDetail().getPremiumAmount() != null ? insuredDto.getPlanPremiumDetail().getPremiumAmount().toString() : "";
            if (insuredDto.getPlanPremiumDetail().getPremiumAmount() != null && insuredDto.getNoOfAssured() != null) {
                BigDecimal premiumAmount = insuredDto.getPlanPremiumDetail().getPremiumAmount().divide(new BigDecimal(insuredDto.getNoOfAssured()));
                premium = premiumAmount.toPlainString();
            }
            return premium;
        }

        @Override
        public GHInsuredDto populateInsuredDetail(GHInsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            Cell noOfAssuredCell = row.getCell(headers.indexOf(NO_OF_ASSURED.getDescription()));
            String noOfAssuredCellValue = getCellValue(noOfAssuredCell);
            BigDecimal planPremium = null;
            if (isNotEmpty(noOfAssuredCellValue) && isNotEmpty(cellValue)) {
                planPremium = BigDecimal.valueOf(Double.valueOf(noOfAssuredCellValue)).multiply(BigDecimal.valueOf(Double.valueOf(cellValue)));
            }
            GHInsuredDto.GHPlanPremiumDetailDto planPremiumDetailDto = insuredDto.getPlanPremiumDetail() != null ? insuredDto.getPlanPremiumDetail() : new GHInsuredDto.GHPlanPremiumDetailDto();
            planPremiumDetailDto.setPremiumAmount(planPremium);
            insuredDto.setPlanPremiumDetail(planPremiumDetailDto);
            return insuredDto;
        }

        @Override
        public GHInsuredDto.GHInsuredDependentDto populateInsuredDependentDetail(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            Cell noOfAssuredCell = row.getCell(headers.indexOf(NO_OF_ASSURED.getDescription()));
            String noOfAssuredCellValue = getCellValue(noOfAssuredCell);
            BigDecimal planPremium = null;
            if (isNotEmpty(noOfAssuredCellValue) && isNotEmpty(cellValue)) {
                planPremium = BigDecimal.valueOf(Double.valueOf(noOfAssuredCellValue)).multiply(BigDecimal.valueOf(Double.valueOf(cellValue)));
            }
            GHInsuredDto.GHPlanPremiumDetailDto planPremiumDetailDto = insuredDependentDto.getPlanPremiumDetail() != null ? insuredDependentDto.getPlanPremiumDetail() : new GHInsuredDto.GHPlanPremiumDetailDto();
            planPremiumDetailDto.setPremiumAmount(planPremium);
            insuredDependentDto.setPlanPremiumDetail(planPremiumDetailDto);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
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
    }, PRE_EXISTING_ILLNESS("Pre-Existing Illness") {
        @Override
        public String getAllowedValue(GHInsuredDto insuredDto) {
            return "";
        }

        @Override
        public GHInsuredDto populateInsuredDetail(GHInsuredDto insuredDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDto.setExistingIllness(cellValue);
            return insuredDto;
        }

        @Override
        public GHInsuredDto.GHInsuredDependentDto populateInsuredDependentDetail(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, Row row, List<String> headers) {
            int cellNumber = headers.indexOf(this.getDescription());
            Cell cell = row.getCell(cellNumber);
            String cellValue = getCellValue(cell);
            insuredDependentDto.setExistingIllness(cellValue);
            return insuredDependentDto;
        }

        @Override
        public String getAllowedValue(GHInsuredDto.GHInsuredDependentDto insuredDependentDto) {
            return insuredDependentDto.getExistingIllness();
        }

        @Override
        public String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders) {
            return "";
        }
    };

    private String description;

    GHInsuredExcelHeader(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static List<String> getAllHeader() {
        List<String> headers = Lists.newArrayList();
        for (GHInsuredExcelHeader ghInsuredExcelHeader : GHInsuredExcelHeader.values()) {
            headers.add(ghInsuredExcelHeader.getDescription());
        }
        return headers;
    }

    public static List<String> getAllHeaderForParser() {
        List<String> headers = Lists.newArrayList();
        for (GHInsuredExcelHeader glInsuredExcelHeader : GHInsuredExcelHeader.values()) {
            headers.add(glInsuredExcelHeader.name());
        }
        return headers;
    }


    public static List<String> getAllowedHeaders(IPlanAdapter planAdapter, List<PlanId> planIds) {
        List<PlanCoverageDetailDto> planCoverageDetailDtoList = planAdapter.getPlanAndCoverageDetail(planIds);
        List<String> headers = GHInsuredExcelHeader.getAllHeader();
        int count = 1;
        for (PlanCoverageDetailDto planCoverageDetailDto : planCoverageDetailDtoList) {
            for (PlanCoverageDetailDto.CoverageDto coverageDto : planCoverageDetailDto.getCoverageDtoList()) {
                headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count));
                headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.OPTIONAL_COVERAGE_SA_HEADER);
                headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.PREMIUM_CELL_HEADER_NAME);
                headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.OPTIONAL_COVERAGE_PREMIUM_VISIBILITY_HEADER);
                int benefitCount = 1;
                for (PlanCoverageDetailDto.BenefitDto benefitDto : coverageDto.getBenefits()) {
                    headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + (AppConstants.OPTIONAL_COVERAGE_BENEFIT_HEADER + benefitCount));
                    headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + (AppConstants.OPTIONAL_COVERAGE_BENEFIT_HEADER + benefitCount + " Limit"));
                    benefitCount++;
                }
                count++;
            }
        }

        return ImmutableList.copyOf(headers);
    }

    public static List<String> getAllowedHeaderForParser(IPlanAdapter planAdapter, List<PlanId> planIds) {
        List<PlanCoverageDetailDto> planCoverageDetailDtoList = planAdapter.getPlanAndCoverageDetail(planIds);
        List<String> headers = GHInsuredExcelHeader.getAllHeaderForParser();
        int count = 1;
        for (PlanCoverageDetailDto planCoverageDetailDto : planCoverageDetailDtoList) {
            for (PlanCoverageDetailDto.CoverageDto coverageDto : planCoverageDetailDto.getCoverageDtoList()) {
                headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count));
                headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.OPTIONAL_COVERAGE_SA_HEADER);
                headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.PREMIUM_CELL_HEADER_NAME);
                headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + AppConstants.OPTIONAL_COVERAGE_PREMIUM_VISIBILITY_HEADER);
                int benefitCount = 1;
                for (PlanCoverageDetailDto.BenefitDto benefitDto : coverageDto.getBenefits()) {
                    headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + (AppConstants.OPTIONAL_COVERAGE_BENEFIT_HEADER + benefitCount));
                    headers.add((AppConstants.OPTIONAL_COVERAGE_HEADER + count) + " " + (AppConstants.OPTIONAL_COVERAGE_BENEFIT_HEADER + benefitCount + " Limit"));
                    benefitCount++;
                }
                count++;
            }
        }
        return ImmutableList.copyOf(headers);
    }

    public abstract String getAllowedValue(GHInsuredDto insuredDto);

    public abstract GHInsuredDto populateInsuredDetail(GHInsuredDto insuredDto, Row row, List<String> headers);

    public abstract GHInsuredDto.GHInsuredDependentDto populateInsuredDependentDetail(GHInsuredDto.GHInsuredDependentDto insuredDependentDto, Row row, List<String> headers);

    public abstract String getAllowedValue(GHInsuredDto.GHInsuredDependentDto insuredDependentDto);

    public abstract String validateAndIfNotBuildErrorMessage(IPlanAdapter planAdapter, Row row, String value, List<String> excelHeaders);

}
