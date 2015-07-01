package com.pla.core.repository;

import com.pla.core.domain.model.notification.NotificationTemplate;
import com.pla.core.domain.model.notification.NotificationTemplateId;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * Created by Admin on 6/24/2015.
 */
public interface NotificationTemplateRepository extends MongoRepository<NotificationTemplate,NotificationTemplateId> {

    @Query(value = "{'lineOfBusiness' : ?0,'processType':?1, 'waitingFor' :?2 ,'reminderType' : ?3}")
    public NotificationTemplate findNotification(LineOfBusinessEnum lineOfBusiness,ProcessType processType,WaitingForEnum waitingFor,ReminderTypeEnum reminderType);
}
