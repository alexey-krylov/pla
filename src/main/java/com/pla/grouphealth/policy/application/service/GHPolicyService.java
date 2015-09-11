package com.pla.grouphealth.policy.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.core.domain.model.agent.AgentId;
import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.grouphealth.policy.presentation.dto.GHPolicyMailDto;
import com.pla.grouphealth.policy.presentation.dto.PolicyDetailDto;
import com.pla.grouphealth.policy.presentation.dto.SearchGHPolicyDto;
import com.pla.grouphealth.policy.query.GHPolicyFinder;
import com.pla.grouphealth.policy.repository.GHPolicyRepository;
import com.pla.grouphealth.proposal.presentation.dto.GHProposalMandatoryDocumentDto;
import com.pla.grouphealth.sharedresource.dto.AgentDetailDto;
import com.pla.grouphealth.sharedresource.dto.GHInsuredDto;
import com.pla.grouphealth.sharedresource.dto.GHPremiumDetailDto;
import com.pla.grouphealth.sharedresource.dto.ProposerDto;
import com.pla.grouphealth.sharedresource.model.vo.*;
import com.pla.grouphealth.sharedresource.query.GHFinder;
import com.pla.grouphealth.sharedresource.service.GHInsuredExcelGenerator;
import com.pla.publishedlanguage.dto.ClientDocumentDto;
import com.pla.publishedlanguage.dto.SearchDocumentDetailDto;
import com.pla.publishedlanguage.underwriter.contract.IUnderWriterAdapter;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.OpportunityId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PolicyId;
import com.pla.sharedkernel.util.PDFGeneratorUtils;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.velocity.app.VelocityEngine;
import org.joda.time.DateTime;
import org.nthdimenzion.common.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.velocity.VelocityEngineUtils;

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
public class GHPolicyService {

    @Autowired
    private GHPolicyFinder ghPolicyFinder;

    @Autowired
    private GHFinder ghFinder;

    @Autowired
    private GHPolicyRepository ghPolicyRepository;

    @Autowired
    private GHInsuredExcelGenerator ghInsuredExcelGenerator;

    @Autowired
    private IUnderWriterAdapter underWriterAdapter;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private VelocityEngine velocityEngine;


    public List<PolicyDetailDto> findAllPolicy() {
        List<Map> allPolicies = ghPolicyFinder.findAllPolicy();
        if (isEmpty(allPolicies)) {
            return Lists.newArrayList();
        }
        List<PolicyDetailDto> policies = allPolicies.stream().map(new Function<Map, PolicyDetailDto>() {
            @Override
            public PolicyDetailDto apply(Map map) {
                PolicyDetailDto policyDetailDto = transformToDto(map);
                return policyDetailDto;
            }
        }).collect(Collectors.toList());
        return policies;
    }

    public PolicyDetailDto getPolicyDetail(String policyId) {
        Map policyMap = ghPolicyFinder.findPolicyById(policyId);
        PolicyDetailDto policyDetailDto = transformToDto(policyMap);
        return policyDetailDto;
    }

    public List<PolicyDetailDto> searchPolicy(SearchGHPolicyDto searchGHPolicyDto) {
        List<Map> searchedPolices = ghPolicyFinder.searchPolicy(searchGHPolicyDto.getPolicyNumber(), searchGHPolicyDto.getPolicyHolderName(),searchGHPolicyDto.getProposalNumber());
        if (isEmpty(searchedPolices)) {
            return Lists.newArrayList();
        }
        List<PolicyDetailDto> transformedPolicies = searchedPolices.stream().map(new Function<Map, PolicyDetailDto>() {
            @Override
            public PolicyDetailDto apply(Map map) {
                PolicyDetailDto policyDetailDto = transformToDto(map);
                return policyDetailDto;
            }
        }).collect(Collectors.toList());
        return transformedPolicies;
    }

    private PolicyDetailDto transformToDto(Map policyMap) {
        DateTime inceptionDate = policyMap.get("inceptionOn") != null ? new DateTime(policyMap.get("inceptionOn")) : null;
        DateTime expiryDate = policyMap.get("expiredOn") != null ? new DateTime(policyMap.get("expiredOn")) : null;
        GHProposer ghProposer = policyMap.get("proposer") != null ? (GHProposer) policyMap.get("proposer") : null;
        PolicyNumber policyNumber = policyMap.get("policyNumber") != null ? (PolicyNumber) policyMap.get("policyNumber") : null;//
        PolicyDetailDto policyDetailDto = new PolicyDetailDto();
        policyDetailDto.setPolicyId(policyMap.get("_id").toString());
        policyDetailDto.setInceptionDate(inceptionDate);
        policyDetailDto.setExpiryDate(expiryDate);
        policyDetailDto.setPolicyHolderName(ghProposer != null ? ghProposer.getProposerName() : "");
        policyDetailDto.setPolicyNumber(policyNumber.getPolicyNumber());
        policyDetailDto.setStatus((String) policyMap.get("status"));
        return policyDetailDto;
    }

    public AgentDetailDto getAgentDetail(PolicyId policyId) {
        Map policyMap = ghPolicyFinder.findPolicyById(policyId.getPolicyId());
        AgentId agentMap = (AgentId) policyMap.get("agentId");
        String agentId = agentMap.getAgentId();
        Map<String, Object> agentDetail = ghFinder.getAgentById(agentId);
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

    public GHPremiumDetailDto getPremiumDetail(PolicyId policyId) {
        GroupHealthPolicy groupHealthPolicy = ghPolicyRepository.findOne(policyId);
        GHPremiumDetailDto premiumDetailDto = getPremiumDetail(groupHealthPolicy);
        return premiumDetailDto;
    }

    private GHPremiumDetailDto getPremiumDetail(GroupHealthPolicy groupHealthPolicy) {
        GHPremiumDetail premiumDetail = groupHealthPolicy.getPremiumDetail();
        if (premiumDetail == null) {
            return new GHPremiumDetailDto();
        }
        GHPremiumDetailDto premiumDetailDto = new GHPremiumDetailDto(premiumDetail.getAddOnBenefit(), premiumDetail.getProfitAndSolvency(), premiumDetail.getDiscount(), premiumDetail.getWaiverOfExcessLoading(), premiumDetail.getVat(), premiumDetail.getPolicyTermValue());
        GHPremiumDetail.PremiumInstallment premiumInstallment = premiumDetail.getPremiumInstallment();
        if (premiumInstallment != null) {
            premiumDetailDto = premiumDetailDto.addOptedInstallmentDetail(premiumInstallment.getNoOfInstallment(), premiumInstallment.getInstallmentAmount());
        }
        if (isNotEmpty(premiumDetail.getInstallments())) {
            for (GHPremiumDetail.PremiumInstallment installment : premiumDetail.getInstallments()) {
                premiumDetailDto = premiumDetailDto.addInstallments(installment.getNoOfInstallment(), installment.getInstallmentAmount());
            }
        }
        premiumDetailDto = premiumDetailDto.addFrequencyPremiumAmount(premiumDetail.getAnnualPremiumAmount(), premiumDetail.getSemiAnnualPremiumAmount(), premiumDetail.getQuarterlyPremiumAmount(), premiumDetail.getMonthlyPremiumAmount());
        premiumDetailDto = premiumDetailDto.addNetTotalPremiumAmount(premiumDetail.getNetTotalPremium());
        premiumDetailDto = premiumDetailDto.updateWithOptedFrequency(premiumDetail.getOptedFrequencyPremium() != null ? premiumDetail.getOptedFrequencyPremium().getPremiumFrequency() : null);
        return premiumDetailDto;
    }

    public ProposerDto getProposerDetail(PolicyId policyId) {
        Map proposal = ghPolicyFinder.findPolicyById(policyId.getPolicyId());
        GHProposer proposer = (GHProposer) proposal.get("proposer");
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
        Map policyMap = ghPolicyFinder.findPolicyById(policyId);
        List<GHInsured> insureds = (List<GHInsured>) policyMap.get("insureds");
        List<GHInsuredDto> insuredDtoList = isNotEmpty(insureds) ? insureds.stream().map(new Function<GHInsured, GHInsuredDto>() {
            @Override
            public GHInsuredDto apply(GHInsured insured) {
                GHInsuredDto insuredDto = new GHInsuredDto();
                insuredDto.setCompanyName(insured.getCompanyName());
                insuredDto.setManNumber(insured.getManNumber());
                insuredDto.setNrcNumber(insured.getNrcNumber());
                insuredDto.setSalutation(insured.getSalutation());
                insuredDto.setFirstName(insured.getFirstName());
                insuredDto.setLastName(insured.getLastName());
                insuredDto.setDateOfBirth(insured.getDateOfBirth());
                insuredDto.setGender(insured.getGender());
                insuredDto.setCategory(insured.getCategory());
                insuredDto.setOccupationClass(insured.getOccupationClass());
                insuredDto.setOccupationCategory(insured.getOccupationCategory());
                insuredDto.setNoOfAssured(insured.getNoOfAssured());
                insuredDto.setMinAgeEntry(insured.getMinAgeEntry());
                insuredDto.setMaxAgeEntry(insured.getMaxAgeEntry());
                insuredDto.setExistingIllness(insured.getExistingIllness());
                GHPlanPremiumDetail planPremiumDetail = insured.getPlanPremiumDetail();
                GHInsuredDto.GHPlanPremiumDetailDto planPremiumDetailDto = new GHInsuredDto.GHPlanPremiumDetailDto(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getPlanCode(), planPremiumDetail.getPremiumAmount(), planPremiumDetail.getSumAssured());
                insuredDto = insuredDto.addPlanPremiumDetail(planPremiumDetailDto);
                List<GHInsuredDto.GHCoveragePremiumDetailDto> coveragePremiumDetailDtoList = isNotEmpty(insured.getPlanPremiumDetail().getCoveragePremiumDetails()) ? insured.getPlanPremiumDetail().getCoveragePremiumDetails().stream().map(new Function<GHCoveragePremiumDetail, GHInsuredDto.GHCoveragePremiumDetailDto>() {
                    @Override
                    public GHInsuredDto.GHCoveragePremiumDetailDto apply(GHCoveragePremiumDetail coveragePremiumDetail) {
                        final GHInsuredDto.GHCoveragePremiumDetailDto[] coveragePremiumDetailDto = {new GHInsuredDto.GHCoveragePremiumDetailDto(coveragePremiumDetail.getCoverageCode(),
                                coveragePremiumDetail.getCoverageId().getCoverageId(), coveragePremiumDetail.getPremium(), coveragePremiumDetail.getSumAssured(), coveragePremiumDetail.getPremiumVisibility())};
                        if (isNotEmpty(coveragePremiumDetail.getBenefitPremiumLimits())) {
                            coveragePremiumDetail.getBenefitPremiumLimits().forEach(benefitPremiumLimit -> {
                                GHInsuredDto.GHCoveragePremiumDetailDto.GHCoverageBenefitDetailDto ghCoverageBenefitDetailDto = new GHInsuredDto.GHCoveragePremiumDetailDto.GHCoverageBenefitDetailDto();
                                ghCoverageBenefitDetailDto.setBenefitCode(benefitPremiumLimit.getBenefitCode());
                                ghCoverageBenefitDetailDto.setBenefitId(benefitPremiumLimit.getBenefitId() != null ? benefitPremiumLimit.getBenefitId().getBenefitId() : "");
                                ghCoverageBenefitDetailDto.setBenefitLimit(benefitPremiumLimit.getBenefitLimit());
                                coveragePremiumDetailDto[0] = coveragePremiumDetailDto[0].addBenefit(ghCoverageBenefitDetailDto);
                            });
                        }
                        return coveragePremiumDetailDto[0];
                    }
                }).collect(Collectors.toList()) : Lists.newArrayList();
                insuredDto = insuredDto.addCoveragePremiumDetails(coveragePremiumDetailDtoList);
                Set<GHInsuredDto.GHInsuredDependentDto> insuredDependentDtoList = isNotEmpty(insured.getInsuredDependents()) ? insured.getInsuredDependents().stream().map(new Function<GHInsuredDependent, GHInsuredDto.GHInsuredDependentDto>() {
                    @Override
                    public GHInsuredDto.GHInsuredDependentDto apply(GHInsuredDependent insuredDependent) {
                        GHInsuredDto.GHInsuredDependentDto insuredDependentDto = new GHInsuredDto.GHInsuredDependentDto();
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
                        insuredDependentDto.setExistingIllness(insuredDependent.getExistingIllness());
                        insuredDependentDto.setMinAgeEntry(insuredDependent.getMinAgeEntry());
                        insuredDependentDto.setMaxAgeEntry(insuredDependent.getMaxAgeEntry());
                        insuredDependentDto.setOccupationClass(insuredDependent.getOccupationClass());
                        insuredDependentDto.setOccupationCategory(insuredDependent.getOccupationCategory());
                        insuredDependentDto.setNoOfAssured(insuredDependent.getNoOfAssured());
                        GHPlanPremiumDetail planPremiumDetail = insuredDependent.getPlanPremiumDetail();
                        GHInsuredDto.GHPlanPremiumDetailDto planPremiumDetailDto = new GHInsuredDto.GHPlanPremiumDetailDto(planPremiumDetail.getPlanId().getPlanId(), planPremiumDetail.getPlanCode(), planPremiumDetail.getPremiumAmount(), planPremiumDetail.getSumAssured());
                        insuredDependentDto = insuredDependentDto.addPlanPremiumDetail(planPremiumDetailDto);
                        List<GHInsuredDto.GHCoveragePremiumDetailDto> dependentCoveragePremiumDetailDtoList = isNotEmpty(insuredDependent.getPlanPremiumDetail().getCoveragePremiumDetails()) ? insuredDependent.getPlanPremiumDetail().getCoveragePremiumDetails().stream().map(new Function<GHCoveragePremiumDetail, GHInsuredDto.GHCoveragePremiumDetailDto>() {
                            @Override
                            public GHInsuredDto.GHCoveragePremiumDetailDto apply(GHCoveragePremiumDetail coveragePremiumDetail) {
                                final GHInsuredDto.GHCoveragePremiumDetailDto[] coveragePremiumDetailDto = {new GHInsuredDto.GHCoveragePremiumDetailDto(coveragePremiumDetail.getCoverageCode(),
                                        coveragePremiumDetail.getCoverageId().getCoverageId(), coveragePremiumDetail.getPremium(), coveragePremiumDetail.getSumAssured(), coveragePremiumDetail.getPremiumVisibility())};
                                if (isNotEmpty(coveragePremiumDetail.getBenefitPremiumLimits())) {
                                    coveragePremiumDetail.getBenefitPremiumLimits().forEach(benefitPremiumLimit -> {
                                        GHInsuredDto.GHCoveragePremiumDetailDto.GHCoverageBenefitDetailDto ghCoverageBenefitDetailDto = new GHInsuredDto.GHCoveragePremiumDetailDto.GHCoverageBenefitDetailDto();
                                        ghCoverageBenefitDetailDto.setBenefitCode(benefitPremiumLimit.getBenefitCode());
                                        ghCoverageBenefitDetailDto.setBenefitId(benefitPremiumLimit.getBenefitId() != null ? benefitPremiumLimit.getBenefitId().getBenefitId() : "");
                                        ghCoverageBenefitDetailDto.setBenefitLimit(benefitPremiumLimit.getBenefitLimit());
                                        coveragePremiumDetailDto[0] = coveragePremiumDetailDto[0].addBenefit(ghCoverageBenefitDetailDto);
                                    });
                                }
                                return coveragePremiumDetailDto[0];
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
        HSSFWorkbook hssfWorkbook = ghInsuredExcelGenerator.generateInsuredExcel(insuredDtoList, planIds);
        return hssfWorkbook;
    }

    private List<PlanId> getAgentAuthorizedPlans(String agentId) {
        List<Map<String, Object>> authorizedPlans = ghFinder.getAgentAuthorizedPlan(agentId);
        List<PlanId> planIds = authorizedPlans.stream().map(new Function<Map<String, Object>, PlanId>() {
            @Override
            public PlanId apply(Map<String, Object> authorizePlanMap) {
                String planId = (String) authorizePlanMap.get("planId");
                return new PlanId(planId);
            }
        }).collect(Collectors.toList());
        return planIds;
    }

    public List<GHProposalMandatoryDocumentDto> findMandatoryDocuments(String policyId) {
        Map policyMap = ghPolicyFinder.findPolicyById(policyId);
        List<GHInsured> insureds = (List<GHInsured>) policyMap.get("insureds");
        List<GHProposerDocument> uploadedDocuments = policyMap.get("proposerDocuments") != null ? (List<GHProposerDocument>) policyMap.get("proposerDocuments") : Lists.newArrayList();
        List<SearchDocumentDetailDto> documentDetailDtos = Lists.newArrayList();
        insureds.forEach(ghInsured -> {
            GHPlanPremiumDetail planPremiumDetail = ghInsured.getPlanPremiumDetail();
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
            if (isNotEmpty(ghInsured.getInsuredDependents())) {
                ghInsured.getInsuredDependents().forEach(insuredDependent -> {
                    GHPlanPremiumDetail dependentPlanPremiumDetail = insuredDependent.getPlanPremiumDetail();
                    documentDetailDtos.add(new SearchDocumentDetailDto(dependentPlanPremiumDetail.getPlanId()));
                    if (isNotEmpty(dependentPlanPremiumDetail.getCoveragePremiumDetails())) {
                        List<CoverageId> coverageIds = dependentPlanPremiumDetail.getCoveragePremiumDetails().stream().map(new Function<GHCoveragePremiumDetail, CoverageId>() {
                            @Override
                            public CoverageId apply(GHCoveragePremiumDetail ghCoveragePremiumDetail) {
                                return ghCoveragePremiumDetail.getCoverageId();
                            }
                        }).collect(Collectors.toList());
                        documentDetailDtos.add(new SearchDocumentDetailDto(planPremiumDetail.getPlanId(), coverageIds));
                    }
                });
            }
        });
        Set<ClientDocumentDto> mandatoryDocuments = underWriterAdapter.getMandatoryDocumentsForApproverApproval(documentDetailDtos, ProcessType.ENROLLMENT);
        List<GHProposalMandatoryDocumentDto> mandatoryDocumentDtos = Lists.newArrayList();
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


    public GHPolicyMailDto getPreScriptedEmail(PolicyId policyId) {
        GroupHealthPolicy groupHealthPolicy = ghPolicyRepository.findOne(policyId);
        String subject = "PLA Insurance - Group Health - Quotation ID : " + groupHealthPolicy.getPolicyNumber();
        String mailAddress = groupHealthPolicy.getProposer().getContactDetail() != null ? groupHealthPolicy.getProposer().getContactDetail().getContactPersonDetail().getContactPersonEmail() : "";
        mailAddress = isEmpty(mailAddress) ? "" : mailAddress;
        Map<String, Object> emailContent = Maps.newHashMap();
        emailContent.put("mailSentDate", groupHealthPolicy.getInceptionOn().toString(AppConstants.DD_MM_YYY_FORMAT));
        emailContent.put("contactPersonName",
                groupHealthPolicy.getProposer().getContactDetail() != null ? groupHealthPolicy.getProposer().getContactDetail().getContactPersonDetail().getContactPersonName() : "");
        emailContent.put("proposerName", groupHealthPolicy.getProposer().getProposerName());
        Map<String, Object> emailContentMap = Maps.newHashMap();
        emailContentMap.put("emailContent", emailContent);
        String emailBody = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "emailtemplate/grouphealth/policy/grouphealthPolicyTemplate.vm", emailContentMap);
        GHPolicyMailDto dto = new GHPolicyMailDto(subject, emailBody, new String[]{mailAddress});
        dto.setPolicyId(policyId.getPolicyId());
        dto.setPolicyNumber(groupHealthPolicy.getPolicyNumber().getPolicyNumber());
        return dto;
    }


    public byte[] getPolicyPDF(String policyId) throws IOException, JRException {
        byte[] pdfData = PDFGeneratorUtils.createPDFReportByList(Lists.newArrayList(), "jasperpdf/template/grouphealth/policy/GHPolicy.jrxml");
        return pdfData;
    }
}
