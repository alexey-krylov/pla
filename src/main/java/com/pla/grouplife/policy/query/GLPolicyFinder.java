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

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GLFinder glFinder;

    private static final String GL_POLICY_COLLECTION_NAME = "group_life_policy";

    public List<Map> findAllPolicy() {
        return mongoTemplate.findAll(Map.class, GL_POLICY_COLLECTION_NAME);
    }

    public Map findPolicyById(String policyId) {
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(policyId)), Map.class, GL_POLICY_COLLECTION_NAME);
    }

    public List<Map> searchPolicy(String policyNumber, String policyHolderName) {
        return glFinder.searchPolicy(policyNumber, policyHolderName, new String[]{"IN_FORCE"});
    }


}
