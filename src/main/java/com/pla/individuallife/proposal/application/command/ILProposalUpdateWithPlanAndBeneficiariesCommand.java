package com.pla.individuallife.proposal.application.command;

import com.pla.individuallife.sharedresource.model.vo.Beneficiary;
import com.pla.individuallife.sharedresource.model.vo.ProposalPlanDetail;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Set;

/**
 * Created by Karunakar on 6/19/2015.
 */
@Getter
@Setter
@NoArgsConstructor
public class ILProposalUpdateWithPlanAndBeneficiariesCommand {

    private UserDetails userDetails;

    private String proposalId;

    private ProposalPlanDetail proposalPlanDetail;

    private Set<Beneficiary> beneficiaries;

}

