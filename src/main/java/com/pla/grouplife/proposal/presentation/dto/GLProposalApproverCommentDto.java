package com.pla.grouplife.proposal.presentation.dto;

import com.pla.grouplife.sharedresource.model.vo.GLProposalStatus;
import com.pla.sharedkernel.identifier.ProposalId;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

/**
 * Created by Samir on 7/14/2015.
 */
@Getter
@Setter
public class GLProposalApproverCommentDto {

    private ObjectId id;

    private ProposalId proposalId;

    private GLProposalStatus proposalStatus;

    private DateTime modifiedOn;

    private String modifiedBy;

    private String comment;

}
