package com.pla.core.application.service.notification;

import com.google.common.collect.Maps;
import com.pla.core.domain.model.notification.NotificationRole;
import com.pla.core.domain.model.notification.NotificationTemplate;
import com.pla.core.domain.model.notification.NotificationTemplateId;
import com.pla.core.repository.NotificationTemplateRepository;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.pla.core.domain.exception.NotificationException.raiseDuplicateEntryException;
import static com.pla.core.domain.exception.NotificationException.raiseErrorInReload;

/**
 * Created by Admin on 6/18/2015.
 */
@DomainService
public class NotificationService {

    private EntityManager entityManager;

    private NotificationTemplateRepository notificationTemplateRepository;

    private IIdGenerator idGenerator;


    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Autowired
    public NotificationService(NotificationTemplateRepository notificationTemplateRepository, IIdGenerator idGenerator) {
        this.notificationTemplateRepository = notificationTemplateRepository;
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
        checkArgument(processType.isValidWaitingFor(waitingFor), "The "+waitingFor+" waiting for is not associated with "+processType);
        checkArgument(waitingFor.isValidReminderType(reminderType), "The "+reminderType+" is not associated with "+waitingFor);
        boolean isExists = isNotificationTemplateExists(lineOfBusiness,processType,waitingFor,reminderType);
        if (isExists){
            raiseDuplicateEntryException();
        }
        NotificationTemplateId notificationTemplateId = new NotificationTemplateId(idGenerator.nextId());
        NotificationTemplate notificationTemplate = NotificationTemplate.createNotification(notificationTemplateId,lineOfBusiness, processType,
                waitingFor, reminderType);
        notificationTemplate = notificationTemplate.withReminderFile(reminderFile);
        notificationTemplateRepository.save(notificationTemplate);
        return AppConstants.SUCCESS;
    }

    public boolean reloadNotificationTemplate(String notificationId,byte[] reminderFile){
        NotificationTemplate existedNotificationTemplate = notificationTemplateRepository.findOne(new NotificationTemplateId(notificationId));
        if (existedNotificationTemplate==null){
            raiseErrorInReload();
        }
        existedNotificationTemplate = existedNotificationTemplate.withReminderFile(reminderFile);
        notificationTemplateRepository.save(existedNotificationTemplate);
        return AppConstants.SUCCESS;
    }

    public boolean isNotificationTemplateExists(LineOfBusinessEnum lineOfBusiness,ProcessType processType,WaitingForEnum waitingFor,ReminderTypeEnum reminderType){
        NotificationTemplate notificationTemplate =  notificationTemplateRepository.findNotification(lineOfBusiness, processType, waitingFor, reminderType);
        if (notificationTemplate !=null){
            return true;
        }
        return false;
    }

    public byte[] getReminderFile(LineOfBusinessEnum lineOfBusiness,ProcessType processType,WaitingForEnum waitingFor,ReminderTypeEnum reminderType) {
        NotificationTemplate notificationTemplate = notificationTemplateRepository.findNotification(lineOfBusiness, processType, waitingFor, reminderType);
        checkArgument(notificationTemplate !=null,"Notification is not configured");
        byte[] reminderFile =  notificationTemplate.getReminderFile();
        return reminderFile;
    }

    public List<Map<String,Object>> getNotificationTypeBy(LineOfBusinessEnum lineOfBusinessEnum,ProcessType processType,WaitingForEnum waitingForEnum){
        Optional<ProcessType> processTypeOptional = lineOfBusinessEnum.getProcessTypeList().parallelStream().filter(new ProcessFilterPredicate(processType)).findAny();
        if (processTypeOptional.isPresent()){
            Optional<WaitingForEnum> waitingForEnumOptional = processTypeOptional.get().getWaitingForList().parallelStream().filter(new Predicate<WaitingForEnum>() {
                @Override
                public boolean test(WaitingForEnum waitingFor) {
                    return waitingFor.equals(waitingForEnum);
                }
            }).findAny();
            if (waitingForEnumOptional.isPresent()){
                return waitingForEnumOptional.get().getReminderTypes().parallelStream().map(new Function<ReminderTypeEnum, Map<String, Object>>() {
                    @Override
                    public Map<String, Object> apply(ReminderTypeEnum reminderTypeEnum) {
                        Map<String, Object> waitingForMap = Maps.newLinkedHashMap();
                        waitingForMap.put("reminderType", reminderTypeEnum);
                        waitingForMap.put("description", reminderTypeEnum.toString());
                        return waitingForMap;
                    }
                }).collect(Collectors.toList());
            }
        }
        return Collections.EMPTY_LIST;
    }

    public List<Map<String,Object>> getWaitingForBy(LineOfBusinessEnum lineOfBusinessEnum,ProcessType processType) {
        Optional<ProcessType> processTypeOptional = lineOfBusinessEnum.getProcessTypeList().parallelStream().filter(new ProcessFilterPredicate(processType)).findAny();
        if (processTypeOptional.isPresent()) {
            return processTypeOptional.get().getWaitingForList().parallelStream().map(new Function<WaitingForEnum, Map<String, Object>>() {
                @Override
                public Map<String, Object> apply(WaitingForEnum waitingForEnum) {
                    Map<String, Object> waitingForMap = Maps.newLinkedHashMap();
                    waitingForMap.put("waitingFor", waitingForEnum);
                    waitingForMap.put("description", waitingForEnum.toString());
                    return waitingForMap;
                }
            }).collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

    private class ProcessFilterPredicate implements Predicate<ProcessType> {
        private ProcessType process;
        public ProcessFilterPredicate(ProcessType processType) {
            this.process  = processType;
        }

        @Override
        public boolean test(ProcessType processType) {
            return process.equals(processType);
        }
    }
}
