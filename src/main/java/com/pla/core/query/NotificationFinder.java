package com.pla.core.query;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.domain.model.notification.NotificationHistory;
import com.pla.core.domain.model.notification.NotificationId;
import com.pla.core.domain.model.notification.NotificationTemplate;
import com.pla.core.domain.model.notification.NotificationTemplateId;
import com.pla.core.repository.NotificationTemplateRepository;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by pradyumna on 18-06-2015.
 */
@Component
public class NotificationFinder {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private NotificationTemplateRepository notificationTemplateRepository;
    private ObjectMapper objectMapper;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Autowired
    public NotificationFinder(NotificationTemplateRepository notificationTemplateRepository){
        this.notificationTemplateRepository = notificationTemplateRepository;
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setVisibilityChecker(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.ANY));
    }

    public static final String findAllNotificationRole = " SELECT role_type roleType,line_of_business lineOfBusiness,process processType FROM notification_role";

    public static final String findAllNotification = "SELECT n.notification_id notificationId,n.generated_on generatedOn,n.line_of_business lineOfBusiness, " +
            " n.process_type processType,n.reminder_type reminderType,n.request_number requestNumber, " +
            " n.waiting_for waitingFor FROM notification n INNER JOIN notification_role nr " +
            " ON n.role_type=nr.role_type WHERE n.role_type in (:authorities) ";


    public static final String findILQuotationProposerDetailQuery = "SELECT p.plan_name planName,il.proposer_email_address emailAddress,il.proposer_first_name firstName, il.proposer_surname surName, " +
            " il.proposer_title salutation,il.quotation_number quotationNumber,il.shared_on sharedOn FROM individual_life_quotation il INNER JOIN plan_coverage_benefit_assoc p " +
            " ON il.plan_id = p.plan_id WHERE il.quotation_id=:quotationId";

    public static final String findEmailNotificationContentQuery = "SELECT email_address emailId,generated_on generatedOn,line_of_business lineOfBusiness,process_type processType, " +
            " reminder_template reminderTemplate,reminder_type reminderType,request_number requestNumber,waiting_for waitingFor FROM notification " +
            " WHERE notification_id=:notificationId ";

    private static Properties roleTypeProperties = new Properties();

    static {
        ClassLoader bundleClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            roleTypeProperties.load(bundleClassLoader.getResourceAsStream("messages_en.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Map<String,Object>> findAllNotificationRole(){
        return namedParameterJdbcTemplate.query(findAllNotificationRole, new ColumnMapRowMapper()).parallelStream().map(new Function<Map<String, Object>, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(Map<String, Object> notificationRoleMap) {
                notificationRoleMap.put("lineOfBusinessDescription", LineOfBusinessEnum.valueOf(notificationRoleMap.get("lineOfBusiness").toString()).toString());
                notificationRoleMap.put("processTypeDescription", ProcessType.valueOf(notificationRoleMap.get("processType").toString()).toString());
                notificationRoleMap.put("roleTypeDescription", roleTypeProperties.getProperty(notificationRoleMap.get("roleType").toString()).toString());
                return notificationRoleMap;
            }
        }).collect(Collectors.toList());
    }

    public List<Map<String,Object>> findAllTemplates() {
        List<NotificationTemplate> notificationTemplates = notificationTemplateRepository.findAll();
        return notificationTemplates.parallelStream().map(new NotificationTemplateTransformer()).collect(Collectors.toList());
    }

    public List<Map<String, Object>> getNotificationByRole(Collection<? extends GrantedAuthority> authorities) {
        List<String> grantedAuthorities = authorities.parallelStream().map(new Function<GrantedAuthority, String>() {
            @Override
            public String apply(GrantedAuthority grantedAuthority) {
                return grantedAuthority.getAuthority();
            }
        }).collect(Collectors.toList());
        List<Map<String,Object>> notificationList =  namedParameterJdbcTemplate.query(findAllNotification, new MapSqlParameterSource("authorities", grantedAuthorities),new ColumnMapRowMapper());
        if (isNotEmpty(notificationList)) {
            return notificationList.parallelStream().map(new Function<Map<String, Object>, Map<String, Object>>() {
                @Override
                public Map<String, Object> apply(Map<String, Object> notificationMap) {
                    notificationMap.put("lineOfBusinessDescription", LineOfBusinessEnum.valueOf(notificationMap.get("lineOfBusiness").toString()).toString());
                    notificationMap.put("processTypeDescription", ProcessType.valueOf(notificationMap.get("processType").toString()).toString());
                    notificationMap.put("waitingForDescription", WaitingForEnum.valueOf(notificationMap.get("waitingFor").toString()).toString());
                    notificationMap.put("reminderTypeDescription", ReminderTypeEnum.valueOf(notificationMap.get("reminderType").toString()).toString());

                    return notificationMap;
                }
            }).collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

    public Map<String,Object> findILQuotationProposerDetail(String quotationId){
        List<Map<String,Object>> quotationProposerDetail = namedParameterJdbcTemplate.query(findILQuotationProposerDetailQuery, new MapSqlParameterSource("quotationId", quotationId), new ColumnMapRowMapper());
        return isNotEmpty(quotationProposerDetail)?quotationProposerDetail.get(0): Collections.EMPTY_MAP;
    }

    public List<Map<String,Object>> getNotificationTemplateById(NotificationTemplateId notificationTemplateId){
        NotificationTemplate notificationTemplate = notificationTemplateRepository.findOne(notificationTemplateId);
        if(notificationTemplate!=null){
            return Lists.newArrayList(notificationTemplate).parallelStream().map(new NotificationTemplateTransformer()).collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

    private class NotificationTemplateTransformer implements Function<NotificationTemplate, Map<String,Object>> {
        @Override
        public Map<String, Object> apply(NotificationTemplate notificationTemplate) {
            Map<String, Object> notificationTemplateMap = objectMapper.convertValue(notificationTemplate, Map.class);
            notificationTemplateMap.put("lineOfBusinessDescription", LineOfBusinessEnum.valueOf(notificationTemplateMap.get("lineOfBusiness").toString()).toString());
            notificationTemplateMap.put("processTypeDescription", ProcessType.valueOf(notificationTemplateMap.get("processType").toString()).toString());
            notificationTemplateMap.put("waitingForDescription", WaitingForEnum.valueOf(notificationTemplateMap.get("waitingFor").toString()).toString());
            notificationTemplateMap.put("reminderTypeDescription", ReminderTypeEnum.valueOf(notificationTemplateMap.get("reminderType").toString()).toString());
            return notificationTemplateMap;
        }
    }

    public List<Map<String,Object>> emailContent(NotificationId notificationId){
        List<Map<String,Object>> emailNotificationDetail = namedParameterJdbcTemplate.query(findEmailNotificationContentQuery, new MapSqlParameterSource("notificationId", notificationId.getNotificationId()), new ColumnMapRowMapper());
        if (isNotEmpty(emailNotificationDetail)){
            return emailNotificationDetail.parallelStream().map(new Function<Map<String, Object>, Map<String, Object>>() {
                @Override
                public Map<String, Object> apply(Map<String, Object> notificationMap) {
                    Map<String, Object> emailContent = Maps.newLinkedHashMap();
                    emailContent.put("subject", "Quotation First Reminder");
                    emailContent.put("mailSentDate", notificationMap.get("generatedOn"));
                    emailContent.put("emailAddress", new String[]{notificationMap.get("emailId").toString()});
                    String emailBody = new String((byte[]) notificationMap.get("reminderTemplate"), Charset.forName("UTF-8"));
                    emailContent.put("emailBody", emailBody);
                    emailContent.put("notificationId", notificationId);
                    return emailContent;
                }
            }).collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

    public List<Map<String,Object>> getNotificationHistoryDetail(){
        List<NotificationHistory> notificationHistories = mongoTemplate.findAll(NotificationHistory.class);
        if (isNotEmpty(notificationHistories)){
           return notificationHistories.parallelStream().map(new Function<NotificationHistory, Map<String,Object>>() {
                @Override
                public Map<String, Object> apply(NotificationHistory notificationHistory) {
                    Map<String, Object> notificationHistoryMap = objectMapper.convertValue(notificationHistory, Map.class);
                    notificationHistoryMap.put("lineOfBusinessDescription", LineOfBusinessEnum.valueOf(notificationHistoryMap.get("lineOfBusiness").toString()).toString());
                    notificationHistoryMap.put("processTypeDescription", ProcessType.valueOf(notificationHistoryMap.get("processType").toString()).toString());
                    notificationHistoryMap.put("waitingForDescription", WaitingForEnum.valueOf(notificationHistoryMap.get("waitingFor").toString()).toString());
                    notificationHistoryMap.put("reminderTypeDescription", ReminderTypeEnum.valueOf(notificationHistoryMap.get("reminderType").toString()).toString());
                    notificationHistoryMap.put("sentOn", notificationHistoryMap.get("generatedOn"));
                    return notificationHistoryMap;
                }
            }).collect(Collectors.toList());
        }
       return Collections.EMPTY_LIST;
    }
}
