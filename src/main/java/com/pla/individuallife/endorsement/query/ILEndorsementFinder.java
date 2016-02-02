package com.pla.individuallife.endorsement.query;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.BasicDBObject;
import com.pla.grouplife.endorsement.domain.model.GroupLifeEndorsement;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.individuallife.endorsement.domain.model.IndividualLifeEndorsement;
import com.pla.individuallife.sharedresource.model.ILEndorsementType;
import com.pla.sharedkernel.domain.model.EndorsementStatus;
import com.pla.sharedkernel.identifier.PolicyId;
import org.axonframework.repository.Repository;
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
 * Created by Raghu on 8/11/2015.
 */
@Service
public class ILEndorsementFinder {

    public static final String FIND_AGENT_PLANS_QUERY = "SELECT agent_id as agentId,plan_id as planId FROM `agent_authorized_plan` WHERE agent_id=:agentId";
    @Autowired
    private MongoTemplate mongoTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private Repository<IndividualLifeEndorsement> ilEndorsementMongoRepository;


    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Map findEndorsementById(String endorsementId) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", endorsementId);
        Map endorsement = mongoTemplate.findOne(new BasicQuery(query), Map.class, "individual_life_endorsement");
        return endorsement;
    }

    public List<IndividualLifeEndorsement> findEndorsement(String endorsementId) {
        Criteria premiumCriteria = Criteria.where("_id").is(endorsementId);
        Query query = new Query(premiumCriteria);
        List<IndividualLifeEndorsement> premiums = mongoTemplate.find(query, IndividualLifeEndorsement.class);
        return premiums;
    }

    public List<Map> findEndorsementByPolicyId(String policyNumber) {
        Criteria endorsementCriteria = Criteria.where("policy.policyNumber.policyNumber").is(policyNumber);
        endorsementCriteria.and("status").is(EndorsementStatus.APPROVED);
        Query query = new Query(endorsementCriteria);
        return mongoTemplate.find(query, Map.class, "individual_life_endorsement");
    }

    public List<IndividualLifeEndorsement> findEndorsementByPolicyNumber(String policyNumber) {
        Criteria endorsementCriteria = Criteria.where("policy.policyNumber.policyNumber").is(policyNumber);
        endorsementCriteria.and("status").is(EndorsementStatus.APPROVED);
        Query query = new Query(endorsementCriteria);
        return   mongoTemplate.find(query, IndividualLifeEndorsement.class);
    }

    public List<Map> searchEndorsement(ILEndorsementType endorsementType, String endorsementRequestNumber, String policyNumber, String policyHolderSurName, String policyHolderFirstName,
                                       String lifeAssuredSurName, String lifeAssuredFirstName, String policyHolderNrc, String lifeAssuredNrc, String[] statuses) {
        Criteria criteria = Criteria.where("status").in(statuses);
        if (endorsementType == null
            && isEmpty(endorsementRequestNumber)
            && isEmpty(policyNumber)
            && isEmpty(policyHolderFirstName)
            && isEmpty(policyHolderSurName)
            && isEmpty(lifeAssuredFirstName)
            && isEmpty(lifeAssuredSurName)
            && isEmpty(policyHolderNrc)
            && isEmpty(lifeAssuredNrc)) {
                return Lists.newArrayList();
        }
        if (endorsementType != null) {
            criteria = criteria.and("endorsementType").is(endorsementType.name());
        }

        if (isNotEmpty(endorsementRequestNumber)) {
            criteria = criteria.and("endorsementRequestNumber").is(endorsementRequestNumber);
        }

        if (isNotEmpty(policyNumber)) {
            criteria = criteria.and("ilPolicyDto.policyNumber.policyNumber").is(policyNumber);
        }
        if (isNotEmpty(policyHolderFirstName)) {
            //String policyHolderNamePattern = "^" + policyHolderName;
            String policyHolderNamePattern = "^" + policyHolderFirstName;
            criteria = criteria.and("ilPolicyDto.proposer.firstName").regex(Pattern.compile(policyHolderNamePattern, Pattern.CASE_INSENSITIVE));
        }
        if (isNotEmpty(policyHolderSurName)) {
            //String policyHolderNamePattern = "^" + policyHolderName;
            String policyHolderNamePattern = "^" + policyHolderSurName;
            criteria = criteria.and("ilPolicyDto.proposer.surname").regex(Pattern.compile(policyHolderNamePattern, Pattern.CASE_INSENSITIVE));
        }
        if (isNotEmpty(policyHolderNrc)) {
            //String policyHolderNamePattern = "^" + policyHolderName;
            String policyHolderNRC = policyHolderNrc;
            criteria = criteria.and("ilPolicyDto.proposer.nrc").is(policyHolderNRC);
        }
        if (isNotEmpty(lifeAssuredFirstName)) {
            //String policyHolderNamePattern = "^" + policyHolderName;
            String lifeAssuredNamePattern = "^" + lifeAssuredFirstName;
            criteria = criteria.and("ilPolicyDto.proposedAssured.firstName").regex(Pattern.compile(lifeAssuredNamePattern, Pattern.CASE_INSENSITIVE));
        }
        if (isNotEmpty(lifeAssuredSurName)) {
            //String policyHolderNamePattern = "^" + policyHolderName;
            String lifeAssuredNamePattern = "^" + lifeAssuredSurName;
            criteria = criteria.and("ilPolicyDto.proposedAssured.surname").regex(Pattern.compile(lifeAssuredNamePattern, Pattern.CASE_INSENSITIVE));
        }
        if (isNotEmpty(lifeAssuredNrc)) {
            //String policyHolderNamePattern = "^" + policyHolderName;
            String lifeAssuredNRC = lifeAssuredNrc;
            criteria = criteria.and("ilPolicyDto.proposedAssured.nrc").is(lifeAssuredNRC);
        }
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.ASC, "endorsementRequestNumber"));
        return mongoTemplate.find(query, Map.class, "individual_life_endorsement");
    }

    public Map<String, Object> getPolicyDetail(String endorsementId) throws ParseException {
        Criteria endorsementCriteria = Criteria.where("_id").is(endorsementId);
        Query query = new Query(endorsementCriteria);
        query.fields().include("policy.policyId").include("policy.policyHolderName").include("policy.policyNumber").include("endorsementType.").include("effectiveDate");
        List<Map> ilEndorsement = mongoTemplate.find(query, Map.class, "individual_life_endorsement");
        if (isEmpty(ilEndorsement)){
            return Collections.EMPTY_MAP;
        }
        Criteria policyCriteria = Criteria.where("_id").is(((Map) ilEndorsement.get(0).get("policy")).get("policyId"));
        Query policyQuery = new Query(policyCriteria);
        policyQuery.fields().include("inceptionOn").include("expiredOn");
        List<Map> ilPolicy = mongoTemplate.find(policyQuery, Map.class, "individual_life_endorsement");
        if (isEmpty(ilPolicy)){
            return Collections.EMPTY_MAP;
        }
        Map<String,Object> policyDetailMap = Maps.newLinkedHashMap();
        policyDetailMap.put("policyHolderName",((Map) ilEndorsement.get(0).get("policy")).get("policyHolderName"));
        policyDetailMap.put("policyNumber",((Map)((Map) ilEndorsement.get(0).get("policy")).get("policyNumber")).get("policyNumber"));
        policyDetailMap.put("inceptionDate",ilPolicy.get(0).get("inceptionOn"));
        policyDetailMap.put("expiredDate",ilPolicy.get(0).get("expiredOn"));
        policyDetailMap.put("effectiveDate", ilEndorsement.get(0).get("effectiveDate"));
        policyDetailMap.put("endorsementType", GLEndorsementType.valueOf((String) ilEndorsement.get(0).get("endorsementType")).getDescription());
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

    public IndividualLifeEndorsement findEndorsementByEndorsementId(String endorsementId) {
        if (isEmpty(endorsementId)) {
            return null;
        }
        Criteria criteria = Criteria.where("_id").is(endorsementId);
        Query query = new Query(criteria);
        List<IndividualLifeEndorsement> endorsements =  mongoTemplate.find(query, IndividualLifeEndorsement.class);
        return endorsements.get(0);

/*        BasicDBObject query1 = new BasicDBObject();
        query1.put("_id", endorsementId);
        Map endorsement = mongoTemplate.findOne(new BasicQuery(query1), Map.class, "individual_life_endorsement");*/
        //return null;
    }
}
