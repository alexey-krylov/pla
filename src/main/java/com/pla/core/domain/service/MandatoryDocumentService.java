package com.pla.core.domain.service;

import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.MandatoryDocument;
import com.pla.core.domain.model.ProcessType;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 * Created by Admin on 3/27/2015.
 */
@DomainService
public class MandatoryDocumentService {

    private AdminRoleAdapter adminRoleAdapter;

    @Autowired
    public MandatoryDocumentService(AdminRoleAdapter adminRoleAdapter) {
        this.adminRoleAdapter = adminRoleAdapter;
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
}
