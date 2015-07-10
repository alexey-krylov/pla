package com.pla.core.query;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.collect.Lists;
import com.pla.core.domain.model.notification.NotificationTemplate;
import com.pla.core.domain.model.notification.NotificationTemplateId;
import com.pla.core.repository.NotificationTemplateRepository;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
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
            " n.process_type processType,n.reminder_template template,n.reminder_type reminderType,n.request_number requestNumber, " +
            " n.waiting_for waitingFor FROM notification n INNER JOIN notification_role nr " +
            " ON n.role_type=nr.role_type WHERE n.role_type in (:authorities) ";


    public static final String findILQuotationProposerDetailQuery = "SELECT p.plan_name planName,il.proposer_email_address emailAddress,il.proposer_first_name firstName, il.proposer_surname surName, " +
            " il.proposer_title salutation,il.quotation_number quotationNumber,il.shared_on sharedOn FROM individual_life_quotation il INNER JOIN plan_coverage_benefit_assoc p " +
            " ON il.plan_id = p.plan_id WHERE il.quotation_id=:quotationId";

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
        return namedParameterJdbcTemplate.query(findAllNotification, new MapSqlParameterSource("authorities", grantedAuthorities),new ColumnMapRowMapper());
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
}
