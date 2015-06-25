package com.pla.core.application.service.notification;

import com.pla.core.domain.model.notification.NotificationRole;
import com.pla.core.domain.model.notification.NotificationTemplate;
import com.pla.core.domain.model.notification.NotificationTemplateId;
import com.pla.core.repository.NotificationRepository;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.nthdimenzion.common.AppConstants;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Admin on 6/18/2015.
 */
@DomainService
public class NotificationService {

    private EntityManager entityManager;

    private NotificationRepository notificationRepository;

    private IIdGenerator idGenerator;

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired
    public NotificationService(NotificationRepository notificationRepository, IIdGenerator idGenerator) {
        this.notificationRepository = notificationRepository;
        this.idGenerator = idGenerator;
    }

    @Transactional
    public boolean createNotificationRoleMapping(String roleType, LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType){
        NotificationRole notificationRole = NotificationRole.createNotificationRoleMapping(roleType,lineOfBusinessEnum,processType);
        entityManager.persist(notificationRole);
        return AppConstants.SUCCESS;
    }

    public boolean uploadNotificationTemplate(LineOfBusinessEnum lineOfBusiness, ProcessType processType, WaitingForEnum waitingFor, ReminderTypeEnum reminderType, byte[] reminderFile){
        checkArgument(lineOfBusiness.isValidProcess(processType), "The process "+processType+" is not associated with "+lineOfBusiness);
        checkArgument(processType.isValidWaitingFor(waitingFor), "The "+waitingFor+" waiting for is not associated with "+lineOfBusiness);
        checkArgument(waitingFor.isValidReminderType(reminderType), "The "+reminderType+" is not associated with "+waitingFor);
        NotificationTemplateId notificationTemplateId = new NotificationTemplateId(idGenerator.nextId());
        NotificationTemplate notificationTemplate = NotificationTemplate.createNotification(notificationTemplateId,lineOfBusiness, processType,
                waitingFor, reminderType);
        notificationTemplate = notificationTemplate.withReminderFile(reminderFile);
        notificationRepository.save(notificationTemplate);
        return AppConstants.SUCCESS;
    }

    public boolean reloadNotificationTemplate(String notificationId,byte[] reminderFile){
        NotificationTemplate existedNotificationTemplate = notificationRepository.findOne(new NotificationTemplateId(notificationId));
        existedNotificationTemplate = existedNotificationTemplate.withReminderFile(reminderFile);
        notificationRepository.save(existedNotificationTemplate);
        return AppConstants.SUCCESS;
    }

    public boolean isNotificationTemplateExists(LineOfBusinessEnum lineOfBusiness,ProcessType processType,WaitingForEnum waitingFor,ReminderTypeEnum reminderType){
        NotificationTemplate notificationTemplate =  notificationRepository.findNotification(lineOfBusiness, processType, waitingFor, reminderType);
        if (notificationTemplate !=null){
            return true;
        }
        return false;
    }

}
