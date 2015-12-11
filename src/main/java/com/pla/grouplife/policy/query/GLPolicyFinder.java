package com.pla.grouplife.policy.query;

import com.pla.grouplife.sharedresource.query.GLFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Samir on 7/9/2015.
 */
@Service
public class GLPolicyFinder {

    private static final String GL_POLICY_COLLECTION_NAME = "group_life_policy";
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private GLFinder glFinder;

    public List<Map> findAllPolicy() {
        return mongoTemplate.findAll(Map.class, GL_POLICY_COLLECTION_NAME);
    }

    public Map findPolicyById(String policyId) {
        return glFinder.findPolicyById(policyId);
    }

    public List<Map> searchPolicy(String policyNumber, String policyHolderName, String clientId, String proposalNumber) {
        return glFinder.searchPolicy(policyNumber, policyHolderName, clientId, new String[]{"IN_FORCE"}, proposalNumber);
    }

    public Map findProposalIdByPolicyId(String policyId) {
        Query query = new Query(Criteria.where("_id").is(policyId));
        query.fields().include("proposal");
        return mongoTemplate.findOne(query, Map.class, GL_POLICY_COLLECTION_NAME);
    }

    public Map findProposalIdByPolicyNumber(String policyNumber) {
        Query query = new Query(Criteria.where("policyNumber.policyNumber").is(policyNumber));
        return mongoTemplate.findOne(query, Map.class, GL_POLICY_COLLECTION_NAME);
    }

    public Map findActiveMemberFromPolicyByPolicyId(String policyId){
        Map policyMap  = glFinder.findActiveMemberFromPolicyByPolicyId(policyId);
         return policyMap;
    }


}
