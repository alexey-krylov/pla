package com.pla.individuallife.policy.finder;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

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

    public List<Map> searchPolicy(String policyNumber, String policyHolderName, String proposalNumber) {
        if (isEmpty(policyHolderName) && isEmpty(policyNumber) && isEmpty(proposalNumber)) {
            return Lists.newArrayList();
        }
        Criteria criteria = Criteria.where("policyStatus").is("IN_FORCE");
        if (isNotEmpty(policyHolderName)) {
            String proposerPattern = "^" + policyHolderName;
            criteria = criteria.and("proposer.firstName").regex(Pattern.compile(proposerPattern, Pattern.CASE_INSENSITIVE));
        }
        if (isNotEmpty(policyNumber)) {
            criteria = criteria.and("policyNumber.policyNumber").is(policyNumber);
        }
        if (isNotEmpty(proposalNumber)) {
            criteria = criteria.and("proposal.proposalNumber.proposalNumber").is(proposalNumber);
        }
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.ASC, "policyNumber.policyNumber"));
        return mongoTemplate.find(query, Map.class, IL_POLICY_COLLECTION_NAME);
    }
}
