package com.pla.core.presentation.command;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.BenefitId;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.LineOfBusinessId;
import com.pla.sharedkernel.identifier.PlanId;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.joda.time.LocalDate;
import org.nthdimenzion.presentation.LocalJodaDateDeserializer;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author: pradyumna
 * @since 1.0 23/03/2015
 */
@Setter
@Getter
@ToString
public class CreatePlanCommand {

    PlanId planId;
    Detail planDetail;
    AssuredDetail sumAssured;
    TermDetail policyTerm;
    TermDetail premiumTerm;
    PremiumTermType premiumTermType;
    PolicyTermType policyTermType;
    List<PlanCoverageDetail> coverages = new ArrayList<>();
}

@Getter
@Setter
@ToString
class Detail {
    String planName;
    String planCode;
    @JsonDeserialize(using = LocalJodaDateDeserializer.class)
    LocalDate launchDate;
    @JsonDeserialize(using = LocalJodaDateDeserializer.class)
    LocalDate withdrawalDate;
    int freeLookPeriod = 15;
    int minEntryAge;
    int maxEntryAge;
    boolean taxApplicable;
    int surrenderAfter;
    Set<Relationship> applicableRelationships;
    Set<EndorsementType> endorsementTypes;
    LineOfBusinessId lineOfBusinessId;
    PlanType planType;
    ClientType clientType;
}

@Getter
@Setter
class AssuredDetail {
    private List<AssuredValue> sumAssuredValue = new ArrayList<AssuredValue>();
    private CoverageId coverageId;
    private int percentage;
    private BigDecimal maxLimit;
    private BigDecimal minSumInsured;
    private BigDecimal maxSumInsured;
    private int multiplesOf;
    private SumAssuredType sumAssuredType;
    private BigDecimal incomeMultiplier;

    public SortedSet<BigDecimal> getSumAssuredValue() {
        SortedSet<BigDecimal> set = new TreeSet<BigDecimal>();
        for (AssuredValue each : sumAssuredValue) {
            set.add(new BigDecimal(each.getText()));
        }
        return set;
    }
}

@Getter
@Setter
class AssuredValue {
    private String text;
}

@Getter
@Setter
class TermValue {
    private Integer text;
}

@Getter
@Setter
class TermDetail {

    Set<TermValue> validTerms = new HashSet<TermValue>();
    Set<TermValue> maturityAges = new HashSet<TermValue>();
    int maxMaturityAge;
    private Integer groupTerm;

    public SortedSet<Integer> getValidTerms() {
        SortedSet<Integer> set = new TreeSet<Integer>();
        for (TermValue each : validTerms) {
            set.add(each.getText());
        }
        return set;
    }

    public SortedSet<Integer> getMaturityAges() {
        SortedSet<Integer> set = new TreeSet<Integer>();
        for (TermValue each : maturityAges) {
            set.add(each.getText());
        }
        return set;
    }
}

@Getter
@Setter
class PlanCoverageDetail {
    private CoverageId coverageId;
    private CoverageCover coverageCover;
    private CoverageType coverageType;
    private String deductibleType;
    private BigDecimal deductibleAmount;
    private int waitingPeriod;
    private int minAge;
    private int maxAge;
    private Boolean taxApplicable;
    private AssuredDetail coverageSumAssured;
    private TermDetail coverageTerm;
    private CoverageTermType coverageTermType;
    private List<MaturityAmountDetail> maturityAmounts = new ArrayList<>();
    private List<PlanCoverageBenefitDetail> planCoverageBenefits = new ArrayList<>();
}

@Getter
@Setter
class MaturityAmountDetail {
    int maturityYear;
    BigDecimal guaranteedSurvivalBenefitAmount;
}


@Getter
@Setter
class PlanCoverageBenefitDetail {
    private BenefitId benefitId;
    private CoverageBenefitDefinition definedPer;
    private CoverageBenefitType coverageBenefitType;
    private BigDecimal benefitLimit;
    private BigDecimal maxLimit;

}
