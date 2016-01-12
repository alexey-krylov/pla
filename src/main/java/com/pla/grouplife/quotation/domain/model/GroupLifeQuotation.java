package com.pla.grouplife.quotation.domain.model;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.quotation.domain.event.GLQuotationClosureEvent;
import com.pla.grouplife.quotation.domain.event.GLQuotationConvertedEvent;
import com.pla.grouplife.quotation.domain.event.GLQuotationEndSagaEvent;
import com.pla.grouplife.quotation.domain.event.GLQuotationSharedEvent;
import com.pla.grouplife.sharedresource.model.vo.*;
import com.pla.sharedkernel.event.GLProposerAddedEvent;
import com.pla.sharedkernel.identifier.OpportunityId;
import com.pla.sharedkernel.identifier.QuotationId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.axonframework.domain.AbstractAggregateRoot;
import org.axonframework.eventsourcing.annotation.AggregateIdentifier;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Set;
import java.util.function.ToIntFunction;

import static com.google.common.base.Preconditions.checkArgument;
import static com.pla.grouplife.quotation.domain.exception.QuotationException.raiseQuotationNotModifiableException;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/7/2015.
 */
@Document(collection = "group_life_quotation")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GroupLifeQuotation extends AbstractAggregateRoot<QuotationId> implements IQuotation {

    @Id
    @AggregateIdentifier
    private QuotationId quotationId;

    private String quotationCreator;

    private AgentId agentId;

    private Proposer proposer;

    private Set<Insured> insureds;

    private QuotationStatus quotationStatus;

    private int versionNumber;

    private String quotationNumber;

    private LocalDate generatedOn;

    private LocalDate sharedOn;

    private QuotationId parentQuotationId;

    private PremiumDetail premiumDetail;

    private OpportunityId opportunityId;

    private Industry industry;

    private boolean samePlanForAllRelation;

    private boolean samePlanForAllCategory;

    private String schemeName;


    private GroupLifeQuotation(String quotationCreator, String quotationNumber, QuotationId quotationId, AgentId agentId, Proposer proposer, QuotationStatus quotationStatus, int versionNumber) {
        checkArgument(isNotEmpty(quotationCreator));
        checkArgument(isNotEmpty(quotationNumber));
        checkArgument(quotationId != null);
        checkArgument(agentId != null);
        checkArgument(proposer != null);
        checkArgument(QuotationStatus.DRAFT.equals(quotationStatus));
        this.quotationCreator = quotationCreator;
        this.quotationId = quotationId;
        this.agentId = agentId;
        this.proposer = proposer;
        this.quotationStatus = quotationStatus;
        this.versionNumber = versionNumber;
        this.quotationNumber = quotationNumber;
    }

    private GroupLifeQuotation(String quotationNumber, String quotationCreator, QuotationId quotationId, int versionNumber, QuotationStatus quotationStatus) {
        checkArgument(isNotEmpty(quotationNumber));
        checkArgument(isNotEmpty(quotationCreator));
        checkArgument(quotationId != null);
        checkArgument(QuotationStatus.DRAFT.equals(quotationStatus));
        this.quotationNumber = quotationNumber;
        this.quotationCreator = quotationCreator;
        this.quotationId = quotationId;
        this.versionNumber = versionNumber;
        this.quotationStatus = quotationStatus;
    }

    public static GroupLifeQuotation createWithAgentAndProposerDetail(String quotationNumber, String quotationCreator, QuotationId quotationId, AgentId agentId, Proposer proposer) {
        return new GroupLifeQuotation(quotationCreator, quotationNumber, quotationId, agentId, proposer, QuotationStatus.DRAFT, 0);
    }

    public GroupLifeQuotation updateWithPremiumDetail(PremiumDetail premiumDetail) {
        checkInvariant();
        this.premiumDetail = premiumDetail;
        return this;
    }

    public GroupLifeQuotation updateWithAgent(AgentId agentId) {
        checkInvariant();
        this.agentId = agentId;
        return this;
    }

    public GroupLifeQuotation updateWithIndustry(Industry industry) {
        checkInvariant();
        this.industry = industry;
        return this;
    }

    public GroupLifeQuotation updateWithProposer(Proposer proposer) {
        checkInvariant();
        this.proposer = proposer;
        return this;
    }

    public GroupLifeQuotation updateWithInsured(Set<Insured> insureds) {
        checkInvariant();
        this.insureds = insureds;
        return this;
    }

    public GroupLifeQuotation updateWithOpportunityId(OpportunityId opportunityId) {
        this.opportunityId = opportunityId;
        return this;
    }

    @Override
    public QuotationId getIdentifier() {
        return quotationId;
    }

    @Override
    public void closeQuotation() {
        this.quotationStatus = QuotationStatus.CLOSED;
        registerEvent(new GLQuotationConvertedEvent(quotationId));
    }

    @Override
    public void purgeQuotation() {
        this.quotationStatus = QuotationStatus.PURGED;
    }

    @Override
    public void declineQuotation() {
        this.quotationStatus = QuotationStatus.DECLINED;
        registerEvent(new GLQuotationClosureEvent(quotationId));
    }

    @Override
    public void generateQuotation(LocalDate generatedOn) {
        this.quotationStatus = QuotationStatus.GENERATED;
        this.generatedOn = generatedOn;
        ProposerContactDetail proposerContactDetail = this.proposer.getContactDetail();
        if (this.proposer != null && this.proposer.getContactDetail() != null) {
            registerEvent(new GLProposerAddedEvent(proposer.getProposerName(), proposer.getProposerCode(),
                    proposerContactDetail.getAddressLine1(), proposerContactDetail.getAddressLine2(), proposerContactDetail.getPostalCode(),
                    proposerContactDetail.getProvince(), proposerContactDetail.getTown(), proposerContactDetail.getEmailAddress()));
        }
    }

    public void shareQuotation(LocalDate sharedOn) {
        if (QuotationStatus.GENERATED.equals(this.quotationStatus)) {
            this.quotationStatus = QuotationStatus.SHARED;
            this.sharedOn = sharedOn;
            registerEvent(new GLQuotationSharedEvent(quotationId));
        }
    }

    public void cancelSchedules() {
        registerEvent(new GLQuotationEndSagaEvent(this.getQuotationId()));
    }

    @Override
    public boolean requireVersioning() {
        return (QuotationStatus.GENERATED.equals(this.quotationStatus) || QuotationStatus.SHARED.equals(this.quotationStatus));
    }

    public GroupLifeQuotation cloneQuotation(String quotationNumber, String quotationCreator, QuotationId quotationId, int versionNumber, QuotationId parentQuotationId) {
        GroupLifeQuotation newQuotation = new GroupLifeQuotation(quotationNumber, quotationCreator, quotationId, versionNumber, QuotationStatus.DRAFT);
        newQuotation.parentQuotationId = parentQuotationId;
        newQuotation.proposer = this.proposer;
        newQuotation.agentId = this.agentId;
        newQuotation.insureds = this.insureds;
        newQuotation.premiumDetail = this.premiumDetail;
        newQuotation.industry = this.industry;
        newQuotation.samePlanForAllCategory = this.samePlanForAllCategory;
        newQuotation.samePlanForAllRelation = this.samePlanForAllRelation;
        newQuotation.schemeName = this.schemeName;
        newQuotation.opportunityId = opportunityId;
        return newQuotation;
    }

    private void checkInvariant() {
        if (QuotationStatus.CLOSED.equals(this.quotationStatus) || QuotationStatus.DECLINED.equals(this.quotationStatus)) {
            raiseQuotationNotModifiableException();
        }
    }

    public BigDecimal getNetAnnualPremiumPaymentAmount(PremiumDetail premiumDetail, BigDecimal totalBasicPremium) {
        BigDecimal hivDiscountAmount = premiumDetail.getHivDiscount() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getHivDiscount().divide(new BigDecimal(100))));
        BigDecimal valuedClientDiscountAmount = premiumDetail.getValuedClientDiscount() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getValuedClientDiscount().divide(new BigDecimal(100))));
        BigDecimal longTermDiscountAmount = premiumDetail.getLongTermDiscount() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getLongTermDiscount().divide(new BigDecimal(100))));

        BigDecimal addOnBenefitAmount = premiumDetail.getAddOnBenefit() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getAddOnBenefit().divide(new BigDecimal(100))));
        BigDecimal profitAndSolvencyAmount = premiumDetail.getProfitAndSolvency() == null ? BigDecimal.ZERO : totalBasicPremium.multiply((premiumDetail.getProfitAndSolvency().divide(new BigDecimal(100))));
        BigDecimal industryLoadingFactor = BigDecimal.ZERO;
        if (this.industry != null) {
            industryLoadingFactor = this.industry.getLoadingFactor();
        }
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



    public Integer getTotalNoOfLifeCovered() {
        Integer totalNoOfLifeCovered = 0;
        totalNoOfLifeCovered = insureds.stream().mapToInt(new ToIntFunction<Insured>() {
            @Override
            public int applyAsInt(Insured value) {
                return value.getNoOfAssured()!=null?value.getNoOfAssured():value.getCategory()!=null?1:0;
            }
        }).sum();
        Integer dependentSize = 0;
        for (Insured insured : insureds){
            if (isNotEmpty(insured.getInsuredDependents())) {
                dependentSize = insured.getInsuredDependents().parallelStream().mapToInt(new ToIntFunction<InsuredDependent>() {
                    @Override
                    public int applyAsInt(InsuredDependent value) {
                        return value.getNoOfAssured()!=null?value.getNoOfAssured():value.getCategory()!=null?1:0;
                    }
                }).sum();
            }
            dependentSize = +dependentSize;
        }
        return totalNoOfLifeCovered + dependentSize;
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

    public BigDecimal getTotalBasicPremiumForInsured() {
        BigDecimal totalBasicAnnualPremium = BigDecimal.ZERO;
        for (Insured insured : this.insureds) {
            totalBasicAnnualPremium = totalBasicAnnualPremium.add(insured.getBasicAnnualPremium());
        }
        return totalBasicAnnualPremium;
    }

    public BigDecimal getTotalSemiAnnualPremiumForInsured() {
        BigDecimal totalBasicAnnualPremium = BigDecimal.ZERO;
        for (Insured insured : this.insureds) {
            totalBasicAnnualPremium = totalBasicAnnualPremium.add(insured.getBasicSemiAnnualPremium());
        }
        return totalBasicAnnualPremium;
    }

    public BigDecimal getTotalQuarterlyPremiumForInsured() {
        BigDecimal totalBasicAnnualPremium = BigDecimal.ZERO;
        for (Insured insured : this.insureds) {
            totalBasicAnnualPremium = totalBasicAnnualPremium.add(insured.getBasicQuarterlyPremium());
        }
        return totalBasicAnnualPremium;
    }

    public BigDecimal getTotalMonthlyPremiumForInsured() {
        BigDecimal totalBasicAnnualPremium = BigDecimal.ZERO;
        for (Insured insured : this.insureds) {
            totalBasicAnnualPremium = totalBasicAnnualPremium.add(insured.getBasicMonthlyPremium());
        }
        return totalBasicAnnualPremium;
    }


    public GroupLifeQuotation updateFlagSamePlanForAllRelation(boolean samePlanForAllRelation) {
        checkInvariant();
        this.samePlanForAllRelation = samePlanForAllRelation;
        return this;
    }

    public GroupLifeQuotation updateFlagSamePlanForAllCategory(boolean samePlanForAllCategory) {
        checkInvariant();
        this.samePlanForAllCategory = samePlanForAllCategory;
        return this;
    }

    public GroupLifeQuotation updateWithSchemeName(String schemeName) {
        checkInvariant();
        this.schemeName = schemeName;
        return this;
    }

}
