package com.pla.grouplife.endorsement.query;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.pla.grouplife.endorsement.application.service.GroupLifeEndorsementChecker;
import com.pla.grouplife.endorsement.domain.model.GroupLifeEndorsement;
import com.pla.grouplife.policy.query.GLPolicyFinder;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.sharedkernel.domain.model.EndorsementStatus;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.identifier.EndorsementId;
import com.pla.sharedkernel.identifier.PolicyId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 8/11/2015.
 */
@Service
public class GLEndorsementFinder {

    public static final String FIND_AGENT_PLANS_QUERY = "SELECT agent_id as agentId,plan_id as planId FROM `agent_authorized_plan` WHERE agent_id=:agentId";
    @Autowired
    private MongoTemplate mongoTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private GLPolicyFinder glPolicyFinder;

    @Autowired
    private GroupLifeEndorsementChecker groupLifeEndorsementChecker;


    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Map findEndorsementById(String endorsementId) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", endorsementId);
        Map proposal = mongoTemplate.findOne(new BasicQuery(query), Map.class, "group_life_endorsement");
        return proposal;
    }

    public List<GroupLifeEndorsement> findEndorsement(String endorsementId) {
        Criteria premiumCriteria = Criteria.where("_id").is(endorsementId);
        Query query = new Query(premiumCriteria);
        List<GroupLifeEndorsement> premiums = mongoTemplate.find(query, GroupLifeEndorsement.class);
        return premiums;
    }

    public List<Map> findEndorsementByPolicyId(String policyNumber) {
        Criteria endorsementCriteria = Criteria.where("policy.policyNumber.policyNumber").is(policyNumber);
        endorsementCriteria.and("status").is(EndorsementStatus.APPROVED);
        Query query = new Query(endorsementCriteria);
        return mongoTemplate.find(query, Map.class, "group_life_endorsement");
    }

    public List<GroupLifeEndorsement> findEndorsementByPolicyNumber(String policyNumber) {
        Criteria endorsementCriteria = Criteria.where("policy.policyNumber.policyNumber").is(policyNumber);
        endorsementCriteria.and("status").is(EndorsementStatus.APPROVED);
        Query query = new Query(endorsementCriteria);
        return   mongoTemplate.find(query, GroupLifeEndorsement.class);
    }

    public List<Map> searchEndorsement(GLEndorsementType endorsementType, String endorsementNumber, String endorsementId, String policyNumber, String policyHolderName, String[] statuses) {
        Criteria criteria = Criteria.where("status").in(statuses);
        if (endorsementType == null && isEmpty(endorsementNumber) && isEmpty(endorsementId) && isEmpty(policyNumber) && isEmpty(policyHolderName)) {
            return Lists.newArrayList();
        }
        if (isNotEmpty(endorsementId)) {
            criteria = criteria.and("_id").is(new EndorsementId(endorsementId));
        }
        if (endorsementType != null) {
            criteria = criteria.and("endorsementType").is(endorsementType.name());
        }
        if (isNotEmpty(endorsementNumber)) {
            criteria = criteria.and("endorsementNumber.endorsementNumber").is(endorsementNumber);
        }
        if (isNotEmpty(policyHolderName)) {
            String policyHolderNamePattern = "^" + policyHolderName;
            criteria = criteria.and("policy.policyHolderName").regex(Pattern.compile(policyHolderNamePattern, Pattern.CASE_INSENSITIVE));
        }

        if (isNotEmpty(policyNumber)) {
            criteria = criteria.and("policy.policyNumber.policyNumber").is(policyNumber);
        }
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.ASC, "endorsementNumber.endorsementNumber"));
        return mongoTemplate.find(query, Map.class, "group_life_endorsement");
    }

    public Map<String, Object> getPolicyDetail(String endorsementId) throws ParseException {
        Criteria endorsementCriteria = Criteria.where("_id").is(endorsementId);
        Query query = new Query(endorsementCriteria);
        query.fields().include("policy.policyId").include("policy.policyHolderName").include("policy.policyNumber").include("endorsementType.").include("effectiveDate");
        List<Map> glEndorsement = mongoTemplate.find(query, Map.class, "group_life_endorsement");
        if (isEmpty(glEndorsement)){
            return Collections.EMPTY_MAP;
        }
        Criteria policyCriteria = Criteria.where("_id").is(((Map) glEndorsement.get(0).get("policy")).get("policyId"));
        Query policyQuery = new Query(policyCriteria);
        policyQuery.fields().include("inceptionOn").include("expiredOn");
        List<Map> glPolicy = mongoTemplate.find(policyQuery, Map.class, "group_life_policy");
        if (isEmpty(glPolicy)){
            return Collections.EMPTY_MAP;
        }
        Map<String,Object> policyDetailMap = Maps.newLinkedHashMap();
        policyDetailMap.put("policyHolderName",((Map) glEndorsement.get(0).get("policy")).get("policyHolderName"));
        policyDetailMap.put("policyNumber",((Map)((Map) glEndorsement.get(0).get("policy")).get("policyNumber")).get("policyNumber"));
        policyDetailMap.put("inceptionDate",glPolicy.get(0).get("inceptionOn"));
        policyDetailMap.put("expiredDate",glPolicy.get(0).get("expiredOn"));
        policyDetailMap.put("effectiveDate", glEndorsement.get(0).get("effectiveDate"));
        policyDetailMap.put("endorsementType", GLEndorsementType.valueOf((String)glEndorsement.get(0).get("endorsementType")).getDescription());
        return policyDetailMap;
    }

    public List<Map<String, Object>> getAgentAuthorizedPlan(String agentId) {
        return namedParameterJdbcTemplate.query(FIND_AGENT_PLANS_QUERY, new MapSqlParameterSource().addValue("agentId", agentId), new ColumnMapRowMapper());
    }

    public List<Map<String,Object>> getEndorsementByPolicyId(PolicyId policyId){
        Criteria endorsementCriteria = Criteria.where("policy.policyId.policyId").is(policyId);
        Query query = new Query(endorsementCriteria);
        /*query.fields().include("endorsementId.endorsementId").include("endorsementNumber").include("endorsementType").include("effectiveDate")
                .include("submittedOn");*/
        List<GroupLifeEndorsement> groupLifeEndorsements = mongoTemplate.find(query, GroupLifeEndorsement.class);
        if (isEmpty(groupLifeEndorsements)){
            return Collections.EMPTY_LIST;
        }
        return groupLifeEndorsements.parallelStream().map(new Function<GroupLifeEndorsement, Map<String,Object>>() {
            @Override
            public Map<String, Object> apply(GroupLifeEndorsement groupLifeEndorsement) {
                Map<String,Object> groupLifeEndorsementMap = Maps.newLinkedHashMap();
                groupLifeEndorsementMap.put("endorsementId",groupLifeEndorsement.getEndorsementId().getEndorsementId());
                groupLifeEndorsementMap.put("endorsementType",groupLifeEndorsement.getEndorsementType().getDescription());
                groupLifeEndorsementMap.put("effectiveDate",groupLifeEndorsement.getEffectiveDate());
                groupLifeEndorsementMap.put("policyId",groupLifeEndorsement.getPolicy().getPolicyId());
                groupLifeEndorsementMap.put("policyNumber",groupLifeEndorsement.getEndorsementId().getEndorsementId());
                groupLifeEndorsementMap.put("",groupLifeEndorsement.getEndorsementId().getEndorsementId());
                return null;
            }
        }).collect(Collectors.toList());
    }

    public List<Insured> getActiveInsured(PolicyId policyId){
        Map<String,Object> policyMap = glPolicyFinder.findActiveMemberFromPolicyByPolicyId(policyId.getPolicyId());
        List<Insured> insureds = (List<Insured>) policyMap.get("insureds");
        PolicyNumber policyNumber = (PolicyNumber) policyMap.get("policyNumber");
        return groupLifeEndorsementChecker.getNewCategoryAndRelationInsuredDetail(insureds,policyNumber.getPolicyNumber());
    }
}
