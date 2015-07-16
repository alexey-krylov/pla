package com.pla.individuallife.proposal.presentation.dto;

import com.pla.individuallife.proposal.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * Created by Karunakar on 6/30/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ILProposalDto {

    private  String proposalId;

    private String submittedOn;

    private ProposedAssuredDto proposedAssured;

    private ProposerDto proposer;

    private Set<RiderDetail> riders;

    private String proposalNumber;

    private ProposalPlanDetail proposalPlanDetail;

    private List<Beneficiary> beneficiaries;

    private GeneralDetails generalDetails;

    private AdditionalDetails additionaldetails;

    private PremiumPaymentDetails premiumPaymentDetails;

    private BigDecimal totalBeneficiaryShare;

    private List<Question> compulsoryHealthStatement;

    private FamilyPersonalDetail familyPersonalDetail;

    private Set<AgentDetailDto> agentCommissionDetails;

    private String proposalStatus;

    private PremiumDetailDto premiumDetailDto;

}


