package com.pla.grouphealth.claim.cashless.query;

import com.google.common.collect.Lists;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaim;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationRequest;
import com.pla.grouphealth.claim.cashless.presentation.dto.SearchReopenedClaimDetailDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.claim.SearchGroupHealthCashlessClaimRecordDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.SearchPreAuthorizationRecordDto;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */
@Finder
@Component
public class GHCashlessClaimFinder {

    private MongoTemplate mongoTemplate;
    private  final  String PRE_AUTHORIZATION_DETAIL="PRE_AUTHORIZATION_DETAIL";

    private  final String GH_CASHLESS_CLAIM="GROUP_HEALTH_CASHLESS_CLAIM";
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
        if(isNotEmpty(searchPreAuthorizationRecordDto.getUnderwriterLevel()) && searchPreAuthorizationRecordDto.getUnderwriterLevel().equalsIgnoreCase("LEVEL1")){
            query.addCriteria(new Criteria().and("status").is(PreAuthorizationRequest.Status.UNDERWRITING_LEVEL1));
        }
        if(isNotEmpty(searchPreAuthorizationRecordDto.getUnderwriterLevel()) && searchPreAuthorizationRecordDto.getUnderwriterLevel().equalsIgnoreCase("LEVEL2")){
            query.addCriteria(new Criteria().and("status").is(PreAuthorizationRequest.Status.UNDERWRITING_LEVEL2));
        }
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
            query.addCriteria(new Criteria().and("preAuthorizationRequestId").is(searchPreAuthorizationRecordDto.getPreAuthorizationId()));
        }
        query.with(new Sort(Sort.Direction.ASC, "preAuthorizationRequestId"));
        return mongoTemplate.find(query, PreAuthorizationRequest.class, "PRE_AUTHORIZATION_REQUEST");

    }

    public List<PreAuthorizationRequest> getPreAuthorizationRequestByCriteria(SearchPreAuthorizationRecordDto searchPreAuthorizationRecordDto, List usernames) {
        if(isEmpty(searchPreAuthorizationRecordDto.getBatchNumber())&& isEmpty(searchPreAuthorizationRecordDto.getClientId()) &&
                isEmpty(searchPreAuthorizationRecordDto.getPolicyNumber())&&isEmpty(searchPreAuthorizationRecordDto.getPreAuthorizationId())&&
                isEmpty(searchPreAuthorizationRecordDto.getHcpCode())) {

            return Lists.newArrayList();
        }
        Query query = new Query();
        query.addCriteria(new Criteria().and("preAuthorizationProcessorUserId").in(usernames));
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
            query.addCriteria(new Criteria().and("preAuthorizationRequestId").is(searchPreAuthorizationRecordDto.getPreAuthorizationId()));
        }
        query.with(new Sort(Sort.Direction.ASC, "preAuthorizationRequestId"));
        return mongoTemplate.find(query, PreAuthorizationRequest.class, "PRE_AUTHORIZATION_REQUEST");
    }

    public List<GroupHealthCashlessClaim> getCashlessClaimByCriteria(SearchGroupHealthCashlessClaimRecordDto searchGroupHealthCashlessClaimRecordDto, ArrayList<String> usernames){
        if(isEmpty(searchGroupHealthCashlessClaimRecordDto.getBatchNumber())&& isEmpty(searchGroupHealthCashlessClaimRecordDto.getClientId())&&
                isEmpty(searchGroupHealthCashlessClaimRecordDto.getGroupHealthCashlessClaimId()) && isEmpty(searchGroupHealthCashlessClaimRecordDto.getHcpCode())
                && isEmpty(searchGroupHealthCashlessClaimRecordDto.getPolicyHolderName()) && isEmpty(searchGroupHealthCashlessClaimRecordDto.getPolicyNumber())
                &&isEmpty(searchGroupHealthCashlessClaimRecordDto.getUnderwriterLevel())){
            return Lists.newArrayList();
        }
        Query query = new Query();
        query.addCriteria(new Criteria().and("claimProcessorUserId").in(usernames));
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getBatchNumber())){
            query.addCriteria(new Criteria().and("batchNumber").is(searchGroupHealthCashlessClaimRecordDto.getBatchNumber()));
        }
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getHcpCode())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimHCPDetail.hcpCode.hcpCode").is(searchGroupHealthCashlessClaimRecordDto.getHcpCode()));
        }
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getPolicyNumber())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimPolicyDetail.policyNumber.policyNumber").is(searchGroupHealthCashlessClaimRecordDto.getPolicyNumber()));
        }
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getClientId())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimPolicyDetail.assuredDetail.clientId").is(searchGroupHealthCashlessClaimRecordDto.getClientId()));
        }
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getGroupHealthCashlessClaimId())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimId").is(searchGroupHealthCashlessClaimRecordDto.getGroupHealthCashlessClaimId()));
        }
        query.with(new Sort(Sort.Direction.ASC, "groupHealthCashlessClaimId"));
        return mongoTemplate.find(query, GroupHealthCashlessClaim.class, "GROUP_HEALTH_CASHLESS_CLAIM");
    }

    public List<GroupHealthCashlessClaim> searchGroupHealthCashlessClaimForUnderwriterByCriteria(SearchGroupHealthCashlessClaimRecordDto searchGroupHealthCashlessClaimRecordDto, List username){
        if(isEmpty(searchGroupHealthCashlessClaimRecordDto.getBatchNumber())&& isEmpty(searchGroupHealthCashlessClaimRecordDto.getClientId())&&
                isEmpty(searchGroupHealthCashlessClaimRecordDto.getGroupHealthCashlessClaimId()) && isEmpty(searchGroupHealthCashlessClaimRecordDto.getHcpCode())
                && isEmpty(searchGroupHealthCashlessClaimRecordDto.getPolicyHolderName()) && isEmpty(searchGroupHealthCashlessClaimRecordDto.getPolicyNumber())
                &&isEmpty(searchGroupHealthCashlessClaimRecordDto.getUnderwriterLevel())){
            return Lists.newArrayList();
        }
        Query query = new Query();
        query.addCriteria(new Criteria().and("claimUnderWriterUserId").in(username));
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getUnderwriterLevel()) && searchGroupHealthCashlessClaimRecordDto.getUnderwriterLevel().equalsIgnoreCase("LEVEL1")){
            query.addCriteria(new Criteria().and("status").is(PreAuthorizationRequest.Status.UNDERWRITING_LEVEL1));
        }
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getUnderwriterLevel()) && searchGroupHealthCashlessClaimRecordDto.getUnderwriterLevel().equalsIgnoreCase("LEVEL2")){
            query.addCriteria(new Criteria().and("status").is(PreAuthorizationRequest.Status.UNDERWRITING_LEVEL2));
        }
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getBatchNumber())){
            query.addCriteria(new Criteria().and("batchNumber").is(searchGroupHealthCashlessClaimRecordDto.getBatchNumber()));
        }
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getHcpCode())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimHCPDetail.hcpCode.hcpCode").is(searchGroupHealthCashlessClaimRecordDto.getHcpCode()));
        }
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getPolicyNumber())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimPolicyDetail.policyNumber.policyNumber").is(searchGroupHealthCashlessClaimRecordDto.getPolicyNumber()));
        }
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getClientId())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimPolicyDetail.assuredDetail.clientId").is(searchGroupHealthCashlessClaimRecordDto.getClientId()));
        }
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getGroupHealthCashlessClaimId())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimId").is(searchGroupHealthCashlessClaimRecordDto.getGroupHealthCashlessClaimId()));
        }
        query.with(new Sort(Sort.Direction.ASC, "groupHealthCashlessClaimId"));
        return mongoTemplate.find(query, GroupHealthCashlessClaim.class, "GROUP_HEALTH_CASHLESS_CLAIM");

    }

    public List<GroupHealthCashlessClaim> searchCashlessClaimBillMismatchCriteria(SearchGroupHealthCashlessClaimRecordDto searchGroupHealthCashlessClaimRecordDto, List username){
        if(isEmpty(searchGroupHealthCashlessClaimRecordDto.getBatchNumber())&& isEmpty(searchGroupHealthCashlessClaimRecordDto.getClientId())&&
                isEmpty(searchGroupHealthCashlessClaimRecordDto.getGroupHealthCashlessClaimId()) && isEmpty(searchGroupHealthCashlessClaimRecordDto.getHcpCode())
                && isEmpty(searchGroupHealthCashlessClaimRecordDto.getPolicyHolderName()) && isEmpty(searchGroupHealthCashlessClaimRecordDto.getPolicyNumber())){
            return Lists.newArrayList();
        }
        Query query = new Query();
        query.addCriteria(new Criteria().and("claimUnderWriterUserId").in(username));
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getBatchNumber())){
            query.addCriteria(new Criteria().and("batchNumber").is(searchGroupHealthCashlessClaimRecordDto.getBatchNumber()));
        }
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getHcpCode())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimHCPDetail.hcpCode.hcpCode").is(searchGroupHealthCashlessClaimRecordDto.getHcpCode()));
        }
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getPolicyNumber())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimPolicyDetail.policyNumber.policyNumber").is(searchGroupHealthCashlessClaimRecordDto.getPolicyNumber()));
        }
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getClientId())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimPolicyDetail.assuredDetail.clientId").is(searchGroupHealthCashlessClaimRecordDto.getClientId()));
        }
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getGroupHealthCashlessClaimId())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimId").is(searchGroupHealthCashlessClaimRecordDto.getGroupHealthCashlessClaimId()));
        }
        query.with(new Sort(Sort.Direction.ASC, "groupHealthCashlessClaimId"));
        return mongoTemplate.find(query, GroupHealthCashlessClaim.class, "GROUP_HEALTH_CASHLESS_CLAIM");

    }

    public List<GroupHealthCashlessClaim> searchCashlessClaimServiceMismatchCriteria(SearchGroupHealthCashlessClaimRecordDto searchGroupHealthCashlessClaimRecordDto, List username){
        if(isEmpty(searchGroupHealthCashlessClaimRecordDto.getBatchNumber())&& isEmpty(searchGroupHealthCashlessClaimRecordDto.getClientId())&&
                isEmpty(searchGroupHealthCashlessClaimRecordDto.getGroupHealthCashlessClaimId()) && isEmpty(searchGroupHealthCashlessClaimRecordDto.getHcpCode())
                && isEmpty(searchGroupHealthCashlessClaimRecordDto.getPolicyHolderName()) && isEmpty(searchGroupHealthCashlessClaimRecordDto.getPolicyNumber())){
            return Lists.newArrayList();
        }
        Query query = new Query();
        query.addCriteria(new Criteria().and("claimUnderWriterUserId").in(username));
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getBatchNumber())){
            query.addCriteria(new Criteria().and("batchNumber").is(searchGroupHealthCashlessClaimRecordDto.getBatchNumber()));
        }
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getHcpCode())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimHCPDetail.hcpCode.hcpCode").is(searchGroupHealthCashlessClaimRecordDto.getHcpCode()));
        }
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getPolicyNumber())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimPolicyDetail.policyNumber.policyNumber").is(searchGroupHealthCashlessClaimRecordDto.getPolicyNumber()));
        }
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getClientId())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimPolicyDetail.assuredDetail.clientId").is(searchGroupHealthCashlessClaimRecordDto.getClientId()));
        }
        if(isNotEmpty(searchGroupHealthCashlessClaimRecordDto.getGroupHealthCashlessClaimId())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimId").is(searchGroupHealthCashlessClaimRecordDto.getGroupHealthCashlessClaimId()));
        }
        query.with(new Sort(Sort.Direction.ASC, "groupHealthCashlessClaimId"));
        return mongoTemplate.find(query, GroupHealthCashlessClaim.class, "GROUP_HEALTH_CASHLESS_CLAIM");

    }

    public List<GroupHealthCashlessClaim> searchReopenedGroupHealthCashlessClaimByCriteria(SearchReopenedClaimDetailDto searchReopenedClaimDetailDto, List<String> usernames) {
        if(isEmpty(searchReopenedClaimDetailDto.getAssuredName())&& isEmpty(searchReopenedClaimDetailDto.getClientId())&&
                isEmpty(searchReopenedClaimDetailDto.getAssuredNRCNumber()) && isEmpty(searchReopenedClaimDetailDto.getClaimNumber())
                && isEmpty(searchReopenedClaimDetailDto.getPolicyHolderName()) && isEmpty(searchReopenedClaimDetailDto.getPolicyNumber())){
            return Lists.newArrayList();
        }
        Query query = new Query();
        query.addCriteria(new Criteria().and("claimReopenProcessorUserId").in(usernames));
        if(isNotEmpty(searchReopenedClaimDetailDto.getPolicyHolderName())){
            query.addCriteria(new Criteria().and("ghProposer.proposerName").is(searchReopenedClaimDetailDto.getPolicyHolderName()));
        }
        if(isNotEmpty(searchReopenedClaimDetailDto.getClaimNumber())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimId").is(searchReopenedClaimDetailDto.getClaimNumber()));
        }
        if(isNotEmpty(searchReopenedClaimDetailDto.getPolicyNumber())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimPolicyDetail.policyNumber.policyNumber").is(searchReopenedClaimDetailDto.getPolicyNumber()));
        }
        if(isNotEmpty(searchReopenedClaimDetailDto.getClientId())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimPolicyDetail.assuredDetail.clientId").is(searchReopenedClaimDetailDto.getClientId()));
        }
        if(isNotEmpty(searchReopenedClaimDetailDto.getAssuredName())){
            query.addCriteria(new Criteria()
                    .and("groupHealthCashlessClaimId.assuredDetail.firstName")
                    .regex(searchReopenedClaimDetailDto.getAssuredName())
                    .orOperator(Criteria.where("groupHealthCashlessClaimId.assuredDetail.surname").regex(searchReopenedClaimDetailDto.getAssuredName())));
        }
        if(isNotEmpty(searchReopenedClaimDetailDto.getAssuredNRCNumber())){
            query.addCriteria(new Criteria().and("groupHealthCashlessClaimId.assuredDetail.nrcNumber").is(searchReopenedClaimDetailDto.getAssuredNRCNumber()));
        }
        query.with(new Sort(Sort.Direction.ASC, "groupHealthCashlessClaimId"));
        return mongoTemplate.find(query, GroupHealthCashlessClaim.class, "GROUP_HEALTH_CASHLESS_CLAIM");
    }
}
