package com.pla.sharedkernel.service;

import com.pla.grouplife.proposal.application.service.GLProposalService;
import com.pla.grouplife.sharedresource.model.vo.GLProposerDocument;
import com.pla.publishedlanguage.dto.ClientDocumentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Admin on 8/25/2015.
 */
@Service
public class GLMandatoryDocumentChecker {

    @Autowired
    private GLProposalService glProposalService;

    public boolean isRequiredForSubmission(String proposalId) {
        List<String> uploadedDocumentNames = getAllUploadedMandatoryDocument(proposalId);
        Set<ClientDocumentDto> documentRequiredForSubmission = glProposalService.getMandatoryDocumentRequiredForSubmission(proposalId);
        long count = documentRequiredForSubmission.parallelStream().filter(new MandatoryDocumentFilter(uploadedDocumentNames)).count();
        return count!=0;
    }

    public List<String> findDocumentRequiredForSubmission(String proposalId) {
        List<String> uploadedDocumentNames =  getAllUploadedMandatoryDocument(proposalId);
        Set<ClientDocumentDto> documentRequiredForSubmission = glProposalService.getMandatoryDocumentRequiredForSubmission(proposalId);
        return documentRequiredForSubmission.parallelStream().filter(new MandatoryDocumentFilter(uploadedDocumentNames)).map(clientDocumentDto->clientDocumentDto.getDocumentName()).collect(Collectors.toList());
    }


    private List<String> getAllUploadedMandatoryDocument(String proposalId){
        List<GLProposerDocument> uploadedDocument = glProposalService.getUploadedMandatoryDocument(proposalId);
        return uploadedDocument.parallelStream().filter(new Predicate<GLProposerDocument>() {
            @Override
            public boolean test(GLProposerDocument glProposerDocument) {
                if (glProposerDocument.isRequireForSubmission() && glProposerDocument.isMandatory())
                    return glProposerDocument.getGridFsDocId()==null;
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
