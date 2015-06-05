package com.pla.underwriter.finder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.pla.publishedlanguage.dto.UnderWriterRoutingLevelDetailDto;
import com.pla.underwriter.domain.model.UnderWriterDocument;
import com.pla.underwriter.domain.model.UnderWriterRoutingLevel;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 5/13/2015.
 */
@Service
public class UnderWriterFinder {

    private MongoTemplate mongoTemplate;

    private ObjectMapper objectMapper;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public UnderWriterFinder(MongoTemplate mongoTemplate) {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
        this.mongoTemplate = mongoTemplate;
    }


    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public static final String FIND_ALL_DOCUMENT_APPROVED_BY_SERVICE_PROVIDER =  "SELECT documentName,documentCode FROM document_view WHERE isProvided = 'YES'";

    public static final String FIND_PLAN_COVERAGE_DETAIL_BY_PLAN_CODE = " SELECT DISTINCT planName,c.coverage_name coverageName FROM  plan_coverage_benefit_assoc_view p INNER JOIN coverage c " +
            "   ON coverageId = c.coverage_id " +
            "   WHERE planCode=:code AND coverageId=:id AND optional ='0' ";

    public static final String FIND_PLAN_NAME_BY_CODE = "SELECT planName FROM plan_coverage_benefit_assoc_view WHERE planCode =:code LIMIT 1";

    public List<Map> findAllUnderWriterDocument() {

        List<UnderWriterDocument> allUnderWriterDocument = mongoTemplate.findAll(UnderWriterDocument.class, "under_writer_document");
        List<Map> underWriterDocumentList = new ArrayList<Map>();
        for (UnderWriterDocument underWriterDocument : allUnderWriterDocument) {
            Map<String,Object> underWriterDocumentMap = objectMapper.convertValue(underWriterDocument, Map.class);
            underWriterDocumentMap = planCoverageDetailTransformer(underWriterDocument.getCoverageId().getCoverageId(),underWriterDocument.getPlanCode(),underWriterDocumentMap);
            underWriterDocumentList.add(underWriterDocumentMap);
        }
        return underWriterDocumentList;
    }

    private Map<String,Object> planCoverageDetailTransformer(String coverageId,String planCode,Map<String,Object> underWriterMap){
        if (UtilValidator.isNotEmpty(coverageId)) {
            SqlParameterSource sqlParameterSource = new MapSqlParameterSource("code", planCode).addValue("id", coverageId);
            Map<String, Object> planCoverageDetail = namedParameterJdbcTemplate.queryForMap(FIND_PLAN_COVERAGE_DETAIL_BY_PLAN_CODE, sqlParameterSource);
            underWriterMap.put("planName", planCoverageDetail.get("planName"));
            underWriterMap.put("coverageName", planCoverageDetail.get("coverageName"));
        }
        else {
            SqlParameterSource sqlParameterSource = new MapSqlParameterSource("code",planCode);
            String planName =(String) namedParameterJdbcTemplate.queryForMap(FIND_PLAN_NAME_BY_CODE, sqlParameterSource).get("planName");
            underWriterMap.put("planName",planName);
        }
        return underWriterMap;
    }

    public List<Map> findAllUnderWriterRoutingLevel() {
        List<UnderWriterRoutingLevel> allUnderWriterRoutingLevel = mongoTemplate.findAll(UnderWriterRoutingLevel.class, "under_writing_router");
        List<Map> underWritingRoutingLevelList = new ArrayList<Map>();
        for (UnderWriterRoutingLevel underWriterRoutingLevel : allUnderWriterRoutingLevel) {
            Map underWritingRoutingLevelMap = objectMapper.convertValue(underWriterRoutingLevel, Map.class);
            underWritingRoutingLevelMap = planCoverageDetailTransformer(underWriterRoutingLevel.getCoverageId().getCoverageId(),underWriterRoutingLevel.getPlanCode(),underWritingRoutingLevelMap);
            underWritingRoutingLevelList.add(underWritingRoutingLevelMap);
        }
        return underWritingRoutingLevelList;
    }

    public UnderWriterRoutingLevel findUnderWriterRoutingLevel(UnderWriterRoutingLevelDetailDto underWriterRoutingLevelDetailDto) {
        Criteria underWriterCriteria = Criteria.where("planCode").is(underWriterRoutingLevelDetailDto.getPlanCode())
                .and("effectiveFrom").lte(underWriterRoutingLevelDetailDto.getEffectiveFrom().toDate()).and("validTill").is(null);
        underWriterCriteria = underWriterRoutingLevelDetailDto.getCoverageId() != null? underWriterCriteria.and("coverageId.coverageId").is(underWriterRoutingLevelDetailDto.getCoverageId()):
                underWriterCriteria.and("coverageId.coverageId").is(null);
        Query query = new Query(underWriterCriteria);
        List<UnderWriterRoutingLevel> underWriterRoutingLevel = mongoTemplate.find(query, UnderWriterRoutingLevel.class);
        checkArgument(isNotEmpty(underWriterRoutingLevel), "Under Writer Routing Level can not be null");

        checkArgument(underWriterRoutingLevel.size() == 1);
        return underWriterRoutingLevel.get(0);
    }

    public List<Map<String, Object>> getAllDocumentApprovedByServiceProvider() {
        return namedParameterJdbcTemplate.query(FIND_ALL_DOCUMENT_APPROVED_BY_SERVICE_PROVIDER, new ColumnMapRowMapper());
    }
}

