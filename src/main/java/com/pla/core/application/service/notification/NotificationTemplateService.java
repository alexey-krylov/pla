package com.pla.core.application.service.notification;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.pla.core.query.NotificationFinder;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.exception.ProcessInfoException;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 6/30/2015.
 */
@Service
public class NotificationTemplateService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private NotificationFinder notificationFinder;

    @Autowired
    private IProcessInfoAdapter iProcessInfoAdapter;

    public static ImmutableMap<LineOfBusinessEnum,String> proposalEntitiesMap = ImmutableMap.of(LineOfBusinessEnum.GROUP_LIFE,"group_life_proposal",LineOfBusinessEnum.GROUP_HEALTH,"group_health_proposal");

    public static ImmutableMap<LineOfBusinessEnum,String> quotationEntitiesMap = ImmutableMap.of(LineOfBusinessEnum.GROUP_LIFE,"group_life_quotation",LineOfBusinessEnum.GROUP_HEALTH,"group_health_quotation",
            LineOfBusinessEnum.INDIVIDUAL_LIFE,"individual_life_quotation");


    public Map<String,Object> getQuotationNotificationTemplateData(LineOfBusinessEnum lineOfBusiness, String quotationId) throws ProcessInfoException {
        int closureTimePeriod = iProcessInfoAdapter.getClosureTimePeriod(lineOfBusiness, ProcessType.QUOTATION);
        Map<String,Object> notificationQuotationMap = null;
        if (LineOfBusinessEnum.INDIVIDUAL_LIFE.equals(lineOfBusiness)){
            notificationQuotationMap = notificationFinder.findILQuotationProposerDetail(quotationId);
            notificationQuotationMap.put("closureDays",closureTimePeriod);
            return notificationQuotationMap;
        }
        Criteria quotationCriteria = Criteria.where("_id").is(quotationId);
        Query query = new Query(quotationCriteria);
        query.fields().include("proposer.proposerName").include("quotationNumber").include("sharedOn").include("insureds.planPremiumDetail.planId").
                include("proposer.contactDetail.addressLine1").include("proposer.contactDetail.addressLine2").include("proposer.contactDetail.province").include("proposer.contactDetail.town")
                .include("proposer.contactDetail.emailAddress").exclude("_id");
        List<Map> quotationNotificationDetail =  mongoTemplate.find(query, Map.class, quotationEntitiesMap.get(lineOfBusiness));
        notificationQuotationMap = transformQuotationNotificationData(quotationNotificationDetail);
        notificationQuotationMap.put("closureDays",closureTimePeriod);
        checkArgument(isNotEmpty(quotationNotificationDetail), "Notification detail not found for the quotation");
        return notificationQuotationMap;
    }

    private Map<String,Object> transformQuotationNotificationData(List<Map> notificationData){
        Map<String,Object> notificationQuotationMap = Maps.newLinkedHashMap();
        notificationData.parallelStream().map(new Function<Map, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(Map map) {
                Map<String, Object> proposerMap = (Map) map.get("proposer");
                if (isNotEmpty(proposerMap)) {
                    notificationQuotationMap.put("proposerName", proposerMap.get("proposerName"));
                    Map<String, Object> contactDetailMap = (Map) proposerMap.get("contactDetail");
                    notificationQuotationMap.put("addressLine1", contactDetailMap.get("addressLine1"));
                    notificationQuotationMap.put("addressLine2", contactDetailMap.get("addressLine2"));
                    notificationQuotationMap.put("province", contactDetailMap.get("province"));
                    notificationQuotationMap.put("town", contactDetailMap.get("town"));
                    notificationQuotationMap.put("emailAddress", contactDetailMap.get("emailAddress"));
                    notificationQuotationMap.put("requestNumber", map.get("quotationNumber"));
                }
                return notificationQuotationMap;
            }
        }).collect(Collectors.toList());
        return notificationQuotationMap;
    }


    public Map<String,Object> getProposalNotificationTemplateData(LineOfBusinessEnum lineOfBusiness, String proposalId){
        Criteria proposalCriteria = Criteria.where("_id").is(proposalId);
        Query query = new Query(proposalCriteria);
        query.fields().include("proposer").include("proposalNumber").exclude("_id");
        List<Map> quotationNotificationDetailMap =  mongoTemplate.find(query, Map.class, proposalEntitiesMap.get(lineOfBusiness));
        return quotationNotificationDetailMap.get(0);
    }
}
