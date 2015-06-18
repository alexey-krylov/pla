package com.pla.core.query;

import com.pla.core.domain.model.notification.NotificationRole;
import com.pla.core.presentation.dto.NotificationTemplateDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Created by pradyumna on 18-06-2015.
 */
@Component
public class NotificationFinder {

    public List<NotificationRole> findAllNotificationRole() {
        return Collections.EMPTY_LIST;
    }

    public List<NotificationTemplateDto> findAllTemplates() {
        return Collections.EMPTY_LIST;
    }
}
