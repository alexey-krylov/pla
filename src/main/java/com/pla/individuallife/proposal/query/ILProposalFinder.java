package com.pla.individuallife.proposal.query;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.core.domain.model.CoverageName;
import com.pla.core.domain.model.plan.premium.Premium;
import com.pla.core.query.CoverageFinder;
import com.pla.core.query.PlanFinder;
import com.pla.core.query.PremiumFinder;
import com.pla.individuallife.proposal.domain.model.*;
import com.pla.individuallife.proposal.presentation.dto.*;
import com.pla.publishedlanguage.contract.IPremiumCalculator;
import com.pla.publishedlanguage.domain.model.ComputedPremiumDto;
import com.pla.publishedlanguage.domain.model.PremiumCalculationDto;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor;
import com.pla.publishedlanguage.dto.ClientDocumentDto;
import com.pla.publishedlanguage.dto.SearchDocumentDetailDto;
import com.pla.publishedlanguage.dto.UnderWriterRoutingLevelDetailDto;
import com.pla.publishedlanguage.underwriter.contract.IUnderWriterAdapter;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.RoutingLevel;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import com.pla.underwriter.domain.model.UnderWriterDocument;
import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import com.pla.underwriter.domain.model.UnderWriterRoutingLevel;
import com.pla.underwriter.finder.UnderWriterFinder;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
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
    private IPremiumCalculator premiumCalculator;

    @Autowired
    private PremiumFinder premiumFinder;

    @Autowired
    private PlanFinder planFinder;

    @Autowired
    private CoverageFinder coverageFinder;

    @Autowired
    private IUnderWriterAdapter underWriterAdapter;

    @Autowired
    private UnderWriterFinder underWriterFinder;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource, MongoTemplate mongoTemplate) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.mongoTemplate = mongoTemplate;
    }

    public static final String FIND_ACTIVE_AGENT_BY_FIRST_NAME_QUERY = "SELECT * FROM agent WHERE first_name =:firstName";

    public static final String FIND_AGENT_BY_ID_QUERY = "SELECT agent_id as agentId, first_name as firstName, last_name as lastName FROM agent WHERE agent_id=:agentId AND agent_status = 'ACTIVE'";

    /**
     * Find all the Plans by Agent Id and for a line of business.
     */
    private static final String SEARCH_PLAN_BY_AGENT_IDS = "SELECT DISTINCT C.* FROM AGENT A JOIN agent_authorized_plan b " +
            "ON A.`agent_id`=B.`agent_id` JOIN plan_coverage_benefit_assoc C " +
            "ON B.`plan_id`=C.`plan_id` where A.agent_id IN (:agentIds) and c.line_of_business=:lineOfBusiness group by A.agent_id";

    public Map<String, Object> getAgentById(String agentId) {
        Preconditions.checkArgument(UtilValidator.isNotEmpty(agentId));
        return namedParameterJdbcTemplate.queryForMap(FIND_AGENT_BY_ID_QUERY, new MapSqlParameterSource().addValue("agentId", agentId));
    }

    public String getAgentFullNameById(String agentId) {
        Preconditions.checkArgument(UtilValidator.isNotEmpty(agentId));
        Map<String, Object> map = namedParameterJdbcTemplate.queryForMap(FIND_AGENT_BY_ID_QUERY, new MapSqlParameterSource().addValue("agentId", agentId));
        if (map.get("lastName") != null)
            return map.get("firstName") + " " + map.get("lastName");
        else
            return map.get("firstName").toString();
    }

    public List<Map<String, Object>> findAllOptionalCoverages(String planId) {
        // Query against the view
        List<Map<String, Object>> resultSet = namedParameterJdbcTemplate.queryForList("select DISTINCT * from plan_coverage where plan_id in (:planId) " +
                "AND (optional = 1)", new MapSqlParameterSource().addValue("planId", planId.toString()));
        return resultSet;
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
            criteria = criteria != null ? criteria.and("agentCommissionShareModel.commissionShare").elemMatch(Criteria.where("agentId.agentId").is(agentCode)) : Criteria.where("agentCommissionShareModel.commissionShare").elemMatch(Criteria.where("agentId.agentId").is(agentCode));
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

    public PremiumDetailDto getPremiumDetail(String proposalId) {

        PremiumDetailDto premiumDetailDto = new PremiumDetailDto();

        Set<RiderPremiumDto> riderPremiumDtoSet = new HashSet<RiderPremiumDto>();

        BasicDBObject query = new BasicDBObject();
        query.put("_id", proposalId);
        Map proposal = mongoTemplate.findOne(new BasicQuery(query), Map.class, "individual_life_proposal");

        ProposalPlanDetail planDetail = (ProposalPlanDetail) proposal.get("proposalPlanDetail");


        PremiumCalculationDto premiumCalculationDto = new PremiumCalculationDto(new PlanId(planDetail.getPlanId()), LocalDate.now(), PremiumFrequency.ANNUALLY, 365);

        DateTime dob = new DateTime(((ProposedAssured) proposal.get("proposedAssured")).getDateOfBirth());
        Integer age = Years.yearsBetween(dob, DateTime.now()).getYears() + 1;

        Premium premium = premiumFinder.findPremium(premiumCalculationDto);
        List<PremiumInfluencingFactor> premiumInfluencingFactors = premium.getPremiumInfluencingFactors();

        for (PremiumInfluencingFactor premiumInfluencingFactor : premiumInfluencingFactors) {
            if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.SUM_ASSURED)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, planDetail.getSumAssured().toString());
            if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.GENDER)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, ((ProposedAssured) proposal.get("proposedAssured")).getGender().toString());
            if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.AGE)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, String.valueOf(age));
            if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.POLICY_TERM)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, planDetail.getPolicyTerm().toString());
            if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, planDetail.getPremiumPaymentTerm().toString());
            if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.OCCUPATION_CLASS)))
                premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.OCCUPATION_CLASS, ((ProposedAssured) proposal.get("proposedAssured")).getEmploymentDetail().getOccupationClass());
        }

        List<ComputedPremiumDto> computedPremiums = premiumCalculator.calculateBasicPremiumWithPolicyFee(premiumCalculationDto);
        premiumDetailDto.setPlanAnnualPremium(ComputedPremiumDto.getAnnualPremium(computedPremiums));

        BigDecimal totalPremium = premiumDetailDto.getPlanAnnualPremium();
        BigDecimal semiAnnualPremium = ComputedPremiumDto.getSemiAnnualPremium(computedPremiums);
        BigDecimal quarterlyPremium = ComputedPremiumDto.getQuarterlyPremium(computedPremiums);
        BigDecimal monthlyPremium = ComputedPremiumDto.getMonthlyPremium(computedPremiums);

        Set<RiderDetailDto> riderList = planDetail.getRiderDetails();

        if (riderList != null) {
            for (RiderDetailDto rider : riderList) {
                if ((new BigDecimal(rider.getSumAssured().toString()).compareTo(new BigDecimal("0.0")) != 0) || (rider.getCoverTerm() != 0)) {
                    premiumCalculationDto = new PremiumCalculationDto(new PlanId(planDetail.getPlanId()), LocalDate.now(), PremiumFrequency.ANNUALLY, 365);
                    premiumCalculationDto = premiumCalculationDto.addCoverage(new CoverageId(rider.getCoverageId()));
                    premiumInfluencingFactors = premiumFinder.findPremium(premiumCalculationDto).getPremiumInfluencingFactors();

                    for (PremiumInfluencingFactor premiumInfluencingFactor : premiumInfluencingFactors) {
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.SUM_ASSURED)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.SUM_ASSURED, rider.getSumAssured().toString());
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.GENDER)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.GENDER, ((ProposedAssured) proposal.get("proposedAssured")).getGender().toString());
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.AGE)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.AGE, String.valueOf(age));
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.POLICY_TERM)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.POLICY_TERM, rider.getCoverTerm().toString());
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.PREMIUM_PAYMENT_TERM, rider.getWaiverOfPremium().toString());
                        if (premiumInfluencingFactor.name().equalsIgnoreCase(String.valueOf(PremiumInfluencingFactor.OCCUPATION_CLASS)))
                            premiumCalculationDto.addInfluencingFactorItemValue(PremiumInfluencingFactor.OCCUPATION_CLASS, ((ProposedAssured) proposal.get("proposedAssured")).getEmploymentDetail().getOccupationClass());
                    }
                    RiderPremiumDto rd = new RiderPremiumDto();
                    rd.setCoverageId(new CoverageId(rider.getCoverageId()));
                    rd.setCoverageName(new CoverageName(coverageFinder.getCoverageDetail(rider.getCoverageId()).get("coverageName").toString()));
                    computedPremiums = premiumCalculator.calculateBasicPremium(premiumCalculationDto);
                    rd.setAnnualPremium(ComputedPremiumDto.getAnnualPremium(computedPremiums));
                    totalPremium = totalPremium.add(ComputedPremiumDto.getAnnualPremium(computedPremiums));
                    semiAnnualPremium = semiAnnualPremium.add(ComputedPremiumDto.getSemiAnnualPremium(computedPremiums));
                    quarterlyPremium = quarterlyPremium.add(ComputedPremiumDto.getQuarterlyPremium(computedPremiums));
                    monthlyPremium = monthlyPremium.add(ComputedPremiumDto.getMonthlyPremium(computedPremiums));
                    riderPremiumDtoSet.add(rd);
                }
            }
        }
        premiumDetailDto.setRiderPremiumDtos(riderPremiumDtoSet);
        premiumDetailDto.setTotalPremium(totalPremium);
        premiumDetailDto.setPlanName(planFinder.getPlanName(new PlanId(planDetail.getPlanId())));
        premiumDetailDto.setAnnualPremium(totalPremium);
        premiumDetailDto.setMonthlyPremium(monthlyPremium);
        premiumDetailDto.setQuarterlyPremium(quarterlyPremium);
        premiumDetailDto.setSemiannualPremium(semiAnnualPremium);
        return premiumDetailDto;
    }

    public List<ILProposalMandatoryDocumentDto> findMandatoryDocuments(String proposalId) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", proposalId);
        Map proposal = mongoTemplate.findOne(new BasicQuery(query), Map.class, "individual_life_proposal");
        List<ILProposerDocument> uploadedDocuments = proposal.get("proposalDocuments") != null ? (List<ILProposerDocument>) proposal.get("proposalDocuments") : Lists.newArrayList();
        ProposalPlanDetail planDetail = (ProposalPlanDetail) proposal.get("proposalPlanDetail");
        List<CoverageId> coverageIds = ((Set<RiderDetailDto>) planDetail.getRiderDetails()).stream().map(rider -> new CoverageId(rider.getCoverageId())).collect(Collectors.toList());

        UnderWriterRoutingLevelDetailDto routingLevelDetailDto = new UnderWriterRoutingLevelDetailDto(new PlanId(planDetail.getPlanId()), LocalDate.now(), ProcessType.ENROLLMENT.name());
        coverageIds.stream().forEach(coverageId -> routingLevelDetailDto.addCoverage(coverageId));
        DateTime dob = new DateTime(((ProposedAssured) proposal.get("proposedAssured")).getDateOfBirth());
        Integer age = Years.yearsBetween(dob, DateTime.now()).getYears() + 1;
        List<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem> underWriterInfluencingFactorItems = new ArrayList<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem>();
        RoutingLevel routinglevel = null;
        try {
            UnderWriterRoutingLevel underWriterRoutingLevel = underWriterFinder.findUnderWriterRoutingLevel(routingLevelDetailDto);
            //TODO : need to add other influencing items
            for (UnderWriterInfluencingFactor underWriterInfluencingFactor : underWriterRoutingLevel.getUnderWriterInfluencingFactors()) {
                if (underWriterInfluencingFactor.name().equalsIgnoreCase(String.valueOf(UnderWriterInfluencingFactor.AGE)))
                    underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.AGE.name(), age.toString()));
                if (underWriterInfluencingFactor.name().equalsIgnoreCase(String.valueOf(UnderWriterInfluencingFactor.SUM_ASSURED)))
                    underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.SUM_ASSURED.name(), (((ProposalPlanDetail) proposal.get("proposalPlanDetail")).getSumAssured().toString())));
            }
            routingLevelDetailDto.setUnderWriterInfluencingFactor(underWriterInfluencingFactorItems);
            routinglevel = underWriterAdapter.getRoutingLevel(routingLevelDetailDto);
        } catch (IllegalArgumentException ex) {

        }
        List<ClientDocumentDto> mandatoryDocuments = new ArrayList<ClientDocumentDto>();
        if (routinglevel != null) {
            UnderWriterDocument underWriterDocument = underWriterFinder.getUnderWriterDocumentSetUp(routingLevelDetailDto.getPlanId(), routingLevelDetailDto.getCoverageId(), LocalDate.now(), ProcessType.ENROLLMENT.name());
            underWriterInfluencingFactorItems = new ArrayList<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem>();
            //TODO : need to add other influencing items
            for (UnderWriterInfluencingFactor underWriterInfluencingFactor : underWriterDocument.getUnderWriterInfluencingFactors()) {
                if (underWriterInfluencingFactor.name().equalsIgnoreCase(String.valueOf(UnderWriterInfluencingFactor.AGE)))
                    underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.AGE.name(), age.toString()));
                if (underWriterInfluencingFactor.name().equalsIgnoreCase(String.valueOf(UnderWriterInfluencingFactor.SUM_ASSURED)))
                    underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.SUM_ASSURED.name(), (((ProposalPlanDetail) proposal.get("proposalPlanDetail")).getSumAssured().toString())));
            }
            routingLevelDetailDto.setUnderWriterInfluencingFactor(underWriterInfluencingFactorItems);
            mandatoryDocuments = underWriterAdapter.getDocumentsForUnderWriterApproval(routingLevelDetailDto);
        } else {
            List<SearchDocumentDetailDto> documentDetailDtos = Lists.newArrayList();
            SearchDocumentDetailDto searchDocumentDetailDto = new SearchDocumentDetailDto(new PlanId(planDetail.getPlanId()));
            documentDetailDtos.add(searchDocumentDetailDto);
            documentDetailDtos.add(new SearchDocumentDetailDto(new PlanId(planDetail.getPlanId()), coverageIds));
            mandatoryDocuments.addAll(underWriterAdapter.getMandatoryDocumentsForApproverApproval(documentDetailDtos, ProcessType.ENROLLMENT));
        }
        List<ILProposalMandatoryDocumentDto> mandatoryDocumentDtos = Lists.newArrayList();
        if (isNotEmpty(mandatoryDocuments)) {
            mandatoryDocumentDtos = mandatoryDocuments.stream().map(new Function<ClientDocumentDto, ILProposalMandatoryDocumentDto>() {
                @Override
                public ILProposalMandatoryDocumentDto apply(ClientDocumentDto clientDocumentDto) {
                    ILProposalMandatoryDocumentDto mandatoryDocumentDto = new ILProposalMandatoryDocumentDto(clientDocumentDto.getDocumentCode(), clientDocumentDto.getDocumentName());
                    Optional<ILProposerDocument> proposerDocumentOptional = uploadedDocuments.stream().filter(new Predicate<ILProposerDocument>() {
                        @Override
                        public boolean test(ILProposerDocument ilProposerDocument) {
                            return clientDocumentDto.getDocumentCode().equals(ilProposerDocument.getDocumentId());
                        }
                    }).findAny();
                    if (proposerDocumentOptional.isPresent()) {
                        try {
                            if (isNotEmpty(proposerDocumentOptional.get().getGridFsDocId())) {
                                GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(proposerDocumentOptional.get().getGridFsDocId())));
                                mandatoryDocumentDto.updateWithContent(IOUtils.toByteArray(gridFSDBFile.getInputStream()));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return mandatoryDocumentDto;
                }
            }).collect(Collectors.toList());
        }
        return mandatoryDocumentDtos;

    }

    private class TransformToILSearchProposalDto implements Function<Map, ILSearchProposalDto> {

        @Override
        public ILSearchProposalDto apply(Map map) {
            String proposalId = map.get("_id").toString();
            String generatedOn = map.get("generatedOn") != null ? (String) map.get("generatedOn") : "";
            String proposalStatus = map.get("proposalStatus") != null ? (String) map.get("proposalStatus") : "";
            String proposalNumber = map.get("proposalNumber") != null ? (String) map.get("proposalNumber") : "";
            Proposer proposerMap = map.get("proposer") != null ? (Proposer) map.get("proposer") : null;
            String proposerName = proposerMap != null ? proposerMap.getFirstName() + " " + proposerMap.getSurname() : "";
            String agentNames = ((AgentCommissionShareModel) map.get("agentCommissionShareModel")).getCommissionShare().stream().map(x -> getAgentFullNameById(x.getAgentId().toString())).collect(Collectors.joining(" , "));
            ILSearchProposalDto ilSearchProposalDto = new ILSearchProposalDto(proposalNumber, proposerName, "NRCNumber", agentNames, "Agent Code", proposalId, generatedOn, proposalStatus);
            return ilSearchProposalDto;
        }
    }


    public ILProposalDto getProposalById(String proposalId) {
        ILProposalDto dto = new ILProposalDto();
        BasicDBObject query = new BasicDBObject();
        query.put("_id", proposalId);
        Map proposal = mongoTemplate.findOne(new BasicQuery(query), Map.class, "individual_life_proposal");
        if (proposal.get("proposedAssured") != null) {
            dto.setProposedAssured(ProposedAssuredBuilder.getProposedAssuredBuilder((ProposedAssured) proposal.get("proposedAssured")).createProposedAssuredDto());
        }
        if (proposal.get("proposer") != null) {
            dto.setProposer(ProposerBuilder.getProposerBuilder((Proposer) proposal.get("proposer")).createProposerDto());
        }
        dto.setProposalPlanDetail((ProposalPlanDetail) proposal.get("proposalPlanDetail"));
        dto.setBeneficiaries((List<Beneficiary>) proposal.get("beneficiaries"));
        dto.setTotalBeneficiaryShare(new BigDecimal(proposal.get("totalBeneficiaryShare").toString()) );
        dto.setGeneralDetails((GeneralDetails) proposal.get("generateDetails"));
        dto.setCompulsoryHealthStatement((List<Question>) proposal.get("compulsoryHealthStatement"));
        dto.setFamilyPersonalDetail((FamilyPersonalDetail) proposal.get("familyPersonalDetail"));
        dto.setAdditionaldetails((AdditionalDetails) proposal.get("additionaldetails"));
        dto.setPremiumPaymentDetails((PremiumPaymentDetails) proposal.get("premiumPaymentDetails"));
        // TODO : commenting for time being untill premium tab details completed
        /* if(dto.getProposalPlanDetail() != null)
        dto.setPremiumDetailDto(getPremiumDetail(proposalId)); */
        // TODO : need to set document details once it is ready

        AgentCommissionShareModel model = (AgentCommissionShareModel) proposal.get("agentCommissionShareModel");

        Set<AgentDetailDto> agentCommissionDetails = new HashSet<AgentDetailDto>();
        model.getCommissionShare().forEach(commissionShare -> agentCommissionDetails.add(new AgentDetailDto(commissionShare.getAgentId().toString(), getAgentFullNameById(commissionShare.getAgentId().toString()), commissionShare.getAgentCommission())));
        dto.setAgentCommissionDetails(agentCommissionDetails);
        dto.setProposalId(proposalId);
        dto.setProposalStatus(proposal.get("proposalStatus").toString());
        dto.setProposalNumber(proposal.get("proposalNumber").toString());
        return dto;
    }

    public String getProposalNumberById(String proposalId) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", proposalId);
        Map proposal = mongoTemplate.findOne(new BasicQuery(query), Map.class, "individual_life_proposal");
        return proposal.get("proposalNumber").toString();
    }

    public List<Map<String, Object>> getPlans(String proposalId) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", proposalId);
        Map proposal = mongoTemplate.findOne(new BasicQuery(query), Map.class, "individual_life_proposal");

        List <String> agentIds = new ArrayList<String>();
        ((AgentCommissionShareModel) proposal.get("agentCommissionShareModel")).getCommissionShare().stream().forEach(x -> agentIds.add(x.getAgentId().toString()));
        List<Map<String, Object>>  result =  namedParameterJdbcTemplate.query(SEARCH_PLAN_BY_AGENT_IDS, new MapSqlParameterSource().addValue("agentIds", agentIds).addValue("lineOfBusiness", "Individual Life"), new ColumnMapRowMapper());
        return result;

    }
}
