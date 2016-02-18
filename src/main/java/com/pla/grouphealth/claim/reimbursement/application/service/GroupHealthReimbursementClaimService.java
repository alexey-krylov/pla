package com.pla.grouphealth.claim.reimbursement.application.service;

import com.pla.grouphealth.claim.reimbursement.query.GroupHealthReimbursementClaimFinder;
import com.pla.publishedlanguage.contract.IAuthenticationFacade;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.app.VelocityEngine;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */
@DomainService
public class GroupHealthReimbursementClaimService {
    @Autowired
    @Qualifier("authenticationFacade")
    IAuthenticationFacade authenticationFacade;
    @Autowired
    SequenceGenerator sequenceGenerator;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private VelocityEngine velocityEngine;
    @Autowired
    private GroupHealthReimbursementClaimFinder groupHealthReimbursementClaimFinder;

    public String getLoggedInUsername() {
        String userName = StringUtils.EMPTY;
        Authentication authentication = authenticationFacade.getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            userName = authentication.getName();
        }
        return userName;
    }

    public UserDetails getUserDetailFromAuthentication() {
        Authentication authentication = authenticationFacade.getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            return (UserDetails) authentication.getPrincipal();
        }
        return null;
    }
}
