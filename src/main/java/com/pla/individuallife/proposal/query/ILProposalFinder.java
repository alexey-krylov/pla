package com.pla.individuallife.proposal.query;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.pla.individuallife.proposal.domain.model.Proposer;
import com.pla.individuallife.proposal.presentation.dto.ILSearchProposalDto;
import com.pla.sharedkernel.identifier.QuotationId;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Karunakar on 6/25/2015.
 */
@Service
@Finder
public class ILProposalFinder {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private MongoTemplate mongoTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource, MongoTemplate mongoTemplate) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.mongoTemplate = mongoTemplate;
    }

    public static final String FIND_ACTIVE_AGENT_BY_FIRST_NAME_QUERY = "SELECT * FROM agent WHERE first_name =:firstName";

    public static final String FIND_AGENT_BY_ID_QUERY = "SELECT agent_id as agentId, first_name as firstName, last_name as lastName FROM agent WHERE agent_id=:agentId AND agent_status = 'ACTIVE'";

    public Map<String, Object> getAgentById(String agentId) {
        Preconditions.checkArgument(UtilValidator.isNotEmpty(agentId));
        return namedParameterJdbcTemplate.queryForMap(FIND_AGENT_BY_ID_QUERY, new MapSqlParameterSource().addValue("agentId", agentId));
    }

    public List<ILSearchProposalDto> searchProposal(ILSearchProposalDto ilSearchProposalDto) {
        Criteria criteria = Criteria.where("proposalStatus").in(new String[]{"DRAFT", "SUBMITTED"});
        String proposalNumber = ilSearchProposalDto.getProposalNumber();
        String proposalId = ilSearchProposalDto.getProposalId();
        String agentCode = ilSearchProposalDto.getAgentCode();
        String proposerName = ilSearchProposalDto.getProposerName();
        String agentName = ilSearchProposalDto.getAgentName();
        String proposerNrcNumber =  ilSearchProposalDto.getProposerNrcNumber();
        if (isEmpty(proposalNumber) && isEmpty(proposalId) && isEmpty(agentCode) && isEmpty(proposerName) && isEmpty(agentName) && isEmpty(proposerNrcNumber)) {
            return Lists.newArrayList();
        }
        if (isNotEmpty(proposalId)) {
            criteria = criteria.and("_id").is(new QuotationId(proposalId));
        }
        if (isNotEmpty(proposalNumber)) {
            criteria = criteria.and("proposalNumber").is(proposalNumber);
        }
        if (isNotEmpty(agentCode)) {
            criteria = criteria != null ? criteria.and("agentCommissionShareModel.commissionShare.agentId.agentId").is(agentCode) : Criteria.where("agentCommissionShareModel.commissionShare.agentId.agentId").is(agentCode);
        }
        if (isNotEmpty(proposerName)) {
            String proposerPattern = "^"+proposerName;
            criteria = criteria != null ? criteria.and("proposer.firstName").regex(Pattern.compile(proposerPattern, Pattern.CASE_INSENSITIVE)) : Criteria.where("proposer.firstName").regex(Pattern.compile(proposerPattern,Pattern.CASE_INSENSITIVE));
        }
        if (isNotEmpty(proposerNrcNumber)) {
            String proposerNrcPattern = "^"+proposerNrcNumber;
            criteria = criteria != null ? criteria.and("proposer.nrc").regex(Pattern.compile(proposerNrcPattern, Pattern.CASE_INSENSITIVE)) : Criteria.where("proposer.nrc").regex(Pattern.compile(proposerNrcPattern,Pattern.CASE_INSENSITIVE));
        }
        Set<String> agentIds = null;
        if (isNotEmpty(agentName)) {
            List<Map<String, Object>> agentList = namedParameterJdbcTemplate.queryForList(FIND_ACTIVE_AGENT_BY_FIRST_NAME_QUERY, new MapSqlParameterSource().addValue("firstName", agentName));
            agentIds = agentList.stream().map(new Function<Map<String, Object>, String>() {
                @Override
                public String apply(Map<String, Object> stringObjectMap) {
                    return (String) stringObjectMap.get("agentId");
                }
            }).collect(Collectors.toSet());
        }
        if (isNotEmpty(agentIds)) {
            criteria = criteria.and("agentId.agentId").in(agentIds);
        }
        Query query = new Query(criteria);
        query.with(new Sort(Sort.Direction.ASC,"proposalNumber"));
        query.with(new Sort(Sort.Direction.DESC,"versionNumber"));
        List<Map> allProposals = mongoTemplate.find(query, Map.class, "individual_life_proposal");
        List<ILSearchProposalDto> ilProposalDtoList = allProposals.stream().map(new TransformToILSearchProposalDto()).collect(Collectors.toList());
        return ilProposalDtoList;
    }

    private class TransformToILSearchProposalDto implements Function<Map, ILSearchProposalDto> {

        @Override
        public ILSearchProposalDto apply(Map map) {
            String proposalId = map.get("_id").toString();
            LocalDate generatedOn = map.get("generatedOn") != null ? new LocalDate((Date) map.get("generatedOn")) : null;
            String proposalStatus = map.get("proposalStatus") != null ? (String) map.get("proposalStatus") : "";
            String proposalNumber = map.get("proposalNumber") != null ? (String) map.get("proposalNumber") : "";
            Proposer proposerMap = map.get("proposer") != null ? (Proposer) map.get("proposer") : null;
            String proposerName = proposerMap != null ? proposerMap.getFirstName() : "";
            ILSearchProposalDto ilSearchProposalDto = new ILSearchProposalDto(proposalNumber, proposerName, "NRCNumber", "AgenName", "Agent Code", proposalId, "createdOn", "version", proposalStatus);
            return ilSearchProposalDto;
        }
    }

}
