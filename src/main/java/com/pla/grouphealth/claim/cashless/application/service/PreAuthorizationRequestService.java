package com.pla.grouphealth.claim.cashless.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.hcp.domain.model.HCP;
import com.pla.core.hcp.domain.model.HCPCode;
import com.pla.core.hcp.query.HCPFinder;
import com.pla.core.repository.PlanRepository;
import com.pla.grouphealth.claim.cashless.domain.model.*;
import com.pla.grouphealth.claim.cashless.presentation.dto.*;
import com.pla.grouphealth.claim.cashless.query.PreAuthorizationFinder;
import com.pla.grouphealth.claim.cashless.repository.PreAuthorizationRepository;
import com.pla.grouphealth.claim.cashless.repository.PreAuthorizationRequestRepository;
import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.grouphealth.policy.domain.model.PolicyStatus;
import com.pla.grouphealth.policy.repository.GHPolicyRepository;
import com.pla.grouphealth.proposal.presentation.dto.GHProposalMandatoryDocumentDto;
import com.pla.grouphealth.sharedresource.model.vo.*;
import com.pla.publishedlanguage.dto.ClientDocumentDto;
import com.pla.publishedlanguage.dto.SearchDocumentDetailDto;
import com.pla.publishedlanguage.underwriter.contract.IUnderWriterAdapter;
import com.pla.sharedkernel.domain.model.FamilyId;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.Relationship;
import com.pla.sharedkernel.identifier.CoverageId;
import lombok.NoArgsConstructor;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.nthdimenzion.axonframework.repository.GenericMongoRepository;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.util.Assert;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;
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
    private GenericMongoRepository<PreAuthorizationRequest> preAuthorizationRequestMongoRepository;
    @Autowired
    private PreAuthorizationRequestRepository preAuthorizationRequestRepository;
    @Autowired
    private IUnderWriterAdapter underWriterAdapter;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private PreAuthorizationFinder preAuthorizationFinder;

    public PreAuthorizationClaimantDetailCommand getPreAuthorizationByPreAuthorizationIdAndClientId(PreAuthorizationId preAuthorizationId, String clientId) {
        PreAuthorization preAuthorization = preAuthorizationRepository.findOne(preAuthorizationId);
        if(isNotEmpty(preAuthorization)){
            return constructPreAuthorizationClaimantDetailDtoFromPreAuthorization(preAuthorization, clientId);
        }
        return PreAuthorizationClaimantDetailCommand.getInstance();
    }

    private PreAuthorizationClaimantDetailCommand constructPreAuthorizationClaimantDetailDtoFromPreAuthorization(PreAuthorization preAuthorization, String clientId) {
        PreAuthorizationDetail preAuthorizationDetail = preAuthorization.getPreAuthorizationDetails().iterator().next();
        notNull(preAuthorizationDetail, "PreAuthorizationDetail cannot be null");
        PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand = new PreAuthorizationClaimantDetailCommand();
        preAuthorizationClaimantDetailCommand.updateWithBatchNumber(preAuthorization.getBatchNumber())
                .updateWithPreAuthorizationId(preAuthorization.getPreAuthorizationId())
                .updateWithPreAuthorizationDate(preAuthorization.getBatchDate().toLocalDate())
                .updateWithClaimantHCPDetailDto(constructClaimantHCPDetailDto(preAuthorization.getHcpCode(), preAuthorizationDetail.getHospitalizationEvent()))
                .updateWithClaimantPolicyDetailDto(constructClaimantPolicyDetailDto(preAuthorizationDetail.getPolicyNumber(), clientId))
                .updateWithDiagnosisTreatment(constructDiagnosisTreatmentDto(preAuthorization))
                .updateWithIllnessDetails(constructIllnessDetailDto(preAuthorization))
                .updateWithDrugServices(constructDrugServiceDtos(preAuthorization));
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
                ClaimantPolicyDetailDto claimantPolicyDetailDto = new ClaimantPolicyDetailDto()
                        .updateWithPreAuthorizationClaimantProposerDetail(ghProposer.getContactDetail(), ghProposer.getProposerName(), ghProposer.getProposerCode())
                        .updateWithPolicyName(groupHealthPolicy.getSchemeName())
                        .updateWithPolicyNumber(isNotEmpty(groupHealthPolicy.getPolicyNumber()) ? groupHealthPolicy.getPolicyNumber().getPolicyNumber() : StringUtils.EMPTY);
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
            claimantPolicyDetailDto.updateWithCoverageBenefitDetails(planDetail);
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

    public PreAuthorizationRequestId createUpdatePreAuthorizationRequest(PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand) {
        String preAuthorizationRequestId = preAuthorizationClaimantDetailCommand.getPreAuthorizationRequestId();
        PreAuthorizationRequest preAuthorizationRequest = isNotEmpty(preAuthorizationRequestId) ?
                isNotEmpty(getPreAuthorizationRequestById(new PreAuthorizationRequestId(preAuthorizationRequestId)))
                        ? getPreAuthorizationRequestById(new PreAuthorizationRequestId(preAuthorizationRequestId))
                        : new PreAuthorizationRequest(PreAuthorizationRequest.Status.DRAFT) : new PreAuthorizationRequest(PreAuthorizationRequest.Status.DRAFT);
        preAuthorizationRequest.updateWithPreAuthorizationRequestId()
                .updateWithPreAuthorizationId(preAuthorizationClaimantDetailCommand.getPreAuthorizationId())
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
                .updateWithPreAuthorizationRequestDrugService(preAuthorizationClaimantDetailCommand.getDrugServicesDtos());
        preAuthorizationRequestRepository.save(preAuthorizationRequest);
        return preAuthorizationRequest.getPreAuthorizationRequestId();
    }

    private PreAuthorizationRequest getPreAuthorizationRequestById(PreAuthorizationRequestId preAuthorizationRequestId) {
        return preAuthorizationRequestRepository.findOne(preAuthorizationRequestId);
    }

    public List<GHProposalMandatoryDocumentDto> findMandatoryDocuments(FamilyId familyId) {
        GroupHealthPolicy groupHealthPolicy = ghPolicyRepository.findDistinctPolicyByFamilyId(familyId, PolicyStatus.IN_FORCE);
        if(isEmpty(groupHealthPolicy)){
            groupHealthPolicy = ghPolicyRepository.findDistinctPolicyByDependentFamilyId(familyId, PolicyStatus.IN_FORCE);
        }
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
        List<GHProposalMandatoryDocumentDto> mandatoryDocumentDtos = Lists.newArrayList();
        List<GHProposerDocument> uploadedDocuments = Lists.newArrayList();
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

    private GHPlanPremiumDetail getGHPlanPremiumDetailByFamilyId(FamilyId familyId, GroupHealthPolicy groupHealthPolicy) {
        Set<GHInsured> insureds = groupHealthPolicy.getInsureds();
        GHInsured groupHealthInsured = null;
        GHInsuredDependent ghInsuredDependent = null;
        if(isNotEmpty(insureds)){
            Optional<GHInsured> groupHealthInsuredOptional = insureds.stream().filter(ghInsured -> ghInsured.getFamilyId().equals(familyId)).findFirst();
            if(groupHealthInsuredOptional.isPresent()) {
                groupHealthInsured = groupHealthInsuredOptional.get();
            }
            if(isEmpty(groupHealthInsured)) {
                Optional<GHInsuredDependent> ghInsuredDependentOptional = insureds.stream().flatMap(new Function<GHInsured, Stream<GHInsuredDependent>>() {
                    @Override
                    public Stream<GHInsuredDependent> apply(GHInsured ghInsured) {
                        return ghInsured.getInsuredDependents().stream();
                    }
                }).filter(gHInsuredDependent -> gHInsuredDependent.getFamilyId().equals(familyId)).findFirst();
                if(ghInsuredDependentOptional.isPresent()) {
                    ghInsuredDependent = ghInsuredDependentOptional.get();
                }
            }
        }
        return isNotEmpty(groupHealthInsured) ? groupHealthInsured.getPlanPremiumDetail() : ghInsuredDependent.getPlanPremiumDetail();
    }

    public List<PreAuthorizationClaimantDetailCommand> getPreAuthorizationRequestByCriteria(SearchPreAuthorizationRecordDto searchPreAuthorizationRecordDto) {
        List<PreAuthorizationRequest> preAuthorizationRequests = preAuthorizationFinder.getPreAuthorizationRequestByCriteria(searchPreAuthorizationRecordDto);
        return isNotEmpty(preAuthorizationRequests) ? preAuthorizationRequests.parallelStream().map(new Function<PreAuthorizationRequest, PreAuthorizationClaimantDetailCommand>() {
            @Override
            public PreAuthorizationClaimantDetailCommand apply(PreAuthorizationRequest preAuthorizationRequest) {
                return new PreAuthorizationClaimantDetailCommand()
                        .updateWithBatchNumber(preAuthorizationRequest.getBatchNumber())
                        .updateWithPreAuthorizationRequestId(preAuthorizationRequest.getPreAuthorizationRequestId())
                        .updateWithClaimType(preAuthorizationRequest.getClaimType())
                        .updateWithClaimIntimationDate(preAuthorizationRequest.getClaimIntimationDate())
                        .updateWithPolicy(preAuthorizationRequest.getPreAuthorizationRequestPolicyDetail())
                        .updateWithHcp(preAuthorizationRequest.getPreAuthorizationRequestHCPDetail());
            }
        }).collect(Collectors.toList()) : Lists.newArrayList();
    }

    public PreAuthorizationClaimantDetailCommand getPreAuthorizationClaimantDetailCommandFromPreAuthorizationRequestId(PreAuthorizationRequestId preAuthorizationRequestId){
        PreAuthorizationRequest preAuthorizationRequest = getPreAuthorizationRequestById(preAuthorizationRequestId);
        Assert.notNull(preAuthorizationRequest, "No PreAuthorizationRequest found with given Id");
        if(isNotEmpty(preAuthorizationRequest))
            return constructPreAuthorizationClaimantDetailCommand(preAuthorizationRequest);
        return null;
    }

    private PreAuthorizationClaimantDetailCommand constructPreAuthorizationClaimantDetailCommand(PreAuthorizationRequest preAuthorizationRequest) {
        PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand = new PreAuthorizationClaimantDetailCommand();
        if(isNotEmpty(preAuthorizationRequest)) {
            preAuthorizationClaimantDetailCommand
                    .updateWithPreAuthorizationRequestId(preAuthorizationRequest.getPreAuthorizationRequestId())
                    .updateWithPreAuthorizationId(preAuthorizationRequest.getPreAuthorizationId())
                    .updateWithBatchNumber(preAuthorizationRequest.getBatchNumber())
                    .updateWithClaimType(preAuthorizationRequest.getClaimType())
                    .updateWithClaimIntimationDate(preAuthorizationRequest.getClaimIntimationDate())
                    .updateWithPreAuthorizationDate(preAuthorizationRequest.getClaimIntimationDate())
                    .updateWithClaimantHCPDetailDto(constructClaimantHCPDetailDtoFromPreAuthorizationRequestHCPDetail(preAuthorizationRequest.getPreAuthorizationRequestHCPDetail()))
                    .updateWithDiagnosisTreatment(constructDiagnosisTreatmentDtoListFromPreAuthorizationRequest(preAuthorizationRequest.getPreAuthorizationRequestDiagnosisTreatmentDetails()))
                    .updateWithIllnessDetails(constructIllnessDetailDtoFromPreAuthorizationRequest(preAuthorizationRequest.getPreAuthorizationRequestIllnessDetail()))
                    .updateWithDrugServices(constructDrugServiceDtoFromPreAuthorizationRequest(preAuthorizationRequest.getPreAuthorizationRequestDrugServices()))
                    .updateWithClaimantPolicyDetailDto(constructClaimantPolicyDetailDtoFromPreAuthorizationRequest(preAuthorizationRequest.getPreAuthorizationRequestPolicyDetail(), preAuthorizationRequest.getRelationship(), preAuthorizationRequest.getCategory(), preAuthorizationRequest.getGhProposer()));
        }
        return preAuthorizationClaimantDetailCommand;
    }

    private ClaimantPolicyDetailDto constructClaimantPolicyDetailDtoFromPreAuthorizationRequest(PreAuthorizationRequestPolicyDetail preAuthorizationRequestPolicyDetail, String relationship, String category, GHProposer ghProposer) {
        ClaimantPolicyDetailDto claimantPolicyDetailDto = null;
        if(isNotEmpty(preAuthorizationRequestPolicyDetail)){
            claimantPolicyDetailDto = new ClaimantPolicyDetailDto()
                    .updateWithPolicyNumber(preAuthorizationRequestPolicyDetail.getPolicyNumber())
                    .updateWithPolicyName(preAuthorizationRequestPolicyDetail.getPolicyName())
                    .updateWithPlanCode(preAuthorizationRequestPolicyDetail.getPlanCode())
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
        if(isNotEmpty(proposer)){
            preAuthorizationClaimantProposerDetail = new PreAuthorizationClaimantProposerDetail();
            preAuthorizationClaimantProposerDetail.updateWithProposerDetails(proposer);
        }
        return preAuthorizationClaimantProposerDetail;
    }

    private Set<ClaimantPolicyDetailDto.CoverageDetailDto> constructCoverageListFromPreAuthorizationRequestAssuredDetail(ClaimantPolicyDetailDto claimantPolicyDetailDto, Set<PreAuthorizationRequestCoverageDetail> coverageDetailDtoList) {
        return isNotEmpty(coverageDetailDtoList) ? coverageDetailDtoList.parallelStream().map(new Function<PreAuthorizationRequestCoverageDetail, ClaimantPolicyDetailDto.CoverageDetailDto>() {
            @Override
            public ClaimantPolicyDetailDto.CoverageDetailDto apply(PreAuthorizationRequestCoverageDetail preAuthorizationRequestCoverageDetail) {
                ClaimantPolicyDetailDto.CoverageDetailDto coverageDetailDto = claimantPolicyDetailDto.new CoverageDetailDto();
                try {
                    BeanUtils.copyProperties(coverageDetailDto, preAuthorizationRequestCoverageDetail);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return coverageDetailDto;
            }
        }).collect(Collectors.toSet()) : Sets.newHashSet();
    }

    private DependentAssuredDetail constructDependentAssuredDetailFromPreAuthorizationRequestAssuredDetail(PreAuthorizationRequestAssuredDetail assuredDetail) {
        DependentAssuredDetail dependentAssuredDetail = null;
        if(assuredDetail.isDependentAssuredDetailPresent()){
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
        if(!preAuthorizationRequestAssuredDetail.isDependentAssuredDetailPresent()){
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
        }).filter(Objects::nonNull).collect(Collectors.toList()) :  Lists.newArrayList();
    }

    private IllnessDetailDto constructIllnessDetailDtoFromPreAuthorizationRequest(PreAuthorizationRequestIllnessDetail preAuthorizationRequestIllnessDetail) {
        IllnessDetailDto illnessDetailDto = null;
        if(isNotEmpty(preAuthorizationRequestIllnessDetail)){
            illnessDetailDto = new IllnessDetailDto();
            try {
                BeanUtils.copyProperties(illnessDetailDto, preAuthorizationRequestIllnessDetail);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return illnessDetailDto;
    }

    private List<DiagnosisTreatmentDto>  constructDiagnosisTreatmentDtoListFromPreAuthorizationRequest(Set<PreAuthorizationRequestDiagnosisTreatmentDetail> preAuthorizationRequestDiagnosisTreatmentDetails) {
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
        if(isNotEmpty(preAuthorizationRequestHCPDetail)){
            claimantHCPDetailDto = new ClaimantHCPDetailDto();
            try {
                BeanUtils.copyProperties(claimantHCPDetailDto, preAuthorizationRequestHCPDetail);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return claimantHCPDetailDto;
    }
}
