package com.pla.individuallife.proposal.presentation.dto;

import com.pla.sharedkernel.domain.model.RoutingLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Created by Prasant on 17-Jun-15.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ILSearchProposalForApprovalDto extends ILSearchProposalDto{

    private RoutingLevel routingLevel;

}
