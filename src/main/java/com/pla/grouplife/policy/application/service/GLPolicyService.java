package com.pla.grouplife.policy.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouplife.policy.domain.model.GroupLifePolicy;
import com.pla.grouplife.sharedresource.dto.GLPolicyDetailDto;
import com.pla.grouplife.sharedresource.dto.SearchGLPolicyDto;
import com.pla.grouplife.policy.query.GLPolicyFinder;
import com.pla.grouplife.policy.repository.GLPolicyRepository;
import com.pla.grouplife.proposal.domain.model.GLProposerDocument;
import com.pla.grouplife.proposal.presentation.dto.GLProposalMandatoryDocumentDto;
import com.pla.grouplife.sharedresource.dto.AgentDetailDto;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.grouplife.sharedresource.dto.PremiumDetailDto;
import com.pla.grouplife.sharedresource.dto.ProposerDto;
import com.pla.grouplife.sharedresource.model.vo.*;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.grouplife.sharedresource.service.GLInsuredExcelGenerator;
import com.pla.publishedlanguage.dto.ClientDocumentDto;
import com.pla.publishedlanguage.dto.SearchDocumentDetailDto;
import com.pla.publishedlanguage.underwriter.contract.IUnderWriterAdapter;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.identifier.OpportunityId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PolicyId;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 7/9/2015.
 */
@Service
public class GLPolicyService {

    @Autowired
    private GLPolicyFinder glPolicyFinder;

    @Autowired
    private GLFinder glFinder;

    @Autowired
    private GLPolicyRepository glPolicyRepository;

    @Autowired
    private GLInsuredExcelGenerator glInsuredExcelGenerator;

    @Autowired
    private IUnderWriterAdapter underWriterAdapter;

    @Autowired
    private GridFsTemplate gridFsTemplate;



    public List<GLPolicyDetailDto> findAllPolicy() {
        List<Map> allPolicies = glPolicyFinder.findAllPolicy();
        if (isEmpty(allPolicies)) {
            return Lists.newArrayList();
        }
        List<GLPolicyDetailDto> policies = allPolicies.stream().map(new Function<Map, GLPolicyDetailDto>() {
            @Override
            public GLPolicyDetailDto apply(Map map) {
                GLPolicyDetailDto policyDetailDto = transformToDto(map);
                return policyDetailDto;
            }
        }).collect(Collectors.toList());
        return policies;
    }

    public GLPolicyDetailDto getPolicyDetail(String policyId) {
        Map policyMap = glPolicyFinder.findPolicyById(policyId);
        GLPolicyDetailDto policyDetailDto = transformToDto(policyMap);
        return policyDetailDto;
    }

    public List<GLPolicyDetailDto> searchPolicy(SearchGLPolicyDto searchGLPolicyDto) {
        List<Map> searchedPolices = glPolicyFinder.searchPolicy(searchGLPolicyDto.getPolicyNumber(),searchGLPolicyDto.getClientId(), searchGLPolicyDto.getPolicyHolderName(),searchGLPolicyDto.getProposalNumber());
        if (isEmpty(searchedPolices)) {
            return Lists.newArrayList();
        }
        List<GLPolicyDetailDto> transformedPolicies = searchedPolices.stream().map(new Function<Map, GLPolicyDetailDto>() {
            @Override
            public GLPolicyDetailDto apply(Map map) {
                GLPolicyDetailDto policyDetailDto = transformToDto(map);
                return policyDetailDto;
            }
        }).collect(Collectors.toList());
        return transformedPolicies;
    }

    private GLPolicyDetailDto transformToDto(Map policyMap) {
        DateTime inceptionDate = policyMap.get("inceptionOn") != null ? new DateTime((Date) policyMap.get("inceptionOn")) : null;
        DateTime expiryDate = policyMap.get("expiredOn") != null ? new DateTime((Date) policyMap.get("expiredOn")) : null;
        Proposer glProposer = policyMap.get("proposer") != null ? (Proposer) policyMap.get("proposer") : null;
        PolicyNumber policyNumber = policyMap.get("policyNumber") != null ? (PolicyNumber) policyMap.get("policyNumber") : null;//
        GLPolicyDetailDto policyDetailDto = new GLPolicyDetailDto();
        policyDetailDto.setPolicyId(policyMap.get("_id").toString());
        policyDetailDto.setInceptionDate(inceptionDate);
        policyDetailDto.setExpiryDate(expiryDate);
        policyDetailDto.setPolicyHolderName(glProposer != null ? glProposer.getProposerName() : "");
        policyDetailDto.setPolicyNumber(policyNumber.getPolicyNumber());
        policyDetailDto.setStatus((String) policyMap.get("status"));
        return policyDetailDto;
    }

    public AgentDetailDto getAgentDetail(PolicyId policyId) {
        Map policyMap = glPolicyFinder.findPolicyById(policyId.getPolicyId());
        AgentId agentMap = (AgentId) policyMap.get("agentId");
        String agentId = agentMap.getAgentId();
        Map<String, Object> agentDetail = glFinder.getAgentById(agentId);
        AgentDetailDto agentDetailDto = new AgentDetailDto();
        agentDetailDto.setAgentId(agentId);
        agentDetailDto.setActive("ACTIVE".equalsIgnoreCase((String) agentDetail.get("agentStatus")));
        agentDetailDto.setBranchName((String) agentDetail.get("branchName"));
        agentDetailDto.setTeamName((String) agentDetail.get("teamName"));
        agentDetailDto.setAgentName(agentDetail.get("firstName") + " " + (agentDetail.get("lastName") == null ? "" : (String) agentDetail.get("lastName")));
        agentDetailDto.setAgentMobileNumber(agentDetail.get("mobileNumber") != null ? (String) agentDetail.get("mobileNumber") : "");
        agentDetailDto.setAgentSalutation(agentDetail.get("title") != null ? (String) agentDetail.get("title") : "");
        return agentDetailDto;
    }

    public PremiumDetailDto getPremiumDetail(PolicyId policyId) {
        GroupLifePolicy groupHealthPolicy = glPolicyRepository.findOne(policyId);
        PremiumDetailDto premiumDetailDto = getPremiumDetail(groupHealthPolicy);
        return premiumDetailDto;
    }

    private PremiumDetailDto getPremiumDetail(GroupLifePolicy groupLifePolicy) {
        PremiumDetail premiumDetail = groupLifePolicy.getPremiumDetail();
        if (premiumDetail == null) {
            return new PremiumDetailDto();
        }
        PremiumDetailDto premiumDetailDto = new PremiumDetailDto(premiumDetail.getAddOnBenefit(), premiumDetail.getProfitAndSolvency(), premiumDetail.getHivDiscount(), premiumDetail.getValuedClientDiscount(), premiumDetail.getLongTermDiscount(), premiumDetail.getPolicyTermValue());
        PremiumDetail.PremiumInstallment premiumInstallment = premiumDetail.getPremiumInstallment();
        if (premiumInstallment != null) {
            premiumDetailDto = premiumDetailDto.addOptedInstallmentDetail(premiumInstallment.getNoOfInstallment(), premiumInstallment.getInstallmentAmount());
        }
        if (isNotEmpty(premiumDetail.getInstallments())) {
            for (PremiumDetail.PremiumInstallment installment : premiumDetail.getInstallments()) {
                premiumDetailDto = premiumDetailDto.addInstallments(installment.getNoOfInstallment(), installment.getInstallmentAmount());
            }
        }
        premiumDetailDto = premiumDetailDto.addFrequencyPremiumAmount(premiumDetail.getAnnualPremiumAmount(), premiumDetail.getSemiAnnualPremiumAmount(), premiumDetail.getQuarterlyPremiumAmount(), premiumDetail.getMonthlyPremiumAmount());
        premiumDetailDto = premiumDetailDto.addNetTotalPremiumAmount(premiumDetail.getNetTotalPremium());
        premiumDetailDto = premiumDetailDto.updateWithOptedFrequency(premiumDetail.getOptedFrequencyPremium() != null ? premiumDetail.getOptedFrequencyPremium().getPremiumFrequency() : null);
        return premiumDetailDto;
    }

    public ProposerDto getProposerDetail(PolicyId policyId) {
        Map proposal = glPolicyFinder.findPolicyById(policyId.getPolicyId());
        Proposer proposer = (Proposer) proposal.get("proposer");
        ProposerDto proposerDto = new ProposerDto(proposer);
        if (proposal.get("opportunityId") != null) {
            OpportunityId opportunityId = (OpportunityId) proposal.get("opportunityId");
            proposerDto.setOpportunityId(opportunityId.getOpportunityId());
        }
        return proposerDto;
    }

    public HSSFWorkbook getInsuredTemplateExcel(String policyId) throws IOException {
        AgentDetailDto agentDetailDto = getAgentDetail(new PolicyId(policyId));
        List<PlanId> planIds = getAgentAuthorizedPlans(agentDetailDto.getAgentId());
        Map policyMap = glPolicyFinder.findPolicyById(policyId);
        List<Insured> insureds = (List<Insured>) policyMap.get("insureds");
        List<InsuredDto> insuredDtoList = isNotEmpty(insureds) ? insureds.stream().map(new Function<Insured, InsuredDto>() {
            @Override
            public InsuredDto apply(Insured insured) {
                InsuredDto insuredDto = new InsuredDto();
                insuredDto.setCompanyName(insured.getCompanyName());
                insuredDto.setManNumber(insured.getManNumber());
                insuredDto.setNrcNumber(insured.getNrcNumber());
                insuredDto.setSalutation(insured.getSalutation());
                insuredDto.setFirstName(insured.getFirstName());
                insuredDto.setLastName(insured.getLastName());
                insuredDto.setDateOfBirth(insured.getDateOfBirth());
                insuredDto.setGender(insured.getGender());
                insuredDto.setCategory(insured.getCategory());
                insuredDto.setAnnualIncome(insured.getAnnualIncome());
                insuredDto.setOccupationClass(insured.getOccupationClass());
                insuredDto.setOccupationCategory(insured.getCategory());
                insuredDto.setNoOfAssured(insured.getNoOfAssured());
                PlanPremiumDetail planPremiumDetail = insured.getPlanPremiumDetail();
                InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = new InsuredDto.PlanPremiumDetailDto(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getPlanCode(), planPremiumDetail.getPremiumAmount(), planPremiumDetail.getSumAssured());
                insuredDto = insuredDto.addPlanPremiumDetail(planPremiumDetailDto);
                List<InsuredDto.CoveragePremiumDetailDto> coveragePremiumDetailDtoList = isNotEmpty(insured.getCoveragePremiumDetails()) ? insured.getCoveragePremiumDetails().stream().map(new Function<CoveragePremiumDetail, InsuredDto.CoveragePremiumDetailDto>() {
                    @Override
                    public InsuredDto.CoveragePremiumDetailDto apply(CoveragePremiumDetail coveragePremiumDetail) {
                        InsuredDto.CoveragePremiumDetailDto coveragePremiumDetailDto = new InsuredDto.CoveragePremiumDetailDto(coveragePremiumDetail.getCoverageCode(),
                                coveragePremiumDetail.getCoverageId().getCoverageId(), coveragePremiumDetail.getPremium(), coveragePremiumDetail.getSumAssured());
                        return coveragePremiumDetailDto;
                    }
                }).collect(Collectors.toList()) : Lists.newArrayList();
                insuredDto = insuredDto.addCoveragePremiumDetails(coveragePremiumDetailDtoList);
                Set<InsuredDto.InsuredDependentDto> insuredDependentDtoList = isNotEmpty(insured.getInsuredDependents()) ? insured.getInsuredDependents().stream().map(new Function<InsuredDependent, InsuredDto.InsuredDependentDto>() {
                    @Override
                    public InsuredDto.InsuredDependentDto apply(InsuredDependent insuredDependent) {
                        InsuredDto.InsuredDependentDto insuredDependentDto = new InsuredDto.InsuredDependentDto();
                        insuredDependentDto.setCompanyName(insuredDependent.getCompanyName());
                        insuredDependentDto.setManNumber(insuredDependent.getManNumber());
                        insuredDependentDto.setNrcNumber(insuredDependent.getNrcNumber());
                        insuredDependentDto.setSalutation(insuredDependent.getSalutation());
                        insuredDependentDto.setFirstName(insuredDependent.getFirstName());
                        insuredDependentDto.setLastName(insuredDependent.getLastName());
                        insuredDependentDto.setDateOfBirth(insuredDependent.getDateOfBirth());
                        insuredDependentDto.setRelationship(insuredDependent.getRelationship());
                        insuredDependentDto.setGender(insuredDependent.getGender());
                        insuredDependentDto.setCategory(insuredDependent.getCategory());
                        insuredDependentDto.setOccupationClass(insuredDependent.getOccupationClass());
                        insuredDependentDto.setOccupationCategory(insuredDependent.getCategory());
                        insuredDependentDto.setNoOfAssured(insuredDependent.getNoOfAssured());
                        PlanPremiumDetail planPremiumDetail = insuredDependent.getPlanPremiumDetail();
                        InsuredDto.PlanPremiumDetailDto planPremiumDetailDto = new InsuredDto.PlanPremiumDetailDto(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getPlanCode(), planPremiumDetail.getPremiumAmount(), planPremiumDetail.getSumAssured());
                        insuredDependentDto = insuredDependentDto.addPlanPremiumDetail(planPremiumDetailDto);
                        List<InsuredDto.CoveragePremiumDetailDto> dependentCoveragePremiumDetailDtoList = isNotEmpty(insuredDependent.getCoveragePremiumDetails()) ? insuredDependent.getCoveragePremiumDetails().stream().map(new Function<CoveragePremiumDetail, InsuredDto.CoveragePremiumDetailDto>() {
                            @Override
                            public InsuredDto.CoveragePremiumDetailDto apply(CoveragePremiumDetail coveragePremiumDetail) {
                                InsuredDto.CoveragePremiumDetailDto coveragePremiumDetailDto = new InsuredDto.CoveragePremiumDetailDto(coveragePremiumDetail.getCoverageCode(),
                                        coveragePremiumDetail.getCoverageId().getCoverageId(), coveragePremiumDetail.getPremium(), coveragePremiumDetail.getSumAssured());
                                return coveragePremiumDetailDto;
                            }
                        }).collect(Collectors.toList()) : Lists.newArrayList();
                        insuredDependentDto = insuredDependentDto.addCoveragePremiumDetails(dependentCoveragePremiumDetailDtoList);
                        return insuredDependentDto;
                    }
                }).collect(Collectors.toSet()) : Sets.newHashSet();
                insuredDto = insuredDto.addInsuredDependent(insuredDependentDtoList);
                return insuredDto;
            }
        }).collect(Collectors.toList()) : Lists.newArrayList();
        HSSFWorkbook hssfWorkbook = glInsuredExcelGenerator.generateInsuredExcel(insuredDtoList, planIds);
        return hssfWorkbook;
    }

    private List<PlanId> getAgentAuthorizedPlans(String agentId) {
        List<Map<String, Object>> authorizedPlans = glFinder.getAgentAuthorizedPlan(agentId);
        List<PlanId> planIds = authorizedPlans.stream().map(new Function<Map<String, Object>, PlanId>() {
            @Override
            public PlanId apply(Map<String, Object> authorizePlanMap) {
                String planId = (String) authorizePlanMap.get("planId");
                return new PlanId(planId);
            }
        }).collect(Collectors.toList());
        return planIds;
    }

    public List<GLProposalMandatoryDocumentDto> findMandatoryDocuments(String policyId) {
        Map policyMap = glPolicyFinder.findPolicyById(policyId);
        List<Insured> insureds = (List<Insured>) policyMap.get("insureds");
        List<GLProposerDocument> uploadedDocuments = policyMap.get("proposerDocuments") != null ? (List<GLProposerDocument>) policyMap.get("proposerDocuments") : Lists.newArrayList();
        List<SearchDocumentDetailDto> documentDetailDtos = Lists.newArrayList();
        insureds.forEach(ghInsured -> {
            PlanPremiumDetail planPremiumDetail = ghInsured.getPlanPremiumDetail();
            SearchDocumentDetailDto searchDocumentDetailDto = new SearchDocumentDetailDto(planPremiumDetail.getPlanId());
            documentDetailDtos.add(searchDocumentDetailDto);
            if (isNotEmpty(ghInsured.getInsuredDependents())) {
                ghInsured.getInsuredDependents().forEach(insuredDependent -> {
                    PlanPremiumDetail dependentPlanPremiumDetail = insuredDependent.getPlanPremiumDetail();
                    documentDetailDtos.add(new SearchDocumentDetailDto(dependentPlanPremiumDetail.getPlanId()));
                });
            }
        });
        Set<ClientDocumentDto> mandatoryDocuments = underWriterAdapter.getMandatoryDocumentsForApproverApproval(documentDetailDtos, ProcessType.ENROLLMENT);
        List<GLProposalMandatoryDocumentDto> mandatoryDocumentDtos = Lists.newArrayList();
        if (isNotEmpty(mandatoryDocuments)) {
            mandatoryDocumentDtos = mandatoryDocuments.stream().map(new Function<ClientDocumentDto, GLProposalMandatoryDocumentDto>() {
                @Override
                public GLProposalMandatoryDocumentDto apply(ClientDocumentDto clientDocumentDto) {
                    GLProposalMandatoryDocumentDto mandatoryDocumentDto = new GLProposalMandatoryDocumentDto(clientDocumentDto.getDocumentCode(), clientDocumentDto.getDocumentName());
                    Optional<GLProposerDocument> proposerDocumentOptional = uploadedDocuments.stream().filter(new Predicate<GLProposerDocument>() {
                        @Override
                        public boolean test(GLProposerDocument glProposerDocument) {
                            return clientDocumentDto.getDocumentCode().equals(glProposerDocument.getDocumentId());
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

}
