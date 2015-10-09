package com.pla.individuallife.policy.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.individuallife.policy.finder.ILPolicyFinder;
import com.pla.individuallife.policy.presentation.dto.ILPolicyDto;
import com.pla.individuallife.policy.presentation.dto.ILPolicyMailDto;
import com.pla.individuallife.policy.presentation.dto.PolicyDetailDto;
import com.pla.individuallife.policy.presentation.dto.SearchILPolicyDto;
import com.pla.individuallife.proposal.presentation.dto.ILProposalMandatoryDocumentDto;
import com.pla.individuallife.sharedresource.dto.AgentDetailDto;
import com.pla.individuallife.sharedresource.model.vo.*;
import com.pla.publishedlanguage.dto.ClientDocumentDto;
import com.pla.publishedlanguage.dto.SearchDocumentDetailDto;
import com.pla.publishedlanguage.dto.UnderWriterRoutingLevelDetailDto;
import com.pla.publishedlanguage.underwriter.contract.IUnderWriterAdapter;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.sharedkernel.domain.model.RoutingLevel;
import com.pla.sharedkernel.identifier.CoverageId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PolicyId;
import com.pla.sharedkernel.util.PDFGeneratorUtils;
import com.pla.underwriter.domain.model.UnderWriterDocument;
import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import com.pla.underwriter.domain.model.UnderWriterRoutingLevel;
import com.pla.underwriter.exception.UnderWriterException;
import com.pla.underwriter.finder.UnderWriterFinder;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.app.VelocityEngine;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Years;
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

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Admin on 8/4/2015.
 */
@Service
public class ILPolicyService {

    @Autowired
    private ILPolicyFinder ilPolicyFinder;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private IUnderWriterAdapter underWriterAdapter;

    @Autowired
    private UnderWriterFinder underWriterFinder;

    @Autowired
    private VelocityEngine velocityEngine;

    private PolicyDetailDto transformToDto(Map policyMap) {
        DateTime inceptionDate = policyMap.get("inceptionOn") != null ? new DateTime(policyMap.get("inceptionOn")) : null;
        DateTime expiryDate = policyMap.get("expiredOn") != null ? new DateTime(policyMap.get("expiredOn")) : null;
        Proposer ghProposer = policyMap.get("proposer") != null ? (Proposer) policyMap.get("proposer") : null;
        PolicyNumber policyNumber = policyMap.get("policyNumber") != null ? (PolicyNumber) policyMap.get("policyNumber") : null;//
        PolicyDetailDto policyDetailDto = new PolicyDetailDto();
        policyDetailDto.setPolicyId(policyMap.get("_id").toString());
        policyDetailDto.setInceptionDate(inceptionDate);
        policyDetailDto.setExpiryDate(expiryDate);
        policyDetailDto.setPolicyHolderName(ghProposer != null ? ghProposer.getFirstName() : "");
        policyDetailDto.setPolicyNumber(policyNumber.getPolicyNumber());
        policyDetailDto.setStatus((String) policyMap.get("policyStatus"));
        return policyDetailDto;
    }

    public PolicyDetailDto getPolicyDetail(String policyId) {
        Map policyMap = ilPolicyFinder.findPolicyById(policyId);
        PolicyDetailDto policyDetailDto = transformToDto(policyMap);
        return policyDetailDto;
    }

    public List<PolicyDetailDto> findAllPolicy() {
        List<Map> allPolicies = ilPolicyFinder.findAllPolicy();
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

    public List<PolicyDetailDto> searchPolicy(SearchILPolicyDto searchILPolicyDto) {
        List<Map> searchedPolices = ilPolicyFinder.searchPolicy(searchILPolicyDto.getPolicyNumber(), searchILPolicyDto.getPolicyHolderName(),searchILPolicyDto.getProposalNumber());
        if (isEmpty(searchedPolices)) {
            return Lists.newArrayList();
        }
        List<PolicyDetailDto> transformedPolicies = searchedPolices.stream().map(new Function<Map,PolicyDetailDto>() {
            @Override
            public PolicyDetailDto apply(Map map) {
                PolicyDetailDto policyDetailDto = transformToDto(map);
                return policyDetailDto;
            }
        }).collect(Collectors.toList());
        return transformedPolicies;
    }

    public List<ILProposalMandatoryDocumentDto> findMandatoryDocuments(String policyId) {
        Map proposal =  ilPolicyFinder.getPolicyByPolicyId(new PolicyId(policyId));
        if (isEmpty(proposal))
            return Collections.EMPTY_LIST;
        List<ILProposerDocument> uploadedDocuments = proposal.get("proposalDocuments") != null ? (List<ILProposerDocument>) proposal.get("proposalDocuments") : Lists.newArrayList();
        ProposalPlanDetail planDetail = (ProposalPlanDetail) proposal.get("proposalPlanDetail");
        if (planDetail==null){
            return Collections.EMPTY_LIST;
        }
        UnderWriterRoutingLevelDetailDto routingLevelDetailDto = new UnderWriterRoutingLevelDetailDto(new PlanId(planDetail.getPlanId()), LocalDate.now(), ProcessType.ENROLLMENT.name());
        DateTime dob = new DateTime(((ProposedAssured) proposal.get("proposedAssured")).getDateOfBirth());
        Integer age = Years.yearsBetween(dob, DateTime.now()).getYears() + 1;
        RoutingLevel routinglevel = findRoutingLevel(routingLevelDetailDto, policyId, age);
        List<ClientDocumentDto> mandatoryDocuments = new ArrayList<ClientDocumentDto>();
        if (routinglevel != null) {
            mandatoryDocuments = findClientDocument(routingLevelDetailDto, proposal, age);
        } else {
            List<SearchDocumentDetailDto> documentDetailDtos = Lists.newArrayList();
            SearchDocumentDetailDto searchDocumentDetailDto = new SearchDocumentDetailDto(new PlanId(planDetail.getPlanId()));
            documentDetailDtos.add(searchDocumentDetailDto);
            List<CoverageId> coverageIds = planDetail.getRiderDetails().stream().map(rider -> new CoverageId(rider.getCoverageId())).collect(Collectors.toList());
            documentDetailDtos.add(new SearchDocumentDetailDto(new PlanId(planDetail.getPlanId()), coverageIds));
            mandatoryDocuments.addAll(underWriterAdapter.getMandatoryDocumentsForApproverApproval(documentDetailDtos, ProcessType.ENROLLMENT));
        }
        if (isNotEmpty(mandatoryDocuments)) {
            return mandatoryDocuments.stream().map(new Function<ClientDocumentDto, ILProposalMandatoryDocumentDto>() {
                @Override
                public ILProposalMandatoryDocumentDto apply(ClientDocumentDto clientDocumentDto) {
                    ILProposalMandatoryDocumentDto mandatoryDocumentDto = new ILProposalMandatoryDocumentDto(clientDocumentDto.getDocumentCode(), clientDocumentDto.getDocumentName());
                    mandatoryDocumentDto.setIsApproved(false);
                    mandatoryDocumentDto.setMandatory(false);
                    Optional<ILProposerDocument> proposerDocumentOptional = uploadedDocuments.stream().filter(new Predicate<ILProposerDocument>() {
                        @Override
                        public boolean test(ILProposerDocument ilProposerDocument) {
                            return clientDocumentDto.getDocumentCode().equals(ilProposerDocument.getDocumentId());
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
                                mandatoryDocumentDto.setIsApproved(proposerDocumentOptional.get().isApproved());
                                mandatoryDocumentDto.setMandatory(proposerDocumentOptional.get().isMandatory());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return mandatoryDocumentDto;
                }
            }).collect(Collectors.toList());
        }
        return Collections.EMPTY_LIST;
    }

    public RoutingLevel findRoutingLevel(UnderWriterRoutingLevelDetailDto routingLevelDetailDto, String policyId, Integer age) {
        Map proposal = ilPolicyFinder.getPolicyByPolicyId(new PolicyId(policyId));
        RoutingLevel oldRoutingLevel = null;
        RoutingLevel currentRoutingLevel = null;
        ProposalPlanDetail planDetail = (ProposalPlanDetail) proposal.get("proposalPlanDetail");
        currentRoutingLevel = findRoutingLevel(routingLevelDetailDto, proposal, age);
        for (ILRiderDetail rider : planDetail.getRiderDetails()) {
            routingLevelDetailDto.addCoverage(new CoverageId(rider.getCoverageId()));
            oldRoutingLevel = currentRoutingLevel;
            currentRoutingLevel = findRoutingLevel(routingLevelDetailDto, proposal, age);
            if( oldRoutingLevel!= null && currentRoutingLevel!= null && oldRoutingLevel.ordinal() < currentRoutingLevel.ordinal()) currentRoutingLevel = oldRoutingLevel;
            if(currentRoutingLevel == null && oldRoutingLevel != null) currentRoutingLevel = oldRoutingLevel;
        }
        return  currentRoutingLevel;
    }

    private RoutingLevel findRoutingLevel(UnderWriterRoutingLevelDetailDto routingLevelDetailDto, Map proposal, Integer age) {
        List<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem> underWriterInfluencingFactorItems = new ArrayList<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem>();
        try {
            UnderWriterRoutingLevel underWriterRoutingLevel = underWriterFinder.findUnderWriterRoutingLevel(routingLevelDetailDto);
            //TODO : need to add other influencing items
            for (UnderWriterInfluencingFactor underWriterInfluencingFactor : underWriterRoutingLevel.getUnderWriterInfluencingFactors()) {
                if (underWriterInfluencingFactor.name().equalsIgnoreCase(String.valueOf(UnderWriterInfluencingFactor.AGE)))
                    underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.AGE.name(), age.toString()));
                if (underWriterInfluencingFactor.name().equalsIgnoreCase(String.valueOf(UnderWriterInfluencingFactor.SUM_ASSURED))) {
                    //TODO : Need to add previous sum assured values from the previous policies
                    underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.SUM_ASSURED.name(), (((ProposalPlanDetail) proposal.get("proposalPlanDetail")).getSumAssured().toString())));
                }
            }
            routingLevelDetailDto.setUnderWriterInfluencingFactor(underWriterInfluencingFactorItems);
            return underWriterAdapter.getRoutingLevel(routingLevelDetailDto);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private List<ClientDocumentDto> findClientDocument(UnderWriterRoutingLevelDetailDto routingLevelDetailDto, Map proposal, Integer age) {

        List<ClientDocumentDto> mandatoryDocuments = new ArrayList<ClientDocumentDto>();

        List<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem> underWriterInfluencingFactorItems = new ArrayList<UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem>();
        try {
            UnderWriterDocument underWriterDocument = underWriterFinder.getUnderWriterDocumentSetUp(routingLevelDetailDto.getPlanId(), null, LocalDate.now(), ProcessType.ENROLLMENT.name());
            //TODO : need to add other influencing items
            for (UnderWriterInfluencingFactor underWriterInfluencingFactor : underWriterDocument.getUnderWriterInfluencingFactors()) {
                if (underWriterInfluencingFactor.name().equalsIgnoreCase(String.valueOf(UnderWriterInfluencingFactor.AGE)))
                    underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.AGE.name(), age.toString()));
                if (underWriterInfluencingFactor.name().equalsIgnoreCase(String.valueOf(UnderWriterInfluencingFactor.SUM_ASSURED))) {
                    //TODO : Need to add previous sum assured values from the previous policies
                    underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.SUM_ASSURED.name(), (((ProposalPlanDetail) proposal.get("proposalPlanDetail")).getSumAssured().toString())));
                }
            }
            routingLevelDetailDto.setUnderWriterInfluencingFactor(underWriterInfluencingFactorItems);
            mandatoryDocuments.addAll(underWriterAdapter.getDocumentsForUnderWriterApproval(routingLevelDetailDto));

        } catch (UnderWriterException ex) {
            ex.printStackTrace();

        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }

        ProposalPlanDetail planDetail = (ProposalPlanDetail) proposal.get("proposalPlanDetail");
        for (ILRiderDetail rider : planDetail.getRiderDetails()) {
            routingLevelDetailDto.addCoverage(new CoverageId(rider.getCoverageId()));
            try{
                UnderWriterDocument underWriterDocument = underWriterFinder.getUnderWriterDocumentSetUp(routingLevelDetailDto.getPlanId(), routingLevelDetailDto.getCoverageId(), LocalDate.now(), ProcessType.ENROLLMENT.name());
                //TODO : need to add other influencing items
                for (UnderWriterInfluencingFactor underWriterInfluencingFactor : underWriterDocument.getUnderWriterInfluencingFactors()) {
                    if (underWriterInfluencingFactor.name().equalsIgnoreCase(String.valueOf(UnderWriterInfluencingFactor.AGE)))
                        underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.AGE.name(), age.toString()));
                    if (underWriterInfluencingFactor.name().equalsIgnoreCase(String.valueOf(UnderWriterInfluencingFactor.SUM_ASSURED))) {
                        //TODO : Need to add previous sum assured values from the previous policies
                        underWriterInfluencingFactorItems.add(new UnderWriterRoutingLevelDetailDto.UnderWriterInfluencingFactorItem(UnderWriterInfluencingFactor.SUM_ASSURED.name(), (((ProposalPlanDetail) proposal.get("proposalPlanDetail")).getSumAssured().toString())));
                    }
                }
                routingLevelDetailDto.setUnderWriterInfluencingFactor(underWriterInfluencingFactorItems);
                mandatoryDocuments.addAll(underWriterAdapter.getDocumentsForUnderWriterApproval(routingLevelDetailDto));
            } catch (UnderWriterException ex) {
                ex.printStackTrace();

            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            }
        }

        return mandatoryDocuments;
    }

    public byte[] getPolicyDocument(PolicyId policyId) throws IOException, JRException {
        ILPolicyDto dto = ilPolicyFinder.getPolicyById(policyId);
        byte[] pdfData = PDFGeneratorUtils.createPDFReportByList(ilPolicyDetailTransformMap(dto), "jasperpdf/template/individuallife/policy/ilpolicy.jrxml");
        return pdfData;
    }

    public List<Map<String,Object>> ilPolicyDetailTransformMap(ILPolicyDto ilPolicyDto){
        String agentId = getDominantAgentId(ilPolicyDto.getAgentCommissionDetails());
        Map<String, Object> agentDetail = ilPolicyFinder.getAgentById(agentId);
        checkArgument(agentDetail != null);
        String branchName  = agentDetail.get("branchName") != null ? (String) agentDetail.get("branchName") : "";
        String agentName =  agentDetail.get("firstName") != null ? (String) agentDetail.get("firstName") : "" + " " + agentDetail.get("lastName") != null ? (String) agentDetail.get("lastName") : "";
        Map<String,Object> ilPolicyDetailMap = Maps.newLinkedHashMap();
        ilPolicyDetailMap.put("proposerName",  ilPolicyDto.getProposer().getFirstName() + " " + ilPolicyDto.getProposer().getSurname()!=null?ilPolicyDto.getProposer().getSurname():"");
        ilPolicyDetailMap.put("policyStartDate", ilPolicyDto.getInceptionOn());
        ilPolicyDetailMap.put("psOffice", branchName);
        ilPolicyDetailMap.put("agentName", agentName);
        ilPolicyDetailMap.put("agentCode", agentId);
        ilPolicyDetailMap.put("contactNumber",agentDetail.get("mobileNumber")!=null?agentDetail.get("mobileNumber"):"");
        ilPolicyDetailMap.put("address", ilPolicyDto.getProposer().getResidentialAddress().getAddress1()+" "+ilPolicyDto.getProposer().getResidentialAddress().getAddress2());
        ilPolicyDetailMap.put("telephoneNumber", ilPolicyDto.getProposer().getMobileNumber());
        ilPolicyDetailMap.put("policyNumber", ilPolicyDto.getPolicyNumber().getPolicyNumber());
        ilPolicyDetailMap.put("policyEndDate", ilPolicyDto.getExpiryDate());
        ilPolicyDetailMap.put("issueBranch", branchName);
        ilPolicyDetailMap.put("issuanceDate", ilPolicyDto.getInceptionOn());

        List<Map<String,Object>> coverDetails =  ilPolicyDto.getProposalPlanDetail().getRiderDetails().parallelStream().map(new Function<ILRiderDetail, Map<String,Object>>() {
            @Override
            public Map<String, Object> apply(ILRiderDetail ilRiderDetail) {
                Map<String,Object> coverageSumAssured = Maps.newLinkedHashMap();
                coverageSumAssured.put("planCoverageName", ilRiderDetail.getCoverageName());
                coverageSumAssured.put("planCoverageSumAssured", ilRiderDetail.getSumAssured()!=null?ilRiderDetail.getSumAssured().toString():"0");
                return coverageSumAssured;
            }
        }).collect(Collectors.toList());
        Map<String,Object> coverageSumAssured = Maps.newLinkedHashMap();
        coverageSumAssured.put("planCoverageName", ilPolicyDto.getProposalPlanDetail().getPlanName());
        coverageSumAssured.put("planCoverageSumAssured", ilPolicyDto.getProposalPlanDetail().getSumAssured().toString());
        ilPolicyDetailMap.put("coverDetails", coverDetails);

        return Lists.newArrayList(ilPolicyDetailMap);
    }

    public ILPolicyMailDto getPreScriptedEmail(String policyId) {
        ILPolicyDto dto = ilPolicyFinder.getPolicyById(new PolicyId(policyId));
        String subject = "PLA Insurance - Individual Life - Policy ID : " + dto.getPolicyNumber().getPolicyNumber();
        String mailAddress = dto.getProposer().getEmailAddress();
        mailAddress = isEmpty(mailAddress) ? "" : mailAddress;
        Map<String, Object> emailContent = Maps.newHashMap();
        emailContent.put("mailSentDate", dto.getInceptionOn());
        emailContent.put("contactPersonName", dto.getProposer().getFirstName());
        emailContent.put("proposerName", dto.getProposer().getFirstName());
        Map<String, Object> emailContentMap = Maps.newHashMap();
        emailContentMap.put("emailContent", emailContent);
        String emailBody = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "emailtemplate/individuallife/policy/individuallifePolicyTemplate.vm", emailContentMap);
        ILPolicyMailDto ilPolicyMailDto = new ILPolicyMailDto(subject, emailBody, new String[]{mailAddress},mailAddress);
        ilPolicyMailDto.setPolicyId(dto.getPolicyId());
        ilPolicyMailDto.setPolicyNumber(dto.getPolicyNumber().getPolicyNumber());
        return ilPolicyMailDto;
    }

    public byte[] getPolicyPDF(String policyId) throws IOException, JRException {
        ILPolicyDto dto = ilPolicyFinder.getPolicyById(new PolicyId(policyId));
        List<Map<String,Object>>  ilPolicyDetailTransformMap = ilPolicyDetailTransformMap(dto);
        byte[] pdfData = PDFGeneratorUtils.createPDFReportByList(ilPolicyDetailTransformMap, "jasperpdf/template/individuallife/policy/ilpolicy.jrxml");
        return pdfData;
    }


    private String getDominantAgentId(Set<AgentDetailDto> agentCommissionShares){
        Optional<AgentDetailDto> agentCommissionShareOptional = agentCommissionShares.parallelStream().max(new Comparator<AgentDetailDto>() {
            @Override
            public int compare(AgentDetailDto agentLeft, AgentDetailDto agentRight) {
                return agentLeft.getCommission().compareTo(agentRight.getCommission());
            }
        });
        if (agentCommissionShareOptional.isPresent()){
            return agentCommissionShareOptional.get().getAgentId();
        }
        return null;
    }

}
