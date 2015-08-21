package com.pla.grouplife.proposal.domain.model;

import com.pla.grouplife.sharedresource.model.vo.GLProposalStatus;
import org.joda.time.DateTime;

/**
 * Created by Samir on 7/6/2015.
 */
public class GLProposalApprover {

    private String userName;

    public GLProposalApprover(String userName) {
        this.userName = userName;
    }

    public GroupLifeProposal submitApproval(DateTime approvalOn, String approvalComment, GroupLifeProposal groupLifeProposal, GLProposalStatus status) {
        groupLifeProposal = groupLifeProposal.markApproverApproval(this.userName, approvalOn, approvalComment, status);
        return groupLifeProposal;
    }

}
