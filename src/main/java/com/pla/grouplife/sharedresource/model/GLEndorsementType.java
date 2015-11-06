package com.pla.grouplife.sharedresource.model;

import com.google.common.collect.Lists;
import com.pla.grouplife.endorsement.domain.model.GLEndorsement;
import com.pla.grouplife.endorsement.domain.model.GLMemberEndorsement;
import com.pla.grouplife.policy.presentation.dto.GLPolicyMailDetailDto;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.InsuredDependent;
import com.pla.sharedkernel.service.EmailAttachment;
import com.pla.sharedkernel.util.PDFGeneratorUtils;
import net.sf.jasperreports.engine.JRException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 8/5/2015.
 */
public enum GLEndorsementType {

    FREE_COVER_LIMIT("Free Cover Limit") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return null;
        }

        @Override
        public EmailAttachment getEndorsementDocumentInPDF(List<GLPolicyMailDetailDto> glEndorsementDetailDto, GLEndorsement glEndorsement) throws IOException, JRException {
            return null;
        }
    }, ASSURED_MEMBER_ADDITION("Member Addition") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return Arrays.asList(GLEndorsementExcelHeader.MAN_NUMBER, GLEndorsementExcelHeader.NRC_NUMBER, GLEndorsementExcelHeader.CATEGORY, GLEndorsementExcelHeader.RELATIONSHIP
                    , GLEndorsementExcelHeader.SALUTATION, GLEndorsementExcelHeader.FIRST_NAME, GLEndorsementExcelHeader.LAST_NAME, GLEndorsementExcelHeader.GENDER,
                    GLEndorsementExcelHeader.DATE_OF_BIRTH, GLEndorsementExcelHeader.OCCUPATION, GLEndorsementExcelHeader.NO_OF_ASSURED,
                    GLEndorsementExcelHeader.ANNUAL_INCOME, GLEndorsementExcelHeader.MAIN_ASSURED_CLIENT_ID);
        }

        @Override
        public EmailAttachment getEndorsementDocumentInPDF(List<GLPolicyMailDetailDto> glEndorsementDetailDto, GLEndorsement glEndorsement) throws IOException, JRException {
            if (isNotEmpty(glEndorsementDetailDto)){
                glEndorsementDetailDto.get(0).setEndorsementDetailHeaderName("Membership Schedule");
            }
            GLMemberEndorsement glMemberEndorsement  = glEndorsement.getMemberEndorsement();
            Set<Insured> insureds =  glMemberEndorsement.getInsureds();
            Optional<Insured> insuredOptional =  insureds.parallelStream().filter(insured ->
                    insured.getNoOfAssured()!=null).findAny();
            Optional<InsuredDependent>  dependentOptional  = Optional.empty();
            for (Insured insured : insureds){
                dependentOptional =   insured.getInsuredDependents().parallelStream().filter(dependent->dependent.getNoOfAssured()!=null).findAny();
            }
            byte[] pdfData = null;
            EmailAttachment emailAttachment = new EmailAttachment();
            if (insuredOptional.isPresent() || dependentOptional.isPresent()) {
                pdfData = PDFGeneratorUtils.createPDFReportByList(glEndorsementDetailDto, "jasperpdf/template/grouplife/endorsement/MemAdditionWithNoOfAssured.jrxml");
            }
            else {
                pdfData = PDFGeneratorUtils.createPDFReportByList(glEndorsementDetailDto, "jasperpdf/template/grouplife/endorsement/MemAddWithDetail.jrxml");
                byte[] detailsData =  PDFGeneratorUtils.createPDFReportByList(glEndorsementDetailDto, "jasperpdf/template/grouplife/endorsement/underwrittingschedule.jrxml");
                String fileName = "Member_addition_details_"+glEndorsementDetailDto.get(0).getEndorsementNumber()+".pdf";
                File file = new File(fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(detailsData);
                fileOutputStream.flush();
                fileOutputStream.close();
                EmailAttachment subAttachment = new EmailAttachment(fileName,"application/pdf", file);
                emailAttachment.setSubAttachments(subAttachment);
            }
            String fileName = "Addition Member Endorsement_"+glEndorsementDetailDto.get(0).getEndorsementNumber()+".pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(pdfData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return emailAttachment.withAttachment(fileName,"application/pdf", file);
        }
    }, ASSURED_MEMBER_DELETION("Member Deletion") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return Arrays.asList(GLEndorsementExcelHeader.CATEGORY, GLEndorsementExcelHeader.RELATIONSHIP
                    , GLEndorsementExcelHeader.NO_OF_ASSURED, GLEndorsementExcelHeader.CLIENT_ID);
        }

        @Override
        public EmailAttachment getEndorsementDocumentInPDF(List<GLPolicyMailDetailDto> glEndorsementDetailDto, GLEndorsement glEndorsement) throws IOException, JRException {
            if (isNotEmpty(glEndorsementDetailDto)){
                glEndorsementDetailDto.get(0).setEndorsementDetailHeaderName("List of Members Deleted");
            }
            GLMemberEndorsement glMemberEndorsement  =glEndorsement.getMemberDeletionEndorsements();
            Set<Insured>insureds=glMemberEndorsement.getInsureds();
            Optional<Insured> insuredOptional =  insureds.parallelStream().filter(insured ->
                    insured.getNoOfAssured()!=null).findAny();
            byte []pdfData=null;
            EmailAttachment emailAttachment = new EmailAttachment();
            Optional<InsuredDependent>  dependentOptional  = Optional.empty();
            for (Insured insured : insureds){
                dependentOptional =   insured.getInsuredDependents().parallelStream().filter(dependent->dependent.getNoOfAssured()!=null).findAny();
            }
            if (insuredOptional.isPresent() || dependentOptional.isPresent()) {
                pdfData = PDFGeneratorUtils.createPDFReportByList(glEndorsementDetailDto, "jasperpdf/template/grouplife/endorsement/MemberDelWithNoOfAssured.jrxml");
            }
            else {
                pdfData = PDFGeneratorUtils.createPDFReportByList(glEndorsementDetailDto, "jasperpdf/template/grouplife/endorsement/MemDelWithDetails.jrxml");
                byte[] detailsData =  PDFGeneratorUtils.createPDFReportByList(glEndorsementDetailDto, "jasperpdf/template/grouplife/endorsement/underwrittingschedule.jrxml");
                String fileName = "Member_deletion_details_"+glEndorsementDetailDto.get(0).getEndorsementNumber()+".pdf";
                File file = new File(fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(detailsData);
                fileOutputStream.flush();
                fileOutputStream.close();
                EmailAttachment subAttachment = new EmailAttachment(fileName,"application/pdf", file);
                emailAttachment.setSubAttachments(subAttachment);
            }
            String fileName = "Deletion Member Assured_"+glEndorsementDetailDto.get(0).getEndorsementNumber()+".pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(pdfData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return emailAttachment.withAttachment(fileName,"application/pdf", file);
        }
    }, MEMBER_PROMOTION("Promotion") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return Arrays.asList(GLEndorsementExcelHeader.CLIENT_ID, GLEndorsementExcelHeader.OLD_ANNUAL_INCOME, GLEndorsementExcelHeader.NEW_ANNUAL_INCOME);
        }

        @Override
        public EmailAttachment getEndorsementDocumentInPDF(List<GLPolicyMailDetailDto> glEndorsementDetailDto, GLEndorsement glEndorsement) throws IOException, JRException {
            if (isNotEmpty(glEndorsementDetailDto)){
                glEndorsementDetailDto.get(0).setEndorsementDetailHeaderName("Membership Schedule");
            }
            GLMemberEndorsement glMemberEndorsement  = glEndorsement.getPremiumEndorsement();
            Set<Insured> insureds =  glMemberEndorsement.getInsureds();
            Optional<Insured> insuredOptional =  insureds.parallelStream().filter(insured ->
                    insured.getNoOfAssured()!=null).findAny();
            byte []pdfData=null;
            EmailAttachment emailAttachment = new EmailAttachment();
            Optional<InsuredDependent>  dependentOptional  = Optional.empty();
            for (Insured insured : insureds){
                dependentOptional =  insured.getInsuredDependents().parallelStream().filter(dependent->dependent.getNoOfAssured()!=null).findAny();
            }
            if (insuredOptional.isPresent() || dependentOptional.isPresent()) {
                pdfData =  PDFGeneratorUtils.createPDFReportByList(glEndorsementDetailDto, "jasperpdf/template/grouplife/endorsement/promotionOfMembersAssured.jrxml");
            }
            else {
                pdfData =  PDFGeneratorUtils.createPDFReportByList(glEndorsementDetailDto, "jasperpdf/template/grouplife/endorsement/promotionOfMembersAssured.jrxml");
                byte[] detailsData =  PDFGeneratorUtils.createPDFReportByList(glEndorsementDetailDto, "jasperpdf/template/grouplife/endorsement/underwrittingschedule.jrxml");
                String fileName = "Member_promotion_details_"+glEndorsementDetailDto.get(0).getEndorsementNumber()+".pdf";
                File file = new File(fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(detailsData);
                fileOutputStream.flush();
                fileOutputStream.close();
                EmailAttachment subAttachment = new EmailAttachment(fileName,"application/pdf", file);
                emailAttachment.setSubAttachments(subAttachment);
            }
            String fileName = "Promotion of Members Assured_"+glEndorsementDetailDto.get(0).getEndorsementNumber()+".pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(pdfData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return emailAttachment.withAttachment(fileName, "application/pdf", file);
        }
    },
    NEW_CATEGORY_RELATION("Introduction of New category") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return Arrays.asList(GLEndorsementExcelHeader.PROPOSER_NAME, GLEndorsementExcelHeader.MAN_NUMBER, GLEndorsementExcelHeader.NRC_NUMBER, GLEndorsementExcelHeader.ANNUAL_INCOME,
                    GLEndorsementExcelHeader.SALUTATION, GLEndorsementExcelHeader.FIRST_NAME, GLEndorsementExcelHeader.LAST_NAME,GLEndorsementExcelHeader.DATE_OF_BIRTH,
                    GLEndorsementExcelHeader.GENDER, GLEndorsementExcelHeader.OCCUPATION, GLEndorsementExcelHeader.CATEGORY,
                    GLEndorsementExcelHeader.RELATIONSHIP,  GLEndorsementExcelHeader.NO_OF_ASSURED, GLEndorsementExcelHeader.PLAN,
                    GLEndorsementExcelHeader.INCOME_MULTIPLIER, GLEndorsementExcelHeader.SUM_ASSURED, GLEndorsementExcelHeader.PLAN_PREMIUM);
        }

        @Override
        public EmailAttachment getEndorsementDocumentInPDF(List<GLPolicyMailDetailDto> glEndorsementDetailDto, GLEndorsement glEndorsement) throws IOException, JRException {
            if (isNotEmpty(glEndorsementDetailDto)){
                glEndorsementDetailDto.get(0).setEndorsementDetailHeaderName("Membership Schedule");
            }
            GLMemberEndorsement glMemberEndorsement= glEndorsement.getNewCategoryRelationEndorsement();
            Set<Insured> insureds=glMemberEndorsement.getInsureds();
            Optional<Insured> insuredOptional =  insureds.parallelStream().filter(insured ->
                    insured.getNoOfAssured()!=null).findAny();
            byte []pdfData=null;
            EmailAttachment emailAttachment = new EmailAttachment();
            Optional<InsuredDependent>  dependentOptional  = Optional.empty();
            for (Insured insured : insureds){
                dependentOptional =  insured.getInsuredDependents().parallelStream().filter(dependent->dependent.getNoOfAssured()!=null).findAny();
            }
            if (insuredOptional.isPresent() || dependentOptional.isPresent()) {
                pdfData =  PDFGeneratorUtils.createPDFReportByList(glEndorsementDetailDto, "jasperpdf/template/grouplife/endorsement/additionNewCategoryRelationship.jrxml");
            }
            else {
                pdfData =  PDFGeneratorUtils.createPDFReportByList(glEndorsementDetailDto, "jasperpdf/template/grouplife/endorsement/additionNewCategoryRelationshipMemberDetails.jrxml");
                byte[] detailsData =  PDFGeneratorUtils.createPDFReportByList(glEndorsementDetailDto, "jasperpdf/template/grouplife/endorsement/underwrittingschedule.jrxml");
                String fileName = "Member_new_category_details_"+glEndorsementDetailDto.get(0).getEndorsementNumber()+".pdf";
                File file = new File(fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(detailsData);
                fileOutputStream.flush();
                fileOutputStream.close();
                EmailAttachment subAttachment = new EmailAttachment(fileName,"application/pdf", file);
                emailAttachment.setSubAttachments(subAttachment);
            }
            String fileName = "Addition New Category Reletionship_"+glEndorsementDetailDto.get(0).getEndorsementNumber()+".pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(pdfData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return emailAttachment.withAttachment(fileName, "application/pdf", file);
        }
    }, CHANGE_POLICY_HOLDER_NAME("Correction of Name-Policyholder") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return null;
        }

        @Override
        public EmailAttachment getEndorsementDocumentInPDF(List<GLPolicyMailDetailDto> glEndorsementDetailDto, GLEndorsement glEndorsement) throws IOException, JRException {
            return null;
        }
    }, CHANGE_ASSURED_NAME("Correction Of Name - Life Assured") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return Arrays.asList(GLEndorsementExcelHeader.CLIENT_ID, GLEndorsementExcelHeader.SALUTATION, GLEndorsementExcelHeader.FIRST_NAME, GLEndorsementExcelHeader.LAST_NAME);
        }

        @Override
        public EmailAttachment getEndorsementDocumentInPDF(List<GLPolicyMailDetailDto> glEndorsementDetailDto, GLEndorsement glEndorsement) throws IOException, JRException {
            return null;
        }
    },
    CHANGE_POLICY_HOLDER_CONTACT_DETAIL("Change of Address") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return null;
        }

        @Override
        public EmailAttachment getEndorsementDocumentInPDF(List<GLPolicyMailDetailDto> glEndorsementDetailDto, GLEndorsement glEndorsement) throws IOException, JRException {
            return null;
        }
    }, CHANGE_DOB("Change Life Assured Date of Birth") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return Arrays.asList(GLEndorsementExcelHeader.CLIENT_ID, GLEndorsementExcelHeader.DATE_OF_BIRTH);
        }

        @Override
        public EmailAttachment getEndorsementDocumentInPDF(List<GLPolicyMailDetailDto> glEndorsementDetailDto, GLEndorsement glEndorsement) throws IOException, JRException {
            return null;
        }
    }, CHANGE_NRC("Correction Life Assured - NRC") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return Arrays.asList(GLEndorsementExcelHeader.CLIENT_ID, GLEndorsementExcelHeader.NRC_NUMBER);
        }

        @Override
        public EmailAttachment getEndorsementDocumentInPDF(List<GLPolicyMailDetailDto> glEndorsementDetailDto, GLEndorsement glEndorsement) throws IOException, JRException {
            return null;
        }
    },
    CHANGE_MAN_NUMBER("Correction Life Assured-MAN Number") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return Arrays.asList(GLEndorsementExcelHeader.CLIENT_ID, GLEndorsementExcelHeader.MAN_NUMBER);
        }

        @Override
        public EmailAttachment getEndorsementDocumentInPDF(List<GLPolicyMailDetailDto> glEndorsementDetailDto, GLEndorsement glEndorsement) throws IOException, JRException {
            return null;
        }
    }, CHANGE_GENDER("Correction Life Assured - Gender") {
        @Override
        public List<GLEndorsementExcelHeader> getAllowedExcelHeaders() {
            return Arrays.asList(GLEndorsementExcelHeader.CLIENT_ID, GLEndorsementExcelHeader.GENDER);
        }

        @Override
        public EmailAttachment getEndorsementDocumentInPDF(List<GLPolicyMailDetailDto> glEndorsementDetailDto, GLEndorsement glEndorsement) throws IOException, JRException {
            return null;
        }
    };

    private String description;

    GLEndorsementType(String description) {
        this.description = description;
    }

    public static List<Map<String, String>> getAllEndorsementType() {
        List<Map<String, String>> endorsementTypes = Lists.newArrayList();
        GLEndorsementType[] glEndorsementTypes = GLEndorsementType.values();
        for (int count = 0; count < glEndorsementTypes.length; count++) {
            GLEndorsementType glEndorsementType = glEndorsementTypes[count];
            Map<String, String> map = new HashMap<>();
            map.put("code", glEndorsementType.name());
            map.put("description", glEndorsementType.getDescription());
            endorsementTypes.add(map);
        }
        return endorsementTypes;
    }

    public String getDescription() {
        return description;
    }

    public abstract List<GLEndorsementExcelHeader> getAllowedExcelHeaders();
    public abstract EmailAttachment getEndorsementDocumentInPDF(List<GLPolicyMailDetailDto> glEndorsementDetailDto, GLEndorsement glEndorsement) throws IOException, JRException;
}
