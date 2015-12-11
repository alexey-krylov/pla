package com.pla.grouplife.endorsement.application.service;

import com.pla.grouplife.endorsement.domain.model.GroupLifeEndorsement;
import com.pla.grouplife.endorsement.query.GLEndorsementFinder;
import com.pla.grouplife.policy.query.GLPolicyFinder;
import com.pla.grouplife.sharedresource.model.GLEndorsementExcelHeader;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.InsuredDependent;
import com.pla.sharedkernel.domain.model.Relationship;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 30-Nov-15.
 */
@Service
public class GroupLifeEndorsementChecker {

    private GLEndorsementFinder glEndorsementFinder;

    private GLPolicyFinder glPolicyFinder;


    @Autowired
    GroupLifeEndorsementChecker(GLEndorsementFinder glEndorsementFinder,GLPolicyFinder glPolicyFinder){
        this.glEndorsementFinder = glEndorsementFinder;
        this.glPolicyFinder =  glPolicyFinder;
    }

    public Boolean isValidMemberDeletion(String policyNumber){
        List<GroupLifeEndorsement> groupLifeEndorsementList =  glEndorsementFinder.findEndorsementByPolicyNumber(policyNumber);
        Map<String,Object> policyMap = glPolicyFinder.findProposalIdByPolicyNumber(policyNumber);
        if (isNotEmpty(groupLifeEndorsementList)){
            Long noOfEndorsedInsured = groupLifeEndorsementList.parallelStream().mapToLong(new ToLongFunctionTransformer()).sum();
            Long noOfInsuredTookPolicy  =0l;
            if (isNotEmpty(policyMap)){
                noOfInsuredTookPolicy =  policyMap.entrySet().parallelStream().mapToLong(new ToLongFunction<Map.Entry<String, Object>>() {
                    @Override
                    public long applyAsLong(Map.Entry<String, Object> policyMap) {
                        if ("insureds".equals(policyMap.getKey() != null ? policyMap.getKey() : "")) {
                            List<Insured> insureds = (List<Insured>) policyMap.getValue();
                            if (isNotEmpty(insureds)) {
                                return insureds.parallelStream().mapToLong(new ToLongFunctionInsuredTransformer()).sum();
                            }
                        }
                        return 0;
                    }
                }).sum();
            }
            return noOfInsuredTookPolicy.compareTo(noOfEndorsedInsured)>=0;
        }
        return true;
    }


    public Boolean isValidMemberAddition(String policyNumber){

        return true;
    }

    public Boolean isValidMemberPromotion(String policyNumber){

        return true;
    }

    private class ToLongFunctionTransformer implements ToLongFunction<GroupLifeEndorsement> {
        @Override
        public long applyAsLong(GroupLifeEndorsement groupLifeEndorsement) {
            if (groupLifeEndorsement.getEndorsementType().equals(GLEndorsementType.ASSURED_MEMBER_ADDITION)) {
                Set<Insured> insureds = groupLifeEndorsement.getEndorsement().getMemberEndorsement().getInsureds();
                return insureds.parallelStream().mapToLong(new ToLongFunctionInsuredTransformer()).sum();
            }
            else if (groupLifeEndorsement.getEndorsementType().equals(GLEndorsementType.ASSURED_MEMBER_DELETION)) {
                Set<Insured> insureds = groupLifeEndorsement.getEndorsement().getMemberDeletionEndorsements().getInsureds();
                return (-(insureds.parallelStream().mapToLong(new ToLongFunctionInsuredTransformer()).sum()));
            }
            else if (groupLifeEndorsement.getEndorsementType().equals(GLEndorsementType.NEW_CATEGORY_RELATION)) {
                Set<Insured> insureds = groupLifeEndorsement.getEndorsement().getNewCategoryRelationEndorsement().getInsureds();
                return insureds.parallelStream().mapToLong(new ToLongFunctionInsuredTransformer()).sum();
            }
            return 0;
        }
    }

    private class ToLongFunctionInsuredTransformer implements ToLongFunction<Insured> {
        @Override
        public long applyAsLong(Insured insured) {
            long noOfInsured = insured.getNoOfAssured() != null ? insured.getNoOfAssured() : insured.getFirstName() != null ? 1 : 0;
            Set<InsuredDependent> insuredDependents = insured.getInsuredDependents();
            if (isNotEmpty(insuredDependents)) {
                long noOfInsuredDependent = insuredDependents.parallelStream().mapToLong(new ToLongFunction<InsuredDependent>() {
                    @Override
                    public long applyAsLong(InsuredDependent dependent) {
                        return dependent.getNoOfAssured() != null ? dependent.getNoOfAssured() : dependent.getFirstName() != null ? 1 : 0;
                    }
                }).sum();
                noOfInsured = noOfInsured + noOfInsuredDependent;
            }
            return noOfInsured;
        }
    }

    public List<Insured> getNewCategoryAndRelationInsuredDetail(List<Insured> insureds,String  policyNumber){
        List<GroupLifeEndorsement> groupLifeEndorsements = glEndorsementFinder.findEndorsementByPolicyNumber(policyNumber);
        for (GroupLifeEndorsement groupLifeEndorsement : groupLifeEndorsements) {
            if (groupLifeEndorsement.getEndorsementType().equals(GLEndorsementType.NEW_CATEGORY_RELATION)) {
                Set<Insured> newCategoryInsureds = groupLifeEndorsement.getEndorsement().getNewCategoryRelationEndorsement().getInsureds();
                for (Insured insured : newCategoryInsureds){
                    insureds.add(insured);
                }
            }
        }
        return insureds;
    }

    public String getTotalNoOfLivesCovered(Row currentRow,List<Insured> insureds,List<String> headers){
        Cell categoryCell =  currentRow.getCell(headers.indexOf(GLEndorsementExcelHeader.CATEGORY.getDescription()));
        String category = getCellValue(categoryCell);
        Cell relationshipCell =  currentRow.getCell(headers.indexOf(GLEndorsementExcelHeader.RELATIONSHIP.getDescription()));
        String relationship = getCellValue(relationshipCell);
        Cell noOfAssured =  currentRow.getCell(headers.indexOf(GLEndorsementExcelHeader.NO_OF_ASSURED.getDescription()));
        String noOfAssuredCellValue = getCellValue(noOfAssured);
        Cell firstNameCell = currentRow.getCell(headers.indexOf(GLEndorsementExcelHeader.FIRST_NAME.getDescription()));
        String  firstName = getCellValue(firstNameCell);
        Cell nrcCellValue = currentRow.getCell(headers.indexOf(GLEndorsementExcelHeader.NRC_NUMBER.getDescription()));
        String nrc = getCellValue(nrcCellValue);
        Cell dobCellValue = currentRow.getCell(headers.indexOf(GLEndorsementExcelHeader.DATE_OF_BIRTH.getDescription()));
        String dob = getCellValue(dobCellValue);
        Cell genderCellValue = currentRow.getCell(headers.indexOf(GLEndorsementExcelHeader.GENDER.getDescription()));
        String  gender = getCellValue(genderCellValue);
        Integer noOfLivesCovered = 0;
        if (isNotEmpty(insureds)){
            noOfLivesCovered =  insureds.parallelStream().filter(new Predicate<Insured>() {
                @Override
                public boolean test(Insured insured) {
                    return insured.getNoOfAssured()==null?insured.getCategory()!=null?insured.getCategory().equals(category):false && Relationship.SELF.description.equals(relationship):
                            insured.getCategory()!=null? insured.getCategory().equals(category):false && Relationship.SELF.description.equals(relationship) && firstName.equals(insured.getFirstName()!=null?insured.getFirstName():false)
                            && nrc.equals(insured.getNrcNumber()!=null?insured.getNrcNumber():false) && dob.equals(insured.getDateOfBirth()!=null?insured.getDateOfBirth().toString(AppConstants.DD_MM_YYY_FORMAT):false) &&
                            gender.equals(insured.getGender()!=null?insured.getGender().name():false);
                }
            }).mapToInt(new ToIntFunction<Insured>() {
                @Override
                public int applyAsInt(Insured value) {
                    Integer assuredSize = value.getNoOfAssured() != null ? value.getNoOfAssured() : value.getCategory() != null ? 1 : 0;
                    Integer dependentSize = 0;
                    if (isNotEmpty(value.getInsuredDependents())) {
                        dependentSize=  value.getInsuredDependents().parallelStream().filter(new Predicate<InsuredDependent>() {
                            @Override
                            public boolean test(InsuredDependent insuredDependent) {
                                return insuredDependent.getNoOfAssured()==null?insuredDependent.getCategory()!=null?insuredDependent.getCategory().equals(category):false && Relationship.SELF.description.equals(relationship):
                                        insuredDependent.getCategory()!=null? insuredDependent.getCategory().equals(category):false && Relationship.SELF.description.equals(relationship) && firstName.equals(insuredDependent.getFirstName()!=null?insuredDependent.getFirstName():false)
                                                && nrc.equals(insuredDependent.getNrcNumber()!=null?insuredDependent.getNrcNumber():false) && dob.equals(insuredDependent.getDateOfBirth()!=null?insuredDependent.getDateOfBirth().toString(AppConstants.DD_MM_YYY_FORMAT):false) &&
                                                gender.equals(insuredDependent.getGender()!=null?insuredDependent.getGender().name():false);
                            }
                        }).mapToInt(new ToIntFunction<InsuredDependent>() {
                            @Override
                            public int applyAsInt(InsuredDependent value) {
                                return value.getNoOfAssured() != null ? value.getNoOfAssured() : value.getCategory() != null ? 1 : 0;
                            }
                        }).sum();
                    }
                    return assuredSize+dependentSize;
                }
            }).sum();
            if (isNotEmpty(noOfAssuredCellValue)){
                return noOfLivesCovered.compareTo(Integer.valueOf(new BigDecimal(noOfAssuredCellValue).intValue()))>=0?"":" The number Of assured does not covered under the policy";
            }
        }
        return "";
    }
}
