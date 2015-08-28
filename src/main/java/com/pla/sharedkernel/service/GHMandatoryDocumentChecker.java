package com.pla.sharedkernel.service;

import com.pla.grouphealth.proposal.application.service.GHProposalService;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerDocument;
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
public class GHMandatoryDocumentChecker {

    @Autowired
    private GHProposalService ghProposalService;

    public boolean isRequiredForSubmission(String proposalId) {
        List<String> uploadedDocumentNames = getAllUploadedMandatoryDocument(proposalId);
        Set<ClientDocumentDto> documentRequiredForSubmission = ghProposalService.getMandatoryDocumentRequiredForSubmission(proposalId);
        long count = documentRequiredForSubmission.parallelStream().filter(new MandatoryDocumentFilter(uploadedDocumentNames)).count();
        return count!=0;
    }

    public List<String> findDocumentRequiredForSubmission(String proposalId) {
        List<String> uploadedDocumentNames =  getAllUploadedMandatoryDocument(proposalId);
        Set<ClientDocumentDto> documentRequiredForSubmission = ghProposalService.getMandatoryDocumentRequiredForSubmission(proposalId);
        return documentRequiredForSubmission.parallelStream().filter(new MandatoryDocumentFilter(uploadedDocumentNames)).map(clientDocumentDto->clientDocumentDto.getDocumentName()).collect(Collectors.toList());
    }

    private List<String> getAllUploadedMandatoryDocument(String proposalId){
        List<GHProposerDocument> uploadedDocument = ghProposalService.getUploadedMandatoryDocument(proposalId);
        return uploadedDocument.parallelStream().filter(new Predicate<GHProposerDocument>() {
            @Override
            public boolean test(GHProposerDocument ghProposerDocument) {
                if (ghProposerDocument.isRequireForSubmission() && ghProposerDocument.isMandatory())
                    return ghProposerDocument.getGridFsDocId()==null;
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
