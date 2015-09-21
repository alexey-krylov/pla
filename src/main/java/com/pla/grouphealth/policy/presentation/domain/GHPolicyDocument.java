package com.pla.grouphealth.policy.presentation.domain;

import com.google.common.collect.Maps;
import com.pla.grouphealth.policy.presentation.dto.GHPolicyDetailDto;
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
public enum GHPolicyDocument {

    POLICY("Policy Sample") {
        @Override
        public EmailAttachment getPolicyDocumentInPDF(List<GHPolicyDetailDto> ghPolicyDetailDtoList) throws IOException, JRException {
            byte[] pdfData = PDFGeneratorUtils.createPDFReportByList(ghPolicyDetailDtoList, "jasperpdf/template/grouphealth/policy/ghPolicy.jrxml");
            String fileName = "Policy Sample.pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(pdfData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return new EmailAttachment(fileName, "application/pdf", file);
        }
    },GH_POLICY_DOC("Group Health Policy") {
        @Override
        public EmailAttachment getPolicyDocumentInPDF(List<GHPolicyDetailDto> ghPolicyDetailDtoList) throws IOException, JRException {
            byte[] pdfData = PDFGeneratorUtils.createPDFReportByList(ghPolicyDetailDtoList, "jasperpdf/template/grouphealth/policy/GHPolicyDoc.jrxml");
            String fileName = "Group Health Policy.pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(pdfData);
            fileOutputStream.flush();
            fileOutputStream.close();
            return new EmailAttachment(fileName, "application/pdf", file);
        }
    };

    private String description;

    GHPolicyDocument(String description){
        this.description = description;
    }

    public static List<Map<String,Object>> getDeclaredPolicyDocument(){
        return Arrays.asList(GHPolicyDocument.values()).parallelStream().map(new Function<GHPolicyDocument, Map<String,Object>>() {
            @Override
            public Map<String, Object> apply(GHPolicyDocument ghPolicyDocument) {
                Map<String,Object> policyDocumentMap = Maps.newLinkedHashMap();
                policyDocumentMap.put("documentCode",ghPolicyDocument.name());
                policyDocumentMap.put("documentName",ghPolicyDocument.toString());
                return policyDocumentMap;
            }
        }).collect(Collectors.toList());
    }



    @Override
    public String toString() {
        return  description;
    }


    public static List<EmailAttachment> getAllPolicyDocument(List<GHPolicyDetailDto> ghQuotationDetailDtos){
        return Arrays.asList(GHPolicyDocument.values()).parallelStream().map(new Function<GHPolicyDocument, EmailAttachment>() {
            @Override
            public EmailAttachment apply(GHPolicyDocument ghPolicyDocument) {
                EmailAttachment emailAttachment = null;
                try {
                    emailAttachment = ghPolicyDocument.getPolicyDocumentInPDF(ghQuotationDetailDtos);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JRException e) {
                    e.printStackTrace();
                }
                return emailAttachment;
            }
        }).collect(Collectors.toList());
    }

    public abstract EmailAttachment getPolicyDocumentInPDF(List<GHPolicyDetailDto> ghPolicyDetailDtoList) throws IOException, JRException;
}
