package com.pla.grouphealth.claim.cashless.query;

import com.google.common.collect.Lists;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationRequest;
import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.SearchPreAuthorizationRecordDto;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */
@Finder
@Component
public class PreAuthorizationFinder {

    private MongoTemplate mongoTemplate;
    private  final  String PRE_AUTHORIZATION_DETAIL="PRE_AUTHORIZATION_DETAIL";
    @Autowired
    public void setDataSource(MongoTemplate mongoTemplate) {

        this.mongoTemplate = mongoTemplate;
    }

    public List<PreAuthorizationRequest> searchPreAuthorizationRecord(SearchPreAuthorizationRecordDto searchPreAuthorizationRecordDto, String username){
        if(isEmpty(searchPreAuthorizationRecordDto.getBatchNumber())&& isEmpty(searchPreAuthorizationRecordDto.getClientId()) &&
                isEmpty(searchPreAuthorizationRecordDto.getPolicyNumber())&&isEmpty(searchPreAuthorizationRecordDto.getPreAuthorizationId())&&
                isEmpty(searchPreAuthorizationRecordDto.getHcpCode())) {

            return Lists.newArrayList();
        }
        Query query = new Query();
        query.addCriteria(new Criteria().and("preAuthorizationUnderWriterUserId").is(username));
        if(isNotEmpty(searchPreAuthorizationRecordDto.getBatchNumber())){
            query.addCriteria(new Criteria().and("batchNumber").is(searchPreAuthorizationRecordDto.getBatchNumber()));
        }
        if(isNotEmpty(searchPreAuthorizationRecordDto.getHcpCode())){
            query.addCriteria(new Criteria().and("preAuthorizationRequestHCPDetail.hcpCode").is(searchPreAuthorizationRecordDto.getHcpCode()));
        }
        if(isNotEmpty(searchPreAuthorizationRecordDto.getPolicyNumber())){
            query.addCriteria(new Criteria().and("preAuthorizationRequestPolicyDetail.policyNumber").is(searchPreAuthorizationRecordDto.getPolicyNumber()));
        }
        if(isNotEmpty(searchPreAuthorizationRecordDto.getClientId())){
            query.addCriteria(new Criteria().and("preAuthorizationRequestPolicyDetail.assuredDetail.clientId").is(searchPreAuthorizationRecordDto.getClientId()));
        }
        if(isNotEmpty(searchPreAuthorizationRecordDto.getPreAuthorizationId())){
            query.addCriteria(new Criteria().and("preAuthorizationRequestId.preAuthorizationRequestId").is(searchPreAuthorizationRecordDto.getPreAuthorizationId()));
        }
        query.with(new Sort(Sort.Direction.ASC, "preAuthorizationRequestId.preAuthorizationRequestId"));
        return mongoTemplate.find(query, PreAuthorizationRequest.class, "PRE_AUTHORIZATION_REQUEST");

    }

    public List<PreAuthorizationRequest> getPreAuthorizationRequestByCriteria(SearchPreAuthorizationRecordDto searchPreAuthorizationRecordDto) {
        if(isEmpty(searchPreAuthorizationRecordDto.getBatchNumber())&& isEmpty(searchPreAuthorizationRecordDto.getClientId()) &&
                isEmpty(searchPreAuthorizationRecordDto.getPolicyNumber())&&isEmpty(searchPreAuthorizationRecordDto.getPreAuthorizationId())&&
                isEmpty(searchPreAuthorizationRecordDto.getHcpCode())) {

            return Lists.newArrayList();
        }
        Query query = new Query();
        if(isNotEmpty(searchPreAuthorizationRecordDto.getBatchNumber())){
            query.addCriteria(new Criteria().and("batchNumber").is(searchPreAuthorizationRecordDto.getBatchNumber()));
        }
        if(isNotEmpty(searchPreAuthorizationRecordDto.getHcpCode())){
            query.addCriteria(new Criteria().and("preAuthorizationRequestHCPDetail.hcpCode").is(searchPreAuthorizationRecordDto.getHcpCode()));
        }
        if(isNotEmpty(searchPreAuthorizationRecordDto.getPolicyNumber())){
            query.addCriteria(new Criteria().and("preAuthorizationRequestPolicyDetail.policyNumber").is(searchPreAuthorizationRecordDto.getPolicyNumber()));
        }
        if(isNotEmpty(searchPreAuthorizationRecordDto.getClientId())){
            query.addCriteria(new Criteria().and("preAuthorizationRequestPolicyDetail.assuredDetail.clientId").is(searchPreAuthorizationRecordDto.getClientId()));
        }
        if(isNotEmpty(searchPreAuthorizationRecordDto.getPreAuthorizationId())){
            query.addCriteria(new Criteria().and("preAuthorizationRequestId.preAuthorizationRequestId").is(searchPreAuthorizationRecordDto.getPreAuthorizationId()));
        }
        query.with(new Sort(Sort.Direction.ASC, "preAuthorizationRequestId.preAuthorizationRequestId"));
        return mongoTemplate.find(query, PreAuthorizationRequest.class, "PRE_AUTHORIZATION_REQUEST");
    }
}
