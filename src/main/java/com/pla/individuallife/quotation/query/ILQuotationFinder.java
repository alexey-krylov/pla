package com.pla.individuallife.quotation.query;

import com.google.common.base.Preconditions;
import com.pla.individuallife.quotation.domain.model.IndividualLifeQuotation;
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
import java.util.*;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Finder
@Service
public class ILQuotationFinder {

    public static final String FIND_AGENT_BY_ID_QUERY = "select * from agent_team_branch_view where agentId =:agentId";
    public static final String FIND_QUOTATION_BY_ID_FOR_PREMIUM_WITH_RIDER_QUERY = "SELECT  r.`coverage_id` AS COVERAGEID, r.`cover_term` AS COVERTERM, r.`sum_assured` AS RIDER_SA," +
            "r.`waiver_of_premium` AS RIDER_PREMIUM_WAIVER, c.`coverage_name` AS COVERAGENAME" +
            "  FROM individual_life_quotation_rider_details r  INNER JOIN coverage c" +
            "ON r.`coverage_id` = c.`coverage_id`" +
            "WHERE r.`individual_life_quotation_quotationId` =:quotationId";
    private static final String IL_QUOTATION_TABLE = "individual_life_quotation";
    public static final String FIND_QUOTATION_BY_ID_QUERY = "select * from " + IL_QUOTATION_TABLE + " where quotation_id =:quotationId";
    public static final String FIND_QUOTATION_BY_ID_FOR_PREMIUM_QUERY = "SELECT quotation_id AS QUOTATIONID, plan_id AS PLANID, date_of_birth AS ASSURED_DOB, " +
            "gender AS ASSURED_GENDER, policy_term AS POLICYTERM, occupation AS ASSURED_OCCUPATION," +
            "premium_payment_term AS PREMIUMPAYMENT_TERM, sum_assured AS SUMASSURED" +
            "FROM "+ IL_QUOTATION_TABLE  +
            " WHERE quotation_id =:quotationId";
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
        IndividualLifeQuotation quotation = ilQuotationRepository.findOne(new QuotationId(quotationId));
        ILQuotationDto dto = new ILQuotationDto();
        ProposerDto proposerDto = new ProposerDto();
        ProposedAssuredDto proposedAssuredDto = new ProposedAssuredDto();
        PlanDetailDto planDetailDto = new PlanDetailDto();
        dto.setAgentId(quotation.getAgentId().toString());
        dto.setPlanId(quotation.getPlanDetail().getPlanId().toString());
        dto.setParentQuotationId(quotation.getParentQuotationId());
        dto.setVersionNumber(quotation.getVersionNumber());
        dto.setQuotationGeneratedOn(quotation.getGeneratedOn());
        dto.setQuotationNumber(quotation.getQuotationNumber());
        dto.setQuotationId(quotation.getQuotationId());
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
        if (quotation.getRiderDetails() != null) {
            for (RiderDetail rider : quotation.getRiderDetails()) {
                RiderDetailDto riderDetailDto = new RiderDetailDto();
                Map m = coverageReferenceMap.get(rider.getCoverageId());
                riderDetailDto.setCoverageName(m.get("coverage_name").toString());
                riderDetailDto.setCoverageId(rider.getCoverageId());
                riderDetailDto.setCoverTerm(rider.getCoverTerm());
                riderDetailDto.setSumAssured(rider.getSumAssured());
                riderDetailDto.setWaiverOfPremium(rider.getWaiverOfPremium());
                riderDetailDtoList.add(riderDetailDto);
            }
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

    public List<ILQuotationDto> getAllQuotation() {
        return namedParameterJdbcTemplate.query("select quotation_id,agent_id,generated_on," +
                        "  il_quotation_status as quotation_status," +
                        "  `parent_quotation_id`," +
                        "  `plan_id`," +
                        "  `sum_assured`," +
                        "  `quotation_creator`," +
                        "  `quotation_number`," +
                        "  `version_number` from " + IL_QUOTATION_TABLE,
                new BeanPropertyRowMapper<ILQuotationDto>(ILQuotationDto.class));
    }

    //TODO Needs to be implemented
    public List<Map> searchQuotation(String quotationNumber, String agentCode, String proposerName) {
        return null;
    }
}
