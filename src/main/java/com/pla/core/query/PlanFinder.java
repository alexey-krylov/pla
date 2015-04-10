/*
 * Copyright (c) 3/5/15 4:08 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.query;

import com.mongodb.BasicDBObject;
import com.pla.sharedkernel.identifier.PlanId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
/**
 * @author: Nischitha
 * @since 1.0 05/03/2015
 */
@Service
public class PlanFinder {


    private MongoTemplate mongoTemplate;

    @Autowired
    public PlanFinder(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Map> findAllPlan() {
        List<Map> allPlan = mongoTemplate.findAll(Map.class, "PLAN");
        return allPlan;
    }

    public Map findPlanByPlanId(PlanId planId) {
        BasicDBObject query = new BasicDBObject();
        query.put("planId", planId);
        Map plan = mongoTemplate.findOne(new BasicQuery(query), Map.class, "PLAN");
        return plan;
    }


}
