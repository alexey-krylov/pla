package com.pla.grouphealth.claim.cashless.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.core.SBCM.domain.model.ServiceBenefitCoverageMapping;
import com.pla.core.SBCM.repository.SBCMRepository;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.domain.model.plan.PlanCoverage;
import com.pla.core.domain.model.plan.PlanCoverageBenefit;
import com.pla.core.dto.CoverageDto;
import com.pla.core.hcp.domain.model.HCP;
import com.pla.core.hcp.domain.model.HCPCode;
import com.pla.core.hcp.domain.model.HCPRate;
import com.pla.core.hcp.domain.model.HCPServiceDetail;
import com.pla.core.hcp.query.HCPFinder;
import com.pla.core.hcp.repository.HCPRateRepository;
import com.pla.core.query.BenefitFinder;
import com.pla.core.query.CoverageFinder;
import com.pla.core.repository.PlanRepository;
import com.pla.grouphealth.claim.cashless.domain.exception.GenerateReminderFollowupException;
import com.pla.grouphealth.claim.cashless.domain.exception.PreAuthorizationInProcessingException;
import com.pla.grouphealth.claim.cashless.domain.exception.RoutingLevelNotFoundException;
import com.pla.grouphealth.claim.cashless.domain.model.*;
import com.pla.grouphealth.claim.cashless.presentation.dto.*;
import com.pla.grouphealth.claim.cashless.query.PreAuthorizationFinder;
import com.pla.grouphealth.claim.cashless.repository.PreAuthorizationRepository;
import com.pla.grouphealth.claim.cashless.repository.PreAuthorizationRequestRepository;
import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.grouphealth.policy.repository.GHPolicyRepository;
import com.pla.grouphealth.proposal.presentation.dto.GHProposalMandatoryDocumentDto;
import com.pla.grouphealth.sharedresource.model.vo.*;
import com.pla.publishedlanguage.contract.IAuthenticationFacade;
import com.pla.publishedlanguage.dto.ClientDocumentDto;
import com.pla.publishedlanguage.dto.SearchDocumentDetailDto;
import com.pla.publishedlanguage.dto.UnderWriterRoutingLevelDetailDto;
import com.pla.publishedlanguage.underwriter.contract.IUnderWriterAdapter;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.BenefitId;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import lombok.NoArgsConstructor;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.axonframework.repository.Repository;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.transaction.Transactional;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;
import static org.springframework.data.domain.Sort.Direction;
import static org.springframework.data.domain.Sort.Order;
import static org.springframework.util.Assert.notNull;

/**
 * Author - Mohan Sharma Created on 1/6/2016.
 */
@DomainService
@NoArgsConstructor
public class PreAuthorizationRequestService {
    @Autowired
    private PreAuthorizationRepository preAuthorizationRepository;
    @Autowired
    private HCPFinder hcpFinder;
    @Autowired
    private GHPolicyRepository ghPolicyRepository;
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private Repository<PreAuthorizationRequest> preAuthorizationRequestMongoRepository;
    @Autowired
    private PreAuthorizationRequestRepository preAuthorizationRequestRepository;
    @Autowired
    private IUnderWriterAdapter underWriterAdapter;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private PreAuthorizationFinder preAuthorizationFinder;
    @Autowired
    CoverageFinder coverageFinder;
    @Autowired
    SBCMRepository sbcmRepository;
    @Autowired
    HCPRateRepository hcpRateRepository;
    @Autowired
    BenefitFinder benefitFinder;
    @Autowired
    @Qualifier("authenticationFacade")
    IAuthenticationFacade authenticationFacade;

    public PreAuthorizationClaimantDetailCommand getPreAuthorizationClaimantDetailCommandFromPreAuthorizationRequestId(PreAuthorizationRequestId preAuthorizationRequestId) {
        PreAuthorizationRequest preAuthorizationRequest = getPreAuthorizationRequestById(preAuthorizationRequestId);
        notNull(preAuthorizationRequest, "No PreAuthorizationRequest found with given Id");
        if (isNotEmpty(preAuthorizationRequest))
            return constructPreAuthorizationClaimantDetailCommand(preAuthorizationRequest);
        return null;
    }

    @Transactional
    public PreAuthorizationClaimantDetailCommand getPreAuthorizationByPreAuthorizationIdAndClientId(PreAuthorization preAuthorization, String clientId) {
        if(isNotEmpty(preAuthorization)){
            return constructPreAuthorizationClaimantDetailDtoFromPreAuthorization(preAuthorization, clientId);
        }
        return PreAuthorizationClaimantDetailCommand.getInstance();
    }

    public PreAuthorizationRequest createPreAuthorizationRequest(PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand) throws GenerateReminderFollowupException {
        PreAuthorizationRequest preAuthorizationRequest = new PreAuthorizationRequest(PreAuthorizationRequest.Status.INTIMATION);
        preAuthorizationRequest.updateWithPreAuthorizationRequestId(preAuthorizationClaimantDetailCommand.getPreAuthorizationId())
                .updateWithPreAuthorizationId(preAuthorizationClaimantDetailCommand.getPreAuthorizationId())
                .updateWithPreAuthorizationDate(preAuthorizationClaimantDetailCommand.getPreAuthorizationDate())
                .updateWithCategory(preAuthorizationClaimantDetailCommand.getClaimantPolicyDetailDto())
                .updateWithRelationship(preAuthorizationClaimantDetailCommand.getClaimantPolicyDetailDto())
                .updateWithClaimType(preAuthorizationClaimantDetailCommand.getClaimType())
                .updateWithClaimIntimationDate(preAuthorizationClaimantDetailCommand.getClaimIntimationDate())
                .updateWithBatchNumber(preAuthorizationClaimantDetailCommand.getBatchNumber())
                .updateWithBatchUploaderUserId(preAuthorizationClaimantDetailCommand.getBatchUploaderUserId())
                .updateWithProposerDetail(preAuthorizationClaimantDetailCommand.getClaimantPolicyDetailDto())
                .updateWithPreAuthorizationRequestPolicyDetail(preAuthorizationClaimantDetailCommand)
                .updateWithPreAuthorizationRequestHCPDetail(preAuthorizationClaimantDetailCommand.getClaimantHCPDetailDto())
                .updateWithPreAuthorizationRequestDiagnosisTreatmentDetail(preAuthorizationClaimantDetailCommand.getDiagnosisTreatmentDtos())
                .updateWithPreAuthorizationRequestIllnessDetail(preAuthorizationClaimantDetailCommand.getIllnessDetailDto())
                .updateWithPreAuthorizationRequestDrugService(preAuthorizationClaimantDetailCommand.getDrugServicesDtos());
        preAuthorizationRequestMongoRepository.add(preAuthorizationRequest);
        return preAuthorizationRequest;
    }

    public PreAuthorizationRequest updatePreAuthorizationRequest(PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand) throws GenerateReminderFollowupException {
        String preAuthorizationRequestId = preAuthorizationClaimantDetailCommand.getPreAuthorizationRequestId();
        notNull(preAuthorizationRequestId, "PreAuthorizationRequestId is empty for the record");
        PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestRepository.findByPreAuthorizationRequestId(preAuthorizationRequestId);
        preAuthorizationRequest.updateWithPreAuthorizationRequestId(preAuthorizationClaimantDetailCommand.getPreAuthorizationId())
                .updateWithPreAuthorizationId(preAuthorizationClaimantDetailCommand.getPreAuthorizationId())
                .updateWithPreAuthorizationDate(preAuthorizationClaimantDetailCommand.getPreAuthorizationDate())
                .updateWithCategory(preAuthorizationClaimantDetailCommand.getClaimantPolicyDetailDto())
                .updateWithRelationship(preAuthorizationClaimantDetailCommand.getClaimantPolicyDetailDto())
                .updateWithClaimType(preAuthorizationClaimantDetailCommand.getClaimType())
                .updateWithClaimIntimationDate(preAuthorizationClaimantDetailCommand.getClaimIntimationDate())
                .updateWithBatchNumber(preAuthorizationClaimantDetailCommand.getBatchNumber())
                .updateWithProposerDetail(preAuthorizationClaimantDetailCommand.getClaimantPolicyDetailDto())
                .updateWithPreAuthorizationRequestPolicyDetail(preAuthorizationClaimantDetailCommand)
                .updateWithPreAuthorizationRequestHCPDetail(preAuthorizationClaimantDetailCommand.getClaimantHCPDetailDto())
                .updateWithPreAuthorizationRequestDiagnosisTreatmentDetail(preAuthorizationClaimantDetailCommand.getDiagnosisTreatmentDtos())
                .updateWithPreAuthorizationRequestIllnessDetail(preAuthorizationClaimantDetailCommand.getIllnessDetailDto())
                .updateWithPreAuthorizationRequestDrugService(preAuthorizationClaimantDetailCommand.getDrugServicesDtos())
                .updateStatus(PreAuthorizationRequest.Status.EVALUATION);
        if(preAuthorizationClaimantDetailCommand.isSubmitEventFired()) {
            //UnderWriterRoutingLevelDetailDto routingLevelDetailDto = new UnderWriterRoutingLevelDetailDto(new PlanId(preAuthorizationRequest.getPreAuthorizationRequestPolicyDetail().getPlanId()), LocalDate.now(), ProcessType.ENROLLMENT.name());
            //getRoutingLevel()
            //preAuthorizationRequest.updateStatus(PreAuthorizationRequest.Status.UNDERWRITING);
        }
        return preAuthorizationRequestRepository.save(preAuthorizationRequest);
    }

    private PreAuthorizationClaimantDetailCommand constructPreAuthorizationClaimantDetailDtoFromPreAuthorization(PreAuthorization preAuthorization, String clientId) {
        PreAuthorizationDetail preAuthorizationDetail = preAuthorization.getPreAuthorizationDetails().iterator().next();
        notNull(preAuthorizationDetail, "PreAuthorizationDetail cannot be null");
        PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand = new PreAuthorizationClaimantDetailCommand();
        preAuthorizationClaimantDetailCommand.updateWithBatchNumber(preAuthorization.getBatchNumber())
                .updateWithPreAuthorizationId(preAuthorization.getPreAuthorizationId())
                .updateWithBatchUploaderUserId(preAuthorization.getBatchUploaderUserId())
                .updateWithPreAuthorizationDate(preAuthorization.getBatchDate().toLocalDate())
                .updateWithClaimantHCPDetailDto(constructClaimantHCPDetailDto(preAuthorization.getHcpCode(), preAuthorizationDetail.getHospitalizationEvent()))
                .updateWithClaimantPolicyDetailDto(constructClaimantPolicyDetailDto(preAuthorizationDetail.getPolicyNumber(), clientId, preAuthorization.getPreAuthorizationDetails(), preAuthorization.getHcpCode()))
                .updateWithDiagnosisTreatment(constructDiagnosisTreatmentDto(preAuthorization))
                .updateWithIllnessDetails(constructIllnessDetailDto(preAuthorization))
                .updateWithDrugServices(constructDrugServiceDtos(preAuthorization))
                .updateWithPreAuthorizationDate(isNotEmpty(preAuthorization.getBatchDate()) ? preAuthorization.getBatchDate().toLocalDate() : LocalDate.now());
        return preAuthorizationClaimantDetailCommand;
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
        if (isNotEmpty(preAuthorizationDetail)) {
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

    private ClaimantPolicyDetailDto constructClaimantPolicyDetailDto(String policyNumber, String clientId, Set<PreAuthorizationDetail> preAuthorizationDetails, HCPCode hcpCode) {
        GroupHealthPolicy groupHealthPolicy = ghPolicyRepository.findPolicyByPolicyNumber(policyNumber);
        if (isNotEmpty(groupHealthPolicy)) {
            GHProposer ghProposer = groupHealthPolicy.getProposer();
            if (isNotEmpty(ghProposer)) {
                ClaimantPolicyDetailDto claimantPolicyDetailDto = new ClaimantPolicyDetailDto()
                        .updateWithPreAuthorizationClaimantProposerDetail(ghProposer.getContactDetail(), ghProposer.getProposerName(), ghProposer.getProposerCode())
                        .updateWithPolicyName(groupHealthPolicy.getSchemeName())
                        .updateWithPolicyNumber(isNotEmpty(groupHealthPolicy.getPolicyNumber()) ? groupHealthPolicy.getPolicyNumber().getPolicyNumber() : StringUtils.EMPTY);
                return updateWithPlanDetailsToClaimantDto(groupHealthPolicy, claimantPolicyDetailDto, clientId, preAuthorizationDetails, hcpCode);
            }

        }
        return ClaimantPolicyDetailDto.getInstance();
    }

    private ClaimantPolicyDetailDto updateWithPlanDetailsToClaimantDto(GroupHealthPolicy groupHealthPolicy, ClaimantPolicyDetailDto claimantPolicyDetailDto, String clientId, Set<PreAuthorizationDetail> preAuthorizationDetails, HCPCode hcpCode) {
        Set<GHInsured> insureds = groupHealthPolicy.getInsureds();
        GHInsured groupHealthInsured = null;
        GHInsuredDependent ghInsuredDependent = null;
        if (isNotEmpty(insureds)) {
            Optional<GHInsured> groupHealthInsuredOptional = insureds.stream().filter(ghInsured -> ghInsured.getFamilyId().getFamilyId().equalsIgnoreCase(clientId)).findFirst();
            if (groupHealthInsuredOptional.isPresent()) {
                groupHealthInsured = groupHealthInsuredOptional.get();
                claimantPolicyDetailDto
                        .updateWithCategory(groupHealthInsured.getCategory())
                        .updateWithRelationship(Relationship.SELF)
                        .updateWithAssuredDetails(groupHealthInsured);
            }
            if (isEmpty(groupHealthInsured)) {
                Optional<GHInsuredDependent> ghInsuredDependentOptional = insureds.stream().flatMap(new Function<GHInsured, Stream<GHInsuredDependent>>() {
                    @Override
                    public Stream<GHInsuredDependent> apply(GHInsured ghInsured) {
                        return ghInsured.getInsuredDependents().stream();
                    }
                }).filter(gHInsuredDependent -> gHInsuredDependent.getFamilyId().getFamilyId().equalsIgnoreCase(clientId)).findFirst();
                if (ghInsuredDependentOptional.isPresent()) {
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
        if (isNotEmpty(planDetail)) {
            List<Plan> plans = planRepository.findPlanByCodeAndName(planDetail.getPlanCode());
            if (isNotEmpty(plans)) {
                Plan plan = plans.get(0);
                claimantPolicyDetailDto
                        .updateWithSumAssured(planDetail.getSumAssured())
                        .updateWithCoverages(constructCoverageBenefitDetails(planDetail, clientId))
                        .updateWithPlanCode(plan.getPlanDetail())
                        .updateWithPlanId(plan.getPlanId())
                        .updateWithPlanName(plan.getPlanDetail())
                        .updateWithCoverageDetails(constructProbableClaimAmountForServices(claimantPolicyDetailDto.getCoverageBenefitDetails(), preAuthorizationDetails, plan, hcpCode, planDetail));
            }
        }
        return claimantPolicyDetailDto;
    }

    private Set<CoverageBenefitDetailDto>  constructProbableClaimAmountForServices(Set<CoverageBenefitDetailDto> coverageBenefitDetails, Set<PreAuthorizationDetail> preAuthorizationDetails, Plan plan, HCPCode hcpCode, GHPlanPremiumDetail planDetail) {
        Set<String> services = getServicesFromPreAuthDetails(preAuthorizationDetails);
        Set<ServiceBenefitCoverageMapping> sbcmSet = null;
        List<Map<String, Object>> refurbishedList = Lists.newArrayList();
        if (isNotEmpty(services) && isNotEmpty(plan.getPlanDetail())) {
            sbcmSet = services.parallelStream().map(new Function<String, List<ServiceBenefitCoverageMapping>>() {
                @Override
                public List<ServiceBenefitCoverageMapping> apply(String service) {
                    List<GHCoveragePremiumDetail> ghCoveragePremiumDetails = planDetail.getCoveragePremiumDetails();
                    if(isEmpty(ghCoveragePremiumDetails))
                        return Collections.EMPTY_LIST;
                    List<ServiceBenefitCoverageMapping> setOfSBCM = planDetail.getCoveragePremiumDetails().stream().map(new Function<GHCoveragePremiumDetail, List<ServiceBenefitCoverageMapping>>() {
                        @Override
                        public List<ServiceBenefitCoverageMapping> apply(GHCoveragePremiumDetail ghCoveragePremiumDetail) {
                            CoverageId coverageId = ghCoveragePremiumDetail.getCoverageId();
                            Set<BenefitPremiumLimit> benefitPremiumLimits = ghCoveragePremiumDetail.getBenefitPremiumLimits();
                            if(isEmpty(benefitPremiumLimits))
                                return Collections.EMPTY_LIST;
                            List<ServiceBenefitCoverageMapping> setOfSBCM = ghCoveragePremiumDetail.getBenefitPremiumLimits().stream().map(new Function<BenefitPremiumLimit, List<ServiceBenefitCoverageMapping>>() {
                                @Override
                                public List<ServiceBenefitCoverageMapping> apply(BenefitPremiumLimit benefitPremiumLimit) {
                                    String benefitCode = benefitPremiumLimit.getBenefitCode();
                                    benefitCode = String.valueOf(new BigDecimal(benefitPremiumLimit.getBenefitCode()).intValue());
                                    List<ServiceBenefitCoverageMapping> serviceBenefitCoverageMappings = sbcmRepository.findAllByCoverageIdAndBenefitCodeAndService(coverageId, benefitCode, service);
                                    return serviceBenefitCoverageMappings;
                                }
                            }).flatMap(sbcmList -> sbcmList.stream()).collect(Collectors.toList());
                            return setOfSBCM;
                        }
                    }).flatMap(sbcmList -> sbcmList.stream()).collect(Collectors.toList());
                    return setOfSBCM;
                }
            }).flatMap(sbcmList -> sbcmList.stream()).collect(Collectors.toSet());
        }
        if (isNotEmpty(sbcmSet)) {
            Map<ServiceBenefitCoverageMapping.CoverageBenefit, List<ServiceBenefitCoverageMapping>> result = sbcmSet.parallelStream().collect(Collectors.groupingBy(ServiceBenefitCoverageMapping::getCoverageBenefit));
            if (isNotEmpty(result)) {
                refurbishedList = result.values().stream().map(new Function<List<ServiceBenefitCoverageMapping>, Map<String, Object>>() {
                    @Override
                    public Map<String, Object> apply(List<ServiceBenefitCoverageMapping> serviceBenefitCoverageMappings) {
                        Map<String, Object> map = Maps.newHashMap();
                        if (isNotEmpty(serviceBenefitCoverageMappings)) {
                            ServiceBenefitCoverageMapping sbcm = serviceBenefitCoverageMappings.iterator().next();
                            map.put("coverageId", sbcm.getCoverageId());
                            map.put("coverageCode", sbcm.getCoverageCode());
                            map.put("coverageName", sbcm.getCoverageName());
                            map.put("benefitId", sbcm.getBenefitId());
                            map.put("benefitCode", sbcm.getBenefitCode());
                            map.put("benefitName", sbcm.getBenefitName());
                            map.put("services", getListOfServices(serviceBenefitCoverageMappings));
                            notNull(getCoverageBenefitDefinition(sbcm.getBenefitId(), plan.getCoverages()), "CoverageBenefitDefinition null for Benefit - " + sbcm.getBenefitId());
                            map.put("coverageBenefitDefinition", getCoverageBenefitDefinition(sbcm.getBenefitId(), plan.getCoverages()));
                        }
                        return map;
                    }
                }).collect(Collectors.toList());
            }
        }
        refurbishedList.stream().forEach(map -> {
            Set<String> serviceList = isNotEmpty(map.get("services")) ? (Set<String>) map.get("services") : Sets.newHashSet();
            BigDecimal payableAmount = BigDecimal.ZERO;
            for(String service : serviceList) {
                HCPRate hcpRate = hcpRateRepository.findHCPRateByHCPCodeAndService(hcpCode, service);
                notNull(hcpRate, "No HCP Rate configured for hcp- " + hcpCode + " service - " + service);
                HCPServiceDetail hcpServiceDetail = isNotEmpty(hcpRate.getHcpServiceDetails()) ? getHCPDetail(hcpRate.getHcpServiceDetails(), service) : null;
                notNull(hcpRate, "No HCP Rate configured as no HCPServiceDetail found.");
                int lengthOfStay = getLengthOfStayByService(service, preAuthorizationDetails);
                BigDecimal amount = calculateProbableClaimAmount(lengthOfStay, hcpServiceDetail.getNormalAmount(), (CoverageBenefitDefinition) map.get("coverageBenefitDefinition"));
                payableAmount = payableAmount.add(amount);
                map.put("payableAmount", payableAmount);
            }
        });
        final List<Map<String, Object>> finalRefurbishedList = refurbishedList;
        return coverageBenefitDetails.stream().map(new Function<CoverageBenefitDetailDto, CoverageBenefitDetailDto>() {
            @Override
            public CoverageBenefitDetailDto apply(CoverageBenefitDetailDto coverageBenefitDetailDto) {
                String coverageId = coverageBenefitDetailDto.getCoverageId();
                Set<BenefitDetailDto> benefitDetails = coverageBenefitDetailDto.getBenefitDetails();
                coverageBenefitDetailDto.updateWithProbableClaimAmount(coverageId, benefitDetails, finalRefurbishedList);
                return coverageBenefitDetailDto;
            }
        }).collect(Collectors.toSet());
    }

    private HCPServiceDetail getHCPDetail(Set<HCPServiceDetail> hcpServiceDetails, String service) {
        Optional<HCPServiceDetail> hcpServiceDetail = hcpServiceDetails.parallelStream().filter(detail -> detail.getServiceAvailed().equalsIgnoreCase(service)).findAny();
        if(hcpServiceDetail.isPresent()){
            return hcpServiceDetail.get();
        }
        return null;
    }

    private BigDecimal calculateProbableClaimAmount(int lengthOfStay, BigDecimal normalAmount, CoverageBenefitDefinition coverageBenefitDefinition) {
        if (CoverageBenefitDefinition.DAY.equals(coverageBenefitDefinition))
            return normalAmount.multiply(new BigDecimal(lengthOfStay));
        return normalAmount;
    }

    private int getLengthOfStayByService(String service, Set<PreAuthorizationDetail> preAuthorizationDetails) {
        for (PreAuthorizationDetail preAuthorizationDetail : preAuthorizationDetails) {
            if (preAuthorizationDetail.getService().trim().equalsIgnoreCase(service.trim()))
                return preAuthorizationDetail.getDiagnosisTreatmentSurgeryLengthOStay();
        }
        return 1;
    }

    private CoverageBenefitDefinition getCoverageBenefitDefinition(BenefitId benefitId, Set<PlanCoverage> coverages) {
        if (isNotEmpty(coverages)) {
            Set<PlanCoverageBenefit> planCoverageBenefits = coverages.stream().flatMap(coverage -> coverage.getPlanCoverageBenefits().stream()).collect(Collectors.toSet());
            for (PlanCoverageBenefit planCoverageBenefit : planCoverageBenefits) {
                if (planCoverageBenefit.getBenefitId().equals(benefitId)) {
                    return planCoverageBenefit.getDefinedPer();
                }
            }
        }
        return null;
    }

    private Set<String> getListOfServices(List<ServiceBenefitCoverageMapping> serviceBenefitCoverageMappings) {
        return isNotEmpty(serviceBenefitCoverageMappings) ? serviceBenefitCoverageMappings.stream().map(ServiceBenefitCoverageMapping::getService).collect(Collectors.toSet()) : Sets.newHashSet();
    }

    private Set<String> getServicesFromPreAuthDetails(Set<PreAuthorizationDetail> preAuthorizationDetails) {
        return isNotEmpty(preAuthorizationDetails) ? preAuthorizationDetails.stream().map(PreAuthorizationDetail::getService).collect(Collectors.toSet()) : Sets.newHashSet();
    }

    private Set<CoverageBenefitDetailDto> constructCoverageBenefitDetails(GHPlanPremiumDetail planDetail, String clientId) {
        return isNotEmpty(planDetail) ? isNotEmpty(planDetail.getCoveragePremiumDetails()) ? planDetail.getCoveragePremiumDetails().parallelStream().map(new Function<GHCoveragePremiumDetail, CoverageBenefitDetailDto>() {
            @Override
            public CoverageBenefitDetailDto apply(GHCoveragePremiumDetail ghCoveragePremiumDetail) {
                CoverageBenefitDetailDto coverageBenefitDetailDto = new CoverageBenefitDetailDto();
                if (isNotEmpty(ghCoveragePremiumDetail.getCoverageId())) {
                    CoverageDto coverageDto = coverageFinder.findCoverageById(ghCoveragePremiumDetail.getCoverageId().getCoverageId());
                    coverageBenefitDetailDto
                            .updateWithCoverageName(coverageDto.getCoverageName())
                            .updateWithCoverageId(coverageDto.getCoverageId())
                            .updateWithCoverageCode(coverageDto.getCoverageCode())
                            .updateWithSumAssured(ghCoveragePremiumDetail.getSumAssured())
                            .updateWithTotalAmountPaid(getTotalAmountPaidTillNow(planDetail, clientId))
                            .updateWithReserveAmount(getReservedAmountOfTheClient(clientId))
                            .updateWithBalanceAndEligibleAmount()
                            .updateWithBenefitDetails(constructBenefitDetails(coverageDto.getBenefitDtos(), coverageBenefitDetailDto, isNotEmpty(ghCoveragePremiumDetail.getBenefitPremiumLimits()) ? ghCoveragePremiumDetail.getBenefitPremiumLimits() : Sets.newHashSet()));
                }
                return coverageBenefitDetailDto;
            }
        }).collect(Collectors.toSet()) : Sets.newHashSet() : Sets.newHashSet();
    }

    private BigDecimal getReservedAmountOfTheClient(String clientId) {
        return BigDecimal.ZERO;
    }

    private BigDecimal getTotalAmountPaidTillNow(GHPlanPremiumDetail planDetail, String clientId) {
        return BigDecimal.ZERO;
    }

    private Set<BenefitDetailDto> constructBenefitDetails(List<Map<String, Object>> benefitDtos, CoverageBenefitDetailDto coverageBenefitDetailDto, Set<BenefitPremiumLimit> benefitPremiumLimits) {
        Set<BenefitDetailDto> benefits = coverageBenefitDetailDto.getBenefitDetails();
        if (isEmpty(benefits))
            benefits = Sets.newHashSet();
        for (BenefitPremiumLimit benefitPremiumLimit : benefitPremiumLimits) {
            for (Map<String, Object> benefit : benefitDtos) {
                String benefitCode = String.valueOf(new BigDecimal(benefitPremiumLimit.getBenefitCode()).intValue());
                if (benefitCode.equals(benefit.get("benefitCode"))) {
                    BenefitDetailDto benefitDetailDto = new BenefitDetailDto();
                    benefitDetailDto.setBenefitName(isNotEmpty(benefit.get("benefitName")) ? benefit.get("benefitName").toString() : StringUtils.EMPTY);
                    benefitDetailDto.setBenefitCode(isNotEmpty(benefit.get("benefitCode")) ? benefit.get("benefitCode").toString() : StringUtils.EMPTY);
                    benefits.add(benefitDetailDto);
                }
            }
        }
        return benefits;
    }

    private ClaimantHCPDetailDto constructClaimantHCPDetailDto(HCPCode hcpCode, String hospitalizationEvent) {
        HCP hcp = hcpFinder.getHCPByHCPCode(hcpCode.getHcpCode());
        if (isNotEmpty(hcp)) {
            ClaimantHCPDetailDto claimantHCPDetailDto = new ClaimantHCPDetailDto();
            claimantHCPDetailDto.updateWithHospitalizationEvent(hospitalizationEvent)
                    .updateWithAddress(hcp.getHcpAddress())
                    .updateWithHCPName(hcp.getHcpName())
                    .updateWithHCPCode(hcp.getHcpCode());
            return claimantHCPDetailDto;
        }
        return ClaimantHCPDetailDto.getInstance();
    }

    private PreAuthorizationRequest getPreAuthorizationRequestById(PreAuthorizationRequestId preAuthorizationRequestId) {
        return preAuthorizationRequestRepository.findByPreAuthorizationRequestId(preAuthorizationRequestId.getPreAuthorizationRequestId());
    }

    public List<GHProposalMandatoryDocumentDto> findMandatoryDocuments(FamilyId familyId, String preAuthorizationId) throws Exception {
        PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestRepository.findByPreAuthorizationRequestId(preAuthorizationId);
        notNull(preAuthorizationRequest, "No PreAuthorization found with the given Id");
        String policyNumber = isNotEmpty(preAuthorizationRequest.getPreAuthorizationRequestPolicyDetail()) ? preAuthorizationRequest.getPreAuthorizationRequestPolicyDetail().getPolicyNumber() : StringUtils.EMPTY;
        GroupHealthPolicy groupHealthPolicy = ghPolicyRepository.findPolicyByPolicyNumber(policyNumber);
        notNull(groupHealthPolicy, "No Policy found for the PreAuthorization with policy number - "+policyNumber);
        List<SearchDocumentDetailDto> documentDetailDtos = Lists.newArrayList();
        GHPlanPremiumDetail planPremiumDetail = getGHPlanPremiumDetailByFamilyId(familyId, groupHealthPolicy);
        SearchDocumentDetailDto searchDocumentDetailDto = new SearchDocumentDetailDto(planPremiumDetail.getPlanId());
        documentDetailDtos.add(searchDocumentDetailDto);
        if (isNotEmpty(planPremiumDetail.getCoveragePremiumDetails())) {
            List<CoverageId> coverageIds = planPremiumDetail.getCoveragePremiumDetails().stream().map(new Function<GHCoveragePremiumDetail, CoverageId>() {
                @Override
                public CoverageId apply(GHCoveragePremiumDetail ghCoveragePremiumDetail) {
                    return ghCoveragePremiumDetail.getCoverageId();
                }
            }).collect(Collectors.toList());
            documentDetailDtos.add(new SearchDocumentDetailDto(planPremiumDetail.getPlanId(), coverageIds));
        }
        Set<ClientDocumentDto> mandatoryDocuments = underWriterAdapter.getMandatoryDocumentsForApproverApproval(documentDetailDtos, ProcessType.CLAIM);
        Set<AdditionalDocument> additionalRequiredDocumentsByUnderwriter = preAuthorizationRequest.getAdditionalRequiredDocumentsByUnderwriter();
        if(isNotEmpty(additionalRequiredDocumentsByUnderwriter)){
            mandatoryDocuments = populateWithAdditionalRequiredDocumentsByUnderwriter(mandatoryDocuments, additionalRequiredDocumentsByUnderwriter);
        }
        List<GHProposalMandatoryDocumentDto> mandatoryDocumentDtos = Lists.newArrayList();
        Set<GHProposerDocument> uploadedDocuments = isNotEmpty(preAuthorizationRequest.getProposerDocuments()) ? preAuthorizationRequest.getProposerDocuments() : Sets.newHashSet();
        if (isNotEmpty(mandatoryDocuments)) {
            mandatoryDocumentDtos = mandatoryDocuments.stream().map(new Function<ClientDocumentDto, GHProposalMandatoryDocumentDto>() {
                @Override
                public GHProposalMandatoryDocumentDto apply(ClientDocumentDto clientDocumentDto) {
                    GHProposalMandatoryDocumentDto mandatoryDocumentDto = new GHProposalMandatoryDocumentDto(clientDocumentDto.getDocumentCode(), clientDocumentDto.getDocumentName());
                    Optional<GHProposerDocument> proposerDocumentOptional = uploadedDocuments.stream().filter(new Predicate<GHProposerDocument>() {
                        @Override
                        public boolean test(GHProposerDocument ghProposerDocument) {
                            return clientDocumentDto.getDocumentCode().equals(ghProposerDocument.getDocumentId());
                        }
                    }).findAny();
                    if (proposerDocumentOptional.isPresent()) {
                        try {
                            if (isNotEmpty(proposerDocumentOptional.get().getGridFsDocId())) {
                                GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(proposerDocumentOptional.get().getGridFsDocId())));
                                mandatoryDocumentDto.setFileName(gridFSDBFile.getFilename());
                                mandatoryDocumentDto.setContentType(gridFSDBFile.getContentType());
                                mandatoryDocumentDto.setGridFsDocId(gridFSDBFile.getId().toString());
                                mandatoryDocumentDto.updateWithContent(IOUtils.toByteArray(gridFSDBFile.getInputStream()));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return mandatoryDocumentDto;
                }
            }).collect(Collectors.toList());
        }
        return mandatoryDocumentDtos;
    }

    private Set<ClientDocumentDto> populateWithAdditionalRequiredDocumentsByUnderwriter(Set<ClientDocumentDto> mandatoryDocuments, Set<AdditionalDocument> additionalRequiredDocumentsByUnderwriter) {
        additionalRequiredDocumentsByUnderwriter.stream().forEach(document -> {
            mandatoryDocuments.add(new ClientDocumentDto(document.getDocumentCode(), document.getDocumentName(), false));
        });
        return mandatoryDocuments;
    }

    private GHPlanPremiumDetail getGHPlanPremiumDetailByFamilyId(FamilyId familyId, GroupHealthPolicy groupHealthPolicy) throws Exception{
        Set<GHInsured> insureds = groupHealthPolicy.getInsureds();
        GHInsured groupHealthInsured = null;
        GHInsuredDependent ghInsuredDependent = null;
        if (isNotEmpty(insureds)) {
            Optional<GHInsured> groupHealthInsuredOptional = insureds.stream().filter(ghInsured -> ghInsured.getFamilyId().equals(familyId)).findFirst();
            if (groupHealthInsuredOptional.isPresent()) {
                groupHealthInsured = groupHealthInsuredOptional.get();
            }
            if (isEmpty(groupHealthInsured)) {
                Optional<GHInsuredDependent> ghInsuredDependentOptional = insureds.stream().flatMap(new Function<GHInsured, Stream<GHInsuredDependent>>() {
                    @Override
                    public Stream<GHInsuredDependent> apply(GHInsured ghInsured) {
                        return ghInsured.getInsuredDependents().stream();
                    }
                }).filter(gHInsuredDependent -> gHInsuredDependent.getFamilyId().equals(familyId)).findFirst();
                if (ghInsuredDependentOptional.isPresent()) {
                    ghInsuredDependent = ghInsuredDependentOptional.get();
                }
            }
        }
        return isNotEmpty(groupHealthInsured) ? groupHealthInsured.getPlanPremiumDetail() : ghInsuredDependent.getPlanPremiumDetail();
    }

    public List<PreAuthorizationClaimantDetailCommand> getPreAuthorizationRequestByCriteria(SearchPreAuthorizationRecordDto searchPreAuthorizationRecordDto) {
        List<PreAuthorizationRequest> preAuthorizationRequests = preAuthorizationFinder.getPreAuthorizationRequestByCriteria(searchPreAuthorizationRecordDto);
        return convertPreAuthorizationListToPreAuthorizationClaimantDetailCommand(preAuthorizationRequests);
    }

    public List<PreAuthorizationClaimantDetailCommand> searchPreAuthorizationForUnderWriterByCriteria(SearchPreAuthorizationRecordDto searchPreAuthorizationRecordDto, String username) {
        List<PreAuthorizationRequest> preAuthorizationRequests = preAuthorizationFinder.searchPreAuthorizationRecord(searchPreAuthorizationRecordDto, username);
        return convertPreAuthorizationListToPreAuthorizationClaimantDetailCommand(preAuthorizationRequests);
    }

    private List<PreAuthorizationClaimantDetailCommand> convertPreAuthorizationListToPreAuthorizationClaimantDetailCommand(List<PreAuthorizationRequest> preAuthorizationRequests) {
        return isNotEmpty(preAuthorizationRequests) ? preAuthorizationRequests.parallelStream().map(new Function<PreAuthorizationRequest, PreAuthorizationClaimantDetailCommand>() {
            @Override
            public PreAuthorizationClaimantDetailCommand apply(PreAuthorizationRequest preAuthorizationRequest) {
                return new PreAuthorizationClaimantDetailCommand()
                        .updateWithStatus(preAuthorizationRequest.getStatus())
                        .updateWithBatchNumber(preAuthorizationRequest.getBatchNumber())
                        .updateWithPreAuthorizationRequestId(preAuthorizationRequest.getPreAuthorizationRequestId())
                        .updateWithClaimType(preAuthorizationRequest.getClaimType())
                        .updateWithClaimIntimationDate(preAuthorizationRequest.getClaimIntimationDate())
                        .updateWithPolicy(preAuthorizationRequest.getPreAuthorizationRequestPolicyDetail())
                        .updateWithHcp(preAuthorizationRequest.getPreAuthorizationRequestHCPDetail());
            }
        }).collect(Collectors.toList()) : Lists.newArrayList();
    }

    private PreAuthorizationClaimantDetailCommand constructPreAuthorizationClaimantDetailCommand(PreAuthorizationRequest preAuthorizationRequest) {
        PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand = new PreAuthorizationClaimantDetailCommand();
        if (isNotEmpty(preAuthorizationRequest)) {
            preAuthorizationClaimantDetailCommand
                    .updateWithPreAuthorizationRequestId(preAuthorizationRequest.getPreAuthorizationRequestId())
                    .updateWithPreAuthorizationId(preAuthorizationRequest.getPreAuthorizationId())
                    .updateWithStatus(preAuthorizationRequest.getStatus())
                    .updateWithBatchNumber(preAuthorizationRequest.getBatchNumber())
                    .updateWithClaimType(preAuthorizationRequest.getClaimType())
                    .updateWithClaimIntimationDate(preAuthorizationRequest.getClaimIntimationDate())
                    .updateWithPreAuthorizationDate(preAuthorizationRequest.getPreAuthorizationDate())
                    .updateWithClaimantHCPDetailDto(constructClaimantHCPDetailDtoFromPreAuthorizationRequestHCPDetail(preAuthorizationRequest.getPreAuthorizationRequestHCPDetail()))
                    .updateWithDiagnosisTreatment(constructDiagnosisTreatmentDtoListFromPreAuthorizationRequest(preAuthorizationRequest.getPreAuthorizationRequestDiagnosisTreatmentDetails()))
                    .updateWithIllnessDetails(constructIllnessDetailDtoFromPreAuthorizationRequest(preAuthorizationRequest.getPreAuthorizationRequestIllnessDetail()))
                    .updateWithDrugServices(constructDrugServiceDtoFromPreAuthorizationRequest(preAuthorizationRequest.getPreAuthorizationRequestDrugServices()))
                    .updateWithClaimantPolicyDetailDto(constructClaimantPolicyDetailDtoFromPreAuthorizationRequest(preAuthorizationRequest.getPreAuthorizationRequestPolicyDetail(), preAuthorizationRequest.getRelationship(), preAuthorizationRequest.getCategory(), preAuthorizationRequest.getGhProposer()))
                    .updateWithSubmittedFlag(preAuthorizationRequest.isSubmitted())
                    .updateWithProcessorUserId(preAuthorizationRequest.getPreAuthorizationProcessorUserId())
                    .updateWithComments(preAuthorizationRequest.getCommentDetails());
        }
        return preAuthorizationClaimantDetailCommand;
    }

    private ClaimantPolicyDetailDto constructClaimantPolicyDetailDtoFromPreAuthorizationRequest(PreAuthorizationRequestPolicyDetail preAuthorizationRequestPolicyDetail, String relationship, String category, GHProposer ghProposer) {
        ClaimantPolicyDetailDto claimantPolicyDetailDto = null;
        if (isNotEmpty(preAuthorizationRequestPolicyDetail)) {
            claimantPolicyDetailDto = new ClaimantPolicyDetailDto()
                    .updateWithPolicyNumber(preAuthorizationRequestPolicyDetail.getPolicyNumber())
                    .updateWithPolicyName(preAuthorizationRequestPolicyDetail.getPolicyName())
                    .updateWithPlanCode(preAuthorizationRequestPolicyDetail.getPlanCode())
                    .updateWithPlanId(preAuthorizationRequestPolicyDetail.getPlanId())
                    .updateWithPlanName(preAuthorizationRequestPolicyDetail.getPlanName())
                    .updateWithSumAssured(preAuthorizationRequestPolicyDetail.getSumAssured())
                    .updateWithRelationship(Relationship.valueOf(relationship))
                    .updateWithCategory(category)
                    .updateWithAssuredDetail(constructAssuredDetailFromPreAuthorizationRequestAssuredDetail(preAuthorizationRequestPolicyDetail.getAssuredDetail()))
                    .updateWithDependentAssuredDetail(constructDependentAssuredDetailFromPreAuthorizationRequestAssuredDetail(preAuthorizationRequestPolicyDetail.getAssuredDetail()))
                    .updateWithCoverageDetails(constructCoverageListFromPreAuthorizationRequestAssuredDetail(claimantPolicyDetailDto, preAuthorizationRequestPolicyDetail.getCoverageDetailDtoList()))
                    .updateWithProposerDetail(constructProposerDetailsFromPreAuthorizationRequestAssuredDetail(ghProposer));
        }
        return claimantPolicyDetailDto;
    }

    private PreAuthorizationClaimantProposerDetail constructProposerDetailsFromPreAuthorizationRequestAssuredDetail(GHProposer proposer) {
        PreAuthorizationClaimantProposerDetail preAuthorizationClaimantProposerDetail = null;
        if (isNotEmpty(proposer)) {
            preAuthorizationClaimantProposerDetail = new PreAuthorizationClaimantProposerDetail();
            preAuthorizationClaimantProposerDetail.updateWithProposerDetails(proposer);
        }
        return preAuthorizationClaimantProposerDetail;
    }

    private Set<CoverageBenefitDetailDto> constructCoverageListFromPreAuthorizationRequestAssuredDetail(ClaimantPolicyDetailDto claimantPolicyDetailDto, Set<PreAuthorizationRequestCoverageDetail> coverageDetailDtoList) {
        return isNotEmpty(coverageDetailDtoList) ? coverageDetailDtoList.parallelStream().map(new Function<PreAuthorizationRequestCoverageDetail, CoverageBenefitDetailDto>() {
            @Override
            public CoverageBenefitDetailDto apply(PreAuthorizationRequestCoverageDetail preAuthorizationRequestCoverageDetail) {
                CoverageBenefitDetailDto coverageBenefitDetailDto = new CoverageBenefitDetailDto()
                        .updateWithCoverageName(preAuthorizationRequestCoverageDetail.getCoverageName())
                        .updateWithCoverageCode(preAuthorizationRequestCoverageDetail.getCoverageCode())
                        .updateWithCoverageId(preAuthorizationRequestCoverageDetail.getCoverageId())
                        .updateWithSumAssured(preAuthorizationRequestCoverageDetail.getSumAssured())
                        .updateWithTotalAmountPaid(preAuthorizationRequestCoverageDetail.getTotalAmountPaid())
                        .updateWithBalanceAmount(preAuthorizationRequestCoverageDetail.getBalanceAmount())
                        .updateWithReserveAmount(preAuthorizationRequestCoverageDetail.getReserveAmount())
                        .updateWithEligibleAmount(preAuthorizationRequestCoverageDetail.getEligibleAmount())
                        .updateWithApprovedAmount(preAuthorizationRequestCoverageDetail.getApprovedAmount());
                Set<BenefitDetailDto> benefitDetailDtos = isNotEmpty(preAuthorizationRequestCoverageDetail.getBenefitDetails()) ? preAuthorizationRequestCoverageDetail.getBenefitDetails().stream().map(benefit -> {
                    BenefitDetailDto benefitDetailDto = new BenefitDetailDto(benefit.getBenefitName(), benefit.getBenefitId(), benefit.getProbableClaimAmount());
                    return benefitDetailDto;
                }).collect(Collectors.toSet()) : Sets.newHashSet();
                coverageBenefitDetailDto.updateWithBenefitDetails(benefitDetailDtos);
                return coverageBenefitDetailDto;
            }
        }).collect(Collectors.toSet()) : Sets.newHashSet();
    }

    private DependentAssuredDetail constructDependentAssuredDetailFromPreAuthorizationRequestAssuredDetail(PreAuthorizationRequestAssuredDetail assuredDetail) {
        DependentAssuredDetail dependentAssuredDetail = null;
        if (assuredDetail.isDependentAssuredDetailPresent()) {
            try {
                dependentAssuredDetail = new DependentAssuredDetail();
                BeanUtils.copyProperties(dependentAssuredDetail, assuredDetail);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return dependentAssuredDetail;
    }

    private AssuredDetail constructAssuredDetailFromPreAuthorizationRequestAssuredDetail(PreAuthorizationRequestAssuredDetail preAuthorizationRequestAssuredDetail) {
        AssuredDetail assuredDetail = null;
        if (!preAuthorizationRequestAssuredDetail.isDependentAssuredDetailPresent()) {
            try {
                assuredDetail = new AssuredDetail();
                BeanUtils.copyProperties(assuredDetail, preAuthorizationRequestAssuredDetail);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return assuredDetail;
    }

    private List<DrugServiceDto> constructDrugServiceDtoFromPreAuthorizationRequest(Set<PreAuthorizationRequestDrugService> preAuthorizationRequestDrugServices) {
        return isNotEmpty(preAuthorizationRequestDrugServices) ? preAuthorizationRequestDrugServices.parallelStream().map(new Function<PreAuthorizationRequestDrugService, DrugServiceDto>() {
            @Override
            public DrugServiceDto apply(PreAuthorizationRequestDrugService preAuthorizationRequestDrugService) {
                DrugServiceDto drugServiceDto = new DrugServiceDto();
                try {
                    BeanUtils.copyProperties(drugServiceDto, preAuthorizationRequestDrugService);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return drugServiceDto;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList()) : Lists.newArrayList();
    }

    private IllnessDetailDto constructIllnessDetailDtoFromPreAuthorizationRequest(PreAuthorizationRequestIllnessDetail preAuthorizationRequestIllnessDetail) {
        IllnessDetailDto illnessDetailDto = null;
        if (isNotEmpty(preAuthorizationRequestIllnessDetail)) {
            illnessDetailDto = new IllnessDetailDto();
            try {
                BeanUtils.copyProperties(illnessDetailDto, preAuthorizationRequestIllnessDetail);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return illnessDetailDto;
    }

    private List<DiagnosisTreatmentDto> constructDiagnosisTreatmentDtoListFromPreAuthorizationRequest(Set<PreAuthorizationRequestDiagnosisTreatmentDetail> preAuthorizationRequestDiagnosisTreatmentDetails) {
        return isNotEmpty(preAuthorizationRequestDiagnosisTreatmentDetails) ? preAuthorizationRequestDiagnosisTreatmentDetails.stream().map(new Function<PreAuthorizationRequestDiagnosisTreatmentDetail, DiagnosisTreatmentDto>() {
            @Override
            public DiagnosisTreatmentDto apply(PreAuthorizationRequestDiagnosisTreatmentDetail preAuthorizationRequestDiagnosisTreatmentDetail) {
                DiagnosisTreatmentDto diagnosisTreatmentDto = new DiagnosisTreatmentDto();
                try {
                    BeanUtils.copyProperties(diagnosisTreatmentDto, preAuthorizationRequestDiagnosisTreatmentDetail);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return diagnosisTreatmentDto;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList()) : Lists.newArrayList();
    }

    private ClaimantHCPDetailDto constructClaimantHCPDetailDtoFromPreAuthorizationRequestHCPDetail(PreAuthorizationRequestHCPDetail preAuthorizationRequestHCPDetail) {
        ClaimantHCPDetailDto claimantHCPDetailDto = null;
        if (isNotEmpty(preAuthorizationRequestHCPDetail)) {
            claimantHCPDetailDto = new ClaimantHCPDetailDto();
            try {
                BeanUtils.copyProperties(claimantHCPDetailDto, preAuthorizationRequestHCPDetail);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return claimantHCPDetailDto;
    }

    public List<PreAuthorizationClaimantDetailCommand> getPreAuthorizationForDefaultList(String preAuthorizationProcessorUserId) {
        List<PreAuthorizationClaimantDetailCommand> result = Lists.newArrayList();
        PageRequest pageRequest = new PageRequest(0, 300, new Sort(new Order(Direction.DESC, "createdOn")));
        Page<PreAuthorizationRequest> pages = preAuthorizationRequestRepository.findAllByPreAuthorizationProcessorUserIdInAndStatusIn(Lists.newArrayList(preAuthorizationProcessorUserId, null), Lists.newArrayList(PreAuthorizationRequest.Status.INTIMATION, PreAuthorizationRequest.Status.EVALUATION, PreAuthorizationRequest.Status.RETURNED), pageRequest);
        if (isNotEmpty(pages) && isNotEmpty(pages.getContent()))
            result = convertPreAuthorizationListToPreAuthorizationClaimantDetailCommand(pages.getContent());
        return result;
    }

    public boolean doesClientBelongToTheGivenPolicy(String clientId, String policyNumber) {
        GroupHealthPolicy groupHealthPolicy = ghPolicyRepository.findPolicyByPolicyNumber(policyNumber);
        if (isEmpty(groupHealthPolicy))
            return Boolean.FALSE;
        return isPresentClientIdUnderPolicy(new FamilyId(clientId), groupHealthPolicy);
    }

    private Boolean isPresentClientIdUnderPolicy(FamilyId familyId, GroupHealthPolicy groupHealthPolicy) {
        Set<GHInsured> insureds = groupHealthPolicy.getInsureds();
        if (isNotEmpty(insureds)) {
            Optional<GHInsured> groupHealthInsuredOptional = insureds.stream().filter(ghInsured -> ghInsured.getFamilyId().equals(familyId)).findFirst();
            if (groupHealthInsuredOptional.isPresent()) {
                return true;
            }
            Optional<GHInsuredDependent> ghInsuredDependentOptional = insureds.stream().flatMap(new Function<GHInsured, Stream<GHInsuredDependent>>() {
                @Override
                public Stream<GHInsuredDependent> apply(GHInsured ghInsured) {
                    return ghInsured.getInsuredDependents().stream();
                }
            }).filter(gHInsuredDependent -> gHInsuredDependent.getFamilyId().equals(familyId)).findFirst();
            if (ghInsuredDependentOptional.isPresent()) {
                return true;
            }
        }
        return false;
    }

    public String checkServiceAndDrugCoveredUnderThePolicy(String clientId, String policyNumber, String service) {
        GroupHealthPolicy groupHealthPolicy = ghPolicyRepository.findPolicyByPolicyNumber(policyNumber);
        String errorMessage = "";
        if (isNotEmpty(groupHealthPolicy)) {
            GHPlanPremiumDetail ghPlanPremiumDetail = null;
            try {
                ghPlanPremiumDetail = getGHPlanPremiumDetailByFamilyId(new FamilyId(clientId), groupHealthPolicy);
            } catch (Exception e) {
                return  "Client is not associated to the Policy - "+groupHealthPolicy.getPolicyNumber().getPolicyNumber()+".\n";
            }
            if(isEmpty(ghPlanPremiumDetail)){
                return  "Client is not associated to the Policy - "+groupHealthPolicy.getPolicyNumber().getPolicyNumber()+".\n";
            }
            List<CoverageDto> coverages = getListOfCoverageAndBenefitsOfThePolicyAndAssociatedPlan(ghPlanPremiumDetail);
            if (isEmpty(coverages)) {
                return "No coverage details found for the policy - "+groupHealthPolicy.getPolicyNumber()+".\n";
            }
            for (CoverageDto coverageDto : coverages) {
                CoverageId  coverageId = coverageDto.getCoverageIdObj();
                List<String> benefitCodes = coverageDto.getBenefitCodes();
                if (isEmpty(benefitCodes))
                    errorMessage = errorMessage + "No Benefit Details found for the coverage - "+ coverageDto.getCoverageName() + " of Policy - "+groupHealthPolicy.getSchemeName()+".\n";
                for (String benefitCode : benefitCodes) {
                    try {
                        benefitCode = isNotEmpty(benefitCode) ? String.valueOf( new BigDecimal(benefitCode).intValue()) : benefitCode;
                    } catch(NumberFormatException e){}
                    List<ServiceBenefitCoverageMapping> serviceBenefitCoverageMappings = sbcmRepository.findAllByCoverageIdAndBenefitCodeAndService(coverageId, benefitCode, service);
                    if (isEmpty(serviceBenefitCoverageMappings))
                        errorMessage = errorMessage + "No Service-Coverage-Benefit Mapping found for service - "+service+" ,Benefit - "+benefitCode+" ,Coverage - "+coverageDto.getCoverageName()+".\n";
                }
            }
        }
        return errorMessage;
    }

    private List<CoverageDto> getListOfCoverageAndBenefitsOfThePolicyAndAssociatedPlan(GHPlanPremiumDetail ghPlanPremiumDetail) {
        String planCode = ghPlanPremiumDetail.getPlanCode();
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        List<CoverageDto> coverageDtos = Lists.newArrayList();
        if(isNotEmpty(plans)) {
            Plan plan = plans.get(0);
            coverageDtos = plan.getCoverages().stream().filter(coverage -> coverage.getCoverageType().equals(CoverageType.BASE)).map(new Function<PlanCoverage, CoverageDto>() {
                @Override
                public CoverageDto apply(PlanCoverage planCoverage) {
                    CoverageDto coverageDto = new CoverageDto();
                    coverageDto.setCoverageIdObj(planCoverage.getCoverageId());
                    Set<PlanCoverageBenefit> coverageBenefits = planCoverage.getPlanCoverageBenefits();
                    if (isNotEmpty(coverageBenefits)) {
                        coverageDto.setCoverageName(coverageBenefits.iterator().next().getCoverageName());
                        List<String> benefitIds = coverageBenefits.stream().map(benefit -> {
                            return benefit.getBenefitId().getBenefitId();
                        }).collect(Collectors.toList());
                        if (isNotEmpty(benefitIds)) {
                            List<String> benefitCodes = benefitFinder.getAllBenefitCodesByBenefitIds(benefitIds);
                            coverageDto.setBenefitCodes(benefitCodes);
                        }
                    }
                    return coverageDto;
                }
            }).collect(Collectors.toList());
        }
        List<GHCoveragePremiumDetail> coveragePremiumDetails = ghPlanPremiumDetail.getCoveragePremiumDetails();
        if(isNotEmpty(coveragePremiumDetails)){
            List<CoverageDto> coverageDtoList = coveragePremiumDetails.stream().map(new Function<GHCoveragePremiumDetail, CoverageDto>() {
                @Override
                public CoverageDto apply(GHCoveragePremiumDetail ghCoveragePremiumDetail) {
                    CoverageDto coverageDto = new CoverageDto();
                    coverageDto.setCoverageIdObj(ghCoveragePremiumDetail.getCoverageId());
                    if (isNotEmpty(ghCoveragePremiumDetail.getCoverageId())) {
                        CoverageDto coverage = coverageFinder.findCoverageById(ghCoveragePremiumDetail.getCoverageId().getCoverageId());
                        coverageDto.setCoverageName(coverage.getCoverageName());
                    }
                    Set<BenefitPremiumLimit> benefitPremiumLimits = ghCoveragePremiumDetail.getBenefitPremiumLimits();
                    if (isNotEmpty(benefitPremiumLimits)) {
                        List<String> benefitCodes = benefitPremiumLimits.stream().map(BenefitPremiumLimit::getBenefitCode).collect(Collectors.toList());
                        coverageDto.setBenefitCodes(benefitCodes);
                    }
                    return coverageDto;
                }
            }).collect(Collectors.toList());
            coverageDtos.addAll(coverageDtoList);
        }
        return coverageDtos;
    }

    public String compareHcpRateByHcpService(String hcpCode, String service){
        String errorMessage="";
        HCPRate hcpRate = hcpRateRepository.findHCPRateByHCPCodeAndService(new HCPCode(hcpCode), service);
        if(isEmpty(hcpRate)) {
            return "No HCP Rate is defined for the service - " + service+".\n";
        }
        return errorMessage;
    }

    public Set<GHProposalMandatoryDocumentDto> findAdditionalDocuments(String preAuthorizationId) {
        PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestRepository.findOne(preAuthorizationId);
        if(isEmpty(preAuthorizationRequest)){
            return Sets.newHashSet();
        }
        Set<GHProposerDocument> uploadedDocuments = isNotEmpty(preAuthorizationRequest.getProposerDocuments()) ? preAuthorizationRequest.getProposerDocuments() : Sets.newHashSet();
        Set<GHProposalMandatoryDocumentDto> mandatoryDocumentDtos = Sets.newHashSet();
        if (isNotEmpty(uploadedDocuments)) {
            mandatoryDocumentDtos = uploadedDocuments.stream().filter(uploadedDocument -> !uploadedDocument.isMandatory()).map(new Function<GHProposerDocument, GHProposalMandatoryDocumentDto>() {
                @Override
                public GHProposalMandatoryDocumentDto apply(GHProposerDocument ghProposerDocument) {
                    GHProposalMandatoryDocumentDto mandatoryDocumentDto = new GHProposalMandatoryDocumentDto(ghProposerDocument.getDocumentId(), ghProposerDocument.getDocumentName());
                    GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(ghProposerDocument.getGridFsDocId())));
                    mandatoryDocumentDto.setFileName(gridFSDBFile.getFilename());
                    mandatoryDocumentDto.setContentType(gridFSDBFile.getContentType());
                    mandatoryDocumentDto.setGridFsDocId(gridFSDBFile.getId().toString());
                    try {
                        mandatoryDocumentDto.updateWithContent(IOUtils.toByteArray(gridFSDBFile.getInputStream()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return mandatoryDocumentDto;
                }
            }).collect(Collectors.toSet());
        }
        return mandatoryDocumentDtos;
    }

    public List<PreAuthorizationClaimantDetailCommand> getDefaultListOfPreAuthorizationAssignedToUnderwriter(String username) {
        List<PreAuthorizationClaimantDetailCommand> result = Lists.newArrayList();
        List<PreAuthorizationRequest> preAuthorizationRequests = preAuthorizationRequestRepository.findAllByPreAuthorizationUnderWriterUserIdAndStatus(username, PreAuthorizationRequest.Status.UNDERWRITING_LEVEL1);
        if (isNotEmpty(preAuthorizationRequests))
            result = convertPreAuthorizationListToPreAuthorizationClaimantDetailCommand(preAuthorizationRequests);
        return result;
    }

    public RoutingLevel getRoutingLevelForPreAuthorization(PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand) throws RoutingLevelNotFoundException {
        UnderWriterRoutingLevelDetailDto routingLevelDetailDto = getUnderWriterRoutingLevelDetailDto(preAuthorizationClaimantDetailCommand);
        RoutingLevel routingLevel = underWriterAdapter.getRoutingLevelWithoutCoverageDetails(routingLevelDetailDto);
        if(isEmpty(routingLevel))
            throw new RoutingLevelNotFoundException(getRoutingLevelExceptionMessage(preAuthorizationClaimantDetailCommand));
        return routingLevel;
    }


    private UnderWriterRoutingLevelDetailDto getUnderWriterRoutingLevelDetailDto(PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand) {
        BigDecimal claimAmount = preAuthorizationClaimantDetailCommand.getSumOfAllProbableClaimAmount();
        int age = preAuthorizationClaimantDetailCommand.getAgeOfTheClient();
        List<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem> underWriterInfluencingFactorItems = new ArrayList<>();
        UnderWriterRoutingLevelDetailDto routingLevelDetailDto = new UnderWriterRoutingLevelDetailDto(new PlanId(preAuthorizationClaimantDetailCommand.getClaimantPolicyDetailDto().getPlanId()), LocalDate.now(), ProcessType.CLAIM.name());
        underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.CLAIM_AMOUNT.name(), claimAmount.toPlainString()));
        underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.AGE.name(), String.valueOf(age)));
        routingLevelDetailDto.setUnderWriterInfluencingFactor(underWriterInfluencingFactorItems);
        return routingLevelDetailDto;
    }

    public String getRoutingLevelExceptionMessage(PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand) {
        BigDecimal claimAmount = preAuthorizationClaimantDetailCommand.getSumOfAllProbableClaimAmount();
        int age = preAuthorizationClaimantDetailCommand.getAgeOfTheClient();
        return "No routing rule found for PreAuthorization : "+preAuthorizationClaimantDetailCommand.getPreAuthorizationRequestId()+" having age : "+age+" and Claim Amount : "+claimAmount;

    }

    public String getLoggedInUsername() {
        String userName = StringUtils.EMPTY;
        Authentication authentication = authenticationFacade.getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            userName = authentication.getName();
        }
        return userName;
    }

    public UserDetails getUserDetailFromAuthentication() {
        Authentication authentication = authenticationFacade.getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            return (UserDetails) authentication.getPrincipal();
        }
        return null;
    }

    public List<PreAuthorizationClaimantDetailCommand> getDefaultListByUnderwriterLevel(String level, String username){
        List<PreAuthorizationRequest> preAuthorizationRequests =Lists.newArrayList();
        List<PreAuthorizationClaimantDetailCommand> result = Lists.newArrayList();
        if(level.equalsIgnoreCase("levelone"))
            preAuthorizationRequests = preAuthorizationRequestRepository.findAllByStatusAndPreAuthorizationUnderWriterUserIdIn(PreAuthorizationRequest.Status.UNDERWRITING_LEVEL1, Lists.newArrayList(username,null));
        if(level.equalsIgnoreCase("leveltwo"))
            preAuthorizationRequests = preAuthorizationRequestRepository.findAllByStatusAndPreAuthorizationUnderWriterUserIdIn(PreAuthorizationRequest.Status.UNDERWRITING_LEVEL2, Lists.newArrayList(username,null));
        if (isNotEmpty(preAuthorizationRequests))
            result = convertPreAuthorizationListToPreAuthorizationClaimantDetailCommand(preAuthorizationRequests);
        return result;
    }

    public void populatePreAuthorizationWithPreAuthorizationUnderWriterUserId(String preAuthorizationId, String userName) throws PreAuthorizationInProcessingException {
        PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestRepository.findOne(preAuthorizationId);
        if(isNotEmpty(preAuthorizationRequest)){
            if(isNotEmpty(preAuthorizationRequest.getPreAuthorizationUnderWriterUserId()) && !preAuthorizationRequest.getPreAuthorizationUnderWriterUserId().equals(userName)){
                throw new PreAuthorizationInProcessingException("The record is already under processing.");
            }
        }
        preAuthorizationRequest.updateWithPreAuthorizationUnderWriterUserId(userName);
        preAuthorizationRequestRepository.save(preAuthorizationRequest);
    }
}
