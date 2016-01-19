package com.pla.individuallife.endorsement.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.individuallife.endorsement.presentation.dto.ILEndorsementDto;
import com.pla.individuallife.endorsement.presentation.dto.SearchILEndorsementDto;
import com.pla.individuallife.endorsement.query.ILEndorsementFinder;
import com.pla.individuallife.policy.domain.model.IndividualLifePolicy;
import com.pla.individuallife.policy.finder.ILPolicyFinder;
import com.pla.individuallife.policy.presentation.dto.ILPolicyDto;
import com.pla.individuallife.proposal.presentation.dto.ILProposalMandatoryDocumentDto;
import com.pla.individuallife.sharedresource.dto.ILPolicyDetailDto;
import com.pla.individuallife.sharedresource.dto.SearchILPolicyDto;
import com.pla.individuallife.sharedresource.model.ILEndorsementType;
import com.pla.individuallife.sharedresource.model.vo.ILProposerDocument;
import com.pla.individuallife.sharedresource.model.vo.Proposer;
import com.pla.individuallife.sharedresource.query.ILClientFinder;
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
import org.springframework.stereotype.Service;

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
 * Created by Raghu on 8/5/2015.
 */
@Service
public class ILEndorsementService {

    @Autowired
    private ILClientFinder ilFinder;
    @Autowired
    private ILEndorsementFinder ilEndorsementFinder;
    @Autowired
    private IUnderWriterAdapter underWriterAdapter;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private IPlanAdapter iPlanAdapter;
    @Autowired
    private ILPolicyFinder ilPolicyFinder;

    public ILEndorsementService() {

    }

    public ILPolicyDto searchPolicy(SearchILPolicyDto searchGLPolicyDto) {

        List<IndividualLifePolicy> searchedPolicy = ilFinder.searchPolicy(searchGLPolicyDto.getPolicyNumber(), new String[]{"IN_FORCE"});
        if (isNotEmpty(searchedPolicy)) {
            IndividualLifePolicy individualLifePolicy = searchedPolicy.get(0);
            ILPolicyDetailDto policyDetailDto = new ILPolicyDetailDto();
            ILPolicyDto ilPolicyDto = new ILPolicyDto();
            PlanId planId = new PlanId(individualLifePolicy.getProposalPlanDetail().getPlanId());
            Set<PlanId> planIds = Sets.newLinkedHashSet();
            planIds.add(planId);
            Set<String> endorsementTypes = iPlanAdapter.getConfiguredEndorsementType(planIds);
            ilPolicyDto.setProposal(individualLifePolicy.getProposal());
            ilPolicyDto.setPremiumPaymentDetails(individualLifePolicy.getPremiumPaymentDetails());
            //ilPolicyDto.setIlEndorsementType(individualLifePolicy.getIL)
            ilPolicyDto.setBeneficiaries(individualLifePolicy.getBeneficiaries());
            //ilPolicyDto.setAgentCommissionDetails(individualLifePolicy.getAgentCommissionShareModel());
            ilPolicyDto.setPolicyNumber(individualLifePolicy.getPolicyNumber());
            ilPolicyDto.setPolicyHolder(individualLifePolicy.getProposer());
            ilPolicyDto.setLifeAssured(individualLifePolicy.getProposedAssured());
            ilPolicyDto.setPolicyId(individualLifePolicy.getPolicyId().getPolicyId());
            ilPolicyDto.setEndorsementTypes(getAllEndorsementTypes(endorsementTypes));
            ilPolicyDto.setInceptionOn(individualLifePolicy.getInceptionOn());
            //ilPolicyDto.getPolicyNumber().setEndorsementTypes(getAllEndorsementTypes(endorsementTypes));
            /*policyDetailDto.setEndorsementTypes(getAllEndorsementTypes(endorsementTypes));
            policyDetailDto.setPolicyHolderName(individualLifePolicy.getProposer().getFirstName());
            policyDetailDto.setPolicyId(individualLifePolicy.getPolicyId().getPolicyId());*/
            return ilPolicyDto;
        }
        return ILPolicyDto.createEmptyDetail();
    }

    private List<Map<String,String>> getAllEndorsementTypes(Set<String> endorsementTypes){

        //List<String> list = new ArrayList().addAll();
        return Arrays.asList(ILEndorsementType.values()).parallelStream().filter(new Predicate<ILEndorsementType>() {
            @Override
            public boolean test(ILEndorsementType glEndorsementType) {
                return endorsementTypes.contains(glEndorsementType.getDescription());
            }
        }).map(endorsement -> {
            Map<String, String> endorsementMap = Maps.newLinkedHashMap();
            endorsementMap.put("code", endorsement.name());
            endorsementMap.put("description", endorsement.getDescription());
            return endorsementMap;
        }).collect(Collectors.toList());
    }


    private ILPolicyDetailDto transformToDto(Map policyMap) {
        DateTime inceptionDate = policyMap.get("inceptionOn") != null ? new DateTime(policyMap.get("inceptionOn")) : null;
        DateTime expiryDate = policyMap.get("expiredOn") != null ? new DateTime(policyMap.get("expiredOn")) : null;
        Proposer glProposer = policyMap.get("proposer") != null ? (Proposer) policyMap.get("proposer") : null;
        PolicyNumber policyNumber = policyMap.get("policyNumber") != null ? (PolicyNumber) policyMap.get("policyNumber") : null;
        ILPolicyDetailDto policyDetailDto = new ILPolicyDetailDto();
        policyDetailDto.setPolicyId(policyMap.get("_id").toString());
        policyDetailDto.setInceptionDate(inceptionDate);
        policyDetailDto.setExpiryDate(expiryDate);

        //Raghu bandi Changed the following sentence

        policyDetailDto.setPolicyHolderName(glProposer != null ? glProposer.getFirstName() : "");

        policyDetailDto.setPolicyNumber(policyNumber.getPolicyNumber());
        policyDetailDto.setStatus((String) policyMap.get("status"));
        return policyDetailDto;
    }

    private PolicyId findPolicyIdFromEndorsement(EndorsementId endorsementId) {
        Map glEndorsementMap = ilEndorsementFinder.findEndorsementById(endorsementId.getEndorsementId());
        Policy policy = glEndorsementMap != null ? (Policy) glEndorsementMap.get("policy") : null;
        PolicyId policyId = policy != null ? policy.getPolicyId() : null;
        return policyId;
    }

    public List<ILEndorsementDto> searchEndorsement(SearchILEndorsementDto searchILEndorsementDto, String[] statuses) {
        List<Map> endorsements = ilEndorsementFinder.searchEndorsement(searchILEndorsementDto.getEndorsementType(), searchILEndorsementDto.getEndorsementRequestNumber(),
                searchILEndorsementDto.getPolicyNumber(), searchILEndorsementDto.getPolicyHolderSurname(), searchILEndorsementDto.getPolicyHolderName(),
                searchILEndorsementDto.getLifeAssuredSurName(), searchILEndorsementDto.getLifeAssuredFirstName(),searchILEndorsementDto.getPolicyHolderNrc(),searchILEndorsementDto.getLifeAssuredNRC(),statuses);
        if (isEmpty(endorsements)) {
            return Lists.newArrayList();
        }
        List<ILEndorsementDto> endorsementDtos = endorsements.stream().map(new ILEndorsementTransformation()).collect(Collectors.toList());
        return endorsementDtos;
    }

    public List<ILEndorsementDto> getApprovedEndorsementByPolicyNumber(String policyNumber) {
        List<Map> endorsements = ilEndorsementFinder.findEndorsementByPolicyId(policyNumber);
        if (isEmpty(endorsements)) {
            return Lists.newArrayList();
        }
        List<ILEndorsementDto> endorsementDtos = endorsements.stream().map(new ILEndorsementTransformation()).collect(Collectors.toList());
        return endorsementDtos;
    }


    public PolicyId getPolicyIdFromEndorsment(String endorsementId) {
        Map endorsementMap = ilEndorsementFinder.findEndorsementById(endorsementId);
        Policy policy = endorsementMap != null ? (Policy) endorsementMap.get("policy") : null;
        PolicyId policyId = policy != null ? policy.getPolicyId() : null;
        return policyId;
    }

    public Map<String, Object> getPolicyDetail(String endorsementId) throws ParseException {
        return ilEndorsementFinder.getPolicyDetail(endorsementId);
    }

    public List<ILProposalMandatoryDocumentDto> findMandatoryDocuments(String endorsementId) {
        Map endorsementMap = ilEndorsementFinder.findEndorsementById(endorsementId);
        PolicyId policyId = getPolicyIdFromEndorsment(endorsementId);
        Map policyMap = ilPolicyFinder.findPolicyById(policyId.getPolicyId());

        //List<Insured> insureds = (List<Insured>) policyMap.get("insureds");

        List<ILProposerDocument> uploadedDocuments = endorsementMap.get("proposerDocuments") != null ? (List<ILProposerDocument>) endorsementMap.get("proposerDocuments") : Lists.newArrayList();
        List<SearchDocumentDetailDto> documentDetailDtos = Lists.newArrayList();

/*        insureds.forEach(ghInsured -> {
            PlanPremiumDetail planPremiumDetail = ghInsured.getPlanPremiumDetail();
            SearchDocumentDetailDto searchDocumentDetailDto = new SearchDocumentDetailDto(planPremiumDetail.getPlanId());
            documentDetailDtos.add(searchDocumentDetailDto);
            if (isNotEmpty(ghInsured.getInsuredDependents())) {
                ghInsured.getInsuredDependents().forEach(insuredDependent -> {
                    PlanPremiumDetail dependentPlanPremiumDetail = insuredDependent.getPlanPremiumDetail();
                    documentDetailDtos.add(new SearchDocumentDetailDto(dependentPlanPremiumDetail.getPlanId()));
                });
            }
        });*/

        Set<ClientDocumentDto> mandatoryDocuments = underWriterAdapter.getMandatoryDocumentsForApproverApproval(documentDetailDtos, ProcessType.ENDORSEMENT);
        List<ILProposalMandatoryDocumentDto> mandatoryDocumentDtos = Lists.newArrayList();
        if (isNotEmpty(mandatoryDocuments)) {
            mandatoryDocumentDtos = mandatoryDocuments.stream().map(new Function<ClientDocumentDto, ILProposalMandatoryDocumentDto>() {
                @Override
                public ILProposalMandatoryDocumentDto apply(ClientDocumentDto clientDocumentDto) {
                    ILProposalMandatoryDocumentDto mandatoryDocumentDto = new ILProposalMandatoryDocumentDto(clientDocumentDto.getDocumentCode(), clientDocumentDto.getDocumentName());
                    Optional<ILProposerDocument> proposerDocumentOptional = uploadedDocuments.stream().filter(new Predicate<ILProposerDocument>() {
                        @Override
                        public boolean test(ILProposerDocument glProposerDocument) {
                            return clientDocumentDto.getDocumentCode().equals(glProposerDocument.getDocumentId());
                        }
                    }).findAny();
                    if (proposerDocumentOptional.isPresent()) {
                        mandatoryDocumentDto.setRequireForSubmission(proposerDocumentOptional.get().isRequireForSubmission());
                        mandatoryDocumentDto.setIsApproved(proposerDocumentOptional.get().isApproved());
                        mandatoryDocumentDto.setMandatory(proposerDocumentOptional.get().isMandatory());
                        mandatoryDocumentDto.setSubmitted(proposerDocumentOptional.get().isApproved());
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


    public Set<ILProposalMandatoryDocumentDto> findAdditionalDocuments(String endorsementId) {
        Map endorsementMap = ilEndorsementFinder.findEndorsementById(endorsementId);
        List<ILProposerDocument> uploadedDocuments = endorsementMap.get("proposerDocuments") != null ? (List<ILProposerDocument>) endorsementMap.get("proposerDocuments") : Lists.newArrayList();
        Set<ILProposalMandatoryDocumentDto> mandatoryDocumentDtos = Sets.newHashSet();
        if (isNotEmpty(uploadedDocuments)) {
            mandatoryDocumentDtos = uploadedDocuments.stream().filter(uploadedDocument -> !uploadedDocument.isMandatory()).map(new Function<ILProposerDocument, ILProposalMandatoryDocumentDto>() {
                @Override
                public ILProposalMandatoryDocumentDto apply(ILProposerDocument glProposerDocument) {
                    ILProposalMandatoryDocumentDto mandatoryDocumentDto = new ILProposalMandatoryDocumentDto(glProposerDocument.getDocumentId(), glProposerDocument.getDocumentName());
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

    public boolean doesAllDocumentWaivesByApprover(String endorsementId){
        List<ILProposalMandatoryDocumentDto> glProposalMandatoryDocumentDtos = findMandatoryDocuments(endorsementId);
        long count =  glProposalMandatoryDocumentDtos.parallelStream().filter(new Predicate<ILProposalMandatoryDocumentDto>() {
            @Override
            public boolean test(ILProposalMandatoryDocumentDto glProposalMandatoryDocumentDto) {
                return glProposalMandatoryDocumentDto.getIsApproved();
            }
        }).count();
        if (count==glProposalMandatoryDocumentDtos.size())
            return true;
        return false;
    }

    private class ILEndorsementTransformation implements Function<Map,ILEndorsementDto> {
        @Override
        public ILEndorsementDto apply(Map map) {
            DateTime effectiveDate = map.get("effectiveDate") != null ? new DateTime(map.get("effectiveDate")) : null;
            String endorsementId = map.get("_id").toString();
            String endorsementNumber = map.get("endorsementNumber") != null ? ((EndorsementNumber) map.get("endorsementNumber")).getEndorsementNumber() : "";
            String endorsementRequestNumber = map.get("endorsementRequestNumber") != null ? ((String) map.get("endorsementRequestNumber")) : "";
            Policy policy = map.get("policy") != null ? (Policy) map.get("policy") : null;
            String policyNumber = policy != null ? policy.getPolicyNumber().getPolicyNumber() : "";
            String policyHolderName = policy != null ? policy.getPolicyHolderName() : "";
            String endorsementTypeInString = map.get("endorsementType") != null ? (String) map.get("endorsementType") : "";
            String endorsementStatus = map.get("status") != null ? (String) map.get("status") : "";
            String endorsementCode = "";
            if (isNotEmpty(endorsementTypeInString)) {
                ILEndorsementType endorsementType = ILEndorsementType.valueOf(endorsementTypeInString);
                endorsementTypeInString = endorsementType.getDescription();
                endorsementCode = endorsementType.name();
            }
            if (isNotEmpty(endorsementStatus)) {
                endorsementStatus = EndorsementStatus.valueOf(endorsementStatus).getDescription();
            }
            ILEndorsementDto glEndorsementDto = new ILEndorsementDto(endorsementId,endorsementNumber,endorsementRequestNumber, policyNumber, endorsementTypeInString,endorsementCode, effectiveDate, policyHolderName, getIntervalInDays(effectiveDate), endorsementStatus);
            return glEndorsementDto;
        }
    }
}
