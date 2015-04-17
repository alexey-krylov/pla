package com.pla.core.domain.service;

import com.google.common.collect.Lists;
import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.MandatoryDocument;
import com.pla.core.domain.model.ProcessType;
import com.pla.core.dto.MandatoryDocumentDto;
import com.pla.core.query.MandatoryDocumentFinder;
import com.pla.core.query.PlanFinder;
import com.pla.sharedkernel.identifier.PlanId;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Set;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 3/27/2015.
 */
@DomainService
public class MandatoryDocumentService {

    private AdminRoleAdapter adminRoleAdapter;
    private MandatoryDocumentFinder mandatoryDocumentFinder;
    private PlanFinder planFinder;

    @Autowired
    public MandatoryDocumentService(AdminRoleAdapter adminRoleAdapter,MandatoryDocumentFinder mandatoryDocumentFinder,PlanFinder planFinder) {
        this.adminRoleAdapter = adminRoleAdapter;
        this.mandatoryDocumentFinder = mandatoryDocumentFinder;
        this.planFinder = planFinder;
    }

    public MandatoryDocument createMandatoryDocument(String planId, String coverageId, ProcessType process, Set<String> documents, UserDetails userDetails){
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        MandatoryDocument mandatoryDocument = admin.createMandatoryDocument(planId, coverageId, process, documents);
        return mandatoryDocument;
    }

    public MandatoryDocument updateMandatoryDocument(MandatoryDocument mandatoryDocument, Set<String> documents, UserDetails userDetails){
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        MandatoryDocument updateMandatoryDocument = admin.updateMandatoryDocument(mandatoryDocument, documents);
        return updateMandatoryDocument;
    }

    public List<MandatoryDocumentDto> getMandatoryDocuments(){
        List<MandatoryDocumentDto> mandatoryDocumentDtos = mandatoryDocumentFinder.getAllMandatoryDocument();
        List<MandatoryDocumentDto> mandatoryDocumentDtoList =  transformMandatoryDocument(mandatoryDocumentDtos);
        return mandatoryDocumentDtoList;
    }

    public List<MandatoryDocumentDto> getMandatoryDocumentById(Long documentId){
        List<MandatoryDocumentDto> mandatoryDocumentDtos = mandatoryDocumentFinder.getMandatoryDocumentById(documentId);
        List<MandatoryDocumentDto> mandatoryDocumentDtoList =  transformMandatoryDocument(mandatoryDocumentDtos);
        return mandatoryDocumentDtoList;
    }

    public List<MandatoryDocumentDto> transformMandatoryDocument(List<MandatoryDocumentDto> mandatoryDocumentDtos){
        List<MandatoryDocumentDto> mandatoryDocumentDtoList = Lists.newArrayList();
        for (MandatoryDocumentDto mandatoryDocumentDto : mandatoryDocumentDtos) {
           String planName = planFinder.getPlanName(new PlanId(mandatoryDocumentDto.getPlanId()));
            List<String> coverages = planFinder.getCoverageName(new PlanId(mandatoryDocumentDto.getPlanId()));
            if (isNotEmpty(planName))
                mandatoryDocumentDto.setPlanName(planName);
            if (isNotEmpty(coverages))
                mandatoryDocumentDto.setCoverageName(coverages.get(0));
            mandatoryDocumentDtoList.add(mandatoryDocumentDto);
        }
        return mandatoryDocumentDtoList;
    }
}
