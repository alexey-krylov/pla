package com.pla.grouplife.endorsement.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.grouplife.endorsement.application.service.excel.generator.GLEndorsementExcelGenerator;
import com.pla.grouplife.endorsement.application.service.excel.parser.GLEndorsementExcelParser;
import com.pla.grouplife.endorsement.dto.GLEndorsementInsuredDto;
import com.pla.grouplife.endorsement.presentation.dto.GLEndorsementDto;
import com.pla.grouplife.endorsement.presentation.dto.SearchGLEndorsementDto;
import com.pla.grouplife.endorsement.query.GLEndorsementFinder;
import com.pla.grouplife.policy.query.GLPolicyFinder;
import com.pla.grouplife.proposal.presentation.dto.GLProposalMandatoryDocumentDto;
import com.pla.grouplife.sharedresource.dto.GLPolicyDetailDto;
import com.pla.grouplife.sharedresource.dto.SearchGLPolicyDto;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.model.vo.GLProposerDocument;
import com.pla.grouplife.sharedresource.model.vo.Insured;
import com.pla.grouplife.sharedresource.model.vo.PlanPremiumDetail;
import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.publishedlanguage.contract.IPlanAdapter;
import com.pla.publishedlanguage.dto.ClientDocumentDto;
import com.pla.publishedlanguage.dto.SearchDocumentDetailDto;
import com.pla.publishedlanguage.underwriter.contract.IUnderWriterAdapter;
import com.pla.sharedkernel.domain.model.*;
import com.pla.sharedkernel.identifier.EndorsementId;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.PolicyId;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nthdimenzion.presentation.AppUtils.getIntervalInDays;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Samir on 8/5/2015.
 */
public class GLEndorsementService {

    @Autowired
    private GLFinder glFinder;

    @Autowired
    private GLEndorsementFinder glEndorsementFinder;

    @Autowired
    private IUnderWriterAdapter underWriterAdapter;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private IPlanAdapter iPlanAdapter;

    @Autowired
    private GLPolicyFinder glPolicyFinder;

    private final Map<GLEndorsementType, GLEndorsementExcelGenerator> excelGenerators;

    private final Map<GLEndorsementType, GLEndorsementExcelParser> excelParsers;


    public GLEndorsementService(Map<GLEndorsementType, GLEndorsementExcelGenerator> excelGenerators, Map<GLEndorsementType, GLEndorsementExcelParser> excelParsers) {
        this.excelGenerators = excelGenerators;
        this.excelParsers = excelParsers;
    }

    public List<GLPolicyDetailDto> searchPolicy(SearchGLPolicyDto searchGLPolicyDto) {
        List<Map> searchedPolices = glFinder.searchPolicy(searchGLPolicyDto.getPolicyNumber(), searchGLPolicyDto.getPolicyHolderName(), searchGLPolicyDto.getClientId(), new String[]{"IN_FORCE"}, searchGLPolicyDto.getProposalNumber());
        List<GLPolicyDetailDto> transformedPolicies = searchedPolices.stream().map(new Function<Map, GLPolicyDetailDto>() {
            @Override
            public GLPolicyDetailDto apply(Map map) {
                GLPolicyDetailDto policyDetailDto = transformToDto(map);
                Set<PlanId> planIds  = findSelectedPlanPolicy(map);
                Set<String> endorsementTypes =  iPlanAdapter.getConfiguredEndorsementType(planIds);
                policyDetailDto.setEndorsementTypes(getAllEndorsementTypeExcludingFCL(endorsementTypes));
                return policyDetailDto;
            }
        }).collect(Collectors.toList());
        return transformedPolicies;
    }


    private List<Map<String,String>> getAllEndorsementTypeExcludingFCL(Set<String> endorsementTypes){
        return Arrays.asList(GLEndorsementType.values()).parallelStream().filter(new Predicate<GLEndorsementType>() {
            @Override
            public boolean test(GLEndorsementType glEndorsementType) {
                return (endorsementTypes.contains(glEndorsementType.getDescription()) && !GLEndorsementType.FREE_COVER_LIMIT.equals(glEndorsementType));
            }
        }).map(endorsement -> {
            Map<String, String> endorsementMap = Maps.newLinkedHashMap();
            endorsementMap.put("code", endorsement.name());
            endorsementMap.put("description", endorsement.getDescription());
            return endorsementMap;
        }).collect(Collectors.toList());
    }


    private Set<PlanId> findSelectedPlanPolicy(Map searchedPolices){
        if (isEmpty(searchedPolices)){
            return Collections.EMPTY_SET;
        }
        List<Insured> insureds = (List<Insured>) searchedPolices.get("insureds");
        Set<PlanId> planIds = Sets.newLinkedHashSet();
        insureds.forEach(ghInsured -> {
            PlanPremiumDetail planPremiumDetail = ghInsured.getPlanPremiumDetail();
            planIds.add(planPremiumDetail.getPlanId());
            if (isNotEmpty(ghInsured.getInsuredDependents())) {
                ghInsured.getInsuredDependents().forEach(insuredDependent -> {
                    PlanPremiumDetail dependentPlanPremiumDetail = insuredDependent.getPlanPremiumDetail();
                    planIds.add(dependentPlanPremiumDetail.getPlanId());
                });
            }
        });
        return planIds;
    }

    private GLPolicyDetailDto transformToDto(Map policyMap) {
        DateTime inceptionDate = policyMap.get("inceptionOn") != null ? new DateTime(policyMap.get("inceptionOn")) : null;
        DateTime expiryDate = policyMap.get("expiredOn") != null ? new DateTime(policyMap.get("expiredOn")) : null;
        Proposer glProposer = policyMap.get("proposer") != null ? (Proposer) policyMap.get("proposer") : null;
        PolicyNumber policyNumber = policyMap.get("policyNumber") != null ? (PolicyNumber) policyMap.get("policyNumber") : null;
        GLPolicyDetailDto policyDetailDto = new GLPolicyDetailDto();
        policyDetailDto.setPolicyId(policyMap.get("_id").toString());
        policyDetailDto.setInceptionDate(inceptionDate);
        policyDetailDto.setExpiryDate(expiryDate);
        policyDetailDto.setPolicyHolderName(glProposer != null ? glProposer.getProposerName() : "");
        policyDetailDto.setPolicyNumber(policyNumber.getPolicyNumber());
        policyDetailDto.setStatus((String) policyMap.get("status"));
        return policyDetailDto;
    }

    public HSSFWorkbook generateEndorsementExcel(GLEndorsementType endorsementType, EndorsementId endorsementId) {
        GLEndorsementExcelGenerator glEndorsementExcelGenerator = excelGenerators.get(endorsementType);
        PolicyId policyId = findPolicyIdFromEndorsement(endorsementId);
        return glEndorsementExcelGenerator.generate(policyId, endorsementId);
    }

    private PolicyId findPolicyIdFromEndorsement(EndorsementId endorsementId) {
        Map glEndorsementMap = glEndorsementFinder.findEndorsementById(endorsementId.getEndorsementId());
        Policy policy = glEndorsementMap != null ? (Policy) glEndorsementMap.get("policy") : null;
        PolicyId policyId = policy != null ? policy.getPolicyId() : null;
        return policyId;
    }

    public boolean isValidExcel(GLEndorsementType glEndorsementType, HSSFWorkbook workbook, EndorsementId endorsementId) {
        GLEndorsementExcelParser glEndorsementExcelParser = excelParsers.get(glEndorsementType);
        PolicyId policyId = findPolicyIdFromEndorsement(endorsementId);
        return glEndorsementExcelParser.isValidExcel(workbook, policyId);
    }

    public GLEndorsementInsuredDto parseExcel(GLEndorsementType glEndorsementType, HSSFWorkbook workbook, EndorsementId endorsementId) {
        GLEndorsementExcelParser glEndorsementExcelParser = excelParsers.get(glEndorsementType);
        PolicyId policyId = findPolicyIdFromEndorsement(endorsementId);
        return glEndorsementExcelParser.transformExcelToGLEndorsementDto(workbook, policyId);
    }

    public List<GLEndorsementDto> searchEndorsement(SearchGLEndorsementDto searchGLEndorsementDto, String[] statuses) {
        List<Map> endorsements = glEndorsementFinder.searchEndorsement(searchGLEndorsementDto.getEndorsementType(), searchGLEndorsementDto.getEndorsementNumber(),
                searchGLEndorsementDto.getEndorsementId(), searchGLEndorsementDto.getPolicyNumber(), searchGLEndorsementDto.getPolicyHolderName(), statuses);
        if (isEmpty(endorsements)) {
            return Lists.newArrayList();
        }
        List<GLEndorsementDto> endorsementDtos = endorsements.stream().map(new Function<Map, GLEndorsementDto>() {
            @Override
            public GLEndorsementDto apply(Map map) {
                DateTime effectiveDate = map.get("effectiveDate") != null ? new DateTime(map.get("effectiveDate")) : null;
                String endorsementId = map.get("_id").toString();
                String endorsementNumber = map.get("endorsementNumber") != null ? ((EndorsementNumber) map.get("endorsementNumber")).getEndorsementNumber() : "";
                Policy policy = map.get("policy") != null ? (Policy) map.get("policy") : null;
                String policyNumber = policy != null ? policy.getPolicyNumber().getPolicyNumber() : "";
                String policyHolderName = policy != null ? policy.getPolicyHolderName() : "";
                String endorsementTypeInString = map.get("endorsementType") != null ? (String) map.get("endorsementType") : "";
                String endorsementStatus = map.get("status") != null ? (String) map.get("status") : "";
                String endorsementCode = "";
                if (isNotEmpty(endorsementTypeInString)) {
                    GLEndorsementType endorsementType = GLEndorsementType.valueOf(endorsementTypeInString);
                    endorsementTypeInString = endorsementType.getDescription();
                    endorsementCode = endorsementType.name();
                }
                if (isNotEmpty(endorsementStatus)) {
                    endorsementStatus = EndorsementStatus.valueOf(endorsementStatus).getDescription();
                }
                GLEndorsementDto glEndorsementDto = new GLEndorsementDto(endorsementId, endorsementNumber, policyNumber, endorsementTypeInString,endorsementCode, effectiveDate, policyHolderName, getIntervalInDays(effectiveDate), endorsementStatus);
                return glEndorsementDto;
            }
        }).collect(Collectors.toList());
        return endorsementDtos;
    }

    public PolicyId getPolicyIdFromEndorsment(String endorsementId) {
        Map endorsementMap = glEndorsementFinder.findEndorsementById(endorsementId);
        Policy policy = endorsementMap != null ? (Policy) endorsementMap.get("policy") : null;
        PolicyId policyId = policy != null ? policy.getPolicyId() : null;
        return policyId;
    }

    public Map<String, Object> getPolicyDetail(String endorsementId) throws ParseException {
        return glEndorsementFinder.getPolicyDetail(endorsementId);
    }

    public List<GLProposalMandatoryDocumentDto> findMandatoryDocuments(String endorsementId) {
        Map endorsementMap = glEndorsementFinder.findEndorsementById(endorsementId);
        PolicyId policyId = getPolicyIdFromEndorsment(endorsementId);
        Map policyMap = glPolicyFinder.findPolicyById(policyId.getPolicyId());
        List<Insured> insureds = (List<Insured>) policyMap.get("insureds");
        List<GLProposerDocument> uploadedDocuments = endorsementMap.get("proposerDocuments") != null ? (List<GLProposerDocument>) endorsementMap.get("proposerDocuments") : Lists.newArrayList();
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
        Set<ClientDocumentDto> mandatoryDocuments = underWriterAdapter.getMandatoryDocumentsForApproverApproval(documentDetailDtos, ProcessType.ENDORSEMENT);
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


    public Set<GLProposalMandatoryDocumentDto> findAdditionalDocuments(String endorsementId) {
        Map endorsementMap = glEndorsementFinder.findEndorsementById(endorsementId);
        List<GLProposerDocument> uploadedDocuments = endorsementMap.get("proposerDocuments") != null ? (List<GLProposerDocument>) endorsementMap.get("proposerDocuments") : Lists.newArrayList();
        Set<GLProposalMandatoryDocumentDto> mandatoryDocumentDtos = Sets.newHashSet();
        if (isNotEmpty(uploadedDocuments)) {
            mandatoryDocumentDtos = uploadedDocuments.stream().filter(uploadedDocument -> !uploadedDocument.isMandatory()).map(new Function<GLProposerDocument, GLProposalMandatoryDocumentDto>() {
                @Override
                public GLProposalMandatoryDocumentDto apply(GLProposerDocument glProposerDocument) {
                    GLProposalMandatoryDocumentDto mandatoryDocumentDto = new GLProposalMandatoryDocumentDto(glProposerDocument.getDocumentId(), glProposerDocument.getDocumentName());
                    GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(glProposerDocument.getGridFsDocId())));
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


}
