package com.pla.individuallife.endorsement.domain.model;

import com.pla.individuallife.sharedresource.model.vo.ILProposerDocument;

import java.util.Set;

/**
 * Created by Samir on 8/27/2015.
 */
public class ILEndorsementApprover {

    private String userName;

    public ILEndorsementApprover(String userName) {
        this.userName = userName;
    }

    public IndividualLifeEndorsement updateWithDocuments(IndividualLifeEndorsement aggregate, Set<ILProposerDocument> documents) {
        return aggregate.updateWithDocuments(documents);
    }
}
