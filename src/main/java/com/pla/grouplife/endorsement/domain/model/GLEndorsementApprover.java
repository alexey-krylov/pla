package com.pla.grouplife.endorsement.domain.model;

import com.pla.grouplife.sharedresource.model.vo.GLProposerDocument;

import java.util.Set;

/**
 * Created by Samir on 8/27/2015.
 */
public class GLEndorsementApprover {

    private String userName;

    public GLEndorsementApprover(String userName) {
        this.userName = userName;
    }

    public GroupLifeEndorsement updateWithDocuments(GroupLifeEndorsement aggregate, Set<GLProposerDocument> documents) {
        return aggregate.updateWithDocuments(documents);
    }
}
