package com.pla.core.query;

import com.pla.core.domain.model.notification.NotificationRole;
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

    public List<NotificationRole> findAllTemplates() {
        return Collections.EMPTY_LIST;
    }
}
