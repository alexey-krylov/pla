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
import org.apache.poi.xwpf.converter.core.FileURIResolver;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.axonframework.commandhandling.annotation.CommandHandler;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.AppConstants;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.nthdimenzion.object.utils.IIdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Admin on 6/29/2015.
 */
@Component
public class NotificationCommandHandler {

    private NotificationTemplateRepository notificationTemplateRepository;
    private IIdGenerator idGenerator;
    private JpaRepositoryFactory jpaRepositoryFactory;
    private NotificationTemplateService notificationTemplateService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    public NotificationCommandHandler(NotificationTemplateRepository notificationTemplateRepository, JpaRepositoryFactory jpaRepositoryFactory,IIdGenerator idGenerator, NotificationTemplateService notificationTemplateService){
        this.notificationTemplateRepository = notificationTemplateRepository;
        this.jpaRepositoryFactory = jpaRepositoryFactory;
        this.idGenerator = idGenerator;
        this.notificationTemplateService = notificationTemplateService;
    }

    @CommandHandler
    public void createQuotationNotification(CreateQuotationNotificationCommand createQuotationNotificationCommand) throws Exception {
        checkArgument(createQuotationNotificationCommand!=null,"Create Quotation command cannot be empty");
        JpaRepository<Notification, NotificationId> notificationRepository = jpaRepositoryFactory.getCrudRepository(Notification.class);
        HashMap<String,String> quotationNotificationDetailMap =   notificationTemplateService.getQuotationNotificationTemplateData(createQuotationNotificationCommand.getLineOfBusiness(), createQuotationNotificationCommand.getQuotationId().getQuotationId());
        Notification notification =  createNotification(createQuotationNotificationCommand.getLineOfBusiness(),createQuotationNotificationCommand.getProcessType(),createQuotationNotificationCommand.getWaitingFor(),
                createQuotationNotificationCommand.getReminderType(),createQuotationNotificationCommand.getQuotationId().getQuotationId(),createQuotationNotificationCommand.getRoleType(),quotationNotificationDetailMap);
        notificationRepository.save(notification);
    }

    @CommandHandler
    public void createProposalNotification(CreateProposalNotificationCommand createProposalNotificationCommand) throws Exception {
        checkArgument(createProposalNotificationCommand!=null,"Create Proposal command cannot be empty");
        JpaRepository<Notification, NotificationId> notificationRepository = jpaRepositoryFactory.getCrudRepository(Notification.class);
        HashMap<String,String>  proposalNotificationDetailMap = notificationTemplateService.getQuotationNotificationTemplateData(createProposalNotificationCommand.getLineOfBusiness(), createProposalNotificationCommand.getProposalId().getProposalId());
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
                                            String requestNumber,String roleType,HashMap<String,String> notificationDetail) throws Exception {
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
                .withEmailAddress(notificationDetail.get("emailAddress") != null ? notificationDetail.get("emailAddress").toString() : "")
                .withReminderTemplate(convert(notificationTemplate.getReminderFile(), notificationDetail, lineOfBusiness,requestNumber))
                        .withRoleType(roleType);
        NotificationId notificationId = new NotificationId(idGenerator.nextId());
        return notificationBuilder.createNotification(notificationId);
    }

    public byte[] convert(byte[] templateFile,HashMap<String,String> notificationMap,LineOfBusinessEnum lineOfBusiness,String requestNumber) throws Exception {
        File tempFile  = new File("./src/main/resources/emailtemplate/notification_"+requestNumber+".docx");
        Files.write(templateFile, tempFile);
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(tempFile);
        VariablePrepare.prepare(wordMLPackage);
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
        notificationMap.put("systemDate", LocalDate.now().toString(AppConstants.DD_MM_YYY_FORMAT));
        notificationMap.put("lineOfBusiness",lineOfBusiness.toString());
        documentPart.variableReplace(notificationMap);
        wordMLPackage.save(tempFile);
        String htmlContent = convertDocxToHtml(tempFile);
        return htmlContent.getBytes();
    }

    public String convertDocxToHtml(File tempFile) throws IOException {
        XWPFDocument document = new XWPFDocument(FileUtils.openInputStream(tempFile));
        XHTMLOptions options = XHTMLOptions.create().URIResolver(new FileURIResolver(new File("word/media")));
        OutputStream outputStream = new ByteArrayOutputStream();
        XHTMLConverter.getInstance().convert(document, outputStream, options);
        FileUtils.forceDelete(tempFile);
        return outputStream.toString();
    }
}
