package com.pla.grouplife.endorsement.domain.model;

import com.pla.grouplife.sharedresource.model.vo.ProposerContactDetail;
import lombok.Getter;

/**
 * Created by Samir on 8/4/2015.
 */
@Getter
public class GLPolicyHolderDetailEndorsement {

    private String proposerName;

    private ProposerContactDetail contactDetail;


    public GLPolicyHolderDetailEndorsement(String proposerName) {
        this.proposerName = proposerName;
    }

    public GLPolicyHolderDetailEndorsement(ProposerContactDetail proposerContactDetail) {
        this.contactDetail = proposerContactDetail;

    }
}
