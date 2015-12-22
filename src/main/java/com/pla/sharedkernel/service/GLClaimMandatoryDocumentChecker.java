package com.pla.sharedkernel.service;

import com.pla.grouplife.claim.application.service.GLClaimService;
import com.pla.grouplife.claim.domain.model.GLClaimDocument;
import com.pla.publishedlanguage.dto.ClientDocumentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by ak
 */
@Service
public class GLClaimMandatoryDocumentChecker {

    @Autowired
    private GLClaimService glClaimService;

    public boolean isRequiredForSubmission(String claimId) {
        List<String> uploadedDocumentNames = getAllUploadedMandatoryDocument(claimId);
        Set<ClientDocumentDto> documentRequiredForSubmission = glClaimService.getMandatoryDocumentRequiredForSubmission(claimId);
        long count = documentRequiredForSubmission.parallelStream().filter(new MandatoryDocumentFilter(uploadedDocumentNames)).count();
        return count!=0;
    }

    public List<String> findDocumentRequiredForSubmission(String claimId) {
        List<String> uploadedDocumentNames =  getAllUploadedMandatoryDocument(claimId);
        Set<ClientDocumentDto> documentRequiredForSubmission = glClaimService.getMandatoryDocumentRequiredForSubmission(claimId);
        return documentRequiredForSubmission.parallelStream().filter(new MandatoryDocumentFilter(uploadedDocumentNames)).map(clientDocumentDto->clientDocumentDto.getDocumentName()).collect(Collectors.toList());
    }


    private List<String> getAllUploadedMandatoryDocument(String claimId){
        List<GLClaimDocument> uploadedDocument = glClaimService.getUploadedMandatoryDocument(claimId);
        return uploadedDocument.parallelStream().filter(new Predicate<GLClaimDocument>() {
            @Override
            public boolean test(GLClaimDocument glClaimDocument) {
                if (glClaimDocument.isRequireForSubmission() && glClaimDocument.isMandatory())
                    return glClaimDocument.getGridFsDocId()==null;
                return false;
            }
        }).map(uploadedDocumentName -> uploadedDocumentName.getDocumentId()).collect(Collectors.toList());
    }


    private class MandatoryDocumentFilter implements Predicate<ClientDocumentDto> {

        private List<String> uploadedDocument;

        public MandatoryDocumentFilter(List<String> uploadedDocument) {
            this.uploadedDocument = uploadedDocument;
        }

        @Override
        public boolean test(ClientDocumentDto clientDocumentDto) {
            return !uploadedDocument.contains(clientDocumentDto.getDocumentCode()) ;
        }
    }
}
