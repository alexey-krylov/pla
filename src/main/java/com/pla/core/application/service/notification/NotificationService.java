package com.pla.core.application.service.notification;

import com.pla.core.domain.model.notification.NotificationRole;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by Admin on 6/18/2015.
 */
@DomainService
public class NotificationService {

    private EntityManager entityManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public boolean createNotificationRoleMapping(String roleType, LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType){
        NotificationRole notificationRole = NotificationRole.createNotificationRoleMapping(roleType,lineOfBusinessEnum,processType);
        entityManager.persist(notificationRole);
        return true;
    }
}
