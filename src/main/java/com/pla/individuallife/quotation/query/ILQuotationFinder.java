package com.pla.individuallife.quotation.query;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.pla.individuallife.quotation.domain.model.ILQuotation;
import com.pla.individuallife.quotation.domain.model.RiderDetail;
import com.pla.individuallife.quotation.presentation.dto.PlanDetailDto;
import com.pla.individuallife.quotation.presentation.dto.ProposedAssuredDto;
import com.pla.individuallife.quotation.presentation.dto.ProposerDto;
import com.pla.individuallife.quotation.presentation.dto.RiderDetailDto;
import com.pla.sharedkernel.identifier.QuotationId;
import org.apache.commons.beanutils.BeanUtils;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Finder
@Service
public class ILQuotationFinder {

    public static final String FIND_AGENT_BY_ID_QUERY = "select * from agent_team_branch_view where agentId =:agentId";
    public static final String FIND_QUOTATION_BY_ID_FOR_PREMIUM_WITH_RIDER_QUERY =
            " SELECT  r.`coverage_id` AS COVERAGEID, r.`cover_term` AS COVERTERM, r.`sum_assured` AS RIDER_SA, " +
            " r.`waiver_of_premium` AS RIDER_PREMIUM_WAIVER, c.`coverage_name` AS COVERAGENAME " +
                    " FROM individual_life_quotation_rider r  INNER JOIN coverage c " +
            " ON r.`coverage_id` = c.`coverage_id` " +
                    " WHERE r.`quotation_id` =:quotationId";
    public static final String FIND_QUOTATION_BY_ID_FOR_PREMIUM_QUERY = "SELECT quotation_id AS QUOTATIONID, plan_id AS PLANID, date_of_birth AS ASSURED_DOB, " +
            " gender AS ASSURED_GENDER, policy_term AS POLICYTERM, occupation AS ASSURED_OCCUPATION, " +
            " premium_payment_term AS PREMIUMPAYMENT_TERM, sum_assured AS SUMASSURED " +
            " FROM individual_life_quotation " +
            " WHERE quotation_id =:quotationId";

    public static final String QUOTATION_SEARCH_QUERY = "SELECT " +
            "  CONCAT(COALESCE(A.FIRST_NAME,' '), ' ', COALESCE(A.last_name,'')) AS agentName," +
            "  CONCAT(COALESCE(IL.first_name,' '), ' ', COALESCE(surname,'')) AS proposedName," +
            "  CONCAT(proposer_first_name,' ',proposer_surname) AS proposerName," +
            "  il_quotation_status AS quotationStatus," +
            "  quotation_id AS quotation_id," +
            "  quotation_number AS quotationNumber," +
            "  generated_on AS generatedOn," +
            "  version_number AS versionNumber " +
            "FROM" +
            "  individual_life_quotation IL " +
            "  JOIN agent A " +
            "    ON IL.agent_id = A.agent_id ";

    private static final String IL_QUOTATION_TABLE = "individual_life_quotation";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private ILQuotationRepository ilQuotationRepository;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Map<String, Object> getAgentById(String agentId) {
        Preconditions.checkArgument(isNotEmpty(agentId));
        return namedParameterJdbcTemplate.queryForMap(FIND_AGENT_BY_ID_QUERY, new MapSqlParameterSource().addValue("agentId", agentId));
    }

    public Map getQuotationforPremiumById(String quotationId) {
        return namedParameterJdbcTemplate.queryForMap(FIND_QUOTATION_BY_ID_FOR_PREMIUM_QUERY, new MapSqlParameterSource().addValue("quotationId", quotationId));
    }

    public List<Map<String, Object>> getQuotationforPremiumWithRiderById(String quotationId) {
        return namedParameterJdbcTemplate.queryForList(FIND_QUOTATION_BY_ID_FOR_PREMIUM_WITH_RIDER_QUERY,
                new MapSqlParameterSource().addValue("quotationId", quotationId));
    }

    public ILQuotationDto getQuotationById(String quotationId) {
        ILQuotation quotation = ilQuotationRepository.findOne(new QuotationId(quotationId));
        ILQuotationDto dto = new ILQuotationDto();
        ProposerDto proposerDto = new ProposerDto();
        ProposedAssuredDto proposedAssuredDto = new ProposedAssuredDto();
        PlanDetailDto planDetailDto = new PlanDetailDto();
        dto.setAgentId(quotation.getAgentId().toString());
        dto.setPlanId(quotation.getPlanDetail().getPlanId().toString());
        dto.setParentQuotationId(quotation.getQuotationARId());
        dto.setVersionNumber(quotation.getVersionNumber());
        dto.setQuotationGeneratedOn(quotation.getGeneratedOn());
        dto.setQuotationNumber(quotation.getQuotationNumber());
        dto.setQuotationId(quotation.getQuotationId());
        dto.setQuotationStatus(quotation.getIlQuotationStatus().name());
        dto.setAssuredTheProposer(quotation.isAssuredTheProposer());
        //TODO change the type in dto
//        dto.setQuotationStatus(quotation.getIlQuotationStatus());
        try {
            if (quotation.getProposer() != null)
                BeanUtils.copyProperties(proposerDto, quotation.getProposer());
            if (quotation.getProposedAssured() != null)
                BeanUtils.copyProperties(proposedAssuredDto, quotation.getProposedAssured());
            if (quotation.getPlanDetail() != null)
                BeanUtils.copyProperties(planDetailDto, quotation.getPlanDetail());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        List<Map<String, Object>> optionalCoverages = findAllOptionalCoverages(planDetailDto.getPlanId());
        Map<String, Map<String, Object>> coverageReferenceMap = new HashMap<>();
        for (Map<String, Object> m : optionalCoverages) {
            coverageReferenceMap.put(m.get("coverage_id").toString(), m);
        }
        List<RiderDetailDto> riderDetailDtoList = new ArrayList<>();
        if (quotation.getRiderDetails() != null && quotation.getRiderDetails().size() > 0) {
            for (RiderDetail rider : quotation.getRiderDetails()) {
                RiderDetailDto riderDetailDto = new RiderDetailDto();
                Map m = coverageReferenceMap.remove(rider.getCoverageId());
                riderDetailDto.setCoverageName(m.get("coverage_name").toString());
                riderDetailDto.setCoverageId(rider.getCoverageId());
                riderDetailDto.setCoverTerm(rider.getCoverTerm());
                riderDetailDto.setSumAssured(rider.getSumAssured());
                riderDetailDto.setWaiverOfPremium(rider.getWaiverOfPremium());
                riderDetailDtoList.add(riderDetailDto);
            }
        }

        for (String coverageId : coverageReferenceMap.keySet()) {
            RiderDetailDto riderDetailDto = new RiderDetailDto();
            Map m = coverageReferenceMap.get(coverageId);
            riderDetailDto.setCoverageName(m.get("coverage_name").toString());
            riderDetailDto.setCoverageId(coverageId);
            riderDetailDto.setCoverTerm(0);
            riderDetailDto.setSumAssured(BigDecimal.ZERO);
            riderDetailDto.setWaiverOfPremium(0);
            riderDetailDtoList.add(riderDetailDto);
        }
        planDetailDto.setRiderDetails(new HashSet<RiderDetailDto>(riderDetailDtoList));
        dto.setPlanDetailDto(planDetailDto);
        dto.setProposedAssured(proposedAssuredDto);
        dto.setProposer(proposerDto);
        return dto;
    }

    public List<Map<String, Object>> findAllOptionalCoverages(String planId) {
        // Query against the view
        List<Map<String, Object>> resultSet = namedParameterJdbcTemplate.queryForList("select * from plan_coverage where plan_id in (:planId) " +
                "AND (optional = 1)", new MapSqlParameterSource().addValue("planId", planId.toString()));
        return resultSet;
    }

    public List<ILSearchQuotationResultDto> searchQuotation(
            String quotationNumber, String proposerName, String proposerNrcNumber, String agentCode, String quotationStatus) {
         boolean isFirst = true;

        if (isEmpty(quotationNumber) && isEmpty(proposerName) && isEmpty(proposerNrcNumber) && isEmpty(agentCode)) {
             return Lists.newArrayList();
         }

        StringBuilder query = new StringBuilder(QUOTATION_SEARCH_QUERY);

         if (isNotEmpty(quotationNumber)) {
             if (isFirst) {
                 query.append(" where (quotation_number = '"+quotationNumber +"'" + "or quotation_id = '"+quotationNumber +"')");
             } else {
                 query.append(" and (quotation_number = '"+quotationNumber +"'" + "or quotation_id = '"+quotationNumber +"')");
             }
             isFirst = false;
         }

         if (isNotEmpty(proposerName)) {
             if (isFirst) {
                 query.append(" where lower(proposer_first_name) like '" + proposerName.toLowerCase() + "%' OR proposer_surname like '" + proposerName.toLowerCase() + "%' ");
             } else {
                 query.append(" and lower(proposer_first_name) like '" + proposerName.toLowerCase() + "%' OR proposer_surname like '" + proposerName.toLowerCase() + "%' ");
             }
             isFirst = false;
         }

         if (isNotEmpty(proposerNrcNumber)) {
             if (isFirst) {
                 query.append(" where proposer_nrc_number = '"+ proposerNrcNumber + "'");
             } else {
                 query.append(" and proposer_nrc_number = '"+ proposerNrcNumber + "'");
             }
             isFirst = false;
         }

         if (isNotEmpty(agentCode)) {
             if (isFirst) {
                 query.append(" where il.agent_id = '" + agentCode + "'");
             } else {
                 query.append(" and il.agent_id = '" + agentCode + "'");
             }
             isFirst = false;
         }

         if (isNotEmpty(quotationStatus)) {
             if (isFirst) {
                 query.append(" where il_quotation_status = '" + quotationStatus + "'");
             } else {
                 query.append(" and il_quotation_status = '" + quotationStatus + "'");
             }
             isFirst = false;
         }

         query.append(" order by version_number desc");
        return namedParameterJdbcTemplate.query(query.toString(), new BeanPropertyRowMapper(ILSearchQuotationResultDto.class));
     }
}
