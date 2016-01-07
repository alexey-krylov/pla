package com.pla.grouphealth.claim.cashless.presentation.dto;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.core.domain.model.plan.PlanCoverage;
import com.pla.core.domain.model.plan.PlanDetail;
import com.pla.grouphealth.sharedresource.model.vo.*;
import com.pla.sharedkernel.domain.model.Relationship;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.nthdimenzion.common.AppConstants;
import org.nthdimenzion.presentation.AppUtils;
import org.nthdimenzion.utils.UtilValidator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Created by Mohan Sharma on 1/6/2016.
 */
@Getter
@Setter
public class ClaimantPolicyDetailDto {
    private String proposerName;
    private String address1;
    private String address2;
    private String postalCode;
    private String province;
    private String town;
    private String emailId;
    private String workPhone;
    private String contactPersonName;
    private String contactPersonWorkPhone;
    private String contactPersonMobileNumber;
    private String contactPersonEmailId;
    private String planCode;
    private String planName;
    private String category;
    private String relationship;
    private BigDecimal sumAssured;
    private Set<CoverageDetailDto> coverageDetailDtoList = Sets.newHashSet();
    private AssuredDetail assuredDetail;
    private DependentAssuredDetail dependentAssuredDetail;

    public static ClaimantPolicyDetailDto getInstance() {
        return new ClaimantPolicyDetailDto();
    }

    public ClaimantPolicyDetailDto updateWithProposerName(String proposerName) {
        this.proposerName = proposerName;
        return this;
    }

    public ClaimantPolicyDetailDto updateWithDetails(GHProposerContactDetail ghProposerContactDetail) {
        if(isNotEmpty(ghProposerContactDetail)) {
            this.address1 = ghProposerContactDetail.getAddressLine1();
            this.address2 = ghProposerContactDetail.getAddressLine2();
            this.postalCode = ghProposerContactDetail.getPostalCode();
            this.province = ghProposerContactDetail.getProvince();
            this.town = ghProposerContactDetail.getTown();
            this.emailId = ghProposerContactDetail.getEmailAddress();
            if(isNotEmpty(ghProposerContactDetail.getContactPersonDetail())) {
                GHProposerContactDetail.ContactPersonDetail contactPersonDetail = ghProposerContactDetail.getContactPersonDetail().get(0);
                this.contactPersonName = contactPersonDetail.getContactPersonName();
                this.contactPersonMobileNumber = contactPersonDetail.getMobileNumber();
                this.contactPersonWorkPhone = contactPersonDetail.getWorkPhoneNumber();
                this.contactPersonEmailId = contactPersonDetail.getContactPersonEmail();
            }
        }
        return this;
    }

    public ClaimantPolicyDetailDto updateWithPlanName(PlanDetail planDetail) {
        if(isNotEmpty(planDetail)) {
            this.planName = planDetail.getPlanName();
        }
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

    public ClaimantPolicyDetailDto updateWithCoverages(List<GHCoveragePremiumDetail> coverages) {
        Set<CoverageDetailDto> coverageDetailDtos = Sets.newHashSet();
        if(isNotEmpty(coverages)){
            for(GHCoveragePremiumDetail ghCoveragePremiumDetail : coverages){
                CoverageDetailDto coverageDetailDto = new CoverageDetailDto(ghCoveragePremiumDetail.getCoverageName(), ghCoveragePremiumDetail.getSumAssured());
                coverageDetailDtos.add(coverageDetailDto);
            }
        }
        this.coverageDetailDtoList = coverageDetailDtos;
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
            assuredDetail.setAgeNextBirthday(age);
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
            dependentAssuredDetail.setAgeNextBirthday(age);
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

    private class CoverageDetailDto {
        String coverageName;
        BigDecimal sumAssured;

        public CoverageDetailDto(String coverageName, BigDecimal sumAssured) {
            this.coverageName = coverageName;
            this.sumAssured = sumAssured;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CoverageDetailDto that = (CoverageDetailDto) o;

            if (coverageName != null ? !coverageName.equals(that.coverageName) : that.coverageName != null)
                return false;
            if (sumAssured != null ? !sumAssured.equals(that.sumAssured) : that.sumAssured != null) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = coverageName != null ? coverageName.hashCode() : 0;
            result = 31 * result + (sumAssured != null ? sumAssured.hashCode() : 0);
            return result;
        }
    }
}
