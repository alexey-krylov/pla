package com.pla.core.domain.model.generalinformation;

import com.google.common.collect.Sets;
import com.pla.core.domain.exception.GeneralInformationException;
import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


/**
 * Created by Admin on 4/1/2015.
 */
@Document(collection = "product_line_information")
@Getter
@Setter(value = AccessLevel.PACKAGE)
@ToString(exclude = {"logger"})
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class ProductLineGeneralInformation {

    @Id
    private String productLineInformationId;

    private LineOfBusinessEnum productLine;

    private QuotationProcessInformation quotationProcessInformation;

    private EnrollmentProcessInformation enrollmentProcessInformation;

    private ReinstatementProcessInformation reinstatementProcessInformation;

    private EndorsementProcessInformation endorsementProcessInformation;

    private ClaimProcessInformation claimProcessInformation;

    private PolicyFeeProcessInformation policyFeeProcessInformation;

    private PolicyProcessMinimumLimit policyProcessMinimumLimit;

    private SurrenderProcessInformation surrenderProcessInformation;

    private MaturityProcessInformation maturityProcessInformation;

    private Set<PremiumFollowUpFrequency> premiumFollowUpFrequency;

    private ModalFactorProcessInformation  modalFactorProcessInformation;

    private DiscountFactorProcessInformation discountFactorProcessInformation;


    private ProductLineGeneralInformation(String productLineInformationId, LineOfBusinessEnum productLineId) {
        this.productLineInformationId = productLineInformationId;
        this.productLine = productLineId;
    }

    public static ProductLineGeneralInformation createProductLineGeneralInformation(LineOfBusinessEnum productLineId) {
        return new ProductLineGeneralInformation(new ObjectId().toString(), productLineId);
    }

    public static Set<ProductLineProcessItem> create(List<Map<ProductLineProcessType, Integer>> quotationProcessItems) {
        Set<ProductLineProcessItem> productLineProcessItems = quotationProcessItems.stream().map(new QuotationProcessInformationTransformer()).collect(Collectors.toSet());
        return productLineProcessItems;
    }

    public ProductLineGeneralInformation withQuotationProcessInformation(List<Map<ProductLineProcessType, Integer>> quotationProcessInformation) {
        this.quotationProcessInformation =  QuotationProcessInformation.create(quotationProcessInformation);
        return this;
    }

    public ProductLineGeneralInformation withEnrollmentProcessGeneralInformation(List<Map<ProductLineProcessType,Integer>> enrollmentProcessGeneralInformation){
        this.enrollmentProcessInformation = EnrollmentProcessInformation.create(enrollmentProcessGeneralInformation);
        return this;
    }

    public ProductLineGeneralInformation withReinstatementProcessInformation(List<Map<ProductLineProcessType,Integer>> reinstatementProcessInformation){
        this.reinstatementProcessInformation = ReinstatementProcessInformation.create(reinstatementProcessInformation);
        return this;
    }

    public ProductLineGeneralInformation withEndorsementProcessInformation(List<Map<ProductLineProcessType,Integer>> endorsementProcessInformation){
        this.endorsementProcessInformation = EndorsementProcessInformation.create(endorsementProcessInformation);
        return this;
    }

    public ProductLineGeneralInformation withClaimProcessInformation(List<Map<ProductLineProcessType,Integer>> claimProcessInformation){
        this.claimProcessInformation = ClaimProcessInformation.create(claimProcessInformation);
        return this;
    }

    public ProductLineGeneralInformation withPolicyProcessMinimumLimit(List<Map<PolicyProcessMinimumLimitType,Integer>> policyProcessMinimumLimit) {
        this.policyProcessMinimumLimit = PolicyProcessMinimumLimit.create(policyProcessMinimumLimit);
        return this;
    }

    public ProductLineGeneralInformation withPolicyFeeProcessInformation(List<Map<PolicyFeeProcessType,Integer>> policyFeeProcessInformation){
        this.policyFeeProcessInformation = PolicyFeeProcessInformation.create(policyFeeProcessInformation);
        return this;
    }

    public ProductLineGeneralInformation withSurrenderProcessInformation(List<Map<ProductLineProcessType,Integer>> surrenderProcessInformation){
        this.surrenderProcessInformation  = SurrenderProcessInformation.create(surrenderProcessInformation);
        return this;
    }

    public ProductLineGeneralInformation withMaturityProcessInformation(List<Map<ProductLineProcessType,Integer>> maturityProcessInformation){
        this.maturityProcessInformation = MaturityProcessInformation.create(maturityProcessInformation);
        return this;
    }

    public ProductLineGeneralInformation withModalFactorProcessInformation(List<Map<ModalFactorItem, BigDecimal>> listOfModalFactorItem) {
        this.modalFactorProcessInformation = ModalFactorProcessInformation.create(listOfModalFactorItem);
        return this;
    }

    public ProductLineGeneralInformation withDiscountFactorProcessInformation(List<Map<DiscountFactorItem, BigDecimal>> listOfDiscountFactorItem) {
        this.discountFactorProcessInformation = DiscountFactorProcessInformation.create(listOfDiscountFactorItem);
        return this;
    }

    public ProductLineGeneralInformation withPremiumFollowUpMonthly(Map<PremiumFrequency, List<Map<ProductLineProcessType,Integer>>> premiumFollowUpFrequencyItems) {
        premiumFollowUpFrequency = Sets.newLinkedHashSet();
        for (Map.Entry<PremiumFrequency, List<Map<ProductLineProcessType, Integer>>> premiumFrequencyFollowUpMap : premiumFollowUpFrequencyItems.entrySet()) {
            premiumFollowUpFrequency.add(new PremiumFollowUpFrequency(premiumFrequencyFollowUpMap.getKey(), create(premiumFrequencyFollowUpMap.getValue())));
        }
        return this;
    }

    public Set<ProductLineProcessItem> getPremiumFollowUpFrequencyFor(PremiumFrequency premiumFrequency) {
        PremiumFollowUpFrequency premiumFollowUpFrequencyItems = premiumFollowUpFrequency.stream().filter(new Predicate<PremiumFollowUpFrequency>() {
            @Override
            public boolean test(PremiumFollowUpFrequency premiumFollowUpFrequency) {
                return premiumFollowUpFrequency.getPremiumFrequency().equals(premiumFrequency);
            }
        }).findAny().get();
        return premiumFollowUpFrequencyItems.getPremiumFollowUpFrequencyItems();
    }

    private static class QuotationProcessInformationTransformer implements Function<Map<ProductLineProcessType,Integer>,ProductLineProcessItem> {
        @Override
        public ProductLineProcessItem apply(Map<ProductLineProcessType,Integer> productLineProcessItemMap) {
            Map.Entry<ProductLineProcessType,Integer> entry = productLineProcessItemMap.entrySet().iterator().next();
            if (!Arrays.asList(ProductLineProcessType.FIRST_REMAINDER, ProductLineProcessType.SECOND_REMAINDER, ProductLineProcessType.LAPSE).contains(entry.getKey())){
                throw new GeneralInformationException("Premium Follow up frequency should not include "+entry.getKey());
            }
            ProductLineProcessItem productLineProcessItem = new ProductLineProcessItem(entry.getKey(), entry.getValue());
            return productLineProcessItem;
        }
    }

}
