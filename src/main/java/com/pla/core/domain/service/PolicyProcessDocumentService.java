package com.pla.core.domain.service;

import com.pla.core.domain.model.Admin;
import com.pla.core.domain.model.PolicyProcessDocument;
import com.pla.core.domain.model.ProcessType;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

/**
 * Created by Admin on 3/27/2015.
 */
@DomainService
public class PolicyProcessDocumentService {

    private AdminRoleAdapter adminRoleAdapter;

    @Autowired
    public PolicyProcessDocumentService(AdminRoleAdapter adminRoleAdapter) {
        this.adminRoleAdapter = adminRoleAdapter;
    }

    public PolicyProcessDocument createPolicyProcessDocument(String planId,String coverageId,ProcessType process, Set<String> documents,UserDetails userDetails){
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        PolicyProcessDocument policyProcessDocument = admin.createPolicyProcessDocument(planId, coverageId, process, documents);
        return policyProcessDocument;
    }

    public PolicyProcessDocument updatePolicyProcessDocument(PolicyProcessDocument policyProcessDocument,Set<String> documents,UserDetails userDetails){
        Admin admin = adminRoleAdapter.userToAdmin(userDetails);
        PolicyProcessDocument updatePolicyProcessDocument = admin.updatePolicyProcessDocument(policyProcessDocument, documents);
        return updatePolicyProcessDocument;
    }
}
