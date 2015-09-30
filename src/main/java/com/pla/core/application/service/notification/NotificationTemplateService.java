package com.pla.core.application.service.notification;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import com.pla.core.domain.model.notification.*;
import com.pla.core.query.NotificationFinder;
import com.pla.core.repository.NotificationHistoryRepository;
import com.pla.sharedkernel.service.GHMandatoryDocumentChecker;
import com.pla.sharedkernel.service.GLMandatoryDocumentChecker;
import com.pla.sharedkernel.service.ILMandatoryDocumentChecker;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
import com.pla.sharedkernel.application.CreateNotificationHistoryCommand;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.ReminderTypeEnum;
import com.pla.sharedkernel.domain.model.WaitingForEnum;
import com.pla.sharedkernel.exception.ProcessInfoException;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xwpf.converter.core.FileURIResolver;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.docx4j.model.datastorage.migration.VariablePrepare;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.joda.time.LocalDate;
import org.nthdimenzion.common.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 6/30/2015.
 */
@Service
public class NotificationTemplateService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationTemplateService.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private NotificationFinder notificationFinder;

    @Autowired
    private IProcessInfoAdapter iProcessInfoAdapter;


    @Autowired
    private NotificationHistoryRepository notificationHistoryRepository;

    @Autowired
    private GLMandatoryDocumentChecker glMandatoryDocumentChecker;

    @Autowired
    private ILMandatoryDocumentChecker ilMandatoryDocumentChecker;

    @Autowired
    private GHMandatoryDocumentChecker ghMandatoryDocumentChecker;


    public static ImmutableMap<LineOfBusinessEnum,String> proposalEntitiesMap = ImmutableMap.of(LineOfBusinessEnum.GROUP_LIFE,"group_life_proposal",LineOfBusinessEnum.GROUP_HEALTH,"group_health_proposal",LineOfBusinessEnum.INDIVIDUAL_LIFE,"individual_life_proposal");

    public static ImmutableMap<LineOfBusinessEnum,String> quotationEntitiesMap = ImmutableMap.of(LineOfBusinessEnum.GROUP_LIFE,"group_life_quotation",LineOfBusinessEnum.GROUP_HEALTH,"group_health_quotation",
            LineOfBusinessEnum.INDIVIDUAL_LIFE,"individual_life_quotation");


    public HashMap<String,String> getQuotationNotificationTemplateData(LineOfBusinessEnum lineOfBusiness, String quotationId) throws ProcessInfoException {
        int closureTimePeriod = iProcessInfoAdapter.getClosureTimePeriod(lineOfBusiness, ProcessType.QUOTATION);
        HashMap<String,String> notificationQuotationMap = null;
        if (LineOfBusinessEnum.INDIVIDUAL_LIFE.equals(lineOfBusiness)){
            notificationQuotationMap = notificationFinder.findILQuotationProposerDetail(quotationId);
            notificationQuotationMap.put("closureDays",String.valueOf(closureTimePeriod));
            return notificationQuotationMap;
        }
        Criteria quotationCriteria = Criteria.where("_id").is(quotationId);
        Query query = new Query(quotationCriteria);
        query.fields().include("proposer.proposerName").include("quotationNumber").include("sharedOn").include("insureds.planPremiumDetail.planId").
                include("proposer.contactDetail.addressLine1").include("proposer.contactDetail.addressLine2").include("proposer.contactDetail.province").include("proposer.contactDetail.town")
                .include("proposer.contactDetail.emailAddress").exclude("_id");
        List<Map> quotationNotificationDetail =  mongoTemplate.find(query, Map.class, quotationEntitiesMap.get(lineOfBusiness));
        HashMap<String,String> emailContentMap = transformQuotationNotificationData(quotationNotificationDetail);
        emailContentMap.put("closureDays",String.valueOf(closureTimePeriod));
        checkArgument(isNotEmpty(quotationNotificationDetail), "Notification detail not found for the quotation");
        return emailContentMap;
    }

    public HashMap<String,String> getProposalNotificationTemplateData(LineOfBusinessEnum lineOfBusiness, String proposalId, WaitingForEnum waitingFor){
        Criteria proposalCriteria = Criteria.where("_id").is(proposalId);
        Query query = new Query(proposalCriteria);
        query.fields().include("proposer.firstName").include("proposer.surname").include("proposer.title").include("proposalNumber").include("proposer.residentialAddress.address.address1")
                .include("proposer.residentialAddress.address.address2").include("proposer.residentialAddress.address.province").include("proposer.residentialAddress.address.town")
                .include("submittedOn").exclude("_id");
        List<Map> proposalNotificationDetailMap = mongoTemplate.find(query, Map.class, proposalEntitiesMap.get(lineOfBusiness));
        HashMap<String,String>  proposalNotificationMap = transformProposalNotificationData(proposalNotificationDetailMap);
        String waitingForEnum = waitingFor!=null?waitingFor.name():"";
        StringBuilder documentNameBuilder = getDocumentsRequiredSubmission(waitingForEnum,lineOfBusiness,proposalId);
        proposalNotificationMap.put("documentName",documentNameBuilder.toString());
        return proposalNotificationMap;
    }

    public String getRequestNumberBy(LineOfBusinessEnum lineOfBusinessEnum,ProcessType processType,String id) throws ProcessInfoException {
        HashMap<String,String>  notificationDetailMap  = null;
        switch (processType){
            case QUOTATION:
                notificationDetailMap  = getQuotationNotificationTemplateData(lineOfBusinessEnum,id);
                if (notificationDetailMap!=null){
                    return notificationDetailMap.get("requestNumber")!=null?notificationDetailMap.get("requestNumber").toString():"";
                }
            case PROPOSAL:
                notificationDetailMap  = getProposalNotificationTemplateData(lineOfBusinessEnum, id, null);
                if (notificationDetailMap!=null) {
                    return notificationDetailMap.get("requestNumber")!=null?notificationDetailMap.get("requestNumber").toString():"";
                }
        }
        return "";
    }

    private HashMap<String,String> transformQuotationNotificationData(List<Map> notificationData){
        HashMap<String,String> notificationQuotationMap = new HashMap<String,String>();
        notificationData.parallelStream().map(new Function<Map, Map<String, String>>() {
            @Override
            public Map<String, String> apply(Map map) {
                Map<String, Object> proposerMap = (Map) map.get("proposer");
                if (isNotEmpty(proposerMap)) {
                    notificationQuotationMap.put("proposerName", proposerMap.get("proposerName").toString());
                    Map<String, Object> contactDetailMap = (Map) proposerMap.get("contactDetail");
                    notificationQuotationMap.put("addressLine1", contactDetailMap.get("addressLine1").toString());
                    notificationQuotationMap.put("addressLine2", contactDetailMap.get("addressLine2").toString());
                    notificationQuotationMap.put("province", contactDetailMap.get("province").toString());
                    notificationQuotationMap.put("town", contactDetailMap.get("town").toString());
                    notificationQuotationMap.put("emailAddress", contactDetailMap.get("emailAddress").toString());
                    notificationQuotationMap.put("requestNumber", map.get("quotationNumber").toString());
                }
                return notificationQuotationMap;
            }
        }).collect(Collectors.toList());
        return notificationQuotationMap;
    }

    private HashMap<String,String> transformProposalNotificationData(List<Map> notificationData){
        HashMap<String,String> notificationQuotationMap = new HashMap<String,String>();
        notificationData.parallelStream().map(new Function<Map, Map<String, String>>() {
            @Override
            public Map<String, String> apply(Map map) {
                Map<String, Object> proposerMap = (Map) map.get("proposer");
                if (isNotEmpty(proposerMap)) {
                    notificationQuotationMap.put("firstName", proposerMap.get("firstName")!=null?proposerMap.get("firstName").toString():"");
                    notificationQuotationMap.put("surname", proposerMap.get("surname")!=null?proposerMap.get("surname").toString():"");
                    notificationQuotationMap.put("title", proposerMap.get("title")!=null?proposerMap.get("title").toString():"");
                    Map<String, Object> contactDetailMap = (Map) proposerMap.get("residentialAddress");
                    Map<String, Object> addressMap = (Map) contactDetailMap.get("address");
                    notificationQuotationMap.put("addressLine1",addressMap.get("address1")!=null?addressMap.get("address1").toString():"");
                    notificationQuotationMap.put("addressLine2", addressMap.get("address2")!=null?addressMap.get("address2").toString():"");
                    notificationQuotationMap.put("province", addressMap.get("province")!=null?addressMap.get("province").toString():"");
                    notificationQuotationMap.put("town", addressMap.get("town")!=null?addressMap.get("town").toString():"");
                    notificationQuotationMap.put("emailAddress", proposerMap.get("emailAddress")!=null?proposerMap.get("emailAddress").toString():"");
                    notificationQuotationMap.put("requestNumber", map.get("proposalNumber")!=null?map.get("proposalNumber").toString():"");
                }
                return notificationQuotationMap;
            }
        }).collect(Collectors.toList());
        return notificationQuotationMap;
    }

    public CreateNotificationHistoryCommand generateHistoryDetail(String notificationId,String[] recipientMailAddress,String emailBody){
        NotificationHistory notificationHistory =  notificationHistoryRepository.findOne(notificationId);
        if (notificationHistory!=null) {
            return new CreateNotificationHistoryCommand(notificationHistory.getRequestNumber(), notificationHistory.getRoleType(), notificationHistory.getLineOfBusiness(),
                    notificationHistory.getProcessType(), notificationHistory.getWaitingFor(), notificationHistory.getReminderType(), recipientMailAddress, emailBody.getBytes(), notificationId);
        }
        return null;
    }



    public NotificationBuilder generateNotification(LineOfBusinessEnum lineOfBusiness, ProcessType process, WaitingForEnum waitingFor, ReminderTypeEnum reminderType,
                                                    String requestNumber, String roleType, byte[] templateFile, HashMap<String, String> notificationDetail) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            System.out.println("\n\n\n\n\n\n\nInside the generate Notification method");
        }
        System.out.println("\n\n\n\n\n\n\nInside the generate Notification method");
        checkArgument(notificationDetail !=null,"Notification details cannot be empty");
        checkArgument(templateFile != null, "Notification Template is not uploaded");
        NotificationBuilder notificationBuilder = Notification.builder();
        notificationBuilder.withLineOfBusiness(lineOfBusiness)
                .withProcessType(process)
                .withRequestNumber(requestNumber)
                .withWaitingFor(waitingFor)
                .withReminderType(reminderType)
                .withEmailAddress(notificationDetail.get("emailAddress") != null ? notificationDetail.get("emailAddress").toString() : "")
                .withReminderTemplate(convert(templateFile, notificationDetail, lineOfBusiness, requestNumber))
                .withRoleType(roleType);
        return notificationBuilder;
    }

    public byte[] convert(byte[] templateFile,HashMap<String,String> notificationMap,LineOfBusinessEnum lineOfBusiness,String requestNumber) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            System.out.println("\n\n\n\n\n\n\nConverting the docx to html");
        }
        System.out.println("\n\n\n\n\n\n\nConverting the docx to html");
        File tempFile  = new File("notification_"+requestNumber+".docx");
        System.out.println("\n\n\n\n\n\n\nFile Got created"+tempFile);
        Files.write(templateFile, tempFile);
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(tempFile);
        VariablePrepare.prepare(wordMLPackage);
        MainDocumentPart documentPart = wordMLPackage.getMainDocumentPart();
        if (LOGGER.isDebugEnabled()) {
            System.out.println("\n\n\n\n\n\n\nReplacing the placeholder in the docx");
        }
        System.out.println("\n\n\n\n\n\n\nReplacing the placeholder in the docx");
        notificationMap.put("systemDate", LocalDate.now().toString(AppConstants.DD_MM_YYY_FORMAT));
        notificationMap.put("lineOfBusiness",lineOfBusiness.toString());
        documentPart.variableReplace(notificationMap);
        wordMLPackage.save(tempFile);
        String htmlContent = convertDocxToHtml(tempFile);
        if (LOGGER.isDebugEnabled()) {
            System.out.println("\n\n\n\n\n\n\nFinished coverting the docx to html");
        }
        System.out.println("\n\n\n\n\n\n\nFinished coverting the docx to html");
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

    private StringBuilder getDocumentsRequiredSubmission(String waitingFor,LineOfBusinessEnum lineOfBusiness,String proposalId){
        StringBuilder documentNameBuilder = new StringBuilder();
        if (WaitingForEnum.MANDATORY_DOCUMENTS.name().equals(waitingFor)){
            List<String> documentsRequiredForSubmission=null;
            switch (lineOfBusiness){
                case INDIVIDUAL_LIFE:
                    documentsRequiredForSubmission  = ilMandatoryDocumentChecker.findDocumentRequiredForSubmission(proposalId);
                    break;
                case GROUP_LIFE:
                    documentsRequiredForSubmission = glMandatoryDocumentChecker.findDocumentRequiredForSubmission(proposalId);
                    break;
                case GROUP_HEALTH:
                    documentsRequiredForSubmission = ghMandatoryDocumentChecker.findDocumentRequiredForSubmission(proposalId);
            }
            for (String documentName : documentsRequiredForSubmission ){
                documentNameBuilder.append(documentName+"\n\t");
            }
        }
        return documentNameBuilder;
    }
}
