package com.pla.individuallife.sharedresource.query;

import com.pla.individuallife.sharedresource.dto.ILClientDetailDto;
import com.pla.individuallife.sharedresource.model.vo.EmploymentDetail;
import com.pla.individuallife.sharedresource.model.vo.ProposedAssured;
import com.pla.individuallife.sharedresource.model.vo.Proposer;
import com.pla.individuallife.sharedresource.model.vo.ResidentialAddress;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.MaritalStatus;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 26-Nov-15.
 */
@Service
public class ILClientFinder {

    private MongoTemplate mongoTemplate;

    private static final String IL_POLICY_COLLECTION_NAME = "individual_life_policy";

    @Autowired
    public void setDataSource( MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }


    public ILClientDetailDto findClientDetailByClientId(String clientId){
        List<Map> policies = findAllPolicy();
        if (isNotEmpty(policies)){
            Optional<Map> policyOptional  = policies.parallelStream().filter(new Predicate<Map>() {
                @Override
                public boolean test(Map policyMap) {
                    Proposer proposer = policyMap.get("proposer") != null ? (Proposer) policyMap.get("proposer") : null;
                    boolean isSameClientId =  proposer.getClientId().equals(clientId);
                    if (!proposer.getIsProposedAssured() && !isSameClientId) {
                        ProposedAssured proposedAssured = policyMap.get("proposedAssured") != null ? (ProposedAssured) policyMap.get("proposedAssured") : null;
                        isSameClientId =   proposedAssured.getClientId().equals(clientId);
                    }
                    return isSameClientId;
                }
            }).findAny();
            if (policyOptional.isPresent()){
                Map policy  = policyOptional.get();
                Proposer proposer = policy.get("proposer") != null ? (Proposer) policy.get("proposer") : null;
                if (proposer.getClientId().equals(clientId)){
                    return populateClientDetail(proposer.getTitle(),proposer.getFirstName(),proposer.getSurname(),
                            proposer.getOtherName(),proposer.getNrc(),proposer.getDateOfBirth(),proposer.getGender(),proposer.getMobileNumber(),
                            proposer.getEmailAddress(),proposer.getMaritalStatus(),proposer.getSpouseFirstName(),proposer.getSpouseLastName(),
                            proposer.getSpouseEmailAddress(),
                            proposer.getSpouseMobileNumber(),proposer.getEmploymentDetail(),proposer.getResidentialAddress());
                }

                else if (!proposer.getIsProposedAssured()){
                    ProposedAssured proposedAssured = policy.get("proposedAssured") != null ? (ProposedAssured) policy.get("proposedAssured") : null;
                    if (proposedAssured.getClientId().equals(clientId)){
                        return populateClientDetail(proposedAssured.getTitle(),proposedAssured.getFirstName(),proposedAssured.getSurname(),
                                proposedAssured.getOtherName(),proposedAssured.getNrc(),proposedAssured.getDateOfBirth(),proposedAssured.getGender(),proposedAssured.getMobileNumber(),
                                proposedAssured.getEmailAddress(),proposedAssured.getMaritalStatus(),proposedAssured.getSpouseFirstName(),proposedAssured.getSpouseLastName(),
                                proposedAssured.getSpouseEmailAddress(),
                                proposedAssured.getSpouseMobileNumber(),proposedAssured.getEmploymentDetail(),proposedAssured.getResidentialAddress());
                    }
                }
            }
        }
        return null;
    }

    public List<Map> findAllPolicy() {
        return mongoTemplate.findAll(Map.class, IL_POLICY_COLLECTION_NAME);
    }

    private ILClientDetailDto populateClientDetail(String title,String firstName,String surName , String otherName,String nrc,
                                                   DateTime dateOfBirth,Gender gender,String mobileNumber,
                                                   String emailAddress,MaritalStatus maritalStatus,String spouseFirstName,String spouseLastName,String spouseEmailAddress,String spouseMobileNumber, EmploymentDetail employmentDetail,ResidentialAddress residentialAddress){

        return new ILClientDetailDto(title,firstName,surName,otherName,nrc,dateOfBirth,gender,mobileNumber,emailAddress,maritalStatus,spouseFirstName,spouseLastName,spouseEmailAddress,spouseMobileNumber,employmentDetail,residentialAddress);
    }

}
