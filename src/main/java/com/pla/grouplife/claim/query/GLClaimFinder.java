package com.pla.grouplife.claim.query;

import com.google.common.collect.Lists;
import com.pla.core.dto.MandatoryDocumentDto;
import com.pla.grouplife.claim.presentation.dto.ClaimMandatoryDocumentDto;
import com.pla.publishedlanguage.dto.SearchDocumentDetailDto;
import com.pla.sharedkernel.domain.model.Gender;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.underwriter.finder.UnderWriterFinder;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by ak
 */
@Finder
@Service

public class GLClaimFinder{

    private static final String GL_POLICY_COLLECTION_NAME = "group_life_policy";
    private static final String GL_LIFE_CLAIM_COLLECTION_NAME = "group_life_claim";
     private static final String GL_LIFE_CLAIM_STATUS_COLLECTION_NAME ="group_life_claim_status";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private MongoTemplate mongoTemplate;

   @Autowired
   private UnderWriterFinder underWriterFinder;
    @Autowired
    public void setDataSource(DataSource dataSource, MongoTemplate mongoTemplate) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.mongoTemplate = mongoTemplate;
    }

    public static final String GET_ALL_MANDATORY_DOCUMENT = "SELECT document_id documentId,coverage_id coverageId,plan_id planId,PROCESS PROCESS " +
            " FROM mandatory_document";

    public static final String GET_ALL_DOCUMENTS_ASSOCIATED_WITH_MANDATORY_DOCUMENT ="SELECT d.document_name documentName,d.document_code documentCode,d.is_provided status " +
            " FROM mandatory_documents md INNER JOIN document d ON md.document_code=d.document_code " +
            " WHERE document_id=:documentId ORDER BY d.document_name";

    public static final String GET_MANDATORY_DOCUMENT_BY_ID =" SELECT document_id documentId,coverage_id coverageId,plan_id planId,PROCESS PROCESS " +
            "  FROM mandatory_document WHERE document_id =:documentId";

    public static final String GET_COVERAGE_ID_ASSOCIATED_WITH_PLAN_AND_PROCESS = "SELECT coverage_id coverageId FROM mandatory_document WHERE  " +
            " plan_id=:planId " +
            " AND PROCESS=:processType";

    public static final String GET_ALL_DOCUMENTS_ASSOCIATED_WITH_PLAN_AND_PROCESS = " SELECT document_id documentId,coverage_id coverageId,plan_id planId,PROCESS PROCESS FROM mandatory_document WHERE  " +
            " plan_id=:planId " +
            " AND PROCESS=:processType";

    public static final String GET_ALL_DOCUMENT_NAME_ASSOCIATED_WITH_DOCUMENT_CODE = " SELECT document_name documentName  FROM document WHERE  " +
            " document_code=:documentCode";







    public List<Map> searchPolicy(String policyNumber, String policyHolderName, String clientId, String[] statuses) {

        if (isEmpty(policyHolderName) && isEmpty(policyNumber) && isEmpty(clientId)) {
            return Lists.newArrayList();
        }
        Criteria criteria = Criteria.where("status").in(statuses);
        if (isNotEmpty(policyNumber)) {

            criteria = criteria.and("policyNumber.policyNumber").is(policyNumber);
        }
        if (isNotEmpty(policyHolderName)) {
            criteria = criteria.and("proposer.proposerName").is(policyHolderName);
        }
        if (isNotEmpty(clientId)) {
            criteria = criteria.and("insureds.familyId.familyId").is(clientId);
        }
        Query query = new Query(criteria);
         query.with(new Sort(Sort.Direction.ASC, "policyNumber.policyNumber"));
        return mongoTemplate.find(query, Map.class, GL_POLICY_COLLECTION_NAME);
    }

    public List<Map> assuredSearchDetail(String firstName, String surName, String dateOfBirth, String clientId, String nrcNumber, String manNumber, Gender gender) {
        Criteria criteria = null;
        if (isNotEmpty(clientId)) {
            criteria = Criteria.where("insureds.firstName").is(firstName);
        }
        if (isNotEmpty(surName)) {
            criteria = criteria.and("insureds.lastName").is(surName);

        }
        if (isNotEmpty(dateOfBirth)) {
            criteria = criteria.and("insureds.dateOfBirth").is(dateOfBirth);
        }
        if (isNotEmpty(firstName)) {
            criteria = Criteria.where("insureds.familyId.familyId").is(clientId);
        }
        if (isNotEmpty(nrcNumber)) {
            criteria = criteria.and("insureds.nrcNumber").is(nrcNumber);
        }
        if (isNotEmpty(manNumber)) {
            criteria = criteria.and("insureds.manNumber").is(manNumber);
        }
        if (isNotEmpty(gender.toString())) {
            criteria = criteria.and("insureds.gender").is(gender.toString());
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Map.class, GL_POLICY_COLLECTION_NAME);
        // return null;
    }

    public List<Map> searchPolicy(String policyNumber) {

        if (isEmpty(policyNumber)) {
            return Lists.newArrayList();
        }
        String[] statuses = new String[]{"IN_FORCE"};
        Criteria criteria = Criteria.where("status").in(statuses);
        if (isNotEmpty(policyNumber)) {
            criteria = criteria.and("policyNumber.policyNumber").is(policyNumber);
        }

        Query query = new Query(criteria);
        // query.with(new Sort(Sort.Direction.ASC, "policyNumber.policyNumber"));
        return mongoTemplate.find(query, Map.class, GL_POLICY_COLLECTION_NAME);
    }

public Map findPolicyByPolicyNumber(String policyNumber) {
        return mongoTemplate.findOne(new Query(Criteria.where("policyNumber.policyNumber").is(policyNumber)), Map.class, GL_POLICY_COLLECTION_NAME);
    }


    public List<Map> getClaimIntimationDetail(String claimNumber, String policyNumber, String policyHolderName, String clientId, String assuredName, String assuredClientId, String nrcNumber) {

     /*
        Criteria criteria = null;
        String[] statuses = new String[]{"INTIMATION"};
        criteria = Criteria.where("claimStatus").in(statuses);
        if (isNotEmpty(claimNumber)) {
            criteria = criteria.and("claimNumber.claimNumber").is(claimNumber);
        }
        */

        Criteria criteria = null;
        if (isNotEmpty(claimNumber)) {
            criteria = Criteria.where("claimNumber.claimNumber").is(claimNumber);
        }
        if (isNotEmpty(policyNumber)) {
            criteria = criteria.and("policy.policyNumber.policyNumber").is(policyNumber);
        }
        if (isNotEmpty(policyHolderName)) {
            criteria = criteria.and("policy.policyHolderName").is(policyHolderName);
        }
        if (isNotEmpty(clientId)) {
            criteria = criteria.and("familyId.familyId").is(clientId);
        }
        if (isNotEmpty(assuredName)) {
            criteria = criteria.and("assuredDetail.firstName").is(assuredName);
        }
        if (isNotEmpty(assuredClientId)) {
            criteria = criteria.and("familyId.familyId").is(assuredClientId);
        }
        if (isNotEmpty(nrcNumber)) {
            criteria = criteria.and("assuredDetail.nrcNumber").is(nrcNumber);
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Map.class, GL_LIFE_CLAIM_COLLECTION_NAME);

    }

    public Map getIntimationDetail(String claimId){
        Criteria criteria = null;
        if (isNotEmpty(claimId)) {
            criteria = Criteria.where("_id").is(claimId);
        }
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, Map.class, GL_LIFE_CLAIM_COLLECTION_NAME);
    }

    public Map getClaimStatusReviewDetail(String claimId){
        Criteria criteria = null;
        String[] statuses = new String[]{"APPROVED"};
        criteria = Criteria.where("claimStatus").in(statuses);

        if (isNotEmpty(claimId)) {
            criteria = criteria.and("claimId").is(claimId);
        }
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, Map.class,GL_LIFE_CLAIM_STATUS_COLLECTION_NAME);
    }




    public List<Map> getClaimDetails(String claimNumber, String policyNumber, String policyHolderName, String clientId, String assuredName, String nrcNumber) {

        Criteria criteria = null;
        String[] statuses = new String[]{"EVALUATION"};
        criteria = Criteria.where("claimStatus").in(statuses);

        if (isNotEmpty(claimNumber)) {
            criteria = criteria.and("claimNumber.claimNumber").is(claimNumber);
        }
        if (isNotEmpty(policyNumber)) {
            criteria = criteria.and("policy.policyNumber.policyNumber").is(policyNumber);
        }
        if (isNotEmpty(policyHolderName)) {
            criteria = criteria.and("policy.policyHolderName").is(policyHolderName);
        }
        if (isNotEmpty(clientId)) {
            criteria = criteria.and("familyId.familyId").is(clientId);
        }
        if (isNotEmpty(assuredName)) {
            criteria = criteria.and("assuredDetail.firstName").is(assuredName);
        }
        if (isNotEmpty(nrcNumber)) {
            criteria = criteria.and("assuredDetail.nrcNumber").is(nrcNumber);
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Map.class, GL_LIFE_CLAIM_COLLECTION_NAME);
    }

    public List<Map> getAllApprovedClaimRecords(String[] statuses) {
        if (isEmpty(statuses)) {
            return Lists.newArrayList();
        }
        Criteria criteria = null;
        criteria = Criteria.where("claimStatus").in(statuses);
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Map.class, GL_LIFE_CLAIM_COLLECTION_NAME);
    }
    //get searched  approved claim records


     public List<Map> getRequiredApprovedClaimDetails(String claimNumber, String policyNumber, String policyHolderName, String clientId, String assuredName, String nrcNumber,String[] statuses) {
        if (isEmpty(claimNumber) && isEmpty(policyNumber) && isEmpty(policyHolderName)&& isEmpty(clientId)&& isEmpty(assuredName)&& isEmpty(nrcNumber)) {
            return Lists.newArrayList();
        }

        Criteria criteria = null;

        criteria = Criteria.where("claimStatus").in(statuses);

        if (isNotEmpty(claimNumber)) {
            criteria = criteria.and("claimNumber.claimNumber").is(claimNumber);
        }
        if (isNotEmpty(policyNumber)) {
            criteria = criteria.and("policy.policyNumber.policyNumber").is(policyNumber);
        }
        if (isNotEmpty(policyHolderName)) {
            criteria = criteria.and("policy.policyHolderName").is(policyHolderName);
        }
        if (isNotEmpty(clientId)) {
            criteria = criteria.and("familyId.familyId").is(clientId);
        }
        if (isNotEmpty(assuredName)) {
            criteria = criteria.and("assuredDetail.firstName").is(assuredName);
        }
        if (isNotEmpty(nrcNumber)) {
            criteria = criteria.and("assuredDetail.nrcNumber").is(nrcNumber);
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Map.class, GL_LIFE_CLAIM_COLLECTION_NAME);
    }




    public List<Map> getApprovedClaimDetails(String claimNumber, String policyNumber, String policyHolderName, String clientId, String assuredName, String nrcNumber,String[] statuses) {
        if (isEmpty(claimNumber) && isEmpty(policyNumber) && isEmpty(policyHolderName)&& isEmpty(clientId)&& isEmpty(assuredName)&& isEmpty(nrcNumber)) {
            return Lists.newArrayList();
        }

        Criteria criteria = null;

        criteria = Criteria.where("claimStatus").in(statuses);

        if (isNotEmpty(claimNumber)) {
            criteria = criteria.and("claimNumber.claimNumber").is(claimNumber);
        }
        if (isNotEmpty(policyNumber)) {
            criteria = criteria.and("policy.policyNumber.policyNumber").is(policyNumber);
        }
        if (isNotEmpty(policyHolderName)) {
            criteria = criteria.and("policy.policyHolderName").is(policyHolderName);
        }
        if (isNotEmpty(clientId)) {
            criteria = criteria.and("familyId.familyId").is(clientId);
        }
        if (isNotEmpty(assuredName)) {
            criteria = criteria.and("assuredDetail.firstName").is(assuredName);
        }
        if (isNotEmpty(nrcNumber)) {
            criteria = criteria.and("assuredDetail.nrcNumber").is(nrcNumber);
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Map.class, GL_LIFE_CLAIM_COLLECTION_NAME);
    }



    public List<Map> getApprovedClaimDetailsLevelOne(String claimNumber, String policyNumber, String policyHolderName, String clientId, String assuredName, String nrcNumber,String[] statuses) {
        if (isEmpty(claimNumber) && isEmpty(policyNumber) && isEmpty(policyHolderName)&& isEmpty(clientId)&& isEmpty(assuredName)&& isEmpty(nrcNumber)) {
            return Lists.newArrayList();
        }

        Criteria criteria = null;

        criteria = Criteria.where("claimStatus").in(statuses);

        if (isNotEmpty(claimNumber)) {
            criteria = criteria.and("claimNumber.claimNumber").is(claimNumber);
        }
        if (isNotEmpty(policyNumber)) {
            criteria = criteria.and("policy.policyNumber.policyNumber").is(policyNumber);
        }
        if (isNotEmpty(policyHolderName)) {
            criteria = criteria.and("policy.policyHolderName").is(policyHolderName);
        }
        if (isNotEmpty(clientId)) {
            criteria = criteria.and("familyId.familyId").is(clientId);
        }
        if (isNotEmpty(assuredName)) {
            criteria = criteria.and("assuredDetail.firstName").is(assuredName);
        }
        if (isNotEmpty(nrcNumber)) {
            criteria = criteria.and("assuredDetail.nrcNumber").is(nrcNumber);
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Map.class, GL_LIFE_CLAIM_COLLECTION_NAME);
    }



    public List<Map> findClaimStatusDetailsById(String claimId) {
        Criteria criteria = null;
       // String[] statuses = new String[]{"APPROVED"};

        String status="APPROVED";
        // criteria = Criteria.where("claimStatus").is(status);
        //criteria = Criteria.where("claimId").is(claimId);


       // return mongoTemplate.find(query, Map.class,GL_LIFE_CLAIM_STATUS_COLLECTION_NAME);
        return null;
    }

    public List<Map> getReopenClaimDetails(String claimNumber, String policyNumber, String policyHolderName, String clientId, String assuredName, String nrcNumber) {
        if (isEmpty(claimNumber) && isEmpty(policyNumber) && isEmpty(policyHolderName)&& isEmpty(clientId) && isEmpty(assuredName)  && isEmpty( nrcNumber)) {
            return Lists.newArrayList();
        }


        Criteria criteria = null;
        String[] statuses = new String[]{"CANCELLED","REPUDIATED"};
        criteria = Criteria.where("claimStatus").in(statuses);

        if (isNotEmpty(claimNumber)) {
            criteria = criteria.and("claimNumber.claimNumber").is(claimNumber);
        }
        if (isNotEmpty(policyNumber)) {
            criteria = criteria.and("policy.policyNumber.policyNumber").is(policyNumber);
        }
        if (isNotEmpty(policyHolderName)) {
            criteria = criteria.and("policy.policyHolderName").is(policyHolderName);
        }
        if (isNotEmpty(clientId)) {
            criteria = criteria.and("familyId.familyId").is(clientId);
        }
        if (isNotEmpty(assuredName)) {
            criteria = criteria.and("assuredDetail.firstName").is(assuredName);
        }
        if (isNotEmpty(nrcNumber)) {
            criteria = criteria.and("assuredDetail.nrcNumber").is(nrcNumber);
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Map.class, GL_LIFE_CLAIM_COLLECTION_NAME);
    }

    public List<Map> getEarlierAssuredClaimDetails(String firstName,String lastName,BigDecimal amount){
        Criteria criteria = null;

        criteria = Criteria.where("assuredDetail.firstName").is(firstName);
        if (isNotEmpty(lastName)) {
            criteria = criteria.and("assuredDetail.lastName").is(lastName);
        }
        if (isNotEmpty(amount)) {
            criteria = criteria.and("claimAmount").is(amount);
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Map.class, GL_LIFE_CLAIM_COLLECTION_NAME);
    }


    public Map findClaimById(String claimId) {
        checkArgument(claimId != null);
        return mongoTemplate.findOne(new Query(Criteria.where("_id").is(claimId)), Map.class,"group_life_claim");
    }

    public Map findProposerByPolicyNumber(String policyNumber) {
        checkArgument(policyNumber != null);
        return mongoTemplate.findOne(new Query(Criteria.where("policyNumber.policyNumber").is(policyNumber)), Map.class,GL_POLICY_COLLECTION_NAME);
    }
    public List<ClaimMandatoryDocumentDto> getMandatoryDocuments(SearchDocumentDetailDto searchDocumentDetailDto, ProcessType processType){
        SqlParameterSource sqlParameterSource =  new MapSqlParameterSource("planId",searchDocumentDetailDto.getPlanId()).addValue("processType", processType.toString());
        List<ClaimMandatoryDocumentDto> listOfMandatoryDocuments =  namedParameterJdbcTemplate.query(GET_ALL_DOCUMENTS_ASSOCIATED_WITH_PLAN_AND_PROCESS, sqlParameterSource, new BeanPropertyRowMapper<ClaimMandatoryDocumentDto>(ClaimMandatoryDocumentDto.class));
       // List<ClaimMandatoryDocumentDto> listOfMandatoryDocuments =  namedParameterJdbcTemplate.queryForList(GET_ALL_DOCUMENTS_ASSOCIATED_WITH_PLAN_AND_PROCESS, sqlParameterSource,ClaimMandatoryDocumentDto.class);
        for (ClaimMandatoryDocumentDto mandatoryDocumentDto : listOfMandatoryDocuments){
            SqlParameterSource parameterSource = new MapSqlParameterSource("documentId",mandatoryDocumentDto.getDocumentId());
            Map  listOfDocument = namedParameterJdbcTemplate.queryForMap(GET_ALL_DOCUMENTS_ASSOCIATED_WITH_MANDATORY_DOCUMENT, parameterSource);
            mandatoryDocumentDto.setDocumentCode((String)listOfDocument.get("document_code"));
            mandatoryDocumentDto.setDocumentName((String)listOfDocument.get("document_name"));
        }

   return  listOfMandatoryDocuments;
    }

    public List<ClaimMandatoryDocumentDto> getAllMandatoryDocuments(String planId, ProcessType processType) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("planId", planId).addValue("processType", processType.toString());
        List<ClaimMandatoryDocumentDto> listOfMandatoryDocuments = namedParameterJdbcTemplate.query(GET_ALL_DOCUMENTS_ASSOCIATED_WITH_PLAN_AND_PROCESS, sqlParameterSource, new BeanPropertyRowMapper<ClaimMandatoryDocumentDto>(ClaimMandatoryDocumentDto.class));
        for (ClaimMandatoryDocumentDto mandatoryDocumentDto : listOfMandatoryDocuments) {
            SqlParameterSource parameterSource = new MapSqlParameterSource("documentId", mandatoryDocumentDto.getDocumentId());
            Map listOfDocument = namedParameterJdbcTemplate.queryForMap(GET_ALL_DOCUMENTS_ASSOCIATED_WITH_MANDATORY_DOCUMENT, parameterSource);
            mandatoryDocumentDto.setDocumentCode((String) listOfDocument.get("document_code"));
            mandatoryDocumentDto.setDocumentName((String) listOfDocument.get("document_name"));
        }

        return listOfMandatoryDocuments;
    }

    public List<MandatoryDocumentDto> getAllClaimMandatoryDocuments(String planId, ProcessType processType) {
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("planId", planId).addValue("processType", processType.toString());
        List<MandatoryDocumentDto> listOfMandatoryDocuments = namedParameterJdbcTemplate.query(GET_ALL_DOCUMENTS_ASSOCIATED_WITH_PLAN_AND_PROCESS, sqlParameterSource, new BeanPropertyRowMapper<MandatoryDocumentDto>(MandatoryDocumentDto.class));
        for (MandatoryDocumentDto mandatoryDocumentDto : listOfMandatoryDocuments) {
            Long docId = mandatoryDocumentDto.getDocumentId();
            listOfMandatoryDocuments = getMandatoryDocumentById(docId);
        }

       return listOfMandatoryDocuments;
    }
 public List<MandatoryDocumentDto> getMandatoryDocumentById(Long documentId){
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("documentId",documentId);
        List<MandatoryDocumentDto> mandatoryDocumentDtos =  namedParameterJdbcTemplate.query(GET_MANDATORY_DOCUMENT_BY_ID, sqlParameterSource, new BeanPropertyRowMapper(MandatoryDocumentDto.class));
        List<Map<String,Object>>  listOfDocument = namedParameterJdbcTemplate.query(GET_ALL_DOCUMENTS_ASSOCIATED_WITH_MANDATORY_DOCUMENT, sqlParameterSource, new ColumnMapRowMapper());
        for (MandatoryDocumentDto mandatoryDocumentDto : mandatoryDocumentDtos){
            List<String> document = Lists.newArrayList();
            for (Map<String,Object> documents :listOfDocument) {
                document.add((String) documents.get("documentName"));
                mandatoryDocumentDto.setDocument(document);
            }
            mandatoryDocumentDto.setDocuments(listOfDocument);
        }
        return mandatoryDocumentDtos;
    }
    public List<Map> getClaimReviewByClaimId(String claimId){
        if(isEmpty(claimId)){
        return Lists.newArrayList();
    }
        Criteria criteria = null;
        String[] statuses = new String[]{"EVALUATION","ROUTED"};
        criteria = Criteria.where("claimStatus").in(statuses);
        if (isNotEmpty(claimId)) {
            criteria = criteria.and("claimId").is(claimId);
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query, Map.class, GL_LIFE_CLAIM_STATUS_COLLECTION_NAME);

    }


}

