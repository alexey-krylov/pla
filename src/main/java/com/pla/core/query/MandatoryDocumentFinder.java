package com.pla.core.query;

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

    public static final String GET_ALL_DOCUMENTS_ASSOCIATED_WITH_MANDATORY_DOCUMENT_QUERY ="SELECT d.document_name documentName " +
            " FROM mandatory_documents md INNER JOIN document d ON md.document_code=d.document_code " +
            " WHERE document_id=:documentId ";

    public static final String GET_MANDATORY_DOCUMENT_BY_ID_QUERY =" SELECT document_id documentId,coverage_id coverageId,plan_id planId,PROCESS PROCESS " +
            "  FROM mandatory_document WHERE document_id =:documentId";

    public static final String GET_COVERAGE_NAME_FOR_GIVEN_COVERAGE_ID_QUERY =" SELECT coverage_name coverageName FROM coverage WHERE coverage_id =:coverageId ";

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

    public String getCoverageNameById(String coverageId){
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("coverageId",coverageId);
        return namedParameterJdbcTemplate.queryForObject(GET_COVERAGE_NAME_FOR_GIVEN_COVERAGE_ID_QUERY,sqlParameterSource,String.class);
    }

    public List<MandatoryDocumentDto> getMandatoryDocumentById(Long documentId){
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("documentId",documentId);
        List<MandatoryDocumentDto> mandatoryDocumentDtos =  namedParameterJdbcTemplate.query(GET_MANDATORY_DOCUMENT_BY_ID_QUERY, sqlParameterSource, new BeanPropertyRowMapper(MandatoryDocumentDto.class));
        List<Map<String,Object>>  listOfDocument = namedParameterJdbcTemplate.query(GET_ALL_DOCUMENTS_ASSOCIATED_WITH_MANDATORY_DOCUMENT_QUERY, sqlParameterSource, new ColumnMapRowMapper());
       for (MandatoryDocumentDto mandatoryDocumentDto : mandatoryDocumentDtos){
           mandatoryDocumentDto.setDocuments(listOfDocument);
       }
        return mandatoryDocumentDtos;
    }
}
