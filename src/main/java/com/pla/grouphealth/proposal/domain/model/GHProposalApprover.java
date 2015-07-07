package com.pla.grouphealth.proposal.domain.model;

import com.pla.grouphealth.sharedresource.model.vo.ProposalStatus;
import org.joda.time.DateTime;

/**
 * Created by Samir on 7/6/2015.
 */
public class GHProposalApprover {

    private String userName;

    public GHProposalApprover(String userName) {
        this.userName = userName;
    }

    public GroupHealthProposal submitApproval(DateTime approvalOn, String approvalComment, GroupHealthProposal groupHealthProposal, ProposalStatus status) {
        groupHealthProposal = groupHealthProposal.markApproverApproval(this.userName, approvalOn, approvalComment, status);
        return groupHealthProposal;
    }

}
