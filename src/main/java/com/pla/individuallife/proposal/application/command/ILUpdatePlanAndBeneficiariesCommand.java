package com.pla.individuallife.proposal.application.command;

import com.pla.individuallife.proposal.domain.model.Beneficiary;
import com.pla.individuallife.proposal.domain.model.ProposalPlanDetail;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Set;

/**
 * Created by Karunakar on 6/19/2015.
 */
public class ILUpdatePlanAndBeneficiariesCommand {

    private UserDetails userDetails;

    private String proposalId;

    ProposalPlanDetail proposalPlanDetail;

    private Set<Beneficiary> beneficiaries;

}

