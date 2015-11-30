package com.pla.grouplife.endorsement.application.service;

import com.pla.grouplife.endorsement.domain.model.GroupLifeEndorsement;
import com.pla.grouplife.endorsement.query.GLEndorsementFinder;
import com.pla.grouplife.policy.query.GLPolicyFinder;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.InsuredDependent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.ToLongFunction;

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


}
