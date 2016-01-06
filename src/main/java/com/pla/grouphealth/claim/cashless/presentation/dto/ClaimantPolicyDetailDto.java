package com.pla.grouphealth.claim.cashless.presentation.dto;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.core.domain.model.plan.PlanCoverage;
import com.pla.core.domain.model.plan.PlanDetail;
import com.pla.grouphealth.sharedresource.model.vo.GHInsured;
import com.pla.grouphealth.sharedresource.model.vo.GHPlanPremiumDetail;
import com.pla.grouphealth.sharedresource.model.vo.GHProposerContactDetail;
import org.nthdimenzion.utils.UtilValidator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Created by Mohan Sharma on 1/6/2016.
 */
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
    private BigDecimal sumAssured;
    private Set<CoverageDetailDto> coverageDetailDtoList = Sets.newHashSet();

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

    public ClaimantPolicyDetailDto updateWithPlanCode(String planCode) {
        this.planCode = planCode;
        return this;
    }

    public ClaimantPolicyDetailDto updateWithCoverages(Set<PlanCoverage> coverages) {
        Set<CoverageDetailDto> coverageDetailDtos = Sets.newHashSet();
        if(isNotEmpty(coverages)){
            for(PlanCoverage planCoverage : coverages){
                CoverageDetailDto coverageDetailDto = new CoverageDetailDto();
                coverageDetailDto.coverageName = planCoverage.getCoverageName();
                coverageDetailDtos.add(coverageDetailDto);
            }
        }
        this.coverageDetailDtoList = coverageDetailDtos;
        return this;
    }

    private class CoverageDetailDto {
        String coverageName;
        BigDecimal sumAssured;

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
