package com.pla.grouphealth.claim.cashless.presentation.dto;

import com.google.common.collect.Sets;
import com.pla.core.domain.model.plan.PlanDetail;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationRequestAssuredDetail;
import com.pla.grouphealth.sharedresource.model.vo.*;
import com.pla.sharedkernel.domain.model.Relationship;
import lombok.*;
import org.apache.commons.lang.StringUtils;
import org.nthdimenzion.presentation.AppUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Author - Mohan Sharma Created on 1/6/2016.
 */
@Getter
@Setter
@NoArgsConstructor
public class ClaimantPolicyDetailDto {
    private String policyNumber;
    private String policyName;
    private String planCode;
    private String planName;
    private String category;
    private String relationship;
    private BigDecimal sumAssured;
    private PreAuthorizationClaimantProposerDetail preAuthorizationClaimantProposerDetail;
    private AssuredDetail assuredDetail;
    private DependentAssuredDetail dependentAssuredDetail;
    private Set<CoverageDetailDto> coverageDetailDtoList;
    private Set<CoverageBenefitDetailDto> coverageBenefitDetails;

    public static ClaimantPolicyDetailDto getInstance() {
        return new ClaimantPolicyDetailDto();
    }

    public ClaimantPolicyDetailDto updateWithPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
        return this;
    }

    public ClaimantPolicyDetailDto updateWithPolicyName(String policyName) {
        this.policyName = policyName;
        return this;
    }

    public ClaimantPolicyDetailDto updateWithPreAuthorizationClaimantProposerDetail(GHProposerContactDetail ghProposerContactDetail, String proposerName, String proposerCode) {
        PreAuthorizationClaimantProposerDetail preAuthorizationClaimantProposerDetail = new PreAuthorizationClaimantProposerDetail();
        if(isNotEmpty(ghProposerContactDetail)) {
            preAuthorizationClaimantProposerDetail.proposerName = proposerName;
            preAuthorizationClaimantProposerDetail.proposerCode = proposerCode;
            preAuthorizationClaimantProposerDetail.address1 = ghProposerContactDetail.getAddressLine1();
            preAuthorizationClaimantProposerDetail.address2 = ghProposerContactDetail.getAddressLine2();
            preAuthorizationClaimantProposerDetail.postalCode = ghProposerContactDetail.getPostalCode();
            preAuthorizationClaimantProposerDetail.province = ghProposerContactDetail.getProvince();
            preAuthorizationClaimantProposerDetail.town = ghProposerContactDetail.getTown();
            preAuthorizationClaimantProposerDetail.emailId = ghProposerContactDetail.getEmailAddress();
            if(isNotEmpty(ghProposerContactDetail.getContactPersonDetail())) {
                GHProposerContactDetail.ContactPersonDetail contactPersonDetail = ghProposerContactDetail.getContactPersonDetail().get(0);
                preAuthorizationClaimantProposerDetail.contactPersonName = contactPersonDetail.getContactPersonName();
                preAuthorizationClaimantProposerDetail.contactPersonMobileNumber = contactPersonDetail.getMobileNumber();
                preAuthorizationClaimantProposerDetail.contactPersonWorkPhone = contactPersonDetail.getWorkPhoneNumber();
                preAuthorizationClaimantProposerDetail.contactPersonEmailId = contactPersonDetail.getContactPersonEmail();
            }
            this.preAuthorizationClaimantProposerDetail = preAuthorizationClaimantProposerDetail;
        }
        return this;
    }

    public ClaimantPolicyDetailDto updateWithPlanName(PlanDetail planDetail) {
        if(isNotEmpty(planDetail)) {
            this.planName = planDetail.getPlanName();
        }
        return this;
    }

    public ClaimantPolicyDetailDto updateWithPlanName(String planName) {
        this.planName = planName;
        return this;
    }

    public ClaimantPolicyDetailDto updateWithPlanCode(PlanDetail planDetail) {
        if(isNotEmpty(planDetail))
            this.planCode = planDetail.getPlanCode();
        return this;
    }

    public ClaimantPolicyDetailDto updateWithCategory(String category) {
        this.category = category;
        return this;
    }

    public ClaimantPolicyDetailDto updateWithRelationship(Relationship relationship) {
        if(isNotEmpty(relationship))
            this.relationship = relationship.name();
        return this;
    }

    public ClaimantPolicyDetailDto updateWithCoverages(Set<CoverageBenefitDetailDto> coverageBenefitDetails) {
        this.coverageBenefitDetails = coverageBenefitDetails;
        return this;
    }

    public ClaimantPolicyDetailDto updateWithAssuredDetails(GHInsured groupHealthInsured) {
        if(isNotEmpty(groupHealthInsured)){
            Integer age = isNotEmpty(groupHealthInsured.getDateOfBirth()) ? AppUtils.getAgeOnNextBirthDate(groupHealthInsured.getDateOfBirth()) : null;
            AssuredDetail assuredDetail = new AssuredDetail();
            assuredDetail.setSalutation(groupHealthInsured.getSalutation());
            assuredDetail.setFirstName(groupHealthInsured.getFirstName());
            assuredDetail.setSurname(groupHealthInsured.getLastName());
            assuredDetail.setDateOfBirth(groupHealthInsured.getDateOfBirth());
            if(age != null) assuredDetail.setAgeNextBirthday(age);
            assuredDetail.setNrcNumber(groupHealthInsured.getNrcNumber());
            assuredDetail.setGender(isNotEmpty(groupHealthInsured.getGender()) ? groupHealthInsured.getGender().name() : StringUtils.EMPTY);
            assuredDetail.setSumAssured(isNotEmpty(groupHealthInsured.getPlanPremiumDetail()) ? groupHealthInsured.getPlanPremiumDetail().getSumAssured() :BigDecimal.ZERO);
            // assuredDetail.setReserveAmount(groupHealthInsured);
            assuredDetail.setCategory(groupHealthInsured.getCategory());
            assuredDetail.setManNumber(groupHealthInsured.getManNumber());
            assuredDetail.setClientId(groupHealthInsured.getFamilyId().getFamilyId());
            this.assuredDetail = assuredDetail;
        }
        return this;
    }

    public ClaimantPolicyDetailDto updateWithDependentAssuredDetail(GHInsuredDependent ghInsuredDependent, GHInsured groupHealthInsured) {
        if(isNotEmpty(ghInsuredDependent)){
            Integer age = isNotEmpty(ghInsuredDependent.getDateOfBirth()) ? AppUtils.getAgeOnNextBirthDate(ghInsuredDependent.getDateOfBirth()) : null;
            DependentAssuredDetail dependentAssuredDetail = new DependentAssuredDetail();
            dependentAssuredDetail.setSalutation(ghInsuredDependent.getSalutation());
            dependentAssuredDetail.setFirstName(ghInsuredDependent.getFirstName());
            dependentAssuredDetail.setSurname(ghInsuredDependent.getLastName());
            dependentAssuredDetail.setDateOfBirth(ghInsuredDependent.getDateOfBirth());
            if(age != null) dependentAssuredDetail.setAgeNextBirthday(age);
            dependentAssuredDetail.setNrcNumber(ghInsuredDependent.getNrcNumber());
            dependentAssuredDetail.setGender(isNotEmpty(ghInsuredDependent.getGender()) ? ghInsuredDependent.getGender().name() : StringUtils.EMPTY);
            dependentAssuredDetail.setSumAssured(isNotEmpty(ghInsuredDependent.getPlanPremiumDetail()) ? ghInsuredDependent.getPlanPremiumDetail().getSumAssured() :BigDecimal.ZERO);
            // dependentAssuredDetail.setReserveAmount(ghInsuredDependent);
            dependentAssuredDetail.setCategory(ghInsuredDependent.getCategory());
            dependentAssuredDetail.setManNumber(ghInsuredDependent.getManNumber());
            dependentAssuredDetail.setClientId(ghInsuredDependent.getFamilyId().getFamilyId());
            if(isNotEmpty(groupHealthInsured)){
                dependentAssuredDetail.setMainAssuredFullName(groupHealthInsured.getFirstName() + " " + groupHealthInsured.getLastName());
                //dependentAssuredDetail.setRelationshipWithMainAssured(groupHealth);
                dependentAssuredDetail.setMainAssuredNRC(groupHealthInsured.getNrcNumber());
                dependentAssuredDetail.setMainAssuredMANNumber(groupHealthInsured.getManNumber());
                dependentAssuredDetail.setMainAssuredLastSalary(BigDecimal.ZERO);
                dependentAssuredDetail.setMainAssuredClientId(isNotEmpty(groupHealthInsured.getFamilyId()) ? groupHealthInsured.getFamilyId().getFamilyId() : StringUtils.EMPTY);
            }
            this.dependentAssuredDetail = dependentAssuredDetail;
        }
        return this;
    }


    public ClaimantPolicyDetailDto updateWithSumAssured(BigDecimal sumAssured) {
        this.sumAssured = sumAssured;
        return this;
    }

    public ClaimantPolicyDetailDto updateWithCoverageBenefitDetails(GHPlanPremiumDetail planDetail) {
        if(isNotEmpty(planDetail)){

        }
        return this;
    }

    public ClaimantPolicyDetailDto updateWithPlanCode(String planCode) {
        this.planCode = planCode;
        return this;
    }

    public ClaimantPolicyDetailDto updateWithAssuredDetail(AssuredDetail assuredDetail) {
        this.assuredDetail = assuredDetail;
        return this;
    }

    public ClaimantPolicyDetailDto updateWithDependentAssuredDetail(DependentAssuredDetail dependentAssuredDetail) {
        this.dependentAssuredDetail = dependentAssuredDetail;
        return this;
    }

    public ClaimantPolicyDetailDto updateWithCoverageDetails(Set<CoverageBenefitDetailDto> coverageBenefitDetails) {
        this.coverageBenefitDetails = coverageBenefitDetails;
        return this;
    }

    public ClaimantPolicyDetailDto updateWithProposerDetail(PreAuthorizationClaimantProposerDetail preAuthorizationClaimantProposerDetail) {
        this.preAuthorizationClaimantProposerDetail = preAuthorizationClaimantProposerDetail;
        return this;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public class CoverageDetailDto {
        String coverageCode;
        String coverageName;
        BigDecimal sumAssured;
    }
}
