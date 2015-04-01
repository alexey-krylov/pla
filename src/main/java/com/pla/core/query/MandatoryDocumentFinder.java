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

    public static final String GET_ALL_DOCUMENTS_ASSOCIATED_WITH_MANDATORY_DOCUMENT_QUERY ="SELECT document_code documentCode" +
            " FROM mandatory_documents WHERE document_id=:documentId";

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

}