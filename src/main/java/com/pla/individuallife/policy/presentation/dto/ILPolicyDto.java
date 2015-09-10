package com.pla.individuallife.policy.presentation.dto;

import com.pla.individuallife.proposal.presentation.dto.PremiumDetailDto;
import com.pla.individuallife.proposal.presentation.dto.RiderDetailDto;
import com.pla.individuallife.sharedresource.dto.AgentDetailDto;
import com.pla.individuallife.sharedresource.dto.ProposedAssuredDto;
import com.pla.individuallife.sharedresource.dto.ProposerDto;
import com.pla.individuallife.sharedresource.model.vo.*;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.domain.model.Proposal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Created by Admin on 8/6/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ILPolicyDto {

    private  String policyId;

    private Proposal proposal;

    private PolicyNumber policyNumber;

    private String inceptionOn;

    private String expiryDate;

    private ProposedAssuredDto proposedAssured;

    private ProposerDto proposer;

    private Set<RiderDetailDto> riders;

    private ProposalPlanDetail proposalPlanDetail;

    private List<Beneficiary> beneficiaries;

    private GeneralDetails generalDetails;

    private AdditionalDetails additionaldetails;

    private PremiumPaymentDetails premiumPaymentDetails;

    private BigDecimal totalBeneficiaryShare;

    private List<Question> compulsoryHealthStatement;

    private FamilyPersonalDetail familyPersonalDetail;

    private Set<AgentDetailDto> agentCommissionDetails;

    private String policyStatus;

    private PremiumDetailDto premiumDetailDto;

    private List<ILProposerDocument> proposerDocuments;

}
