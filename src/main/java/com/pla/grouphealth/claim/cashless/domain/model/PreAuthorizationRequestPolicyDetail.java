package com.pla.grouphealth.claim.cashless.domain.model;

import com.google.common.collect.Sets;
import com.pla.grouphealth.claim.cashless.presentation.dto.AssuredDetail;
import com.pla.grouphealth.claim.cashless.presentation.dto.ClaimantPolicyDetailDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.DependentAssuredDetail;
import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationClaimantDetailCommand;
import lombok.*;
import org.hibernate.annotations.Immutable;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.ValueObject;
import org.nthdimenzion.utils.UtilValidator;

import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.*;

/**
 * Created by Mohan Sharma on 1/9/2016.
 */
@ValueObject
@Immutable
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
@Getter
public class PreAuthorizationRequestPolicyDetail {
    private String policyNumber;
    private String policyName;
    private String planCode;
    private String planName;
    private BigDecimal sumAssured;
    private PreAuthorizationRequestAssuredDetail assuredDetail;
    private Set<PreAuthorizationRequestCoverageDetail> coverageDetailDtoList = Sets.newHashSet();

    public PreAuthorizationRequestPolicyDetail updateWithDetails(PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand) {
        ClaimantPolicyDetailDto claimantPolicyDetailDto = preAuthorizationClaimantDetailCommand.getClaimantPolicyDetailDto();
        if(isNotEmpty(claimantPolicyDetailDto)) {
            this.policyName = claimantPolicyDetailDto.getPolicyName();
            this.policyNumber = claimantPolicyDetailDto.getPolicyNumber();
            this.planCode = claimantPolicyDetailDto.getPlanCode();
            this.planName = claimantPolicyDetailDto.getPlanName();
            this.sumAssured = claimantPolicyDetailDto.getSumAssured();
            PreAuthorizationRequestAssuredDetail assuredDetail = isNotEmpty(this.assuredDetail) ? this.assuredDetail : new PreAuthorizationRequestAssuredDetail();
            this.assuredDetail = assuredDetail.updateWithAssuredDetails(claimantPolicyDetailDto);
            this.coverageDetailDtoList = constructPreAuthorizationRequestCoverageDetail(claimantPolicyDetailDto.getCoverageDetailDtoList());
        }
        return this;
    }

    private Set<PreAuthorizationRequestCoverageDetail> constructPreAuthorizationRequestCoverageDetail(Set<ClaimantPolicyDetailDto.CoverageDetailDto> coverageDetailDtoList) {
        return isNotEmpty(coverageDetailDtoList) ? coverageDetailDtoList.parallelStream().map(new Function<ClaimantPolicyDetailDto.CoverageDetailDto, PreAuthorizationRequestCoverageDetail>() {
            @Override
            public PreAuthorizationRequestCoverageDetail apply(ClaimantPolicyDetailDto.CoverageDetailDto coverageDetailDto) {
                return new PreAuthorizationRequestCoverageDetail(coverageDetailDto.getCoverageCode(), coverageDetailDto.getCoverageName(), coverageDetailDto.getSumAssured());
            }
        }).collect(Collectors.toSet()) : Sets.newHashSet();
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @AllArgsConstructor
    private class PreAuthorizationRequestCoverageDetail {
        String coverageCode;
        String coverageName;
        BigDecimal sumAssured;
    }

    @Getter
    @Setter
    private class PreAuthorizationRequestAssuredDetail {
        private String salutation;
        private String firstName;
        private String surname;
        private LocalDate dateOfBirth;
        private int ageNextBirthday;
        private String nrcNumber;
        private String gender;
        private BigDecimal sumAssured;
        private BigDecimal reserveAmount;
        private String category;
        private String manNumber;
        private String clientId;
        private String mainAssuredFullName;
        private String relationshipWithMainAssured;
        private String mainAssuredNRC;
        private String mainAssuredMANNumber;
        private BigDecimal mainAssuredLastSalary;
        private String mainAssuredClientId;

        public PreAuthorizationRequestAssuredDetail updateWithAssuredDetails(ClaimantPolicyDetailDto claimantPolicyDetailDto) {
            AssuredDetail assuredDetail = claimantPolicyDetailDto.getAssuredDetail();
            DependentAssuredDetail dependentAssuredDetail = claimantPolicyDetailDto.getDependentAssuredDetail();
            if(isNotEmpty(assuredDetail)) {
                this.salutation = assuredDetail.getSalutation();
                this.firstName = assuredDetail.getFirstName();
                this.surname = assuredDetail.getSurname();
                this.dateOfBirth = assuredDetail.getDateOfBirth();
                this.ageNextBirthday = assuredDetail.getAgeNextBirthday();
                this.nrcNumber = assuredDetail.getNrcNumber();
                this.gender = assuredDetail.getGender();
                this.sumAssured = assuredDetail.getSumAssured();
                this.reserveAmount = assuredDetail.getReserveAmount();
                this.category = assuredDetail.getCategory();
                this.manNumber = assuredDetail.getManNumber();
                this.clientId = assuredDetail.getClientId();
            } else{
                this.salutation = dependentAssuredDetail.getSalutation();
                this.firstName = dependentAssuredDetail.getFirstName();
                this.surname = dependentAssuredDetail.getSurname();
                this.dateOfBirth = dependentAssuredDetail.getDateOfBirth();
                this.ageNextBirthday = dependentAssuredDetail.getAgeNextBirthday();
                this.nrcNumber = dependentAssuredDetail.getNrcNumber();
                this.gender = dependentAssuredDetail.getGender();
                this.sumAssured = dependentAssuredDetail.getSumAssured();
                this.reserveAmount = dependentAssuredDetail.getReserveAmount();
                this.category = dependentAssuredDetail.getCategory();
                this.manNumber = dependentAssuredDetail.getManNumber();
                this.clientId = dependentAssuredDetail.getClientId();
                this.mainAssuredClientId = dependentAssuredDetail.getMainAssuredClientId();
                this.mainAssuredFullName = dependentAssuredDetail.getMainAssuredFullName();
                this.relationshipWithMainAssured = dependentAssuredDetail.getRelationshipWithMainAssured();
                this.mainAssuredNRC = dependentAssuredDetail.getMainAssuredNRC();
                this.mainAssuredMANNumber = dependentAssuredDetail.getMainAssuredMANNumber();
                this.mainAssuredLastSalary = dependentAssuredDetail.getMainAssuredLastSalary();
            }
            return this;
        }
    }
}
