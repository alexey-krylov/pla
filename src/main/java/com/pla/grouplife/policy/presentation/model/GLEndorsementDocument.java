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
 * Created by Mirror on 10/30/2015.
 */
public enum GLEndorsementDocument {

    MEMBER_ADDITION_WITH_DETAILS("Member Addition with details") {
        @Override
        public EmailAttachment getPolicyDocumentInPDF(List<GLPolicyMailDetailDto> glQuotationDetailDto) throws IOException, JRException {
            byte[] pdfData =  PDFGeneratorUtils.createPDFReportByList(glQuotationDetailDto, "jasperpdf/template/grouplife/endorsement/additionMemEndorsment.jrxml");
            String fileName = "Addition Member Endorsement.pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(pdfData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return new EmailAttachment(fileName, "application/pdf", file);
        }
    }, DELETION_MEMBER_ASSURED("Deletion Member Assured") {
        @Override
        public EmailAttachment getPolicyDocumentInPDF(List<GLPolicyMailDetailDto> glQuotationDetailDto) throws IOException, JRException {
            byte[] pdfData =  PDFGeneratorUtils.createPDFReportByList(glQuotationDetailDto, "jasperpdf/template/grouplife/endorsement/deletionofMembersAssured.jrxml");
            String fileName = "Deletion Member Assured.pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(pdfData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return new EmailAttachment(fileName, "application/pdf", file);
        }
    }, DELETION_OF_MEMBER_ASSURED_MEMBERS_DETAIL("Deletion of Member Assured Members Detail") {
        @Override
        public EmailAttachment getPolicyDocumentInPDF(List<GLPolicyMailDetailDto> glQuotationDetailDto) throws IOException, JRException {
            byte[] pdfData =  PDFGeneratorUtils.createPDFReportByList(glQuotationDetailDto, "jasperpdf/template/grouplife/endorsement/deletionofMembersAssuredMembersDetails.jrxml");
            String fileName = "Deletion of Member Assured Members Detail.pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(pdfData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return new EmailAttachment(fileName, "application/pdf", file);
        }
    }, PROMOTION_OF_MEMBER_ASSURED("Promotion of Members Assured") {
        @Override
        public EmailAttachment getPolicyDocumentInPDF(List<GLPolicyMailDetailDto> glQuotationDetailDto) throws IOException, JRException {
            byte[] pdfData =  PDFGeneratorUtils.createPDFReportByList(glQuotationDetailDto, "jasperpdf/template/grouplife/endorsement/promotionOfMembersAssured.jrxml");
            String fileName = "Promotion of Members Assured.pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(pdfData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return new EmailAttachment(fileName, "application/pdf", file);
        }
    }, ADDITION_NEW_CATEGORY_RELETIONSHIP("Addition New Category Reletionship") {
        @Override
        public EmailAttachment getPolicyDocumentInPDF(List<GLPolicyMailDetailDto> glQuotationDetailDto) throws IOException, JRException {
            byte[] pdfData =  PDFGeneratorUtils.createPDFReportByList(glQuotationDetailDto, "jasperpdf/template/grouplife/endorsement/additionNewCategoryRelationship.jrxml");
            String fileName = "Addition New Category Reletionship.pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(pdfData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return new EmailAttachment(fileName, "application/pdf", file);
        }
    }, ADDITION_NEW_CATEGORY_RELETIONSHIP_MEMBER_DETAILS("Addition New Category Reletionship Member Details") {
        @Override
        public EmailAttachment getPolicyDocumentInPDF(List<GLPolicyMailDetailDto> glQuotationDetailDto) throws IOException, JRException {
            byte[] pdfData =  PDFGeneratorUtils.createPDFReportByList(glQuotationDetailDto, "jasperpdf/template/grouplife/endorsement/additionNewCategoryRelationshipMemberDetails.jrxml");
            String fileName = "Addition New Category Reletionship Member Details.pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(pdfData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return new EmailAttachment(fileName, "application/pdf", file);
        }

    };

    private String description;

    GLEndorsementDocument(String description){
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public abstract EmailAttachment getPolicyDocumentInPDF(List<GLPolicyMailDetailDto> glQuotationDetailDto) throws IOException, JRException;

}
