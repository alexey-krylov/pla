package com.pla.grouphealth.quotation.query;

import com.mongodb.BasicDBObject;
import com.pla.grouphealth.sharedresource.query.GHFinder;
import org.bson.types.ObjectId;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Samir on 4/14/2015.
 */
@Finder
@Service
public class GHQuotationFinder {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GHFinder ghFinder;


    public Map<String, Object> getAgentById(String agentId) {
        return ghFinder.getAgentById(agentId);
    }

    public Map getQuotationById(String quotationId) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", quotationId);
        Map quotation = mongoTemplate.findOne(new BasicQuery(query), Map.class, "group_health_quotation");
        return quotation;
    }

    public List<Map> getChildQuotations(String parentQuotationId) {
        Criteria criteria = Criteria.where("parentQuotationId").in(new ObjectId(parentQuotationId));
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Map.class, "group_health_quotation");
    }

    public List<Map> searchQuotation(String quotationNumber, String agentCode, String proposerName, String agentName, String quotationId) {
        List<Map> quotations = ghFinder.searchQuotation(quotationNumber, agentCode, proposerName, agentName, quotationId, new String[]{"DRAFT", "GENERATED"});
        return quotations;
    }

    public List<Map<String, Object>> getAgentAuthorizedPlan(String agentId) {
        return ghFinder.getAgentAuthorizedPlan(agentId);
    }

    public Map<String, Object> getCoverageDetail(String coverageId) {
        return ghFinder.getCoverageDetail(coverageId);
    }

    public Map<String, Object> findCoverageDetailByCoverageCode(String coverageCode) {
        return ghFinder.findCoverageDetailByCoverageCode(coverageCode);
    }

    public Map<String, Object> findGeoDetail(String geoId) {
        return ghFinder.findGeoDetail(geoId);
    }
}
