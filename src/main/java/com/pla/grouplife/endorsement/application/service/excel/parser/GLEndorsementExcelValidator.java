package com.pla.grouplife.endorsement.application.service.excel.parser;

import com.google.common.collect.Lists;
import com.pla.grouplife.sharedresource.model.GLEndorsementExcelHeader;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.InsuredDependent;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PolicyId;
import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.nthdimenzion.common.AppConstants;
import org.nthdimenzion.presentation.AppUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
import static org.nthdimenzion.utils.UtilValidator.*;


/**
 * Created by Samir on 8/19/2015.
 */

@Getter
public class GLEndorsementExcelValidator {

    private PolicyId policyId;

    private List<Insured> policyAssureds;

    private IPlanAdapter planAdapter;

    public GLEndorsementExcelValidator(PolicyId policyId, List<Insured> policyAssureds, IPlanAdapter planAdapter) {
        this.policyId = policyId;
        this.policyAssureds = policyAssureds;
        this.planAdapter = planAdapter;
    }


    public boolean isValidCategory(Row row, String value, List<String> excelHeaders) {
        if (isEmpty(value)) {
            return true;
        }
        List<String> categoriesExistInPolicy = Lists.newArrayList();
        policyAssureds.forEach(insured -> {
            if (isNotEmpty(insured.getCategory())) {
                categoriesExistInPolicy.add(insured.getCategory());
            }
            insured.getInsuredDependents().forEach(insuredDependent -> {
                if (isNotEmpty(insured.getCategory())) {
                    categoriesExistInPolicy.add(insuredDependent.getCategory());
                }
            });
        });
        return categoriesExistInPolicy.contains(value);
    }


    public boolean isValidRelationship(Row row, String value, List<String> excelHeaders) {
        if (isEmpty(value)) {
            return false;
        }
        List<String> relationsExistInPolicy = Lists.newArrayList();
        policyAssureds.forEach(insured -> {
            relationsExistInPolicy.add("Self");
            insured.getInsuredDependents().forEach(insuredDependent -> {
                relationsExistInPolicy.add(insuredDependent.getRelationship().description);
            });
        });
        return relationsExistInPolicy.contains(value);
    }


    public boolean isValidNumberOfAssured(Row row, String value, List<String> excelHeaders) {
        Cell relationshipCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.RELATIONSHIP.getDescription()));
        String relationShipValue = getCellValue(relationshipCell);
        if (isEmpty(relationShipValue) && isEmpty(value)) {
            return false;
        }
        if (isNotEmpty(relationShipValue) && isEmpty(value)) {
            return true;
        }
        return (isNotEmpty(value) && Double.valueOf(value).doubleValue() > 0d);
    }

    public boolean isValidMainAssuredClientId(Row row, String value, List<String> excelHeaders) {
        Cell relationshipCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.RELATIONSHIP.getDescription()));
        String relationShipValue = getCellValue(relationshipCell);
        if ("Self".equals(relationShipValue) && isNotEmpty(value)) {
            return false;
        }
        if ("Self".equals(relationShipValue) && isEmpty(value)) {
            return true;
        }
        return isValidClientId(policyAssureds, value);
    }

    public boolean isValidMANNumber(Row row, String value, List<String> excelHeaders) {
        return true;
    }


    public boolean isValidNRCNumber(Row row, String value, List<String> excelHeaders) {
        if (isEmpty(value)) {
            return true;
        }
        return (isNotEmpty(value) && isValidNrcNumber(value));
    }


    public boolean isValidAnnualIncome(Row row, String value, List<String> excelHeaders) {
        if (isEmpty(value)) {
            return true;
        }
        Cell relationshipCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.RELATIONSHIP.getDescription()));
        String relation = getCellValue(relationshipCell);
        Cell categoryCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.CATEGORY.getDescription()));
        String category = getCellValue(categoryCell);
        String planCode = findPlanCodeByRelationAndCategory(this.getPolicyAssureds(), category, relation);
        boolean isIncomeMultiplierPlan = planAdapter.hasPlanContainsIncomeMultiplierSumAssured(planCode);
        Cell annualIncomeCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.ANNUAL_INCOME.getDescription()));
        String annualIncome = getCellValue(annualIncomeCell);
        boolean isValid = true;
        if (isEmpty(annualIncome) && isIncomeMultiplierPlan) {
            isValid = false;
            return isValid;
        }
        try {
            Double valueInDouble = Double.parseDouble(annualIncome);
            isValid = valueInDouble > 0d;
        } catch (Exception e) {
            isValid = false;
        }
        return isValid;


    }


    public boolean isValidSalutation(Row row, String value, List<String> excelHeaders) {
        return true;
    }


    public String isValidFirstName(Row row, String value, List<String> excelHeaders) {
        Cell noOfAssuredCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.NO_OF_ASSURED.getDescription()));
        String noOfAssured = getCellValue(noOfAssuredCell);
        if (isEmpty(noOfAssured) && isEmpty(value)){
            return "First Name cannot be empty";
        }
        if (isNotEmpty(noOfAssured)){
            return "";
        }
        return "";
    }


    public boolean isValidLastName(Row row, String value, List<String> excelHeaders) {
        return true;
    }


    public String isValidDateOfBirth(Row row, String value, List<String> excelHeaders) {
        String errorMessage = "";
        Cell noOfAssuredCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.NO_OF_ASSURED.getDescription()));
        String noOfAssured = getCellValue(noOfAssuredCell);
        if (isEmpty(noOfAssured) && isEmpty(value)){
            return "Date Of birth cannot be empty";
        }
        if (isNotEmpty(noOfAssured)){
            return "";
        }
        Cell relationshipCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.RELATIONSHIP.getDescription()));
        String relation = getCellValue(relationshipCell);
        Cell categoryCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.CATEGORY.getDescription()));
        String category = getCellValue(categoryCell);
        String planCode = findPlanCodeByRelationAndCategory(this.getPolicyAssureds(),category,relation);
        int age = AppUtils.getAgeOnNextBirthDate(LocalDate.parse(value, DateTimeFormat.forPattern(AppConstants.DD_MM_YYY_FORMAT)));
        if (!planAdapter.isValidPlanAge(planCode, age)) {
            errorMessage = errorMessage + " Age is not valid for plan " + planCode + ".";
        }
        return errorMessage;
    }

    public boolean isValidGender(Row row, String value, List<String> excelHeaders) {
        Cell noOfSumAssuredCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.NO_OF_ASSURED.getDescription()));
        String noOfSumAssured = getCellValue(noOfSumAssuredCell);
        if (isNotEmpty(noOfSumAssured) && isEmpty(value)) {
            return true;
        }
        try {
            Gender.valueOf(value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    public boolean isValidOccupation(Row row, String value, List<String> excelHeaders) {
        return true;
    }

    public boolean isValidProposerName(Row row, String value, List<String> excelHeaders) {
        return true;
    }

    public boolean isValidClientId(Row row, String value, List<String> excelHeaders) {
        return isValidClientId(policyAssureds, value);
    }

    public boolean isValidNewAnnualIncome(Row row, String value, List<String> excelHeaders) {
        Cell oldAnnualIncomeCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.OLD_ANNUAL_INCOME.getDescription()));
        String oldIncome = getCellValue(oldAnnualIncomeCell);
        String oldAnnualIncome = oldIncome;
        if (isNotEmpty(value)) {
            oldAnnualIncome = String.valueOf(new BigDecimal(oldIncome).setScale(0, BigDecimal.ROUND_FLOOR));
        }
        if (isEmpty(value)) {
            return false;
        }
        return !(value.equals(oldAnnualIncome));
    }

    public boolean isValidOldAnnualIncome(Row row, String value, List<String> excelHeaders) {
        Cell clientIdCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.CLIENT_ID.getDescription()));
        String clientId = getCellValue(clientIdCell);
        if (!isValidClientId(policyAssureds, clientId)) {
            return false;
        }
        if (isEmpty(value)) {
            return false;
        }
        BigDecimal existingAnnualIncome = null;
        Optional<Insured> insuredOptional = policyAssureds.stream().filter(policyAssured -> (policyAssured.getFamilyId() != null && clientId.equals(policyAssured.getFamilyId().getFamilyId()))).findAny();
        if (insuredOptional.isPresent()) {
            existingAnnualIncome = insuredOptional.get().getAnnualIncome();
        }
        BigDecimal newAnnualIncome = BigDecimal.valueOf(Double.valueOf(value));
        return newAnnualIncome.compareTo(existingAnnualIncome) == 0;
    }

    public boolean isValidNewCategory(Row row, String value, List<String> excelHeaders) {
        Cell oldAnnualIncomeCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.OLD_CATEGORY.getDescription()));
        String oldCategory = getCellValue(oldAnnualIncomeCell);
        if (isEmpty(value)) {
            return false;
        }
        if (value.equals(oldCategory)) {
            return false;
        }
        List<String> categoriesExistInPolicy = Lists.newArrayList();
        policyAssureds.forEach(insured -> {
            categoriesExistInPolicy.add(insured.getCategory());
            insured.getInsuredDependents().forEach(insuredDependent -> {
                categoriesExistInPolicy.add(insuredDependent.getCategory());
            });
        });
        return categoriesExistInPolicy.contains(value);
    }

    public boolean isValidOldCategory(Row row, String value, List<String> excelHeaders) {
        List<String> categoriesExistInPolicy = Lists.newArrayList();
        policyAssureds.forEach(insured -> {
            categoriesExistInPolicy.add(insured.getCategory());
            insured.getInsuredDependents().forEach(insuredDependent -> {
                categoriesExistInPolicy.add(insuredDependent.getCategory());
            });
        });
        return categoriesExistInPolicy.contains(value);
    }

    public boolean isValidPlanPremium(Row row, String value, List<String> excelHeaders) {
        Cell noOfSumAssuredCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.NO_OF_ASSURED.getDescription()));
        String noOfSumAssured = getCellValue(noOfSumAssuredCell);
        if (isNotEmpty(noOfSumAssured) && isEmpty(value)) {
            return false;
        }
        if (isNotEmpty(value) && Double.valueOf(value) < 0) {
            return false;
        }
        return true;
    }

    public boolean isValidSumAssured(Row row, String value, List<String> excelHeaders) {
        return false;
    }

    public boolean isValidIncomeMultiplier(Row row, String value, List<String> excelHeaders) {
        Cell planCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.PLAN.getDescription()));
        Cell relationshipCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.RELATIONSHIP.getDescription()));
        String relationship = getCellValue(relationshipCell);
        String planCode = getCellValue(planCell);
        if (isEmpty(planCode)) {
            return false;
        }
        try {
            planCode = String.valueOf(Double.valueOf(planCode).intValue());
        } catch (Exception e) {
        }
        boolean isValidPlan = isValidPlan(row, planCode, excelHeaders);
        if (!isValidPlan) {
            return false;
        }
        boolean hasPlanIncomeMultiplierSumAssuredType = planAdapter.hasPlanContainsIncomeMultiplierSumAssured(planCode);
        if (hasPlanIncomeMultiplierSumAssuredType && isEmpty(value) && Relationship.SELF.description.equals(relationship.trim())) {
            return false;
        }
        return true;
    }

    public boolean isValidPlan(Row row, String value, List<String> excelHeaders) {
        if (isEmpty(value)) {
            return false;
        }
        String planCode = null;
        try {
            planCode = String.valueOf(Double.valueOf(value).intValue());
        } catch (Exception e) {
            planCode = value;
        }
        boolean isValidPlan = planAdapter.isValidPlanCode(planCode);
        if (!isValidPlan) {
            return false;
        }
        boolean isValidPlanForGL = planAdapter.isValidPlanCodeForBusinessLine(planCode, LineOfBusinessEnum.GROUP_LIFE);
        if (!isValidPlanForGL) {
            return false;
        }
        Cell relationshipCell = row.getCell(excelHeaders.indexOf(GLEndorsementExcelHeader.RELATIONSHIP.getDescription()));
        String relationship = getCellValue(relationshipCell);
        boolean isValidPlanForRelationship = planAdapter.isValidPlanForRelationship(planCode, Relationship.getRelationship(relationship));
        if (!isValidPlanForRelationship) {
            return false;
        }
        return true;
    }

    protected boolean isValidClientId(List<Insured> insureds, String clientId) {
        final boolean[] isValidClientId = {insureds.stream().filter(insured -> (insured.getFamilyId() != null && clientId.equals(insured.getFamilyId().getFamilyId()))).findAny().isPresent()};
        if (isValidClientId[0]) {
            return isValidClientId[0];
        }

        boolean isValidDependentClientId = false;
        for (Insured insured : insureds){
            if (isNotEmpty(insured.getInsuredDependents())){
                Optional<InsuredDependent> dependentOptional =  insured.getInsuredDependents().parallelStream().filter(new Predicate<InsuredDependent>() {
                    @Override
                    public boolean test(InsuredDependent insuredDependent) {
                        return (insuredDependent.getFamilyId()!=null && clientId.equals(insuredDependent.getFamilyId().getFamilyId()));
                    }
                }).findAny();
                if (dependentOptional.isPresent()) {
                    isValidDependentClientId = true;
                    break;
                }
            }
        }
        return isValidDependentClientId;
    }


    private String findPlanCodeByRelationAndCategory(List<Insured> insureds,String category,String relation){
        String planCode = "";
        if (isNotEmpty(insureds)){
            Optional<Insured> insuredOptional = insureds.parallelStream().filter(insured -> insured.getCategory().equals(category) && Relationship.SELF.description.equals(relation)).findAny();
            if (insuredOptional.isPresent()){
                PlanId planId = insuredOptional.get().getPlanPremiumDetail().getPlanId();
                planCode = planAdapter.getPlanCodeById(planId);
            }else {
                for (Insured insured : insureds) {
                    if (isNotEmpty(insured.getInsuredDependents())) {
                        Optional<InsuredDependent> dependentOptional = insured.getInsuredDependents().parallelStream().filter(dependent -> dependent.getCategory().equals(category) && dependent.getRelationship().description.equals(relation)).findAny();
                        if (dependentOptional.isPresent()) {
                            PlanId planId=  dependentOptional.get().getPlanPremiumDetail().getPlanId();
                            planCode = planAdapter.getPlanCodeById(planId);
                        }
                    }
                }
            }
        }
        return planCode;
    }
}
