package com.pla.grouplife.endorsement.domain.service;

import com.pla.grouplife.endorsement.domain.model.GLEndorsementProcessor;
import com.pla.grouplife.endorsement.domain.model.GroupLifeEndorsement;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import org.bson.types.ObjectId;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by Samir on 8/27/2015.
 */
@Service
public class GroupLifeEndorsementService {

    private GroupLifeEndorsementRoleAdapter groupLifeEndorsementRoleAdapter;

    private GLEndorsementNumberGenerator glEndorsementNumberGenerator;

    private GLFinder glFinder;

    @Autowired
    public GroupLifeEndorsementService(GroupLifeEndorsementRoleAdapter groupLifeEndorsementRoleAdapter, GLEndorsementNumberGenerator glEndorsementNumberGenerator, GLFinder glFinder) {
        this.groupLifeEndorsementRoleAdapter = groupLifeEndorsementRoleAdapter;
        this.glEndorsementNumberGenerator = glEndorsementNumberGenerator;
        this.glFinder = glFinder;
    }

    public GroupLifeEndorsement createEndorsement(String policyId, GLEndorsementType glEndorsementType, UserDetails userDetails) {
        GLEndorsementProcessor glEndorsementProcessor = groupLifeEndorsementRoleAdapter.userToEndorsementProcessor(userDetails);
        String endorsementId = ObjectId.get().toString();
        String endorsementNumber = glEndorsementNumberGenerator.getEndorsementNumber(GroupLifeEndorsement.class, LocalDate.now());
        Map<String, Object> policyMap = glFinder.findPolicyById(policyId);
        String policyNumber = ((PolicyNumber) policyMap.get("policyNumber")).getPolicyNumber();
        String policyHolderName = ((Proposer) policyMap.get("proposerName")).getProposerName();
        return glEndorsementProcessor.createEndorsement(endorsementId, endorsementNumber, policyId, policyNumber, policyHolderName, glEndorsementType);
    }
}
