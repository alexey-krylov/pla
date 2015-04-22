package com.pla.core.domain.model.generalinformation;

import com.pla.sharedkernel.domain.model.PolicyFeeProcessType;
import com.pla.sharedkernel.domain.model.PolicyProcessMinimumLimitType;
import com.pla.sharedkernel.domain.model.ProductLineProcessType;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.List;
import java.util.Map;


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

    private LineOfBusinessId productLine;

    private QuotationProcessInformation quotationProcessInformation;

    private EnrollmentProcessInformation enrollmentProcessInformation;

    private ReinstatementProcessInformation reinstatementProcessInformation;

    private EndorsementProcessInformation endorsementProcessInformation;

    private ClaimProcessInformation claimProcessInformation;

    private PolicyFeeProcessInformation policyFeeProcessInformation;

    private PolicyProcessMinimumLimit policyProcessMinimumLimit;

    private SurrenderProcessInformation surrenderProcessInformation;

    private MaturityProcessInformation maturityProcessInformation;


    private ProductLineGeneralInformation(String productLineInformationId, LineOfBusinessId productLineId) {
        this.productLineInformationId = productLineInformationId;
        this.productLine = productLineId;
    }

    public static ProductLineGeneralInformation createProductLineGeneralInformation(LineOfBusinessId productLineId) {
        return new ProductLineGeneralInformation(new ObjectId().toString(), productLineId);
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
}
