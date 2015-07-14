package com.pla.core.application.notification;

import com.google.common.io.Files;
import com.pla.core.application.service.notification.NotificationTemplateService;
import com.pla.core.domain.model.notification.*;
import com.pla.core.repository.NotificationTemplateRepository;
import com.pla.sharedkernel.application.CreateNotificationHistoryCommand;
import com.pla.sharedkernel.application.CreateProposalNotificationCommand;
import com.pla.sharedkernel.application.CreateQuotationNotificationCommand;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.apache.commons.io.FileUtils;
import org.apache.velocity.app.VelocityEngine;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.AppConstants;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.io.File;
import java.io.StringWriter;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Admin on 6/29/2015.
 */
@Component
public class NotificationCommandHandler {

    private NotificationTemplateRepository notificationTemplateRepository;
    private IIdGenerator idGenerator;
    private JpaRepositoryFactory jpaRepositoryFactory;
    private VelocityEngine velocityEngine;
    private NotificationTemplateService notificationTemplateService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    public NotificationCommandHandler(NotificationTemplateRepository notificationTemplateRepository, JpaRepositoryFactory jpaRepositoryFactory,IIdGenerator idGenerator,VelocityEngine velocityEngine, NotificationTemplateService notificationTemplateService){
        this.notificationTemplateRepository = notificationTemplateRepository;
        this.jpaRepositoryFactory = jpaRepositoryFactory;
        this.idGenerator = idGenerator;
        this.velocityEngine  = velocityEngine;
        this.notificationTemplateService = notificationTemplateService;
    }

    @CommandHandler
    public void createQuotationNotification(CreateQuotationNotificationCommand createQuotationNotificationCommand) throws Exception {
        checkArgument(createQuotationNotificationCommand!=null,"Create Quotation command cannot be empty");
        JpaRepository<Notification, NotificationId> notificationRepository = jpaRepositoryFactory.getCrudRepository(Notification.class);
        Map<String,Object> quotationNotificationDetailMap =   notificationTemplateService.getQuotationNotificationTemplateData(createQuotationNotificationCommand.getLineOfBusiness(), createQuotationNotificationCommand.getQuotationId().getQuotationId());
        Notification notification =  createNotification(createQuotationNotificationCommand.getLineOfBusiness(),createQuotationNotificationCommand.getProcessType(),createQuotationNotificationCommand.getWaitingFor(),
                createQuotationNotificationCommand.getReminderType(),createQuotationNotificationCommand.getQuotationId().getQuotationId(),createQuotationNotificationCommand.getRoleType(),quotationNotificationDetailMap);
        notificationRepository.save(notification);
    }

    @CommandHandler
    public void createProposalNotification(CreateProposalNotificationCommand createProposalNotificationCommand) throws Exception {
        checkArgument(createProposalNotificationCommand!=null,"Create Proposal command cannot be empty");
        JpaRepository<Notification, NotificationId> notificationRepository = jpaRepositoryFactory.getCrudRepository(Notification.class);
        Map<String,Object>  proposalNotificationDetailMap = notificationTemplateService.getQuotationNotificationTemplateData(createProposalNotificationCommand.getLineOfBusiness(), createProposalNotificationCommand.getProposalId().getProposalId());
        Notification notification = createNotification(createProposalNotificationCommand.getLineOfBusiness(),createProposalNotificationCommand.getProcessType(),createProposalNotificationCommand.getWaitingFor(),
                createProposalNotificationCommand.getReminderType(),createProposalNotificationCommand.getProposalId().getProposalId(),createProposalNotificationCommand.getRoleType(),proposalNotificationDetailMap);
        notificationRepository.save(notification);
    }

    @CommandHandler
    public void createNotificationHistory(CreateNotificationHistoryCommand createNotificationHistoryCommand) throws Exception {
        NotificationBuilder notificationBuilder = NotificationHistory.builder();
        notificationBuilder.withLineOfBusiness(createNotificationHistoryCommand.getLineOfBusiness())
                .withProcessType(createNotificationHistoryCommand.getProcessType())
                .withRequestNumber(createNotificationHistoryCommand.getRequestNumber())
                .withWaitingFor(createNotificationHistoryCommand.getWaitingFor())
                .withReminderType(createNotificationHistoryCommand.getReminderType())
                .withRecipientMailAddress(createNotificationHistoryCommand.getRecipientMailAddress())
                .withReminderTemplate(createNotificationHistoryCommand.getTemplate())
                .withRoleType(createNotificationHistoryCommand.getRoleType());
        NotificationHistory notificationHistory = notificationBuilder.createNotificationHistory();
        mongoTemplate.save(notificationHistory);
    }



    private Notification createNotification(LineOfBusinessEnum lineOfBusiness, ProcessType process,WaitingForEnum waitingFor, ReminderTypeEnum reminderType,
                                            String requestNumber,String roleType,Map<String,Object> notificationDetail) throws Exception {
        checkArgument(notificationDetail !=null,"Notification details cannot be empty");
        NotificationTemplate notificationTemplate = notificationTemplateRepository.findNotification(lineOfBusiness, process,
                waitingFor, reminderType);
        checkArgument(notificationTemplate != null, "Notification Template has not uploaded");
        NotificationBuilder notificationBuilder = Notification.builder();
        notificationBuilder.withLineOfBusiness(lineOfBusiness)
                .withProcessType(process)
                .withRequestNumber(requestNumber)
                .withWaitingFor(waitingFor)
                .withReminderType(reminderType)
                .withEmailAddress(notificationDetail.get("emailAddress")!=null?notificationDetail.get("emailAddress").toString():"")
                .withReminderTemplate(mergeTemplate(notificationTemplate.getReminderFile(),
                        notificationDetail, requestNumber, lineOfBusiness).toString().getBytes())
                .withRoleType(roleType);
        NotificationId notificationId = new NotificationId(idGenerator.nextId());
        return notificationBuilder.createNotification(notificationId);
    }

    private StringWriter mergeTemplate(byte[] templateFile,Map<String,Object> notificationMap,String requestNumber,LineOfBusinessEnum lineOfBusiness) throws Exception {
        File tempFile  = new File("./src/main/resources/emailtemplate/notification_"+requestNumber+".vm");
        Files.write(templateFile, tempFile);
        notificationMap.put("systemDate", LocalDate.now().toString(AppConstants.DD_MM_YYY_FORMAT));
        notificationMap.put("lineOfBusiness",lineOfBusiness.toString());
        StringWriter writer = new StringWriter();
        VelocityEngineUtils.mergeTemplate(velocityEngine, "emailtemplate/notification_" + requestNumber+ ".vm", "UTF-8", notificationMap, writer);
        FileUtils.forceDelete(tempFile);
        return writer;
    }
}
