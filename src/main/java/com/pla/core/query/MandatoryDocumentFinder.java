package com.pla.core.query;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.pla.core.dto.MandatoryDocumentDto;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * Created by Admin on 3/31/2015.
 */
@Finder
@Service
public class MandatoryDocumentFinder {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public static final String GET_ALL_MANDATORY_DOCUMENT_QUERY = "SELECT document_id documentId,coverage_id coverageId,plan_id planId,PROCESS PROCESS " +
            " FROM mandatory_document";

    public static final String GET_ALL_DOCUMENTS_ASSOCIATED_WITH_MANDATORY_DOCUMENT_QUERY ="SELECT d.document_name documentName,d.document_code documentCode " +
            " FROM mandatory_documents md INNER JOIN document d ON md.document_code=d.document_code " +
            " WHERE document_id=:documentId ";

    public static final String GET_MANDATORY_DOCUMENT_BY_ID_QUERY =" SELECT document_id documentId,coverage_id coverageId,plan_id planId,PROCESS PROCESS " +
            "  FROM mandatory_document WHERE document_id =:documentId";

    public static final String GET_COVERAGE_ID_ASSOCIATED_WITH_PLAN_AND_PROCESS_QUERY = "SELECT coverage_id coverageId FROM mandatory_document WHERE  " +
            " plan_id=:planId " +
            " AND PROCESS=:processType";

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public List<MandatoryDocumentDto> getAllMandatoryDocument(){
        List<MandatoryDocumentDto> listOfMandatoryDocument =  namedParameterJdbcTemplate.query(GET_ALL_MANDATORY_DOCUMENT_QUERY, new BeanPropertyRowMapper(MandatoryDocumentDto.class));
        for (MandatoryDocumentDto mandatoryDocumentDto : listOfMandatoryDocument){
            SqlParameterSource sqlParameterSource = new MapSqlParameterSource("documentId",mandatoryDocumentDto.getDocumentId());
            List<Map<String,Object>>  listOfDocument = namedParameterJdbcTemplate.query(GET_ALL_DOCUMENTS_ASSOCIATED_WITH_MANDATORY_DOCUMENT_QUERY, sqlParameterSource, new ColumnMapRowMapper());
            mandatoryDocumentDto.setDocuments(listOfDocument);
        }
        return listOfMandatoryDocument;
    }

    public List<MandatoryDocumentDto> getMandatoryDocumentById(Long documentId){
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("documentId",documentId);
        List<MandatoryDocumentDto> mandatoryDocumentDtos =  namedParameterJdbcTemplate.query(GET_MANDATORY_DOCUMENT_BY_ID_QUERY, sqlParameterSource, new BeanPropertyRowMapper(MandatoryDocumentDto.class));
        List<Map<String,Object>>  listOfDocument = namedParameterJdbcTemplate.query(GET_ALL_DOCUMENTS_ASSOCIATED_WITH_MANDATORY_DOCUMENT_QUERY, sqlParameterSource, new ColumnMapRowMapper());
        for (MandatoryDocumentDto mandatoryDocumentDto : mandatoryDocumentDtos){
            List<String> document = Lists.newArrayList();
            for (Map<String,Object> documents :listOfDocument) {
                document.add((String) documents.get("documentCode"));
                mandatoryDocumentDto.setDocument(document);
            }
        }
        return mandatoryDocumentDtos;
    }

    public int getMandatoryDocumentCountBy(String planId,String process,String coverageId){
        Preconditions.checkNotNull(planId);
        SqlParameterSource sqlParameterSource =  new MapSqlParameterSource("planId",planId).addValue("processType", process);
        List<Map<String,Object>> optionalCoverageAssociatedWithPlan = namedParameterJdbcTemplate.query(GET_COVERAGE_ID_ASSOCIATED_WITH_PLAN_AND_PROCESS_QUERY, sqlParameterSource, new ColumnMapRowMapper());
        for (Map<String,Object> optionalCoverageMap : optionalCoverageAssociatedWithPlan){
            String optionalCoverageId = (String) optionalCoverageMap.get("coverageId");
            if (coverageId==null && optionalCoverageId == null){
                return 1;
            }
            if (coverageId!= null?coverageId.equals(optionalCoverageId):"".equals(optionalCoverageId)) {
                return 1;
            }
        }
        return 0;
    }

}
