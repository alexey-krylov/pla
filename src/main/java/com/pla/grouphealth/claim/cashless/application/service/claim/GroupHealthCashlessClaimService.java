package com.pla.grouphealth.claim.cashless.application.service.claim;

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
import com.pla.core.hcp.domain.model.HCPServiceDetail;
import com.pla.core.hcp.query.HCPFinder;
import com.pla.core.hcp.repository.HCPRateRepository;
import com.pla.core.query.BenefitFinder;
import com.pla.core.query.CoverageFinder;
import com.pla.core.repository.PlanRepository;
import com.pla.grouphealth.claim.cashless.application.service.preauthorization.PreAuthorizationRequestService;
import com.pla.grouphealth.claim.cashless.domain.exception.RoutingLevelNotFoundException;
import com.pla.grouphealth.claim.cashless.domain.model.claim.*;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationRequest;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationRequestBenefitDetail;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationRequestCoverageDetail;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationRequestPolicyDetail;
import com.pla.grouphealth.claim.cashless.domain.model.sharedmodel.AdditionalDocument;
import com.pla.grouphealth.claim.cashless.presentation.dto.claim.GroupHealthCashlessClaimDrugServiceDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.claim.GroupHealthCashlessClaimDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.claim.GroupHealthCashlessClaimPolicyDetailDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.claim.SearchGroupHealthCashlessClaimRecordDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.ClaimUploadedExcelDataDto;
import com.pla.grouphealth.claim.cashless.query.GHCashlessClaimFinder;
import com.pla.grouphealth.claim.cashless.repository.claim.GroupHealthCashlessClaimRepository;
import com.pla.grouphealth.claim.cashless.repository.preauthorization.PreAuthorizationRequestRepository;
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
import com.pla.sharedkernel.util.ExcelUtilityProvider;
import com.pla.sharedkernel.util.SequenceGenerator;
import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.DateTime;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaim.Status;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

/**
 * Author - Mohan Sharma Created on 2/03/2016.
 */
@DomainService
public class GroupHealthCashlessClaimService {

    @Autowired
    private GroupHealthCashlessClaimRepository groupHealthCashlessClaimRepository;
    @Autowired
    private PreAuthorizationRequestRepository preAuthorizationRequestRepository;
    @Autowired
    private ExcelUtilityProvider excelUtilityProvider;
    @Autowired
    SequenceGenerator sequenceGenerator;
    @Autowired
    private GHPolicyRepository ghPolicyRepository;
    @Autowired
    private HCPFinder hcpFinder;
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private CoverageFinder coverageFinder;
    @Autowired
    private SBCMRepository sbcmRepository;
    @Autowired
    private HCPRateRepository hcpRateRepository;
    @Autowired
    BenefitFinder benefitFinder;
    @Autowired
    PreAuthorizationRequestService preAuthorizationRequestService;
    @Autowired
    private IUnderWriterAdapter underWriterAdapter;
    @Autowired
    private GHCashlessClaimFinder GHCashlessClaimFinder;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    @Qualifier("authenticationFacade")
    IAuthenticationFacade authenticationFacade;
    public boolean isValidInsuredTemplate(HSSFWorkbook insuredTemplateWorkbook, Map dataMap) {
        return excelUtilityProvider.isValidInsuredExcel(insuredTemplateWorkbook, GHCashlessClaimExcelHeader.getAllowedHeaders(), GHCashlessClaimExcelHeader.class, dataMap);
    }

    public GroupHealthCashlessClaim constructGroupHealthCashlessClaimEntity(List<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtos, DateTime batchDate, String batchUploaderUserId, String hcpCode, String batchNumber) {
        notEmpty(claimUploadedExcelDataDtos, "No uploaded details found.");
        ClaimUploadedExcelDataDto claimUploadedExcelDataDto = claimUploadedExcelDataDtos.iterator().next();
        notNull(claimUploadedExcelDataDto, "No uploaded details found.");
        GroupHealthCashlessClaim groupHealthCashlessClaim = new GroupHealthCashlessClaim(Status.INTIMATION);
        groupHealthCashlessClaim
                .updateWithGroupHealthCashlessClaimId(constructGroupHealthCashlessClaimId(claimUploadedExcelDataDto))
                .updateWithCreationDate(batchDate)
                .updateWithBatchNumber(batchNumber)
                .updateWithBatchUploaderUserId(batchUploaderUserId)
                .updateWithClaimType("Cashless")
                .updateWithPreAuthorizationDetails(constructPreAuthorizationDetails(claimUploadedExcelDataDto.policyNumber, claimUploadedExcelDataDto.clientId))
                .updateWithProposerDetails(constructGroupHealthProposer(claimUploadedExcelDataDto))
                .updateWithGroupHealthCashlessClaimHCPDetail(constructHCPDetails(hcpCode, claimUploadedExcelDataDto.getHospitalizationEvent()))
                .updateWithGroupHealthCashlessClaimDiagnosisTreatmentDetails(constructGroupHealthCashlessClaimDiagnosisTreatmentDetails(claimUploadedExcelDataDtos))
                .updateWithGroupHealthCashlessClaimIllnessDetail(constructGroupHealthCashlessClaimIllnessDetail(claimUploadedExcelDataDto))
                .updateWithGroupHealthCashlessClaimDrugServices(constructGroupHealthCashlessClaimDrugServices(claimUploadedExcelDataDtos))
                .updateWithGroupHealthCashlessClaimPolicyDetail(constructGroupHealthCashlessClaimPolicyDetail(claimUploadedExcelDataDtos, claimUploadedExcelDataDto.policyNumber, claimUploadedExcelDataDto.clientId, hcpCode,groupHealthCashlessClaim));
        return groupHealthCashlessClaim;
    }

    public Set<PreAuthorizationDetailTaggedToClaim> constructPreAuthorizationDetails(String policyNumber, String clientId) {
        List<PreAuthorizationRequest> preAuthorizationRequests = preAuthorizationRequestRepository.findAllByPreAuthorizationRequestPolicyDetailPolicyNumberAndPreAuthorizationRequestPolicyDetailAssuredDetailClientIdAndStatus(policyNumber, clientId, PreAuthorizationRequest.Status.APPROVED);
        return constructPreAuthorizationDetail(preAuthorizationRequests);
    }

    private Set<PreAuthorizationDetailTaggedToClaim> constructPreAuthorizationDetail(List<PreAuthorizationRequest> preAuthorizationRequests) {
        return isNotEmpty(preAuthorizationRequests) ? preAuthorizationRequests.parallelStream().map(preAuthorizationRequest ->
                new PreAuthorizationDetailTaggedToClaim()
                        .updateWithPreAuthorizationId(preAuthorizationRequest.getPreAuthorizationRequestId())
                        .updateWithPreAuthorizationDate(preAuthorizationRequest.getPreAuthorizationDate())
                        .updateWithPolicyRelatedDetails(preAuthorizationRequest.getPreAuthorizationRequestPolicyDetail())
                        .updateWithCoverageDetails(preAuthorizationRequest.getPreAuthorizationRequestPolicyDetail())
                        .updateWithAssuredDetails(preAuthorizationRequest.getPreAuthorizationRequestPolicyDetail())).collect(Collectors.toSet()) : Sets.newHashSet();
    }

    private GroupHealthCashlessClaimPolicyDetail constructGroupHealthCashlessClaimPolicyDetail(List<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtos, String policyNumber, String clientId, String hcpCode, GroupHealthCashlessClaim groupHealthCashlessClaim) {
        GroupHealthPolicy groupHealthPolicy = ghPolicyRepository.findPolicyByPolicyNumber(policyNumber);
        GroupHealthCashlessClaimPolicyDetail groupHealthCashlessClaimPolicyDetail = new GroupHealthCashlessClaimPolicyDetail();
        if (isNotEmpty(groupHealthPolicy)) {
            groupHealthCashlessClaimPolicyDetail
                    .updateWithPolicyNumber(groupHealthPolicy.getPolicyNumber())
                    .updateWithPolicyName(groupHealthPolicy.getSchemeName());
            groupHealthCashlessClaimPolicyDetail = updateWithPlanDetails(groupHealthPolicy, groupHealthCashlessClaimPolicyDetail, clientId, claimUploadedExcelDataDtos, hcpCode, groupHealthCashlessClaim);
        }
        return groupHealthCashlessClaimPolicyDetail;
    }

    private GroupHealthCashlessClaimPolicyDetail updateWithPlanDetails(GroupHealthPolicy groupHealthPolicy, GroupHealthCashlessClaimPolicyDetail groupHealthCashlessClaimPolicyDetail, String clientId, List<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtos, String hcpCode, GroupHealthCashlessClaim groupHealthCashlessClaim) {
        Set<GHInsured> insureds = groupHealthPolicy.getInsureds();
        GHInsured groupHealthInsured = null;
        GHInsuredDependent ghInsuredDependent = null;
        if (isNotEmpty(insureds)) {
            Optional<GHInsured> groupHealthInsuredOptional = insureds.stream().filter(ghInsured -> ghInsured.getFamilyId().getFamilyId().equalsIgnoreCase(clientId)).findFirst();
            if (groupHealthInsuredOptional.isPresent()) {
                groupHealthInsured = groupHealthInsuredOptional.get();
                groupHealthCashlessClaim
                        .updateWithCategory(groupHealthInsured.getCategory())
                        .updateWithRelationship(Relationship.SELF);
                groupHealthCashlessClaimPolicyDetail.updateWithAssuredDetails(groupHealthInsured, clientId);
            }
            if (isEmpty(groupHealthInsured)) {
                Optional<GHInsuredDependent> ghInsuredDependentOptional = insureds.stream().flatMap(ghInsured -> ghInsured.getInsuredDependents().stream()).filter(gHInsuredDependent -> gHInsuredDependent.getFamilyId().getFamilyId().equalsIgnoreCase(clientId)).findFirst();
                if (ghInsuredDependentOptional.isPresent()) {
                    ghInsuredDependent = ghInsuredDependentOptional.get();
                    final GHInsuredDependent finalGhInsuredDependent = ghInsuredDependent;
                    groupHealthInsured = insureds.parallelStream().filter(ghInsured -> ghInsured.getInsuredDependents().stream().filter(gHInsuredDependent -> gHInsuredDependent.getFamilyId().getFamilyId().equalsIgnoreCase(finalGhInsuredDependent.getFamilyId().getFamilyId())).findFirst().isPresent()).findFirst().get();
                    groupHealthCashlessClaim
                            .updateWithCategory(ghInsuredDependent.getCategory())
                            .updateWithRelationship(ghInsuredDependent.getRelationship());
                    groupHealthCashlessClaimPolicyDetail.updateWithDependentAssuredDetail(ghInsuredDependent, groupHealthInsured, clientId);
                }
            }
        }
        GHPlanPremiumDetail planDetail = isNotEmpty(groupHealthInsured) ? groupHealthInsured.getPlanPremiumDetail() : ghInsuredDependent.getPlanPremiumDetail();
        if (isNotEmpty(planDetail)) {
            List<Plan> plans = planRepository.findPlanByCodeAndName(planDetail.getPlanCode());
            if (isNotEmpty(plans)) {
                Plan plan = plans.get(0);
                groupHealthCashlessClaimPolicyDetail
                        .updateWithSumAssured(planDetail.getSumAssured())
                        .updateWithCoverages(constructCoverageBenefitDetails(planDetail, clientId, groupHealthCashlessClaim.getPreAuthorizationDetails(), groupHealthPolicy.getPolicyNumber()))
                        .updateWithPlanDetails(plan.getPlanDetail(), plan.getPlanId())
                        .updateWithCoverages(constructProbableClaimAmountForServices(groupHealthCashlessClaimPolicyDetail.getCoverageDetails(), claimUploadedExcelDataDtos, plan, hcpCode, planDetail));
            }
        }
        return groupHealthCashlessClaimPolicyDetail;
    }

    private Set<GroupHealthCashlessClaimCoverageDetail> constructProbableClaimAmountForServices(Set<GroupHealthCashlessClaimCoverageDetail> coverageDetails, List<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtos, Plan plan, String hcpCode, GHPlanPremiumDetail planDetail) {
        Set<String> services = getServicesFromUploadedDetails(claimUploadedExcelDataDtos);
        notEmpty(services, "Error calculating probable claim amount services not found");
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
                            }).flatMap(Collection::stream).collect(Collectors.toList());
                            return setOfSBCM;
                        }
                    }).flatMap(Collection::stream).collect(Collectors.toList());
                    return setOfSBCM;
                }
            }).flatMap(Collection::stream).collect(Collectors.toSet());
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
                            map.put("services", getListOfServices(serviceBenefitCoverageMappings, services));
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
                /*HCPRate hcpRate = hcpRateRepository.findHCPRateByHCPCodeAndService(new HCPCode(hcpCode), service);
                notNull(hcpRate, "No HCP Rate configured for hcp- " + hcpCode + " service - " + service);
                HCPServiceDetail hcpServiceDetail = isNotEmpty(hcpRate.getHcpServiceDetails()) ? getHCPDetail(hcpRate.getHcpServiceDetails(), service) : null;
                notNull(hcpServiceDetail, "No HCP Rate configured as no HCPServiceDetail found.");*/
                Optional<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtoOptional = claimUploadedExcelDataDtos.parallelStream().filter(dto -> dto.getService().trim().equals(service.trim())).findAny();
                if(!claimUploadedExcelDataDtoOptional.isPresent())
                    throw new RuntimeException("No service found to calculate the amount");
                ClaimUploadedExcelDataDto claimUploadedExcelDataDto = claimUploadedExcelDataDtoOptional.get();
                int lengthOfStay = getLengthOfStayByService(service, claimUploadedExcelDataDtos);
                BigDecimal amount = calculateProbableClaimAmount(lengthOfStay, claimUploadedExcelDataDto.getBillAmount(), (CoverageBenefitDefinition) map.get("coverageBenefitDefinition"));
                payableAmount = payableAmount.add(amount);
                map.put("payableAmount", payableAmount);
            }
        });
        final List<Map<String, Object>> finalRefurbishedList = refurbishedList;
        return coverageDetails.stream().map(new Function<GroupHealthCashlessClaimCoverageDetail, GroupHealthCashlessClaimCoverageDetail>() {
            @Override
            public GroupHealthCashlessClaimCoverageDetail apply(GroupHealthCashlessClaimCoverageDetail groupHealthCashlessClaimCoverageDetail) {
                String coverageId = groupHealthCashlessClaimCoverageDetail.getCoverageId();
                Set<GroupHealthCashlessClaimBenefitDetail> benefitDetails = groupHealthCashlessClaimCoverageDetail.getBenefitDetails();
                groupHealthCashlessClaimCoverageDetail.updateWithProbableClaimAmount(coverageId, benefitDetails, finalRefurbishedList);
                return groupHealthCashlessClaimCoverageDetail;
            }
        }).collect(Collectors.toSet());
    }

    private Set<String> getServicesFromUploadedDetails(List<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtos) {
        return isNotEmpty(claimUploadedExcelDataDtos) ? claimUploadedExcelDataDtos.parallelStream().filter(drug -> drug.getStatus().equals(GroupHealthCashlessClaimDrugServiceDto.Status.PROCESS.name())).map(ClaimUploadedExcelDataDto::getService).collect(Collectors.toSet()) : Sets.newHashSet();
    }

    private Set<String> getListOfServices(List<ServiceBenefitCoverageMapping> serviceBenefitCoverageMappings, Set<String> services) {
        return isNotEmpty(serviceBenefitCoverageMappings) ? serviceBenefitCoverageMappings.stream().map(ServiceBenefitCoverageMapping::getService).collect(Collectors.toSet()).stream().filter(s -> services.contains(s)).collect(Collectors.toSet()) : Sets.newHashSet();
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

    private int getLengthOfStayByService(String service, List<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtos) {
        for (ClaimUploadedExcelDataDto claimUploadedExcelDataDto : claimUploadedExcelDataDtos) {
            if (claimUploadedExcelDataDto.getService().trim().equalsIgnoreCase(service.trim()))
                return claimUploadedExcelDataDto.getDiagnosisTreatmentSurgeryLengthOStay();
        }
        return 1;
    }

    private Set<GroupHealthCashlessClaimCoverageDetail> constructCoverageBenefitDetails(GHPlanPremiumDetail planDetail, String clientId, Set<PreAuthorizationDetailTaggedToClaim> preAuthorizationDetails, PolicyNumber policyNumber) {
        return isNotEmpty(planDetail) ? isNotEmpty(planDetail.getCoveragePremiumDetails()) ? planDetail.getCoveragePremiumDetails().parallelStream().map(new Function<GHCoveragePremiumDetail, GroupHealthCashlessClaimCoverageDetail>() {
            @Override
            public GroupHealthCashlessClaimCoverageDetail apply(GHCoveragePremiumDetail ghCoveragePremiumDetail) {
                GroupHealthCashlessClaimCoverageDetail groupHealthCashlessClaimCoverageDetail = new GroupHealthCashlessClaimCoverageDetail();
                if (isNotEmpty(ghCoveragePremiumDetail.getCoverageId())) {
                    CoverageDto coverageDto = coverageFinder.findCoverageById(ghCoveragePremiumDetail.getCoverageId().getCoverageId());
                    groupHealthCashlessClaimCoverageDetail
                            .updateWithCoverageName(coverageDto.getCoverageName())
                            .updateWithCoverageId(coverageDto.getCoverageId())
                            .updateWithCoverageCode(coverageDto.getCoverageCode())
                            .updateWithSumAssured(ghCoveragePremiumDetail.getSumAssured())
                            .updateWithTotalAmountPaid(getTotalAmountPaidTillNow(clientId, policyNumber, coverageDto.getCoverageId()))
                            .updateWithReserveAmount(getReservedAmountOfTheClient(clientId, policyNumber, coverageDto.getCoverageId()))
                            .updateWithBalanceAndEligibleAmount()
                            .updateWithBenefitDetails(constructBenefitDetails(coverageDto.getBenefitDtos(), groupHealthCashlessClaimCoverageDetail, isNotEmpty(ghCoveragePremiumDetail.getBenefitPremiumLimits()) ? ghCoveragePremiumDetail.getBenefitPremiumLimits() : Sets.newHashSet(), preAuthorizationDetails));
                }
                return groupHealthCashlessClaimCoverageDetail;
            }
        }).collect(Collectors.toSet()) : constructCoveragesFromPlanBaseCoverage(planDetail.getPlanCode(), planDetail, clientId, policyNumber) : Sets.newHashSet();
    }

    private Set<GroupHealthCashlessClaimDrugService> constructGroupHealthCashlessClaimDrugServices(List<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtos) {
        return isNotEmpty(claimUploadedExcelDataDtos) ? claimUploadedExcelDataDtos.parallelStream().map(detail -> new GroupHealthCashlessClaimDrugService().updateWithDetails(detail)).collect(Collectors.toSet()) : Sets.newHashSet();
    }

    private GroupHealthCashlessClaimIllnessDetail constructGroupHealthCashlessClaimIllnessDetail(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
        return new GroupHealthCashlessClaimIllnessDetail().updateWithDetails(claimUploadedExcelDataDto);
    }

    private Set<GroupHealthCashlessClaimDiagnosisTreatmentDetail> constructGroupHealthCashlessClaimDiagnosisTreatmentDetails(List<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtos) {
        return isNotEmpty(claimUploadedExcelDataDtos) ? claimUploadedExcelDataDtos.parallelStream().map(detail -> new GroupHealthCashlessClaimDiagnosisTreatmentDetail().updateWithDeatils(detail)).collect(Collectors.toSet()) : Sets.newHashSet();
    }

    private GroupHealthCashlessClaimHCPDetail constructHCPDetails(String hcpCode, String hospitalizationEvent) {
        HCP hcp = hcpFinder.getHCPByHCPCode(hcpCode);
        return new GroupHealthCashlessClaimHCPDetail()
                .updateWithHospitalizationEvent(hospitalizationEvent)
                .updateWithHCPDetails(hcp);
    }

    private GHProposer constructGroupHealthProposer(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
        GroupHealthPolicy groupHealthPolicy = ghPolicyRepository.findPolicyByPolicyNumber(claimUploadedExcelDataDto.policyNumber);

        notNull(groupHealthPolicy, "No Group Health Policy found with the policy number : "+claimUploadedExcelDataDto.policyNumber);
        return groupHealthPolicy.getProposer();
    }

    private String constructGroupHealthCashlessClaimId(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
        LocalDate consultationDate = claimUploadedExcelDataDto.getConsultationDate();
        String groupHealthCashlessClaimId = sequenceGenerator.getSequence(GroupHealthCashlessClaim.class);
        String year = String.format("%02d", consultationDate.getYear());
        groupHealthCashlessClaimId = String.format("%07d", Integer.parseInt(groupHealthCashlessClaimId.trim()))+String.format("%02d", consultationDate.getMonthOfYear())+ (year.length() > 2 ? year.substring(year.length() - 2) : year);
        return groupHealthCashlessClaimId;
    }

    public BigDecimal getReservedAmountOfTheClient(String clientId, PolicyNumber policyNumber, String coverageId) {
        BigDecimal totalReservedAmount = BigDecimal.ZERO;
        assert policyNumber != null;
        List<PreAuthorizationRequest> preAuthorizationRequests = preAuthorizationRequestRepository.findAllByPreAuthorizationRequestPolicyDetailPolicyNumberAndPreAuthorizationRequestPolicyDetailAssuredDetailClientIdAndStatus(policyNumber.getPolicyNumber(), clientId, PreAuthorizationRequest.Status.APPROVED);
        if(isNotEmpty(preAuthorizationRequests)){
            for(PreAuthorizationRequest preAuthorizationRequest : preAuthorizationRequests){
                PreAuthorizationRequestPolicyDetail preAuthorizationRequestPolicyDetail = preAuthorizationRequest.getPreAuthorizationRequestPolicyDetail();
                if(isNotEmpty(preAuthorizationRequestPolicyDetail)){
                    Set<PreAuthorizationRequestCoverageDetail> coverageDetails = preAuthorizationRequestPolicyDetail.getCoverageDetailDtoList();
                    if(isNotEmpty(coverageDetails)){
                        for(PreAuthorizationRequestCoverageDetail coverageDetail : coverageDetails){
                            if(coverageDetail.getCoverageId().equals(coverageId)){
                                totalReservedAmount = totalReservedAmount.add(coverageDetail.getApprovedAmount());
                            }
                        }
                    }
                }
            }
        }
        return totalReservedAmount;
    }

    public BigDecimal getTotalAmountPaidTillNow(String clientId, PolicyNumber policyNumber, String coverageId) {
        BigDecimal amountPaidTillDate = BigDecimal.ZERO;
        List<GroupHealthCashlessClaim> groupHealthCashlessClaims = groupHealthCashlessClaimRepository.findAllByGroupHealthCashlessClaimPolicyDetailPolicyNumberAndGroupHealthCashlessClaimPolicyDetailAssuredDetailClientIdAndStatus(policyNumber, clientId, Status.DISBURSED);
        if(isNotEmpty(groupHealthCashlessClaims)){
            for(GroupHealthCashlessClaim groupHealthCashlessClaim : groupHealthCashlessClaims){
                GroupHealthCashlessClaimPolicyDetail groupHealthCashlessClaimPolicyDetail = groupHealthCashlessClaim.getGroupHealthCashlessClaimPolicyDetail();
                if(isNotEmpty(groupHealthCashlessClaimPolicyDetail)){
                    Set<GroupHealthCashlessClaimCoverageDetail> groupHealthCashlessClaimCoverageDetails = groupHealthCashlessClaimPolicyDetail.getCoverageDetails();
                    if(isNotEmpty(groupHealthCashlessClaimCoverageDetails)){
                        for(GroupHealthCashlessClaimCoverageDetail cashlessClaimCoverageDetail : groupHealthCashlessClaimCoverageDetails){
                            if(cashlessClaimCoverageDetail.getCoverageId().equals(coverageId)){
                                amountPaidTillDate = amountPaidTillDate.add(cashlessClaimCoverageDetail.getApprovedAmount());
                            }
                        }
                    }
                }
            }
        }
        return amountPaidTillDate;
    }

    private Set<GroupHealthCashlessClaimBenefitDetail> constructBenefitDetailsFromBaseCoverage(List<Map<String, Object>> benefitDtos, GroupHealthCashlessClaimCoverageDetail groupHealthCashlessClaimCoverageDetail, Set<PlanCoverageBenefit> planCoverageBenefits) {
        Set<GroupHealthCashlessClaimBenefitDetail> benefits = groupHealthCashlessClaimCoverageDetail.getBenefitDetails();
        if (isEmpty(benefits))
            benefits = Sets.newHashSet();
        for (PlanCoverageBenefit planCoverageBenefit : planCoverageBenefits) {
            for (Map<String, Object> benefit : benefitDtos) {
                String benefitId = planCoverageBenefit.getBenefitId().getBenefitId();
                if (benefitId.equals(benefit.get("benefitId"))) {
                    GroupHealthCashlessClaimBenefitDetail groupHealthCashlessClaimBenefitDetail = new GroupHealthCashlessClaimBenefitDetail();
                    groupHealthCashlessClaimBenefitDetail.setBenefitName(isNotEmpty(benefit.get("benefitName")) ? benefit.get("benefitName").toString() : StringUtils.EMPTY);
                    groupHealthCashlessClaimBenefitDetail.setBenefitCode(isNotEmpty(benefit.get("benefitCode")) ? benefit.get("benefitCode").toString() : StringUtils.EMPTY);
                    benefits.add(groupHealthCashlessClaimBenefitDetail);
                }
            }
        }
        return benefits;
    }

    private Set<GroupHealthCashlessClaimBenefitDetail> constructBenefitDetails(List<Map<String, Object>> benefitDtos, GroupHealthCashlessClaimCoverageDetail groupHealthCashlessClaimCoverageDetail, Set<BenefitPremiumLimit> benefitPremiumLimits, Set<PreAuthorizationDetailTaggedToClaim> preAuthorizationDetails) {
        Set<GroupHealthCashlessClaimBenefitDetail> benefits = groupHealthCashlessClaimCoverageDetail.getBenefitDetails();
        if (isEmpty(benefits))
            benefits = Sets.newHashSet();
        for (BenefitPremiumLimit benefitPremiumLimit : benefitPremiumLimits) {
            for (Map<String, Object> benefit : benefitDtos) {
                String benefitCode = String.valueOf(new BigDecimal(benefitPremiumLimit.getBenefitCode()).intValue());
                if (benefitCode.equals(benefit.get("benefitCode"))) {
                    GroupHealthCashlessClaimBenefitDetail groupHealthCashlessClaimBenefitDetail = new GroupHealthCashlessClaimBenefitDetail();
                    groupHealthCashlessClaimBenefitDetail.setBenefitName(isNotEmpty(benefit.get("benefitName")) ? benefit.get("benefitName").toString() : StringUtils.EMPTY);
                    groupHealthCashlessClaimBenefitDetail.setBenefitCode(isNotEmpty(benefit.get("benefitCode")) ? benefit.get("benefitCode").toString() : StringUtils.EMPTY);
                    groupHealthCashlessClaimBenefitDetail.setPreAuthorizationAmount(getPreAuthorizationAmount(benefitCode, preAuthorizationDetails, groupHealthCashlessClaimCoverageDetail.getCoverageId()));
                    benefits.add(groupHealthCashlessClaimBenefitDetail);
                }
            }
        }
        return benefits;
    }

    private BigDecimal getPreAuthorizationAmount(String benefitCode, Set<PreAuthorizationDetailTaggedToClaim> preAuthorizationDetails, String coverageId) {
        BigDecimal preAuthorizationAmount = BigDecimal.ZERO;
        Set<PreAuthorizationRequestCoverageDetail> coverageDetails = preAuthorizationDetails.stream().map(preAuth -> preAuth.getCoverageDetails()).flatMap(Collection::stream).collect(Collectors.toSet());
        for(PreAuthorizationRequestCoverageDetail coverageDetail : coverageDetails){
            Set<PreAuthorizationRequestBenefitDetail> benefitDetails = coverageDetail.getBenefitDetails();
            if(isNotEmpty(benefitDetails)){
                for(PreAuthorizationRequestBenefitDetail benefitDetail : benefitDetails){
                    if(benefitCode.equals(benefitDetail.getBenefitCode()) && coverageId.equals(coverageDetail.getCoverageId())){
                        preAuthorizationAmount = preAuthorizationAmount.add(coverageDetail.getApprovedAmount());
                    }
                }
            }
        }
        return preAuthorizationAmount;
    }

    private Set<GroupHealthCashlessClaimCoverageDetail> constructCoveragesFromPlanBaseCoverage(String planCode, GHPlanPremiumDetail planDetail, String clientId, PolicyNumber policyNumber) {
        List<Plan> plans = planRepository.findPlanByCodeAndName(planCode);
        Set<GroupHealthCashlessClaimCoverageDetail> groupHealthCashlessClaimCoverageDetails = Sets.newHashSet();
        if(isNotEmpty(plans)) {
            Plan plan = plans.get(0);
            groupHealthCashlessClaimCoverageDetails = plan.getCoverages().stream().filter(coverage -> coverage.getCoverageType().equals(CoverageType.BASE)).map(new Function<PlanCoverage, GroupHealthCashlessClaimCoverageDetail>() {
                @Override
                public GroupHealthCashlessClaimCoverageDetail apply(PlanCoverage planCoverage) {
                    GroupHealthCashlessClaimCoverageDetail groupHealthCashlessClaimCoverageDetail = new GroupHealthCashlessClaimCoverageDetail();
                    if (isNotEmpty(planCoverage.getCoverageId())) {
                        CoverageDto coverageDto = coverageFinder.findCoverageById(planCoverage.getCoverageId().getCoverageId());
                        groupHealthCashlessClaimCoverageDetail
                                .updateWithCoverageName(coverageDto.getCoverageName())
                                .updateWithCoverageId(coverageDto.getCoverageId())
                                .updateWithCoverageCode(coverageDto.getCoverageCode())
                                .updateWithSumAssured(planCoverage.getCoverageSumAssured().getSumAssuredValue().first())
                                .updateWithTotalAmountPaid(getTotalAmountPaidTillNow(clientId, policyNumber, coverageDto.getCoverageId()))
                                .updateWithReserveAmount(getReservedAmountOfTheClient(clientId, policyNumber, coverageDto.getCoverageId()))
                                .updateWithBalanceAndEligibleAmount()
                                .updateWithBenefitDetails(constructBenefitDetailsFromBaseCoverage(coverageDto.getBenefitDtos(), groupHealthCashlessClaimCoverageDetail, planCoverage.getPlanCoverageBenefits()));
                    }
                    return groupHealthCashlessClaimCoverageDetail;
                }
            }).collect(Collectors.toSet());
        }
        return groupHealthCashlessClaimCoverageDetails;
    }

    public List<GHProposalMandatoryDocumentDto> findMandatoryDocuments(FamilyId familyId, String groupHealthCashlessClaimId) throws Exception {
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimRepository.findOne(groupHealthCashlessClaimId);
        notNull(groupHealthCashlessClaim, "No PreAuthorization found with the given Id");
        String policyNumber = isNotEmpty(groupHealthCashlessClaim.getGroupHealthCashlessClaimPolicyDetail()) ? isNotEmpty( groupHealthCashlessClaim.getGroupHealthCashlessClaimPolicyDetail().getPolicyNumber()) ? groupHealthCashlessClaim.getGroupHealthCashlessClaimPolicyDetail().getPolicyNumber().getPolicyNumber() : StringUtils.EMPTY : StringUtils.EMPTY;
        GroupHealthPolicy groupHealthPolicy = ghPolicyRepository.findPolicyByPolicyNumber(policyNumber);
        notNull(groupHealthPolicy, "No Policy found for the PreAuthorization with policy number - "+policyNumber);
        List<SearchDocumentDetailDto> documentDetailDtos = Lists.newArrayList();
        GHPlanPremiumDetail planPremiumDetail = preAuthorizationRequestService.getGHPlanPremiumDetailByFamilyId(familyId, groupHealthPolicy);
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
        Set<AdditionalDocument> additionalRequiredDocumentsByUnderwriter = groupHealthCashlessClaim.getAdditionalRequiredDocumentsByUnderwriter();
        if(isNotEmpty(additionalRequiredDocumentsByUnderwriter)){
            mandatoryDocuments = preAuthorizationRequestService.populateWithAdditionalRequiredDocumentsByUnderwriter(mandatoryDocuments, additionalRequiredDocumentsByUnderwriter);
        }
        List<GHProposalMandatoryDocumentDto> mandatoryDocumentDtos = Lists.newArrayList();
        Set<GHProposerDocument> uploadedDocuments = isNotEmpty(groupHealthCashlessClaim.getProposerDocuments()) ? groupHealthCashlessClaim.getProposerDocuments() : Sets.newHashSet();
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

    public GroupHealthCashlessClaimDto getGroupHealthCashlessClaimDtoBygroupHealthCashlessClaimId(String groupHealthCashlessClaimId) {
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimRepository.findOne(groupHealthCashlessClaimId);
        GroupHealthCashlessClaimDto groupHealthCashlessClaimDto = new GroupHealthCashlessClaimDto();
        if(isNotEmpty(groupHealthCashlessClaim)){
            groupHealthCashlessClaimDto
                    .updateWithGroupHealthCashlessClaimId(groupHealthCashlessClaim.getGroupHealthCashlessClaimId())
                    .updateWithCategory(groupHealthCashlessClaim.getCategory())
                    .updateWithRelationship(groupHealthCashlessClaim.getRelationship())
                    .updateWithClaimType(groupHealthCashlessClaim.getClaimType())
                    .updateWithClaimIntimationDate(groupHealthCashlessClaim.getClaimIntimationDate())
                    .updateWithBatchNumber(groupHealthCashlessClaim.getBatchNumber())
                    .updateWithBatchUploaderUserId(groupHealthCashlessClaim.getBatchUploaderUserId())
                    .updateWithStatus(groupHealthCashlessClaim.getStatus())
                    .updateWithGhProposer(groupHealthCashlessClaim.getGhProposer())
                    .updateWithGroupHealthCashlessClaimHCPDetail(groupHealthCashlessClaim.getGroupHealthCashlessClaimHCPDetail())
                    .updateWithGroupHealthCashlessClaimDiagnosisTreatmentDetails(groupHealthCashlessClaim.getGroupHealthCashlessClaimDiagnosisTreatmentDetails())
                    .updateWithGroupHealthCashlessClaimIllnessDetail(groupHealthCashlessClaim.getGroupHealthCashlessClaimIllnessDetail())
                    .updateWithGroupHealthCashlessClaimDrugServices(groupHealthCashlessClaim.getGroupHealthCashlessClaimDrugServices())
                    .updateWithCommentDetails(groupHealthCashlessClaim.getCommentDetails())
                    .updateWithSubmittedFlag(groupHealthCashlessClaim.isSubmitted())
                    .updateWithSubmissionDate(groupHealthCashlessClaim.getSubmissionDate())
                    .updateWithClaimProcessorUserId(groupHealthCashlessClaim.getClaimProcessorUserId())
                    .updateWithClaimUnderWriterUserId(groupHealthCashlessClaim.getClaimUnderWriterUserId())
                    .updateWithUnderWriterRoutedToSeniorUnderWriterUserId(groupHealthCashlessClaim.getUnderWriterRoutedToSeniorUnderWriterUserId())
                    .updateWithFirstReminderSent(groupHealthCashlessClaim.isFirstReminderSent())
                    .updateWithSecondReminderSent(groupHealthCashlessClaim.isSecondReminderSent())
                    .updateWithRejectionEmailSent(groupHealthCashlessClaim.isRejectionEmailSent())
                    .updateWithAdditionalRequirementEmailSent(groupHealthCashlessClaim.isAdditionalRequirementEmailSent())
                    .updateWithAdditionalRequiredDocumentsByUnderwriter(groupHealthCashlessClaim.getAdditionalRequiredDocumentsByUnderwriter())
                    .updateWithGroupHealthCashlessClaimPolicyDetail(groupHealthCashlessClaim.getGroupHealthCashlessClaimPolicyDetail())
                    .updateWithPreAuthorizationDetails(groupHealthCashlessClaim.getPreAuthorizationDetails());
        }
        return groupHealthCashlessClaimDto;
    }

    public GroupHealthCashlessClaim populateDetailsToGroupHealthCashlessClaim(GroupHealthCashlessClaimDto groupHealthCashlessClaimDto){
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimRepository.findOne(groupHealthCashlessClaimDto.getGroupHealthCashlessClaimId());
        if(isNotEmpty(groupHealthCashlessClaim)){
            groupHealthCashlessClaim
                    .updateWithCategory(groupHealthCashlessClaimDto.getCategory())
                    .updateWithRelationship(Relationship.getRelationship(groupHealthCashlessClaimDto.getRelationship()))
                    .updateWithClaimType(groupHealthCashlessClaimDto.getClaimType())
                    .updateWithClaimIntimationDate(groupHealthCashlessClaimDto.getClaimIntimationDate())
                    .updateWithBatchNumber(groupHealthCashlessClaimDto.getBatchNumber())
                    .updateWithBatchNumber(groupHealthCashlessClaimDto.getBatchNumber())
                    .updateWithBatchUploaderUserId(groupHealthCashlessClaimDto.getBatchUploaderUserId())
                    .updateWithStatus(Status.getStatus(groupHealthCashlessClaimDto.getStatus()))
                    .updateWithGhProposerDto(groupHealthCashlessClaimDto.getGhProposer())
                    .updateWithGroupHealthCashlessClaimHCPDetailFromDto(groupHealthCashlessClaimDto.getGroupHealthCashlessClaimHCPDetail())
                    .updateWithGroupHealthCashlessClaimDiagnosisTreatmentDetailsFromDto(groupHealthCashlessClaimDto.getGroupHealthCashlessClaimDiagnosisTreatmentDetails())
                    .updateWithGroupHealthCashlessClaimIllnessDetailFromDto(groupHealthCashlessClaimDto.getGroupHealthCashlessClaimIllnessDetail())
                    .updateWithGroupHealthCashlessClaimDrugServicesFromDto(groupHealthCashlessClaimDto.getGroupHealthCashlessClaimDrugServices())
                    .updateWithCommentDetails(groupHealthCashlessClaimDto.getCommentDetails())
                    .updateWithSubmittedFlag(groupHealthCashlessClaimDto.isSubmitted())
                    .updateWithSubmissionDate(groupHealthCashlessClaimDto.getSubmissionDate())
                    .updateWithClaimProcessorUserId(groupHealthCashlessClaimDto.getClaimProcessorUserId())
                    .updateWithClaimUnderWriterUserId(groupHealthCashlessClaimDto.getClaimUnderWriterUserId())
                    .updateWithUnderWriterRoutedToSeniorUnderWriterUserId(groupHealthCashlessClaimDto.getUnderWriterRoutedToSeniorUnderWriterUserId())
                    .updateWithFirstReminderSent(groupHealthCashlessClaimDto.isFirstReminderSent())
                    .updateWithSecondReminderSent(groupHealthCashlessClaimDto.isSecondReminderSent())
                    .updateWithRejectionEmailSent(groupHealthCashlessClaimDto.isRejectionEmailSent())
                    .updateWithAdditionalRequirementEmailSent(groupHealthCashlessClaimDto.isAdditionalRequirementEmailSent())
                    .updateWithAdditionalRequiredDocumentsByUnderwriter(groupHealthCashlessClaimDto.getAdditionalRequiredDocumentsByUnderwriter())
                    .updateWithGroupHealthCashlessClaimPolicyDetailFromDto(groupHealthCashlessClaimDto.getGroupHealthCashlessClaimPolicyDetail());;
        }
        return groupHealthCashlessClaim;
    }

    public GroupHealthCashlessClaimDto  reConstructProbableClaimAmountForServices(GroupHealthCashlessClaimDto groupHealthCashlessClaimDto) {
        GroupHealthCashlessClaimPolicyDetailDto groupHealthCashlessClaimPolicyDetailDto = groupHealthCashlessClaimDto.getGroupHealthCashlessClaimPolicyDetail();
        if(isNotEmpty(groupHealthCashlessClaimPolicyDetailDto)) {
            Set<GroupHealthCashlessClaimCoverageDetail> coverageBenefitDetails = groupHealthCashlessClaimPolicyDetailDto.getCoverageDetails();
            GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimRepository.findOne(groupHealthCashlessClaimDto.getGroupHealthCashlessClaimId());
            List<Map<String, Object>> refurbishedList = Lists.newArrayList();
            Set<GroupHealthCashlessClaimDrugServiceDto> groupHealthCashlessClaimDrugServices = groupHealthCashlessClaimDto.getGroupHealthCashlessClaimDrugServices();
            Set<String> services = getServicesAvailedFromPreAuthorization(groupHealthCashlessClaimDrugServices);
            List<ServiceBenefitCoverageMapping> sbcms = sbcmRepository.findAllByPlanCode(groupHealthCashlessClaimDto.getGroupHealthCashlessClaimPolicyDetail().getPlanCode());
            List<Plan> plans = planRepository.findPlanByCodeAndName(groupHealthCashlessClaim.getGroupHealthCashlessClaimPolicyDetail().getPlanCode());
            Plan plan = null;
            if (isNotEmpty(plans)) {
                plan = plans.get(0);
            }
            Set<ServiceBenefitCoverageMapping> sbcmSet = getAllSBCMWithGivenCoverageBenefitPlan(groupHealthCashlessClaim, sbcms);
            if (isNotEmpty(sbcmSet)) {
                Map<ServiceBenefitCoverageMapping.CoverageBenefit, List<ServiceBenefitCoverageMapping>> result = sbcmSet.parallelStream().collect(Collectors.groupingBy(ServiceBenefitCoverageMapping::getCoverageBenefit));
                if (isNotEmpty(result)) {
                    final Plan finalPlan = plan;
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
                                map.put("services", getListOfServices(serviceBenefitCoverageMappings, services));
                                notNull(getCoverageBenefitDefinition(sbcm.getBenefitId(), finalPlan.getCoverages()), "CoverageBenefitDefinition null for Benefit - " + sbcm.getBenefitId());
                                map.put("coverageBenefitDefinition", getCoverageBenefitDefinition(sbcm.getBenefitId(), finalPlan.getCoverages()));
                            }
                            return map;
                        }
                    }).collect(Collectors.toList());
                }
            }
            refurbishedList.stream().forEach(map -> {
                Set<String> serviceList = isNotEmpty(map.get("services")) ? (Set<String>) map.get("services") : Sets.newHashSet();
                BigDecimal payableAmount = BigDecimal.ZERO;
                GroupHealthCashlessClaimHCPDetail hcp = groupHealthCashlessClaim.getGroupHealthCashlessClaimHCPDetail();
                for (String service : serviceList) {
                    Optional<GroupHealthCashlessClaimDrugServiceDto> groupHealthCashlessClaimDrugServiceDtoOptional = groupHealthCashlessClaimDrugServices.stream().filter(drug -> drug.getServiceName().equals(service)).findAny();
                    if(!groupHealthCashlessClaimDrugServiceDtoOptional.isPresent())
                        throw new RuntimeException("No service details found for the given service");
                    GroupHealthCashlessClaimDrugServiceDto groupHealthCashlessClaimDrugServiceDto = groupHealthCashlessClaimDrugServiceDtoOptional.get();
                    /*HCPRate hcpRate = hcpRateRepository.findHCPRateByHCPCodeAndService(hcp.getHcpCode(), service);
                    notNull(hcpRate, "No HCP Rate configured for hcp- " + hcp.getHcpCode() + " service - " + service);
                    HCPServiceDetail hcpServiceDetail = isNotEmpty(hcpRate.getHcpServiceDetails()) ? getHCPDetail(hcpRate.getHcpServiceDetails(), service) : null;
                    notNull(hcpRate, "No HCP Rate configured as no HCPServiceDetail found.");*/
                    int lengthOfStay = getLengthOfStayForClaimService(service, groupHealthCashlessClaim.getGroupHealthCashlessClaimDrugServices());
                    BigDecimal amount = calculateProbableClaimAmount(lengthOfStay, groupHealthCashlessClaimDrugServiceDto.getBillAmount(), (CoverageBenefitDefinition) map.get("coverageBenefitDefinition"));
                    payableAmount = payableAmount.add(amount);
                    map.put("payableAmount", payableAmount);
                }
            });
            final List<Map<String, Object>> finalRefurbishedList = refurbishedList;
            coverageBenefitDetails = coverageBenefitDetails.stream().map(new Function<GroupHealthCashlessClaimCoverageDetail, GroupHealthCashlessClaimCoverageDetail>() {
                @Override
                public GroupHealthCashlessClaimCoverageDetail apply(GroupHealthCashlessClaimCoverageDetail coverageBenefitDetailDto) {
                    String coverageId = coverageBenefitDetailDto.getCoverageId();
                    Set<GroupHealthCashlessClaimBenefitDetail> benefitDetails = coverageBenefitDetailDto.getBenefitDetails();
                    coverageBenefitDetailDto.updateWithProbableClaimAmount(coverageId, benefitDetails, finalRefurbishedList);
                    return coverageBenefitDetailDto;
                }
            }).collect(Collectors.toSet());
            groupHealthCashlessClaimPolicyDetailDto.updateWithCoverages(coverageBenefitDetails);
            groupHealthCashlessClaimDto.updateWithGroupHealthCashlessClaimPolicyDetailDto(groupHealthCashlessClaimPolicyDetailDto);
        }
        return groupHealthCashlessClaimDto;
    }

    private int getLengthOfStayForClaimService(String service, Set<GroupHealthCashlessClaimDrugService> groupHealthCashlessClaimDrugServices) {
        for (GroupHealthCashlessClaimDrugService groupHealthCashlessClaimDrugService : groupHealthCashlessClaimDrugServices) {
            if (groupHealthCashlessClaimDrugService.getServiceName().trim().equalsIgnoreCase(service.trim()))
                return groupHealthCashlessClaimDrugService.getLengthOfStay();
        }
        return 1;
    }

    private Set<ServiceBenefitCoverageMapping> getAllSBCMWithGivenCoverageBenefitPlan(GroupHealthCashlessClaim groupHealthCashlessClaim, List<ServiceBenefitCoverageMapping> sbcms) {
        return isNotEmpty(groupHealthCashlessClaim.getGroupHealthCashlessClaimPolicyDetail().getCoverageDetails()) ?
                groupHealthCashlessClaim.getGroupHealthCashlessClaimPolicyDetail().getCoverageDetails().stream().map(new Function<GroupHealthCashlessClaimCoverageDetail, Set<ServiceBenefitCoverageMapping>>() {
                    @Override
                    public Set<ServiceBenefitCoverageMapping> apply(GroupHealthCashlessClaimCoverageDetail coverage) {
                        return isNotEmpty(coverage.getBenefitDetails()) ? coverage.getBenefitDetails().stream().map(new Function<GroupHealthCashlessClaimBenefitDetail, Set<ServiceBenefitCoverageMapping>>() {
                            @Override
                            public Set<ServiceBenefitCoverageMapping> apply(GroupHealthCashlessClaimBenefitDetail benefit) {
                                return sbcms.stream().filter(new Predicate<ServiceBenefitCoverageMapping>() {
                                    @Override
                                    public boolean test(ServiceBenefitCoverageMapping sbcm) {
                                        return (sbcm.getBenefitCode().equals(benefit.getBenefitCode()) && sbcm.getCoverageId().getCoverageId().equals(coverage.getCoverageId()));
                                    }
                                }).collect(Collectors.toSet());
                            }
                        }).flatMap(data -> data.stream()).collect(Collectors.toSet()) : Sets.newHashSet();
                    }
                }).flatMap(data -> data.stream()).collect(Collectors.toSet()) : Sets.newHashSet();
    }

    private Set<String> getServicesAvailedFromPreAuthorization(Set<GroupHealthCashlessClaimDrugServiceDto> drugServiceDtos) {
        return isNotEmpty(drugServiceDtos) ? drugServiceDtos.stream().filter(drug -> drug.getStatus().equals(GroupHealthCashlessClaimDrugServiceDto.Status.PROCESS)).map(GroupHealthCashlessClaimDrugServiceDto::getServiceName).collect(Collectors.toSet()) : Sets.newHashSet();
    }
    public String getLoggedInUsername() {
        String userName = StringUtils.EMPTY;
        Authentication authentication = authenticationFacade.getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            userName = authentication.getName();
        }
        return userName;
    }

    public List<GroupHealthCashlessClaimDto> getCashlessClaimByDefaultList(String claimUserId) {
        List<GroupHealthCashlessClaimDto> result = Lists.newArrayList();
        PageRequest pageRequest = new PageRequest(0, 300, new Sort(new Sort.Order(Sort.Direction.DESC, "createdOn")));
        Page<GroupHealthCashlessClaim> pages = groupHealthCashlessClaimRepository.findAllByClaimProcessorUserIdInAndStatusIn(Lists.newArrayList(claimUserId, null), Lists.newArrayList(Status.INTIMATION, Status.EVALUATION, Status.RETURNED), pageRequest);
        if (isNotEmpty(pages) && isNotEmpty(pages.getContent()))
            result = convertGroupHealthCashlessClaimToGroupHealthCashlessClaimDto(pages.getContent());
        return result;
    }

    public RoutingLevel getRoutingLevelForPreAuthorization(GroupHealthCashlessClaimDto groupHealthCashlessClaimDto) throws RoutingLevelNotFoundException {
        UnderWriterRoutingLevelDetailDto routingLevelDetailDto = getUnderWriterRoutingLevelDetailDto(groupHealthCashlessClaimDto);
        RoutingLevel routingLevel = underWriterAdapter.getRoutingLevelWithoutCoverageDetails(routingLevelDetailDto);
        if(isEmpty(routingLevel))
            throw new RoutingLevelNotFoundException(getRoutingLevelExceptionMessage(groupHealthCashlessClaimDto));
        return routingLevel;
    }

    private UnderWriterRoutingLevelDetailDto getUnderWriterRoutingLevelDetailDto(GroupHealthCashlessClaimDto groupHealthCashlessClaimDto) {
        BigDecimal claimAmount = groupHealthCashlessClaimDto.getSumOfAllProbableClaimAmount();
        int age = groupHealthCashlessClaimDto.getAgeOfTheClient();
        List<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem> underWriterInfluencingFactorItems = new ArrayList<>();
        UnderWriterRoutingLevelDetailDto routingLevelDetailDto = new UnderWriterRoutingLevelDetailDto(groupHealthCashlessClaimDto.getGroupHealthCashlessClaimPolicyDetail().getPlanId(), LocalDate.now(), ProcessType.CLAIM.name());
        underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.CLAIM_AMOUNT.name(), claimAmount.toPlainString()));
        underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.AGE.name(), String.valueOf(age)));
        routingLevelDetailDto.setUnderWriterInfluencingFactor(underWriterInfluencingFactorItems);
        return routingLevelDetailDto;
    }

    public String getRoutingLevelExceptionMessage(GroupHealthCashlessClaimDto groupHealthCashlessClaimDto) {
        BigDecimal claimAmount = groupHealthCashlessClaimDto.getSumOfAllProbableClaimAmount();
        int age = groupHealthCashlessClaimDto.getAgeOfTheClient();
        return "No routing rule found for Group Health Cashless claim : "+groupHealthCashlessClaimDto.getGroupHealthCashlessClaimId()+" having age : "+age+" and Claim Amount : "+claimAmount;

    }

    public Set<GHProposalMandatoryDocumentDto> findAdditionalDocuments(String groupHealthCashlessClaimId) {
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimRepository.findOne(groupHealthCashlessClaimId);
        if(isEmpty(groupHealthCashlessClaim)){
            return Sets.newHashSet();
        }
        Set<GHProposerDocument> uploadedDocuments = isNotEmpty(groupHealthCashlessClaim.getProposerDocuments()) ? groupHealthCashlessClaim.getProposerDocuments() : Sets.newHashSet();
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

    public List<GroupHealthCashlessClaimDto> getDefaultListByUnderwriterLevel(String level, String username){
        List<GroupHealthCashlessClaim> groupHealthCashlessClaims =Lists.newArrayList();
        List<GroupHealthCashlessClaimDto> result = Lists.newArrayList();
        if(level.equalsIgnoreCase("LEVEL1"))
            groupHealthCashlessClaims = groupHealthCashlessClaimRepository.findAllByStatusAndClaimUnderWriterUserIdIn(PreAuthorizationRequest.Status.UNDERWRITING_LEVEL1, Lists.newArrayList(username,null));
        if(level.equalsIgnoreCase("LEVEL2"))
            groupHealthCashlessClaims = groupHealthCashlessClaimRepository.findAllByStatusAndClaimUnderWriterUserIdIn(PreAuthorizationRequest.Status.UNDERWRITING_LEVEL2, Lists.newArrayList(username,null));
        if (isNotEmpty(groupHealthCashlessClaims))
            result = convertGroupHealthCashlessClaimToGroupHealthCashlessClaimDto(groupHealthCashlessClaims);
        return result;
    }


    private List<GroupHealthCashlessClaimDto> convertGroupHealthCashlessClaimToGroupHealthCashlessClaimDto(List<GroupHealthCashlessClaim> groupHealthCashlessClaims) {
        return isNotEmpty(groupHealthCashlessClaims) ? groupHealthCashlessClaims.parallelStream().map(new Function<GroupHealthCashlessClaim, GroupHealthCashlessClaimDto>() {
            @Override
            public GroupHealthCashlessClaimDto apply(GroupHealthCashlessClaim groupHealthCashlessClaim) {
                return new GroupHealthCashlessClaimDto()
                        .updateWithStatus(groupHealthCashlessClaim.getStatus())
                        .updateWithBatchNumber(groupHealthCashlessClaim.getBatchNumber())
                        .updateWithGroupHealthCashlessClaimId(groupHealthCashlessClaim.getGroupHealthCashlessClaimId())
                        .updateWithClaimType(groupHealthCashlessClaim.getClaimType())
                        .updateWithClaimIntimationDate(groupHealthCashlessClaim.getClaimIntimationDate())
                        .updateWithGroupHealthCashlessClaimPolicyNumber(groupHealthCashlessClaim.getGroupHealthCashlessClaimPolicyDetail())
                        .updateWithGroupHealthCashlessClaimHCPDetail(groupHealthCashlessClaim.getGroupHealthCashlessClaimHCPDetail());
            }
        }).collect(Collectors.toList()) : Lists.newArrayList();
    }
    public Set<String> getAllRelevantServices(String groupHealthCashlessClaimId) {
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimRepository.findOne(groupHealthCashlessClaimId);
        notNull(groupHealthCashlessClaim, "No claim record found with given id");
        Set<String> services = Sets.newHashSet();
        if (isNotEmpty(groupHealthCashlessClaim.getGroupHealthCashlessClaimPolicyDetail())) {
            List<ServiceBenefitCoverageMapping> sbcms = sbcmRepository.findAllByPlanCode(groupHealthCashlessClaim.getGroupHealthCashlessClaimPolicyDetail().getPlanCode());
            Set<ServiceBenefitCoverageMapping> sbcmSet = getAllSBCMWithGivenCoverageBenefitPlan(groupHealthCashlessClaim, sbcms);
            services = isNotEmpty(sbcmSet) ?  sbcmSet.stream().map(ServiceBenefitCoverageMapping::getService).collect(Collectors.toSet()) : services;
        }
        return services;
    }

    public String getUnderwriterLevelForPreAuthorization(String groupHealthCashlessClaimId) {
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimRepository.findOne(groupHealthCashlessClaimId);
        if(isNotEmpty(groupHealthCashlessClaim)){
            if(groupHealthCashlessClaim.getStatus().equals(PreAuthorizationRequest.Status.UNDERWRITING_LEVEL1))
                return "LEVEL1";
            if(groupHealthCashlessClaim.getStatus().equals(PreAuthorizationRequest.Status.UNDERWRITING_LEVEL2))
                return "LEVEL2";
        }
        return StringUtils.EMPTY;
    }

    public boolean checkIfGroupHealthCashlessClaimRejectionEmailSent(String groupHealthCashlessClaimId) {
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimRepository.findOne(groupHealthCashlessClaimId);
        return groupHealthCashlessClaim.isRejectionEmailSent();
    }

    public boolean checkIfGroupHealthCashlessClaimRequirementEmailSent(String groupHealthCashlessClaimId) {
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimRepository.findOne(groupHealthCashlessClaimId);
        return groupHealthCashlessClaim.isAdditionalRequirementEmailSent();
    }

    public GroupHealthCashlessClaim updateRejectionEmailSentFlag(String groupHealthCashlessClaimId) {
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimRepository.findOne(groupHealthCashlessClaimId);
        groupHealthCashlessClaim.updateRejectionEmailSentFlag(Boolean.TRUE);
        groupHealthCashlessClaim = groupHealthCashlessClaimRepository.save(groupHealthCashlessClaim);
        return groupHealthCashlessClaim;
    }

    public GroupHealthCashlessClaim updateRequirementEmailSentFlag(String groupHealthCashlessClaimId) {
        GroupHealthCashlessClaim groupHealthCashlessClaim = groupHealthCashlessClaimRepository.findOne(groupHealthCashlessClaimId);
        groupHealthCashlessClaim.updateRequirementEmailSentFlag(Boolean.TRUE);
        groupHealthCashlessClaim = groupHealthCashlessClaimRepository.save(groupHealthCashlessClaim);
        return groupHealthCashlessClaim;
    }

    public  List<GroupHealthCashlessClaimDto> getCashlessClaimByCriteria (SearchGroupHealthCashlessClaimRecordDto searchGroupHealthCashlessClaimRecordDto){
        List<GroupHealthCashlessClaim> groupHealthCashlessClaims = GHCashlessClaimFinder.getCashlessClaimByCriteria(searchGroupHealthCashlessClaimRecordDto);
       return convertGroupHealthCashlessClaimToGroupHealthCashlessClaimDto(groupHealthCashlessClaims);

    }

    public List<GroupHealthCashlessClaimDto> searchCashlessClaimUnderwriterCriteria (SearchGroupHealthCashlessClaimRecordDto searchGroupHealthCashlessClaimRecordDto){
        List<GroupHealthCashlessClaim> groupHealthCashlessClaims = GHCashlessClaimFinder.getCashlessClaimByCriteria(searchGroupHealthCashlessClaimRecordDto);
        return convertGroupHealthCashlessClaimToGroupHealthCashlessClaimDto(groupHealthCashlessClaims);
    }
}

