package com.pla.individuallife.policy.finder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 8/4/2015.
 */
@Service
public class ILPolicyFinder {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String IL_POLICY_COLLECTION_NAME = "individual_life_policy";

    public List<Map> findAllPolicy() {
        return mongoTemplate.findAll(Map.class, IL_POLICY_COLLECTION_NAME);
    }

    public Map findPolicyById(String policyId) {
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(policyId)), Map.class, IL_POLICY_COLLECTION_NAME);
    }
}
