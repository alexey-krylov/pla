package com.pla.individuallife.proposal.domain.model;

import com.pla.individuallife.sharedresource.model.vo.ILProposerDocument;
import org.joda.time.DateTime;

import java.util.Set;

/**
 * Created by Admin on 7/30/2015.
 */
public class ILProposalApprover {

    private String userName;

    public ILProposalApprover(String userName) {
        this.userName = userName;
    }

    public ILProposalAggregate submitApproval(ILProposalAggregate aggregate, String comment, ILProposalStatus status, String username) {
        return aggregate.submitApproval(DateTime.now(), comment,status,username);
    }

    public ILProposalAggregate routeToNextLevel(ILProposalAggregate aggregate, String comment, ILProposalStatus status) {
        return aggregate.routeToNextLevel(comment,status);
    }

    public ILProposalAggregate updateWithDocuments(ILProposalAggregate aggregate, Set<ILProposerDocument> documents) {
        return aggregate.updateWithDocuments(documents);
    }
}
