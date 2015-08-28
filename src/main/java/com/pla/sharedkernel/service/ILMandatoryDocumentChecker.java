package com.pla.sharedkernel.service;

import com.pla.individuallife.proposal.service.ILProposalService;
import com.pla.individuallife.sharedresource.model.vo.ILProposerDocument;
import com.pla.publishedlanguage.dto.ClientDocumentDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Admin on 8/25/2015.
 */
@Service
public class ILMandatoryDocumentChecker {

    @Autowired
    private ILProposalService ilProposalService;

    public boolean isRequiredForSubmission(String proposalId) {
        List<String> uploadedDocumentNames = getAllUploadedMandatoryDocument(proposalId);
        List<ClientDocumentDto>  documentRequiredForSubmission = ilProposalService.getDocumentRequiredForSubmission(proposalId);
        long count = documentRequiredForSubmission.parallelStream().filter(new MandatoryDocumentFilter(uploadedDocumentNames)).count();
        return count!=0;
    }

    public List<String> findDocumentRequiredForSubmission(String proposalId) {
        List<String> uploadedDocumentNames =  getAllUploadedMandatoryDocument(proposalId);
        List<ClientDocumentDto>  documentRequiredForSubmission = ilProposalService.getDocumentRequiredForSubmission(proposalId);
        return documentRequiredForSubmission.parallelStream().filter(new MandatoryDocumentFilter(uploadedDocumentNames)).map(clientDocumentDto->clientDocumentDto.getDocumentName()).collect(Collectors.toList());
    }

    private List<String> getAllUploadedMandatoryDocument(String proposalId){
        List<ILProposerDocument> uploadedDocument = ilProposalService.getAllUploadedDocument(proposalId);
        return uploadedDocument.parallelStream().filter(new Predicate<ILProposerDocument>() {
            @Override
            public boolean test(ILProposerDocument ilProposerDocument) {
                if (ilProposerDocument.isRequireForSubmission() && ilProposerDocument.isMandatory())
                    return !ilProposerDocument.isApproved();
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
