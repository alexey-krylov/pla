package com.pla.individuallife.policy.presentation.dto;

import com.pla.individuallife.proposal.presentation.dto.PremiumDetailDto;
import com.pla.individuallife.proposal.presentation.dto.RiderDetailDto;
import com.pla.individuallife.sharedresource.dto.AgentDetailDto;
import com.pla.individuallife.sharedresource.dto.ProposedAssuredDto;
import com.pla.individuallife.sharedresource.dto.ProposerDto;
import com.pla.individuallife.sharedresource.model.ILEndorsementType;
import com.pla.individuallife.sharedresource.model.vo.*;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.domain.model.Proposal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Raghu Bandi on 8/6/2015.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ILPolicyDto {

    private String policyId;

    private Proposal proposal;

    private PolicyNumber policyNumber;

    private DateTime inceptionOn;

    private DateTime expiryDate;

    private ProposedAssuredDto proposedAssured;

    private ProposedAssuredDto proposedAssuredNew;

    private ProposerDto proposer;

    private ProposerDto proposerNew;

    private Proposer policyHolder;
    private Proposer policyHolderNew;

    private ProposedAssured lifeAssured;
    private ProposedAssured lifeAssuredNew;


    private Set<RiderDetailDto> riders;

    private ProposalPlanDetail proposalPlanDetail;

    private ProposalPlanDetail proposalPlanDetailNew;

    private List<Beneficiary> beneficiaries;

    private List<Beneficiary> beneficiariesNew;

    private GeneralDetails generalDetails;

    private AdditionalDetails additionaldetails;

    private PremiumPaymentDetails premiumPaymentDetails;

    private PremiumPaymentDetails premiumPaymentDetailsNew;

    private BigDecimal totalBeneficiaryShare;

    private List<Question> compulsoryHealthStatement;

    private FamilyPersonalDetail familyPersonalDetail;

    private Set<AgentDetailDto> agentCommissionDetails;

    private Set<AgentDetailDto> agentCommissionDetailsNew;

    private String policyStatus;

    private PremiumDetailDto premiumDetailDto;

    private PremiumDetailDto premiumDetailDtoNew;

    private List<ILProposerDocument> proposerDocuments;

    private String opportunityId;

    private ILEndorsementType ilEndorsementType;
    private List<Map<String, String>> endorsementTypes;

    private DateTime effectiveDate;

    public static ILPolicyDto createEmptyDetail(){
        return new ILPolicyDto();
    }


}
