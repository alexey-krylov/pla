package com.pla.grouplife.endorsement.application.service;

import com.google.common.collect.Lists;
import com.pla.grouplife.endorsement.application.service.excel.generator.GLEndorsementExcelGenerator;
import com.pla.grouplife.endorsement.application.service.excel.parser.GLEndorsementExcelParser;
import com.pla.grouplife.endorsement.presentation.dto.GLEndorsementDto;
import com.pla.grouplife.endorsement.presentation.dto.SearchGLEndorsementDto;
import com.pla.grouplife.endorsement.query.GLEndorsementFinder;
import com.pla.grouplife.sharedresource.dto.GLPolicyDetailDto;
import com.pla.grouplife.sharedresource.dto.SearchGLPolicyDto;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.grouplife.sharedresource.model.vo.Proposer;
import com.pla.grouplife.sharedresource.query.GLFinder;
import com.pla.publishedlanguage.underwriter.contract.IUnderWriterAdapter;
import com.pla.sharedkernel.domain.model.EndorsementNumber;
import com.pla.sharedkernel.domain.model.EndorsementStatus;
import com.pla.sharedkernel.domain.model.Policy;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.identifier.EndorsementId;
import com.pla.sharedkernel.identifier.PolicyId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
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
                policyDetailDto.setEndorsementTypes(GLEndorsementType.getAllEndorsementTypeExceludingFCL());
                return policyDetailDto;
            }
        }).collect(Collectors.toList());
        return transformedPolicies;
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
        Map glEndorsementMap = glEndorsementFinder.findEndorsementById(endorsementId.getEndorsementId());
        Policy policy = glEndorsementMap != null ? (Policy) glEndorsementMap.get("policy") : null;
        PolicyId policyId = policy != null ? policy.getPolicyId() : null;
        return glEndorsementExcelGenerator.generate(policyId, endorsementId);
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
                if (isNotEmpty(endorsementTypeInString)) {
                    endorsementTypeInString = GLEndorsementType.valueOf(endorsementTypeInString).getDescription();
                }
                if (isNotEmpty(endorsementStatus)) {
                    endorsementStatus = EndorsementStatus.valueOf(endorsementStatus).getDescription();
                }
                GLEndorsementDto glEndorsementDto = new GLEndorsementDto(endorsementId, endorsementNumber, policyNumber, endorsementTypeInString, effectiveDate, policyHolderName, getIntervalInDays(effectiveDate), endorsementStatus);
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

    public Map<String, Object> getPolicyDetail(String endorsementId) {
        return glEndorsementFinder.getPolicyDetail(endorsementId);
    }

}
