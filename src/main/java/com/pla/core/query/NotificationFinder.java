package com.pla.core.query;

import com.pla.core.dto.NotificationTemplateDto;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.nthdimenzion.ddd.domain.annotations.Finder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by pradyumna on 18-06-2015.
 */
@Finder
@Component
public class NotificationFinder {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public static final String findAllNotificationRoleQuery = " SELECT role_type roleType,line_of_business lineOfBusiness,process process FROM notification_role";

    public List<Map<String,Object>> findAllNotificationRole() {
        return namedParameterJdbcTemplate.query(findAllNotificationRoleQuery, new ColumnMapRowMapper()).parallelStream().map(new Function<Map<String, Object>, Map<String, Object>>() {
            @Override
            public Map<String, Object> apply(Map<String, Object> notificationRoleMap) {
                notificationRoleMap.put("lineOfBusiness", LineOfBusinessEnum.valueOf(notificationRoleMap.get("lineOfBusiness").toString()).getDescription());
                notificationRoleMap.put("processType", ProcessType.valueOf(notificationRoleMap.get("process").toString()).getDescription());
                return notificationRoleMap;
            }
        }).collect(Collectors.toList());
    }

    public List<NotificationTemplateDto> findAllTemplates() {
        return Collections.EMPTY_LIST;
    }
}
