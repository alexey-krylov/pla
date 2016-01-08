package com.pla.grouphealth.claim.cashless.query;

import com.google.common.collect.Lists;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorization;
import com.pla.grouphealth.claim.cashless.presentation.dto.SearchPreAuthorizationRecordDto;
import org.joda.time.DateTime;
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
 * Created by Mohan Sharma on 12/30/2015.
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

    public List<PreAuthorization> searchPreAuthorizationRecord (SearchPreAuthorizationRecordDto searchPreAuthorizationRecordDto){

        if(isEmpty(searchPreAuthorizationRecordDto.getBatchNumber())&& isEmpty(searchPreAuthorizationRecordDto.getClientId()) &&
                isEmpty(searchPreAuthorizationRecordDto.getPolicyNumber())&&isEmpty(searchPreAuthorizationRecordDto.getPreAuthorizationId())&&
                isEmpty(searchPreAuthorizationRecordDto.getHcpCode())) {

            return Lists.newArrayList();
        }
        Query query=new Query();
        if(isNotEmpty(searchPreAuthorizationRecordDto.getBatchNumber())){
            query.addCriteria(new Criteria().and("batchNumber").is(searchPreAuthorizationRecordDto.getBatchNumber()));
        }
        if(isNotEmpty(searchPreAuthorizationRecordDto.getHcpCode())){
            query.addCriteria(new Criteria().and("hcpCode.hcpCode").is(searchPreAuthorizationRecordDto.getHcpCode()));
        }
        if(isNotEmpty(searchPreAuthorizationRecordDto.getPolicyNumber())){
            query.addCriteria(new Criteria().and("preAuthorizationDetails.policyNumber").is(searchPreAuthorizationRecordDto.getPolicyNumber()));
        }
        if(isNotEmpty(searchPreAuthorizationRecordDto.getClientId())){
            query.addCriteria(new Criteria().and("preAuthorizationDetails.clientId").is(searchPreAuthorizationRecordDto.getClientId()));
        }
        if(isNotEmpty(searchPreAuthorizationRecordDto.getPreAuthorizationId())){
            query.addCriteria(new Criteria().and("preAuthorizationId.preAuthorizationId").is(searchPreAuthorizationRecordDto.getPreAuthorizationId()));
        }
        query.with(new Sort(Sort.Direction.ASC, "preAuthorizationDetails.policyNumber"));
        return mongoTemplate.find(query, PreAuthorization.class, PRE_AUTHORIZATION_DETAIL);

    }

}
