package com.pla.individuallife.proposal.presentation.dto;

import com.pla.grouphealth.sharedresource.model.vo.ProposalStatus;
import com.pla.sharedkernel.identifier.ProposalId;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.joda.time.DateTime;

/**
 * Created by Admin on 8/3/2015.
 */

@Getter
@Setter
public class ProposalApproverCommentsDto {

    private ObjectId id;

    private ProposalId proposalId;

    private ProposalStatus proposalStatus;

    private DateTime modifiedOn;

    private String modifiedBy;

    private String comment;

    private String status;
}
