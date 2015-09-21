package com.pla.grouplife.policy.domain.model;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.sharedresource.model.vo.*;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.domain.model.Proposal;
import com.pla.sharedkernel.identifier.PolicyId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.domain.AbstractAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Set;
import java.util.function.ToIntFunction;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 7/7/2015.
 */
@Document(collection = "group_life_policy")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GroupLifePolicy extends AbstractAggregateRoot<PolicyId> {

    @Id
    @AggregateIdentifier
    private PolicyId policyId;

    private Proposal proposal;

    private PolicyNumber policyNumber;

    private DateTime inceptionOn;

    private DateTime expiredOn;

    private AgentId agentId;

    private Proposer proposer;

    private Set<Insured> insureds;

    private PremiumDetail premiumDetail;

    private PolicyStatus status;

    public GroupLifePolicy(PolicyId policyId, PolicyNumber policyNumber, Proposal proposal, DateTime inceptionOn, DateTime expiredOn) {
        checkArgument(policyId != null, "Policy ID cannot be empty");
        checkArgument(policyNumber != null, "Policy number cannot be empty");
        checkArgument(proposal != null, "Proposal cannot be empty");
        checkArgument(inceptionOn != null, "Policy inception date cannot be empty");
        checkArgument(expiredOn != null, "Policy expired date cannot be empty");
        this.policyId = policyId;
        this.policyNumber = policyNumber;
        this.inceptionOn = inceptionOn;
        this.expiredOn = expiredOn;
        this.proposal = proposal;
        this.status = PolicyStatus.IN_FORCE;
    }

    public GroupLifePolicy addAgentId(AgentId agentId) {
        this.agentId = agentId;
        return this;
    }

    public GroupLifePolicy addProposer(Proposer ghProposer) {
        this.proposer = ghProposer;
        return this;
    }

    public GroupLifePolicy addInsured(Set<Insured> insureds) {
        this.insureds = insureds;
        return this;
    }

    public GroupLifePolicy addPremium(PremiumDetail premiumDetail) {
        this.premiumDetail = premiumDetail;
        return this;
    }

    public BigDecimal getTotalBasicPremiumForInsured() {
        BigDecimal totalBasicAnnualPremium = BigDecimal.ZERO;
        for (Insured insured : this.insureds) {
            totalBasicAnnualPremium = totalBasicAnnualPremium.add(insured.getBasicAnnualPremium());
        }
        return totalBasicAnnualPremium;
    }

    public Integer getTotalNoOfLifeCovered() {
        Integer totalNoOfLifeCovered = insureds.size();
        Integer dependentSize = insureds.stream().mapToInt(new ToIntFunction<Insured>() {
            @Override
            public int applyAsInt(Insured value) {
                return isNotEmpty(value.getInsuredDependents()) ? value.getInsuredDependents().size() : 0;
            }
        }).sum();
        totalNoOfLifeCovered = totalNoOfLifeCovered + dependentSize;
        return totalNoOfLifeCovered;
    }

    public BigDecimal getTotalSumAssured() {
        BigDecimal totalSumAssured = BigDecimal.ZERO;
        if (isNotEmpty(insureds)) {
            for (Insured insured : insureds) {
                PlanPremiumDetail insuredPlanPremiumDetail = insured.getPlanPremiumDetail();
                totalSumAssured = totalSumAssured.add(insuredPlanPremiumDetail.getSumAssured());
                if (isNotEmpty(insured.getInsuredDependents())) {
                    for (InsuredDependent insuredDependent : insured.getInsuredDependents()) {
                        totalSumAssured = totalSumAssured.add(insuredDependent.getPlanPremiumDetail().getSumAssured());
                    }
                }
            }
        }
        return totalSumAssured;
    }


    public BigDecimal getNetAnnualPremiumPaymentAmount(PremiumDetail premiumDetail) {
        BigDecimal totalBasicPremium = this.getTotalBasicPremiumForInsured();
        BigDecimal hivDiscountAmount = premiumDetail.getHivDiscount() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getHivDiscount().divide(new BigDecimal(100))));
        BigDecimal valuedClientDiscountAmount = premiumDetail.getValuedClientDiscount() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getValuedClientDiscount().divide(new BigDecimal(100))));
        BigDecimal longTermDiscountAmount = premiumDetail.getLongTermDiscount() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getLongTermDiscount().divide(new BigDecimal(100))));

        BigDecimal addOnBenefitAmount = premiumDetail.getAddOnBenefit() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getAddOnBenefit().divide(new BigDecimal(100))));
        BigDecimal profitAndSolvencyAmount = premiumDetail.getProfitAndSolvency() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getProfitAndSolvency().divide(new BigDecimal(100))));
        BigDecimal industryLoadingFactor = BigDecimal.ZERO;
      /*  if (this.industry != null) {
            industryLoadingFactor = this.industry.getLoadingFactor();
        }*/
        BigDecimal totalLoadingAmount = (addOnBenefitAmount.add(profitAndSolvencyAmount)).subtract((hivDiscountAmount.add(valuedClientDiscountAmount).add(longTermDiscountAmount)));
        BigDecimal totalInsuredPremiumAmount = totalBasicPremium.add(totalLoadingAmount);
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.multiply(industryLoadingFactor);
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.setScale(2, BigDecimal.ROUND_CEILING);
        return totalInsuredPremiumAmount;
    }

    public BigDecimal getNetAnnualPremiumPaymentAmountWithoutDiscount(PremiumDetail premiumDetail) {
        BigDecimal totalInsuredPremiumAmount = this.getTotalBasicPremiumForInsured();
        BigDecimal addOnBenefitAmount = premiumDetail.getAddOnBenefit() == null ? BigDecimal.ZERO : totalInsuredPremiumAmount.multiply((premiumDetail.getAddOnBenefit().divide(new BigDecimal(100))));
        BigDecimal profitAndSolvencyAmount = premiumDetail.getProfitAndSolvency() == null ? BigDecimal.ZERO : totalInsuredPremiumAmount.multiply((premiumDetail.getProfitAndSolvency().divide(new BigDecimal(100))));
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.add((addOnBenefitAmount.add(profitAndSolvencyAmount)));
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.setScale(2, BigDecimal.ROUND_CEILING);
        return totalInsuredPremiumAmount;
    }


    @Override
    public PolicyId getIdentifier() {
        return policyId;
    }
}
