package com.pla.grouphealth.quotation.domain.model;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.quotation.domain.event.GHQuotationClosureEvent;
import com.pla.grouphealth.quotation.domain.event.GHQuotationConvertedEvent;
import com.pla.grouphealth.quotation.domain.event.GHQuotationEndSagaEvent;
import com.pla.grouphealth.quotation.domain.event.GHQuotationSharedEvent;
import com.pla.grouphealth.sharedresource.model.vo.*;
import com.pla.sharedkernel.event.GHProposerAddedEvent;
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
import static com.pla.grouphealth.quotation.domain.exception.GHQuotationException.raiseQuotationNotModifiableException;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 4/7/2015.
 */
@Document(collection = "group_health_quotation")
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class GroupHealthQuotation extends AbstractAggregateRoot<QuotationId> implements IQuotation {

    @Id
    @AggregateIdentifier
    private QuotationId quotationId;

    private String quotationCreator;

    private AgentId agentId;

    private GHProposer proposer;

    private Set<GHInsured> insureds;

    private GHQuotationStatus quotationStatus;

    private int versionNumber;

    private String quotationNumber;

    private LocalDate generatedOn;

    private QuotationId parentQuotationId;

    private GHPremiumDetail premiumDetail;

    private LocalDate sharedOn;

    private boolean moratoriumPeriodApplicable;

    private boolean samePlanForAllRelation;

    private boolean samePlanForAllCategory;

    private OpportunityId opportunityId;

    private String schemeName;


    private GroupHealthQuotation(String quotationCreator, String quotationNumber, QuotationId quotationId, AgentId agentId, GHProposer proposer, GHQuotationStatus quotationStatus, int versionNumber) {
        checkArgument(isNotEmpty(quotationCreator));
        checkArgument(isNotEmpty(quotationNumber));
        checkArgument(quotationId != null);
        checkArgument(agentId != null);
        checkArgument(proposer != null);
        checkArgument(GHQuotationStatus.DRAFT.equals(quotationStatus));
        this.quotationCreator = quotationCreator;
        this.quotationId = quotationId;
        this.agentId = agentId;
        this.proposer = proposer;
        this.quotationStatus = quotationStatus;
        this.versionNumber = versionNumber;
        this.quotationNumber = quotationNumber;
    }

    private GroupHealthQuotation(String quotationNumber, String quotationCreator, QuotationId quotationId, int versionNumber, GHQuotationStatus quotationStatus) {
        checkArgument(isNotEmpty(quotationNumber));
        checkArgument(isNotEmpty(quotationCreator));
        checkArgument(quotationId != null);
        checkArgument(GHQuotationStatus.DRAFT.equals(quotationStatus));
        this.quotationNumber = quotationNumber;
        this.quotationCreator = quotationCreator;
        this.quotationId = quotationId;
        this.versionNumber = versionNumber;
        this.quotationStatus = quotationStatus;
    }

    public static GroupHealthQuotation createWithAgentAndProposerDetail(String quotationNumber, String quotationCreator, QuotationId quotationId, AgentId agentId, GHProposer proposer) {
        return new GroupHealthQuotation(quotationCreator, quotationNumber, quotationId, agentId, proposer, GHQuotationStatus.DRAFT, 0);
    }

    public GroupHealthQuotation updateWithPremiumDetail(GHPremiumDetail premiumDetail) {
        checkInvariant();
        this.premiumDetail = premiumDetail;
        return this;
    }

    public GroupHealthQuotation updateWithMoratoriumPeriod(boolean moratoriumPeriodApplicable) {
        checkInvariant();
        this.moratoriumPeriodApplicable = moratoriumPeriodApplicable;
        return this;
    }

    public GroupHealthQuotation updateFlagSamePlanForAllRelation(boolean samePlanForAllRelation) {
        checkInvariant();
        this.samePlanForAllRelation = samePlanForAllRelation;
        return this;
    }

    public GroupHealthQuotation updateFlagSamePlanForAllCategory(boolean samePlanForAllCategory) {
        checkInvariant();
        this.samePlanForAllCategory = samePlanForAllCategory;
        return this;
    }

    public GroupHealthQuotation updateWithAgent(AgentId agentId) {
        checkInvariant();
        this.agentId = agentId;
        return this;
    }

    public GroupHealthQuotation updateWithSchemeName(String schemeName) {
        checkInvariant();
        this.schemeName = schemeName;
        return this;
    }

    public GroupHealthQuotation updateWithProposer(GHProposer proposer) {
        checkInvariant();
        this.proposer = proposer;
        return this;
    }

    public GroupHealthQuotation updateWithInsured(Set<GHInsured> insureds) {
        checkInvariant();
        this.insureds = insureds;
        return this;
    }


    @Override
    public QuotationId getIdentifier() {
        return quotationId;
    }

    @Override
    public void closeQuotation() {
        this.quotationStatus = GHQuotationStatus.CONVERTED;
        registerEvent(new GHQuotationConvertedEvent(quotationId));
    }

    @Override
    public void purgeQuotation() {
        this.quotationStatus = GHQuotationStatus.PURGED;
    }

    @Override
    public void declineQuotation() {
        this.quotationStatus = GHQuotationStatus.DECLINED;
        registerEvent(new GHQuotationClosureEvent(quotationId));
    }

    @Override
    public void generateQuotation(LocalDate generatedOn) {
        this.quotationStatus = GHQuotationStatus.GENERATED;
        this.generatedOn = generatedOn;
        GHProposerContactDetail proposerContactDetail = proposer.getContactDetail();
        if (this.proposer != null && this.proposer.getContactDetail() != null) {
            registerEvent(new GHProposerAddedEvent(proposer.getProposerName(), proposer.getProposerCode(),
                    proposerContactDetail.getAddressLine1(), proposerContactDetail.getAddressLine2(), proposerContactDetail.getPostalCode(),
                    proposerContactDetail.getProvince(), proposerContactDetail.getTown(), proposerContactDetail.getEmailAddress()));
        }
    }

    public void shareQuotation(LocalDate sharedOn) {
        if (GHQuotationStatus.GENERATED.equals(this.getQuotationStatus())) {
            this.sharedOn = sharedOn;
            this.quotationStatus = GHQuotationStatus.SHARED;
            registerEvent(new GHQuotationSharedEvent(quotationId));
        }
    }

    public GroupHealthQuotation updateWithOpportunityId(OpportunityId opportunityId) {
        this.opportunityId = opportunityId;
        return this;
    }

    public void cancelSchedules() {
        registerEvent(new GHQuotationEndSagaEvent(this.getQuotationId()));
    }

    @Override
    public boolean requireVersioning() {
        return (GHQuotationStatus.GENERATED.equals(this.quotationStatus) || GHQuotationStatus.SHARED.equals(this.quotationStatus));
    }

    public GroupHealthQuotation cloneQuotation(String quotationNumber, String quotationCreator, QuotationId quotationId, int versionNumber, QuotationId parentQuotationId) {
        GroupHealthQuotation newQuotation = new GroupHealthQuotation(quotationNumber, quotationCreator, quotationId, versionNumber, GHQuotationStatus.DRAFT);
        newQuotation.parentQuotationId = parentQuotationId;
        newQuotation.proposer = this.proposer;
        newQuotation.agentId = this.agentId;
        newQuotation.insureds = this.insureds;
        newQuotation.premiumDetail = this.premiumDetail;
        newQuotation.samePlanForAllCategory = this.samePlanForAllCategory;
        newQuotation.samePlanForAllRelation = this.samePlanForAllRelation;
        newQuotation.moratoriumPeriodApplicable = this.moratoriumPeriodApplicable;
        newQuotation.schemeName = this.schemeName;
        return newQuotation;
    }

    private void checkInvariant() {
        if (GHQuotationStatus.CONVERTED.equals(this.quotationStatus) || GHQuotationStatus.DECLINED.equals(this.quotationStatus)) {
            raiseQuotationNotModifiableException();
        }
    }

    public BigDecimal getNetAnnualPremiumPaymentAmount(GHPremiumDetail premiumDetail) {
        BigDecimal totalBasicAmount = this.getTotalBasicPremiumForInsured();
        BigDecimal addOnBenefitAmount = premiumDetail.getAddOnBenefit() == null ? BigDecimal.ZERO : totalBasicAmount.multiply((premiumDetail.getAddOnBenefit().divide(new BigDecimal(100))));
        BigDecimal profitAndSolvencyAmount = premiumDetail.getProfitAndSolvency() == null ? BigDecimal.ZERO : totalBasicAmount.multiply((premiumDetail.getProfitAndSolvency().divide(new BigDecimal(100))));
        BigDecimal waiverOfExcessLoading = premiumDetail.getWaiverOfExcessLoading() == null ? BigDecimal.ZERO : totalBasicAmount.multiply((premiumDetail.getWaiverOfExcessLoading().divide(new BigDecimal(100))));
        BigDecimal discountAmount = premiumDetail.getDiscount() == null ? BigDecimal.ZERO : totalBasicAmount.multiply((premiumDetail.getDiscount().divide(new BigDecimal(100))));
        BigDecimal totalInsuredPremiumAmount = totalBasicAmount.add(addOnBenefitAmount.add(profitAndSolvencyAmount).add(waiverOfExcessLoading)).subtract(discountAmount);
        BigDecimal vat = premiumDetail.getVat() == null ? BigDecimal.ZERO : totalInsuredPremiumAmount.multiply((premiumDetail.getVat().divide(new BigDecimal(100))));
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.add(vat);
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.setScale(2, BigDecimal.ROUND_CEILING);
        return totalInsuredPremiumAmount;
    }

    public BigDecimal getNetAnnualPremiumPaymentAmountWithOutDiscountAndVAT(GHPremiumDetail premiumDetail) {
        BigDecimal totalInsuredPremiumAmount = this.getTotalBasicPremiumForInsured();
        BigDecimal addOnBenefitAmount = premiumDetail.getAddOnBenefit() == null ? BigDecimal.ZERO : totalInsuredPremiumAmount.multiply((premiumDetail.getAddOnBenefit().divide(new BigDecimal(100))));
        BigDecimal profitAndSolvencyAmount = premiumDetail.getProfitAndSolvency() == null ? BigDecimal.ZERO : totalInsuredPremiumAmount.multiply((premiumDetail.getProfitAndSolvency().divide(new BigDecimal(100))));
        BigDecimal waiverOfExcessLoading = premiumDetail.getWaiverOfExcessLoading() == null ? BigDecimal.ZERO : totalInsuredPremiumAmount.multiply((premiumDetail.getWaiverOfExcessLoading().divide(new BigDecimal(100))));
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.add((addOnBenefitAmount.add(profitAndSolvencyAmount).add(waiverOfExcessLoading)));
        totalInsuredPremiumAmount = totalInsuredPremiumAmount.setScale(2, BigDecimal.ROUND_CEILING);
        return totalInsuredPremiumAmount;
    }


    public Integer getTotalNoOfLifeCovered() {
        Integer totalNoOfLifeCovered = 0;
        totalNoOfLifeCovered = insureds.stream().mapToInt(new ToIntFunction<GHInsured>() {
            @Override
            public int applyAsInt(GHInsured value) {
                return value.getNoOfAssured()!=null?value.getNoOfAssured():value.getCategory()!=null?1:0;
            }
        }).sum();
        Integer dependentSize = 0;
        for (GHInsured insured : insureds){
            if (isNotEmpty(insured.getInsuredDependents())) {
                dependentSize = insured.getInsuredDependents().parallelStream().mapToInt(new ToIntFunction<GHInsuredDependent>() {
                    @Override
                    public int applyAsInt(GHInsuredDependent value) {
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
            for (GHInsured insured : insureds) {
                GHPlanPremiumDetail insuredPlanPremiumDetail = insured.getPlanPremiumDetail();
                totalSumAssured = totalSumAssured.add(insuredPlanPremiumDetail.getSumAssured());
                if (isNotEmpty(insured.getInsuredDependents())) {
                    for (GHInsuredDependent insuredDependent : insured.getInsuredDependents()) {
                        totalSumAssured = totalSumAssured.add(insuredDependent.getPlanPremiumDetail().getSumAssured());
                    }
                }
            }
        }
        return totalSumAssured;
    }

    public BigDecimal getTotalBasicPremiumForInsured() {
        BigDecimal totalBasicAnnualPremium = BigDecimal.ZERO;
        for (GHInsured insured : this.insureds) {
            totalBasicAnnualPremium = totalBasicAnnualPremium.add(insured.getBasicAnnualPremium());
        }
        return totalBasicAnnualPremium;
    }

    public BigDecimal getTotalSemiAnnualPremiumForInsured() {
        BigDecimal totalBasicAnnualPremium = BigDecimal.ZERO;
        for (GHInsured insured : this.insureds) {
            totalBasicAnnualPremium = totalBasicAnnualPremium.add(insured.getBasicSemiAnnualPremium());
        }
        return totalBasicAnnualPremium;
    }

    public BigDecimal getTotalQuarterlyPremiumForInsured() {
        BigDecimal totalBasicAnnualPremium = BigDecimal.ZERO;
        for (GHInsured insured : this.insureds) {
            totalBasicAnnualPremium = totalBasicAnnualPremium.add(insured.getBasicQuarterlyPremium());
        }
        return totalBasicAnnualPremium;
    }

    public BigDecimal getTotalMonthlyPremiumForInsured() {
        BigDecimal totalBasicAnnualPremium = BigDecimal.ZERO;
        for (GHInsured insured : this.insureds) {
            totalBasicAnnualPremium = totalBasicAnnualPremium.add(insured.getBasicMonthlyPremium());
        }
        return totalBasicAnnualPremium;
    }



    public BigDecimal getTotalPlanPremiumIncludingNonVisibilityCoveragePremium() {
        BigDecimal totalPlanPremium = BigDecimal.ZERO;
        for (GHInsured insured : this.insureds) {
            totalPlanPremium = totalPlanPremium.add(insured.getBasicAnnualPlanPremiumIncludingNonVisibleCoveragePremium());
        }
        return totalPlanPremium;
    }

    public BigDecimal getTotalVisibleCoveragePremium() {
        BigDecimal totalVisibleCoveragePremium = BigDecimal.ZERO;
        for (GHInsured insured : this.insureds) {
            totalVisibleCoveragePremium = totalVisibleCoveragePremium.add(insured.getInsuredBasicAnnualVisibleCoveragePremium());
        }

        return totalVisibleCoveragePremium;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupHealthQuotation that = (GroupHealthQuotation) o;

        if (opportunityId != null ? !opportunityId.equals(that.opportunityId) : that.opportunityId != null)
            return false;
        if (quotationId != null ? !quotationId.equals(that.quotationId) : that.quotationId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return quotationId != null ? quotationId.hashCode() : 0;
    }
}
