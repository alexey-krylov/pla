package com.pla.core.repository;

import com.pla.core.domain.model.notification.NotificationHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Admin on 8/28/2015.
 */
public interface NotificationHistoryRepository extends MongoRepository<NotificationHistory,String> {
}
