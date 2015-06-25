package com.pla.core.query;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.pla.core.domain.model.notification.NotificationTemplate;
import com.pla.core.repository.NotificationRepository;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by pradyumna on 18-06-2015.
 */
@Component
public class NotificationFinder {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private NotificationRepository notificationRepository;
    private ObjectMapper objectMapper;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    @Autowired
    public NotificationFinder(NotificationRepository notificationRepository){
        this.notificationRepository = notificationRepository;
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
                notificationRoleMap.put("lineOfBusiness", LineOfBusinessEnum.valueOf(notificationRoleMap.get("lineOfBusiness").toString()));
                notificationRoleMap.put("processType", ProcessType.valueOf(notificationRoleMap.get("processType").toString()));
                notificationRoleMap.put("roleType", roleTypeProperties.getProperty(notificationRoleMap.get("roleType").toString()));
                return notificationRoleMap;
            }
        }).collect(Collectors.toList());
    }

    public List<Map<String,Object>> findAllTemplates() {
        List<NotificationTemplate> notificationTemplates = notificationRepository.findAll();
        return notificationTemplates.parallelStream().map(new Function<NotificationTemplate, Map<String,Object>>() {
            @Override
            public Map<String,Object> apply(NotificationTemplate notificationTemplate) {
                Map<String,Object> notificationTemplateMap = objectMapper.convertValue(notificationTemplate, Map.class);
                notificationTemplateMap.put("lineOfBusiness", LineOfBusinessEnum.valueOf(notificationTemplateMap.get("lineOfBusiness").toString()));
                notificationTemplateMap.put("processType", ProcessType.valueOf(notificationTemplateMap.get("processType").toString()));
                notificationTemplateMap.put("waitingFor", WaitingForEnum.valueOf(notificationTemplateMap.get("waitingFor").toString()));
                notificationTemplateMap.put("reminderType", ReminderTypeEnum.valueOf(notificationTemplateMap.get("reminderType").toString()));
                return notificationTemplateMap;
            }
        }).collect(Collectors.toList());
    }
}
