package com.pla.grouphealth.claim.cashless.application.service;

import com.google.common.collect.Lists;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.hcp.domain.model.HCP;
import com.pla.core.hcp.domain.model.HCPCode;
import com.pla.core.hcp.query.HCPFinder;
import com.pla.core.repository.PlanRepository;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorization;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationDetail;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationId;
import com.pla.grouphealth.claim.cashless.presentation.dto.*;
import com.pla.grouphealth.claim.cashless.repository.PreAuthorizationRepository;
import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.grouphealth.policy.repository.GHPolicyRepository;
import com.pla.grouphealth.sharedresource.model.vo.GHInsured;
import com.pla.grouphealth.sharedresource.model.vo.GHInsuredDependent;
import com.pla.grouphealth.sharedresource.model.vo.GHPlanPremiumDetail;
import com.pla.grouphealth.sharedresource.model.vo.GHProposer;
import com.pla.sharedkernel.domain.model.Relationship;
import lombok.NoArgsConstructor;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;
import static org.springframework.util.Assert.notNull;

/**
 * Created by Mohan Sharma on 1/6/2016.
 */
@DomainService
@NoArgsConstructor
public class PreAuthorizationRequestService {
    @Autowired
    private PreAuthorizationRepository preAuthorizationRepository;
    @Autowired
    private HCPFinder hcpFinder;
    @Autowired
    GHPolicyRepository ghPolicyRepository;
    @Autowired
    PlanRepository planRepository;

    public PreAuthorizationClaimantDetailDto getPreAuthorizationByPreAuthorizationIdAndClientId(PreAuthorizationId preAuthorizationId, String clientId) {
        PreAuthorization preAuthorization = preAuthorizationRepository.findOne(preAuthorizationId);
        if(isNotEmpty(preAuthorization)){
            return constructPreAuthorizationClaimantDetailDtoFromPreAuthorization(preAuthorization, clientId);
        }
        return PreAuthorizationClaimantDetailDto.getInstance();
    }

    private PreAuthorizationClaimantDetailDto constructPreAuthorizationClaimantDetailDtoFromPreAuthorization(PreAuthorization preAuthorization, String clientId) {
        PreAuthorizationDetail preAuthorizationDetail = preAuthorization.getPreAuthorizationDetails().iterator().next();
        notNull(preAuthorizationDetail, "PreAuthorizationDetail cannot be null");
        PreAuthorizationClaimantDetailDto preAuthorizationClaimantDetailDto = new PreAuthorizationClaimantDetailDto();
        preAuthorizationClaimantDetailDto.updateWithBatchNumber(preAuthorization.getBatchNumber())
                .updateWithPreAuthorizationId(preAuthorization.getPreAuthorizationId().getPreAuthorizationId())
                .updateWithPreAuthorizationDate(preAuthorization.getBatchDate())
                .updateWithClaimantHCPDetailDto(constructClaimantHCPDetailDto(preAuthorization.getHcpCode(), preAuthorizationDetail.getHospitalizationEvent()))
                .updateWithClaimantPolicyDetailDto(constructClaimantPolicyDetailDto(preAuthorizationDetail.getPolicyNumber(), clientId))
                .updateWithDiagnosisTreatment(constructDiagnosisTreatmentDto(preAuthorization))
                .updateWithIllnessDetails(constructIllnessDetailDto(preAuthorization))
                .updateWithDrugServices(constructDrugServiceDtos(preAuthorization));
        return preAuthorizationClaimantDetailDto;
    }

    private List<DrugServiceDto> constructDrugServiceDtos(PreAuthorization preAuthorization) {
        return isNotEmpty(preAuthorization.getPreAuthorizationDetails()) ? preAuthorization.getPreAuthorizationDetails().parallelStream().map(new Function<PreAuthorizationDetail, DrugServiceDto>() {
            @Override
            public DrugServiceDto apply(PreAuthorizationDetail preAuthorizationDetail) {
                return new DrugServiceDto()
                        .updateWithDetails(preAuthorizationDetail);
            }
        }).collect(Collectors.toList()) : Lists.newArrayList();
    }

    private IllnessDetailDto constructIllnessDetailDto(PreAuthorization preAuthorization) {
        PreAuthorizationDetail preAuthorizationDetail = isNotEmpty(preAuthorization.getPreAuthorizationDetails()) ? preAuthorization.getPreAuthorizationDetails().iterator().next() : null;
        if(isNotEmpty(preAuthorizationDetail)){
            return new IllnessDetailDto().updateWithDetails(preAuthorizationDetail);
        }
        return null;
    }

    private List<DiagnosisTreatmentDto> constructDiagnosisTreatmentDto(PreAuthorization preAuthorization) {
        return isNotEmpty(preAuthorization.getPreAuthorizationDetails()) ? preAuthorization.getPreAuthorizationDetails().parallelStream().map(new Function<PreAuthorizationDetail, DiagnosisTreatmentDto>() {
            @Override
            public DiagnosisTreatmentDto apply(PreAuthorizationDetail preAuthorizationDetail) {
                return new DiagnosisTreatmentDto()
                        .updateWithDetails(preAuthorizationDetail);
            }
        }).collect(Collectors.toList()) : Lists.newArrayList();
    }

    private ClaimantPolicyDetailDto constructClaimantPolicyDetailDto(String policyNumber, String clientId) {
        GroupHealthPolicy groupHealthPolicy = ghPolicyRepository.findPolicyByPolicyNumber(policyNumber);
        if(isNotEmpty(groupHealthPolicy)){
            GHProposer ghProposer = groupHealthPolicy.getProposer();
            if(isNotEmpty(ghProposer)) {
                ClaimantPolicyDetailDto claimantPolicyDetailDto = new ClaimantPolicyDetailDto();
                claimantPolicyDetailDto.updateWithProposerName(ghProposer.getProposerName())
                        .updateWithDetails(ghProposer.getContactDetail());
                return updateWithPlanDetailsToClaimantDto(groupHealthPolicy, claimantPolicyDetailDto, clientId);
            }

        }
        return ClaimantPolicyDetailDto.getInstance();
    }

    private ClaimantPolicyDetailDto updateWithPlanDetailsToClaimantDto(GroupHealthPolicy groupHealthPolicy, ClaimantPolicyDetailDto claimantPolicyDetailDto, String clientId) {
        Set<GHInsured> insureds = groupHealthPolicy.getInsureds();
        GHInsured groupHealthInsured = null;
        GHInsuredDependent ghInsuredDependent = null;
        if(isNotEmpty(insureds)){
            Optional<GHInsured> groupHealthInsuredOptional = insureds.stream().filter(ghInsured -> ghInsured.getFamilyId().getFamilyId().equalsIgnoreCase(clientId)).findFirst();
            if(groupHealthInsuredOptional.isPresent()) {
                groupHealthInsured = groupHealthInsuredOptional.get();
                claimantPolicyDetailDto
                        .updateWithCategory(groupHealthInsured.getCategory())
                        .updateWithRelationship(Relationship.SELF)
                        .updateWithAssuredDetails(groupHealthInsured);
            }
            if(isEmpty(groupHealthInsured)) {
                Optional<GHInsuredDependent> ghInsuredDependentOptional = insureds.stream().flatMap(new Function<GHInsured, Stream<GHInsuredDependent>>() {
                    @Override
                    public Stream<GHInsuredDependent> apply(GHInsured ghInsured) {
                        return ghInsured.getInsuredDependents().stream();
                    }
                }).filter(gHInsuredDependent -> gHInsuredDependent.getFamilyId().getFamilyId().equalsIgnoreCase(clientId)).findFirst();
                if(ghInsuredDependentOptional.isPresent()) {
                    ghInsuredDependent = ghInsuredDependentOptional.get();
                    final GHInsuredDependent finalGhInsuredDependent = ghInsuredDependent;
                    groupHealthInsured = insureds.parallelStream().filter(new Predicate<GHInsured>() {
                        @Override
                        public boolean test(GHInsured ghInsured) {
                            return ghInsured.getInsuredDependents().stream().filter(gHInsuredDependent -> gHInsuredDependent.getFamilyId().getFamilyId().equalsIgnoreCase(finalGhInsuredDependent.getFamilyId().getFamilyId())).findFirst().isPresent();
                        }
                    }).findFirst().get();
                    claimantPolicyDetailDto
                            .updateWithCategory(ghInsuredDependent.getCategory())
                            .updateWithRelationship(ghInsuredDependent.getRelationship())
                            .updateWithDependentAssuredDetail(ghInsuredDependent, groupHealthInsured);
                }
            }
        }
        GHPlanPremiumDetail planDetail = isNotEmpty(groupHealthInsured) ? groupHealthInsured.getPlanPremiumDetail() : ghInsuredDependent.getPlanPremiumDetail();
        if(isNotEmpty(planDetail)){
            List<Plan> plans = planRepository.findPlanByCodeAndName(planDetail.getPlanCode());
            if(isNotEmpty(plans)){
                Plan plan = plans.get(0);
                claimantPolicyDetailDto
                        .updateWithSumAssured(planDetail.getSumAssured())
                        .updateWithCoverages(planDetail.getCoveragePremiumDetails())
                        .updateWithPlanCode(plan.getPlanDetail())
                        .updateWithPlanName(plan.getPlanDetail());
            }
        }
        return claimantPolicyDetailDto;
    }

    private ClaimantHCPDetailDto constructClaimantHCPDetailDto(HCPCode hcpCode, String hospitalizationEvent) {
        HCP hcp = hcpFinder.getHCPByHCPCode(hcpCode.getHcpCode());
        if(isNotEmpty(hcp)){
            ClaimantHCPDetailDto claimantHCPDetailDto = new ClaimantHCPDetailDto();
            claimantHCPDetailDto.updateWithHospitalizationEvent(hospitalizationEvent)
                    .updateWithAddress(hcp.getHcpAddress())
                    .updateWithHCPName(hcp.getHcpName())
                    .updateWithHCPCode(hcp.getHcpCode());
            return claimantHCPDetailDto;
        }
        return ClaimantHCPDetailDto.getInstance();
    }

    public Map<String, Object> getPolicyByPreAuthorizationId(PreAuthorizationId preAuthorizationId) {

        return null;
    }
}