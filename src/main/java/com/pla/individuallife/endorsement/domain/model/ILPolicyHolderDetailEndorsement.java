package com.pla.individuallife.endorsement.domain.model;

import com.pla.grouplife.sharedresource.model.vo.ProposerContactDetail;
import com.pla.individuallife.sharedresource.model.vo.Proposer;
import lombok.Getter;

/**
 * Created by Samir on 8/4/2015.
 */
@Getter
public class ILPolicyHolderDetailEndorsement {

    private String proposerName;

    private Proposer contactDetail;


    public ILPolicyHolderDetailEndorsement(String proposerName) {
        this.proposerName = proposerName;
    }

    public ILPolicyHolderDetailEndorsement(Proposer proposerContactDetail) {
        this.contactDetail = proposerContactDetail;

    }
}
