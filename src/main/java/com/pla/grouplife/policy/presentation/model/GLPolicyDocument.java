package com.pla.grouplife.policy.presentation.model;

import com.google.common.collect.Maps;
import com.pla.grouplife.policy.presentation.dto.GLPolicyMailDetailDto;
import com.pla.sharedkernel.service.EmailAttachment;
import com.pla.sharedkernel.util.PDFGeneratorUtils;
import net.sf.jasperreports.engine.JRException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Admin on 9/14/2015.
 */
public enum GLPolicyDocument {
    ACTIVE_AT_WORK("Active at work Declaration Form") {
        @Override
        public EmailAttachment getPolicyDocumentInPDF(List<GLPolicyMailDetailDto> glQuotationDetailDto) throws IOException, JRException {
            byte[] pdfData =  PDFGeneratorUtils.createPDFReportByList(glQuotationDetailDto, "jasperpdf/template/grouplife/policy/coverLetter.jrxml");
            String fileName = "Active at work Declaration Form.pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(pdfData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return new EmailAttachment(fileName, "application/pdf", file);
        }
    },CREDIT_AGREEMENT("Credit Agreement") {
        @Override
        public EmailAttachment getPolicyDocumentInPDF(List<GLPolicyMailDetailDto> glQuotationDetailDto) throws IOException, JRException {
            byte[] pdfData =  PDFGeneratorUtils.createPDFReportByList(glQuotationDetailDto, "jasperpdf/template/grouplife/policy/creditAgreement.jrxml");
            String fileName = "Credit Agreement.pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(pdfData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return new EmailAttachment(fileName, "application/pdf", file);
        }
    },GL_POLICY_DOCUMENT("Group Life Policy Document") {
        @Override
        public EmailAttachment getPolicyDocumentInPDF(List<GLPolicyMailDetailDto> glQuotationDetailDto) throws IOException, JRException {
            byte[] pdfData =  PDFGeneratorUtils.createPDFReportByList(glQuotationDetailDto, "jasperpdf/template/grouplife/policy/glPolicyDocument.jrxml");
            String fileName = "Group Life Policy Document.pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(pdfData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return new EmailAttachment(fileName, "application/pdf", file);
        }
    },
    COVER_LETTER("Cover Letter") {
        @Override
        public EmailAttachment getPolicyDocumentInPDF(List<GLPolicyMailDetailDto> glQuotationDetailDto) throws IOException, JRException {
            byte[] pdfData =  PDFGeneratorUtils.createPDFReportByList(glQuotationDetailDto, "jasperpdf/template/grouplife/policy/coverLetter.jrxml");
            String fileName = "Cover Letter.pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(pdfData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return new EmailAttachment(fileName, "application/pdf", file);
        }
    },LONG_TERM_AGREEMENT("Long Term Agreement") {
        @Override
        public EmailAttachment getPolicyDocumentInPDF(List<GLPolicyMailDetailDto> glQuotationDetailDto) throws IOException, JRException {
            byte[] pdfData =  PDFGeneratorUtils.createPDFReportByList(glQuotationDetailDto, "jasperpdf/template/grouplife/policy/LongTermAgr.jrxml");
            String fileName = "Long Term Agreement.pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(pdfData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return new EmailAttachment(fileName, "application/pdf", file);
        }
    },UNDERWRITING_SCHEDULE("Underwriting Schedule") {
        @Override
        public EmailAttachment getPolicyDocumentInPDF(List<GLPolicyMailDetailDto> glQuotationDetailDto) throws IOException, JRException {
            byte[] pdfData =  PDFGeneratorUtils.createPDFReportByList(glQuotationDetailDto, "jasperpdf/template/grouplife/policy/underwrittingschedule.jrxml");
            String fileName = "Underwriting Schedule.pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(pdfData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return new EmailAttachment(fileName, "application/pdf", file);
        }
    };

    private String description;

    GLPolicyDocument(String description){
        this.description = description;
    }

    public static List<Map<String,Object>> getDeclaredPolicyDocument(){
        return Arrays.asList(GLPolicyDocument.values()).parallelStream().map(new Function<GLPolicyDocument, Map<String,Object>>() {
            @Override
            public Map<String, Object> apply(GLPolicyDocument glPolicyDocument) {
                Map<String,Object> policyDocumentMap = Maps.newLinkedHashMap();
                policyDocumentMap.put("documentCode",glPolicyDocument.name());
                policyDocumentMap.put("documentName",glPolicyDocument.toString());
                return policyDocumentMap;
            }
        }).collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return description;
    }

    public abstract EmailAttachment getPolicyDocumentInPDF(List<GLPolicyMailDetailDto> glQuotationDetailDto) throws IOException, JRException;

    public static List<EmailAttachment> getAllPolicyDocument(List<GLPolicyMailDetailDto> glQuotationDetailDto){
        return Arrays.asList(GLPolicyDocument.values()).parallelStream().map(new Function<GLPolicyDocument, EmailAttachment>() {
            @Override
            public EmailAttachment apply(GLPolicyDocument glPolicyDocument) {
                EmailAttachment emailAttachment = null;
                try {
                    emailAttachment = glPolicyDocument.getPolicyDocumentInPDF(glQuotationDetailDto);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JRException e) {
                    e.printStackTrace();
                }
                return emailAttachment;
            }
        }).collect(Collectors.toList());
    }
}
