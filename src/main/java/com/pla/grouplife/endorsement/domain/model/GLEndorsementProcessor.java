package com.pla.grouplife.endorsement.domain.model;

import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.model.vo.GLEndorsementInsured;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.PremiumDetail;
import com.pla.sharedkernel.domain.model.EndorsementNumber;
import com.pla.sharedkernel.domain.model.Policy;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.identifier.EndorsementId;
import com.pla.sharedkernel.identifier.PolicyId;

import java.util.Set;

/**
 * Created by Samir on 8/27/2015.
 */
public class GLEndorsementProcessor {

    private String userName;

    public GLEndorsementProcessor(String userName) {
        this.userName = userName;
    }

    public GroupLifeEndorsement createEndorsement(String endorsementIdInString, String endorsementNumberInString, String policyId, String policyNumber, String policyHolderName, GLEndorsementType endorsementType) {
        EndorsementId endorsementId = new EndorsementId(endorsementIdInString);
        EndorsementNumber endorsementNumber = new EndorsementNumber(endorsementNumberInString);
        Policy policy = new Policy(new PolicyId(policyId), new PolicyNumber(policyNumber), policyHolderName);
        GroupLifeEndorsement groupLifeEndorsement = new GroupLifeEndorsement(endorsementId, endorsementNumber, policy, endorsementType);
        return groupLifeEndorsement;
    }

    public GroupLifeEndorsement updateWithInsured(GroupLifeEndorsement groupLifeEndorsement, Set<Insured> insureds) {
        return groupLifeEndorsement.updateWithInsureds(insureds);
    }

    public GroupLifeEndorsement updateWithGLEndorsementInsured(GroupLifeEndorsement groupLifeEndorsement, GLEndorsementInsured glEndorsementInsured) {
        return groupLifeEndorsement.updateWithGLEndorsementInsured(glEndorsementInsured);
    }

    public GroupLifeEndorsement updateWithPremiumDetail(GroupLifeEndorsement groupLifeEndorsement, PremiumDetail premiumDetail) {
        return groupLifeEndorsement.updateWithPremiumDetail(premiumDetail);
    }
}
