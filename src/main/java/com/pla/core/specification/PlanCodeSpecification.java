package com.pla.core.specification;

import com.pla.core.domain.model.plan.Plan;
import com.pla.sharedkernel.identifier.PlanId;
import org.nthdimenzion.ddd.domain.annotations.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

/**
 * For checking against duplicate Plan Code.
 *
 * @author: pradyumna
 * @since 1.0 22/03/2015
 */
@Specification
public class PlanCodeSpecification {

    private MongoTemplate mongoTemplate;

    @Autowired
    public PlanCodeSpecification(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public boolean satisfiedOnCreate(PlanId planId, String planName, String planCode) {
        Query planQuery = new Query();
        planQuery.addCriteria(Criteria.where("planDetail").exists(true).orOperator(Criteria.where("planDetail.planCode").is(planCode), Criteria.where("planDetail.planName").is(planName)));
        List<Plan> plans = mongoTemplate.find(planQuery, Plan.class, Plan.DOCUMENT_NAME);
        return plans.size() == 0;
    }

    public boolean satisfiedOnUpdate(PlanId planId, String planName, String planCode) {
        Query planQuery = new Query();
        planQuery.addCriteria(Criteria.where("planId").ne(planId.toString()).orOperator(Criteria.where("planDetail.planName").is(planName),
                Criteria.where("planDetail.planCode").is(planCode)));
        List<Plan> plans = mongoTemplate.find(planQuery, Plan.class, Plan.DOCUMENT_NAME);
        return plans.size() == 0;
    }

}
