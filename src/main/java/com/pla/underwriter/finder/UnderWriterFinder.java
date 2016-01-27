package com.pla.underwriter.finder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.Lists;
import com.pla.publishedlanguage.dto.UnderWriterRoutingLevelDetailDto;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.UnderWriterDocumentId;
import com.pla.underwriter.domain.model.UnderWriterDocument;
import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import com.pla.underwriter.domain.model.UnderWriterProcessType;
import com.pla.underwriter.domain.model.UnderWriterRoutingLevel;
import com.pla.underwriter.repository.UnderWriterDocumentRepository;
import com.pla.underwriter.repository.UnderWriterRoutingLevelRepository;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 5/13/2015.
 */
@Service
public class UnderWriterFinder {

    private ObjectMapper objectMapper;

    private UnderWriterDocumentRepository underWriterDocumentRepository;

    private UnderWriterRoutingLevelRepository underWriterRoutingLevelRepository;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public UnderWriterFinder(UnderWriterDocumentRepository underWriterDocumentRepository, UnderWriterRoutingLevelRepository underWriterRoutingLevelRepository) {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
        this.underWriterDocumentRepository = underWriterDocumentRepository;
        this.underWriterRoutingLevelRepository = underWriterRoutingLevelRepository;
    }


    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public static final String FIND_ALL_DOCUMENT_APPROVED_BY_SERVICE_PROVIDER = "SELECT documentName,documentCode FROM document_view WHERE isProvided = 'YES'";

    public static final String FIND_DOCUMENT_DETAIL_BY_DOCUMENT_CODE = "SELECT documentName,documentCode FROM document_view WHERE documentCode in(:documentIds)";

    public static final String FIND_PLAN_COVERAGE_DETAIL_BY_PLAN_CODE = "SELECT DISTINCT planName,c.coverage_name coverageName,planCode FROM  plan_coverage_benefit_assoc_view p INNER JOIN coverage c " +
            "   ON coverageId = c.coverage_id " +
            "   WHERE planId=:planId AND coverageId=:coverageId AND optional ='1' ";

    public static final String FIND_PLAN_NAME_BY_CODE = "SELECT planName,planCode FROM plan_coverage_benefit_assoc_view WHERE planId =:planId LIMIT 1";




    public List<Map> findAllUnderWriterDocument() {
        List<UnderWriterDocument> allUnderWriterDocument = underWriterDocumentRepository.findEffectiveUnderWriterDocument();
        List<Map> underWriterDocumentList = new ArrayList<Map>();
        for (UnderWriterDocument underWriterDocument : allUnderWriterDocument) {
            Map<String, Object> underWriterDocumentMap = objectMapper.convertValue(underWriterDocument, Map.class);
            underWriterDocumentMap.put("processType", UnderWriterProcessType.valueOf((String) underWriterDocumentMap.get("processType")).getDescription());
            String coverageId = underWriterDocument.getCoverageId() != null ? underWriterDocument.getCoverageId().getCoverageId() : null;
            underWriterDocumentMap = planCoverageDetailTransformer(coverageId, underWriterDocument.getPlanId().getPlanId(), underWriterDocumentMap);
            underWriterDocumentMap.put("effectiveFrom", underWriterDocumentMap.get("effectiveFrom") != null ? DateTime.parse(underWriterDocumentMap.get("effectiveFrom").toString()).toString(AppConstants.DD_MM_YYY_FORMAT) : null);
            underWriterDocumentMap.put("validTill", underWriterDocumentMap.get("validTill") != null ? DateTime.parse(underWriterDocumentMap.get("validTill").toString()).toString(AppConstants.DD_MM_YYY_FORMAT) : null);
            underWriterDocumentList.add(underWriterDocumentMap);
        }
        return underWriterDocumentList;
    }

    public List<Map> findAllUnderWriterRoutingLevel() {
        List<UnderWriterRoutingLevel> allUnderWriterRoutingLevel = underWriterRoutingLevelRepository.findAll();
        List<Map> underWritingRoutingLevelList = new ArrayList<Map>();
        for (UnderWriterRoutingLevel underWriterRoutingLevel : allUnderWriterRoutingLevel) {
            Map underWritingRoutingLevelMap = objectMapper.convertValue(underWriterRoutingLevel, Map.class);
            underWritingRoutingLevelMap = underWriterInfluencingFactorAndProcessTypeTransformer(underWritingRoutingLevelMap);
            String coverageId = underWriterRoutingLevel.getCoverageId() != null ? underWriterRoutingLevel.getCoverageId().getCoverageId() : null;
            underWritingRoutingLevelMap = planCoverageDetailTransformer(coverageId, underWriterRoutingLevel.getPlanId().getPlanId(), underWritingRoutingLevelMap);
            underWritingRoutingLevelMap.put("effectiveFrom", underWritingRoutingLevelMap.get("effectiveFrom") != null ? DateTime.parse(underWritingRoutingLevelMap.get("effectiveFrom").toString()).toString(AppConstants.DD_MM_YYY_FORMAT) : null);
            underWritingRoutingLevelMap.put("validTill", underWritingRoutingLevelMap.get("validTill") != null ? DateTime.parse(underWritingRoutingLevelMap.get("validTill").toString()).toString(AppConstants.DD_MM_YYY_FORMAT) : null);
            underWritingRoutingLevelList.add(underWritingRoutingLevelMap);
        }
        return underWritingRoutingLevelList;
    }

    private Map underWriterInfluencingFactorAndProcessTypeTransformer(Map underWritingRoutingLevelMap) {
        List underWriterRoutingLevelInfluencingFactor = (List) underWritingRoutingLevelMap.get("underWriterInfluencingFactors");
        underWritingRoutingLevelMap.put("underWriterInfluencingFactors", underWriterRoutingLevelInfluencingFactor.parallelStream().map(new Function<Object, String>() {
            @Override
            public String apply(Object underWriterInfluencingFactor) {
                return UnderWriterInfluencingFactor.valueOf((String) underWriterInfluencingFactor).getDescription();
            }
        }).collect(Collectors.toList()));
        underWritingRoutingLevelMap.put("processType", UnderWriterProcessType.valueOf((String) underWritingRoutingLevelMap.get("processType")).getDescription());
        return underWritingRoutingLevelMap;
    }

    public UnderWriterRoutingLevel findUnderWriterRoutingLevel(UnderWriterRoutingLevelDetailDto underWriterRoutingLevelDetailDto) {
        List<UnderWriterRoutingLevel> underWriterRoutingLevel = underWriterRoutingLevelRepository.findByPlanCodeAndCoverageIdAndValidTillAndProcessType(underWriterRoutingLevelDetailDto.getPlanId(), underWriterRoutingLevelDetailDto.getCoverageId(),
                null, underWriterRoutingLevelDetailDto.getProcess());
        checkArgument(isNotEmpty(underWriterRoutingLevel), "Under Writer Router Level can not be null");
        checkArgument(underWriterRoutingLevel.size() == 1);
        return underWriterRoutingLevel.get(0);
    }

    public UnderWriterRoutingLevel findUnderWriterRoutingLevelWithoutCoverageDetails(UnderWriterRoutingLevelDetailDto underWriterRoutingLevelDetailDto) {
        List<UnderWriterRoutingLevel> underWriterRoutingLevel = underWriterRoutingLevelRepository.findAllByPlanIdAndProcessType(underWriterRoutingLevelDetailDto.getPlanId(), underWriterRoutingLevelDetailDto.getProcess());
        checkArgument(isNotEmpty(underWriterRoutingLevel), "Under Writer Router Level can not be null");
        checkArgument(underWriterRoutingLevel.size() == 1);
        return underWriterRoutingLevel.get(0);
    }

    public List<Map<String, Object>> getAllDocumentApprovedByServiceProvider() {
        return namedParameterJdbcTemplate.query(FIND_ALL_DOCUMENT_APPROVED_BY_SERVICE_PROVIDER, new ColumnMapRowMapper());
    }

    public UnderWriterDocument getUnderWriterDocumentSetUp(PlanId planId, CoverageId coverageId, LocalDate effectiveFrom, String processType) {
        List<UnderWriterDocument> underWriterDocument = underWriterDocumentRepository.findUnderWriterDocument(planId, coverageId, effectiveFrom, null, processType);
        checkArgument(isNotEmpty(underWriterDocument), "Under Writer Document can not be null");
        checkArgument(underWriterDocument.size() == 1);
        return underWriterDocument.get(0);
    }

    public Map<String, Object> getUnderWriterDocumentById(String underWriterRoutingLevelId) {
        UnderWriterDocument underWriterDocument = underWriterDocumentRepository.findOne(new UnderWriterDocumentId(underWriterRoutingLevelId));
        Map<String, Object> underWriterDocumentMap = objectMapper.convertValue(underWriterDocument, Map.class);
        List<Map<String, Object>> underWriterLineItemList = underWriterDocument.transformUnderWriterDocumentLineItem();
        underWriterLineItemList = underWriterLineItemList.stream().map(new Function<Map<String, Object>, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(Map<String, Object> underWriterDocumentLineItem) {
                Set<String> documentIds = (Set<String>) underWriterDocumentLineItem.get("underWriterDocuments");
                underWriterDocumentLineItem.put("underWriterDocuments", getDocumentDetailByDocumentCode(Lists.newArrayList(documentIds)));
                return underWriterDocumentLineItem;
            }
        }).collect(Collectors.toList());
        String coverageId = underWriterDocument.getCoverageId() != null ? underWriterDocument.getCoverageId().getCoverageId() : null;
        underWriterDocumentMap = planCoverageDetailTransformer(coverageId, underWriterDocument.getPlanId().getPlanId(), underWriterDocumentMap);
        underWriterDocumentMap.put("underWriterDocumentItems", underWriterLineItemList);
        return underWriterDocumentMap;
    }

    private List<Map<String, Object>> getDocumentDetailByDocumentCode(List<String> listOfDocumentCode) {
        return namedParameterJdbcTemplate.query(FIND_DOCUMENT_DETAIL_BY_DOCUMENT_CODE, new MapSqlParameterSource("documentIds", listOfDocumentCode), new ColumnMapRowMapper());
    }

    private Map<String, Object> planCoverageDetailTransformer(String coverageId, String planId, Map<String, Object> underWriterMap) {
        underWriterMap.put("planName", "");
        underWriterMap.put("coverageName", "");
        if (isNotEmpty(coverageId)) {
            SqlParameterSource sqlParameterSource = new MapSqlParameterSource("planId", planId).addValue("coverageId", coverageId);
            List<Map<String, Object>> planCoverageDetail = namedParameterJdbcTemplate.query(FIND_PLAN_COVERAGE_DETAIL_BY_PLAN_CODE, sqlParameterSource, new ColumnMapRowMapper());
            if (isNotEmpty(planCoverageDetail)) {
                underWriterMap.put("planName", planCoverageDetail.get(0).get("planName"));
                underWriterMap.put("coverageName", planCoverageDetail.get(0).get("coverageName"));
                underWriterMap.put("planCode", planCoverageDetail.get(0).get("planCode"));
            }
        } else {
            SqlParameterSource sqlParameterSource = new MapSqlParameterSource("planId", planId);
            List<Map<String,Object>> planDetailList =  namedParameterJdbcTemplate.query(FIND_PLAN_NAME_BY_CODE, sqlParameterSource, new ColumnMapRowMapper());
            if (isNotEmpty(planDetailList)) {
                underWriterMap.put("planName", planDetailList.get(0).get("planName"));
                underWriterMap.put("planCode", planDetailList.get(0).get("planCode"));
            }
        }
        return underWriterMap;
    }

    public List<Map<String, Object>> getMandatoryDocumentForPlanAndCoverage(String planId, List<CoverageId> coverageIds, ProcessType processType) {
        String findMandatoryDocumentByProcessType = "SELECT ms.document_code documentCode,d.document_name documentName FROM mandatory_document md INNER JOIN mandatory_documents ms   " +
                "   ON md.document_id = ms.document_id  " +
                "   INNER JOIN document d ON ms.document_code = d.document_code " +
                "   WHERE md.process = :processType  AND md.plan_id=:planId ";
        List<String> coverageIdsInString = null;
        if (isNotEmpty(coverageIds)) {
            coverageIdsInString = coverageIds.stream().map(new Function<CoverageId, String>() {
                @Override
                public String apply(CoverageId coverageId) {
                    return coverageId.getCoverageId();
                }
            }).collect(Collectors.toList());
        }
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("processType", processType.name()).addValue("planId", planId).addValue("coverageIds", coverageIdsInString);
        findMandatoryDocumentByProcessType = isEmpty(coverageIdsInString) ? findMandatoryDocumentByProcessType + " AND md.coverage_id is null " : findMandatoryDocumentByProcessType + " AND md.coverage_id in (:coverageIds)";
        return namedParameterJdbcTemplate.query(findMandatoryDocumentByProcessType, sqlParameterSource, new ColumnMapRowMapper());
    }
}

