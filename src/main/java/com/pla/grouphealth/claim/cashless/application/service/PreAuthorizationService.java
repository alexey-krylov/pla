package com.pla.grouphealth.claim.cashless.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.core.hcp.domain.model.HCP;
import com.pla.core.hcp.domain.model.HCPCode;
import com.pla.core.hcp.domain.model.HCPRate;
import com.pla.core.hcp.domain.model.HCPServiceDetail;
import com.pla.core.hcp.presentation.dto.HCPServiceDetailDto;
import com.pla.core.hcp.query.HCPFinder;
import com.pla.core.hcp.repository.HCPRateRepository;
import com.pla.grouphealth.claim.cashless.application.command.UploadPreAuthorizationCommand;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorization;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationDetail;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationId;
import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationDetailDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.SearchPreAuthorizationRecordDto;
import com.pla.grouphealth.claim.cashless.query.PreAuthorizationFinder;
import com.pla.grouphealth.claim.cashless.repository.GHCashlessClaimRepository;
import com.pla.grouphealth.claim.cashless.repository.PreAuthorizationRepository;
import com.pla.grouphealth.policy.domain.model.GroupHealthPolicy;
import com.pla.grouphealth.policy.repository.GHPolicyRepository;
import com.pla.grouphealth.sharedresource.model.vo.GHInsured;
import com.pla.grouphealth.sharedresource.model.vo.GHInsuredDependent;
import com.pla.sharedkernel.util.ExcelUtilityProvider;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.DateTime;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by Mohan Sharma on 12/30/2015.
 */
@DomainService
public class PreAuthorizationService {

    private GHCashlessClaimRepository ghCashlessClaimRepository;
    private PreAuthorizationRepository preAuthorizationRepository;
    private PreAuthorizationFinder preAuthorizationFinder;
    private PreAuthorizationExcelGenerator preAuthorizationExcelGenerator;
    private ExcelUtilityProvider excelUtilityProvider;
    private HCPRateRepository hcpRateRepository;
    private SequenceGenerator sequenceGenerator;
    private HCPFinder hcpFinder;
    private GHPolicyRepository ghPolicyRepository;

    @Autowired
    public PreAuthorizationService(GHCashlessClaimRepository ghCashlessClaimRepository, PreAuthorizationRepository preAuthorizationRepository, PreAuthorizationFinder preAuthorizationFinder, PreAuthorizationExcelGenerator preAuthorizationExcelGenerator, ExcelUtilityProvider excelUtilityProvider, HCPRateRepository hcpRateRepository, SequenceGenerator sequenceGenerator,HCPFinder hcpFinder, GHPolicyRepository ghPolicyRepository) {
        this.ghCashlessClaimRepository = ghCashlessClaimRepository;
        this.preAuthorizationRepository = preAuthorizationRepository;
        this.preAuthorizationFinder = preAuthorizationFinder;
        this.preAuthorizationExcelGenerator = preAuthorizationExcelGenerator;
        this.excelUtilityProvider = excelUtilityProvider;
        this.hcpRateRepository = hcpRateRepository;
        this.sequenceGenerator = sequenceGenerator;
        this.hcpFinder = hcpFinder;
        this.ghPolicyRepository = ghPolicyRepository;
    }

    public HSSFWorkbook getGHCashlessClaimPreAuthtemplate(String hcpCode) {
        HCPRate hcpRate = hcpRateRepository.findHCPRateByHCPCode(hcpCode);
        Set<HCPServiceDetail> hcpServiceDetails = hcpRate.getHcpServiceDetails();
        List<HCPServiceDetailDto> hcpServiceDetailDtos = isNotEmpty(hcpServiceDetails) ? hcpServiceDetails.stream().map(new Function<HCPServiceDetail, HCPServiceDetailDto>() {
            @Override
            public HCPServiceDetailDto apply(HCPServiceDetail hcpServiceDetail) {
                HCPServiceDetailDto hcpServiceDetailDto = new HCPServiceDetailDto()
                        .updateWithServiceDepartment(hcpServiceDetail.getServiceDepartment())
                        .updateWithServiceAvailed(hcpServiceDetail.getServiceAvailed())
                        .updateWithNormalAmount(hcpServiceDetail.getNormalAmount())
                        .updateWithAfterHours(hcpServiceDetail.getAfterHours());
                return hcpServiceDetailDto;
            }
        }).collect(Collectors.toList()) : Lists.newArrayList();
        HSSFWorkbook hssfWorkbook = preAuthorizationExcelGenerator.generateInsuredExcel(hcpServiceDetailDtos);
        return hssfWorkbook;
    }

    public List<Map<String,Object>> getAllHcpNameAndCode(){
        List<HCPRate>hcpRates =hcpRateRepository.findAll();
        return isNotEmpty(hcpRates) ? hcpRates.stream().map(new Function<HCPRate, Map<String,Object>>() {

            @Override
            public Map<String,Object> apply(HCPRate hcpRate) {
                Map<String,Object> map=new HashMap<String,Object>();
                map.put("hcpName",hcpRate.getHcpName());
                map.put("hcpCode",hcpRate.getHcpCode().getHcpCode());
                return map;
            }
        }).collect(Collectors.toList()): Lists.newArrayList();
    }


    public boolean isValidInsuredTemplate(HSSFWorkbook insuredTemplateWorkbook) {
        return excelUtilityProvider.isValidInsuredExcel(insuredTemplateWorkbook, PreAuthorizationExcelHeader.getAllowedHeaders(), PreAuthorizationExcelHeader.class);
    }

    public Set<PreAuthorizationDetailDto> transformToPreAuthorizationDetailDto(HSSFWorkbook preAuthTemplateWorkbook) {
        Set<PreAuthorizationDetailDto> preAuthorizationDetailDtos = Sets.newLinkedHashSet();
        HSSFSheet hssfSheet = preAuthTemplateWorkbook.getSheetAt(0);
        Iterator<Row> rowIterator = hssfSheet.rowIterator();
        Row headerRow = rowIterator.next();
        final List<String> headers = PreAuthorizationExcelHeader.getAllowedHeaders();
        List<Row> dataRows = Lists.newArrayList(rowIterator);
        Iterator<Row> dataRowIterator = dataRows.iterator();
        while (dataRowIterator.hasNext()) {
            Row currentRow = dataRowIterator.next();
            PreAuthorizationDetailDto preAuthorizationDetailDto = new PreAuthorizationDetailDto();
            for (String header : headers) {
                preAuthorizationDetailDto = PreAuthorizationExcelHeader.getEnum(header).populatePreAuthorizationDetail(preAuthorizationDetailDto, currentRow, headers);
            }
            preAuthorizationDetailDtos.add(preAuthorizationDetailDto);
        }
        return preAuthorizationDetailDtos;
    }

    @Transactional
    public int uploadPreAuthorizationDetails(UploadPreAuthorizationCommand uploadPreAuthorizationCommand) {
        String runningSequence = sequenceGenerator.getSequence(PreAuthorization.class);
        runningSequence = String.format("%08d", Integer.parseInt(runningSequence.trim()));
        List<List<PreAuthorizationDetailDto>> refurbishedSet = createSubListBasedOnSimilarCriteria(uploadPreAuthorizationCommand.getPreAuthorizationDetailDtos());
        for(List<PreAuthorizationDetailDto> preAuthorizationDetailDtos : refurbishedSet){
            Set<PreAuthorizationDetail> preAuthorizationDetails = transformToPreAuthorizationDetails(preAuthorizationDetailDtos);
            Set<String> sameServicesPreviouslyAvailedPreAuth = checkAndFindIfServiceAvailedBefore(preAuthorizationDetails);
            PreAuthorization preAuthorization = new PreAuthorization()
                    .updateWithPreAuthorizationId(constructPreAuthorizationId(preAuthorizationDetails))
                    .updateWithPreAuthorizationDetail(preAuthorizationDetails)
                    .updateWithHcpCode(new HCPCode(uploadPreAuthorizationCommand.getHcpCode()))
                    .updateWithBatchDate(uploadPreAuthorizationCommand.getBatchDate())
                    .updateWithBatchNumber(Integer.parseInt(runningSequence));
            if(isNotEmpty(sameServicesPreviouslyAvailedPreAuth))
                preAuthorization.updateWithSameServicesPreviouslyAvailedPreAuth(sameServicesPreviouslyAvailedPreAuth);
            preAuthorizationRepository.save(preAuthorization);
        }
        return Integer.parseInt(runningSequence.trim());
    }

    private Set<String> checkAndFindIfServiceAvailedBefore(Set<PreAuthorizationDetail> preAuthorizationDetails) {
        Set<String> sameServicesPreviouslyAvailedPreAuth = Sets.newLinkedHashSet();
        for(PreAuthorizationDetail preAuthorizationDetail : preAuthorizationDetails) {
            List<PreAuthorization> preAuthorizations = preAuthorizationRepository.findAllPreAuthorizationByServiceAndClientId(preAuthorizationDetail.getClientId(), preAuthorizationDetail.getService());
            sameServicesPreviouslyAvailedPreAuth.addAll(preAuthorizations.stream().map(preAuthorization -> preAuthorization.getPreAuthorizationId().getPreAuthorizationId()).collect(Collectors.toList()));
        }
        return sameServicesPreviouslyAvailedPreAuth;
    }

    private PreAuthorizationId constructPreAuthorizationId(Set<PreAuthorizationDetail> preAuthorizationDetails) {
        Assert.notEmpty(preAuthorizationDetails, "PreAuthorizationDetail cannot be empty");
        PreAuthorizationDetail preAuthorizationDetail = preAuthorizationDetails.iterator().next();
        DateTime consultationDate = preAuthorizationDetail.getConsultationDate();
        String preAuthIdSequence = sequenceGenerator.getSequence(PreAuthorizationId.class);
        String year = String.format("%02d", consultationDate.getYear());
        preAuthIdSequence = String.format("%07d", Integer.parseInt(preAuthIdSequence.trim()))+String.format("%02d", consultationDate.getMonthOfYear())+ (year.length() > 2 ? year.substring(year.length() - 2) : year);
        return new PreAuthorizationId(preAuthIdSequence);
    }

    private List<List<PreAuthorizationDetailDto>> createSubListBasedOnSimilarCriteria(Set<PreAuthorizationDetailDto> preAuthorizationDetailDtos) {
        List<List<PreAuthorizationDetailDto>> refurbishedList = Lists.newArrayList();
        Map<PreAuthorizationDetailDto.ConsultationDateClientIdPolicyNumber, List<PreAuthorizationDetailDto>> result = preAuthorizationDetailDtos.parallelStream().collect(Collectors.groupingBy(PreAuthorizationDetailDto::getConsultationDateClientIdPolicyNumber));
        for(List<PreAuthorizationDetailDto> list : result.values()){
            refurbishedList.add(list);
        }
        return refurbishedList;
    }

    private Set<PreAuthorizationDetail> transformToPreAuthorizationDetails(List<PreAuthorizationDetailDto> preAuthorizationDetailDtos) {
        return isNotEmpty(preAuthorizationDetailDtos) ? preAuthorizationDetailDtos.parallelStream().map(new Function<PreAuthorizationDetailDto, PreAuthorizationDetail>() {
            @Override
            public PreAuthorizationDetail apply(PreAuthorizationDetailDto preAuthorizationDetailDto) {
                PreAuthorizationDetail preAuthorizationDetail = new PreAuthorizationDetail();
                try {
                    BeanUtils.copyProperties(preAuthorizationDetail, preAuthorizationDetailDto);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return preAuthorizationDetail;
            }
        }).collect(Collectors.toSet()) : Sets.newHashSet();
    }

    public List<PreAuthorizationDto> searchPreAuthorizationRecord(SearchPreAuthorizationRecordDto searchPreAuthorizationRecordDto) {
        List<PreAuthorization> preAuthorizations = preAuthorizationFinder.searchPreAuthorizationRecord(searchPreAuthorizationRecordDto);
        List<PreAuthorizationDto> furbishedList = Lists.newLinkedList();
        for(PreAuthorization preAuthorization : preAuthorizations){
            for(PreAuthorizationDetail preAuthorizationDetail : preAuthorization.getPreAuthorizationDetails()){
                HCP hcp = hcpFinder.getHCPByHCPCode(preAuthorization.getHcpCode().getHcpCode());
                PreAuthorizationDto preAuthorizationDto = new PreAuthorizationDto();
                String policyNumber = preAuthorizationDetail.getPolicyNumber();
                String clientId = preAuthorizationDetail.getClientId();
                String policyHolderName = getPolicyHolderName(policyNumber, clientId);
                preAuthorizationDto.setPolicyNumber(policyNumber);
                preAuthorizationDto.setClientId(clientId);
                preAuthorizationDto.setPreAuthorizationId(preAuthorization.getPreAuthorizationId().getPreAuthorizationId());
                preAuthorizationDto.setConsultationDate(preAuthorizationDetail.getConsultationDate());
                preAuthorizationDto.setHcpName(hcp.getHcpName());
                preAuthorizationDto.setPolicyHolderName(policyHolderName);
                furbishedList.add(preAuthorizationDto);
            }
        }
        return furbishedList;
    }

    private String getPolicyHolderName(String policyNumber, String clientId) {
        GroupHealthPolicy groupHealthPolicy = ghPolicyRepository.findPolicyByPolicyNumber(policyNumber);
        if(isNotEmpty(groupHealthPolicy)){
            Set<GHInsured> insureds = groupHealthPolicy.getInsureds();
            GHInsured groupHealthInsured = null;
            GHInsuredDependent ghInsuredDependent = null;
            if(isNotEmpty(insureds)){
                Optional<GHInsured> groupHealthInsuredOptional = insureds.stream().filter(ghInsured -> ghInsured.getFamilyId().getFamilyId().equalsIgnoreCase(clientId)).findFirst();
                if(groupHealthInsuredOptional.isPresent()) {
                    groupHealthInsured = groupHealthInsuredOptional.get();
                    return groupHealthInsured.getFirstName()+" "+groupHealthInsured.getLastName();
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
                        return ghInsuredDependent.getFirstName()+" "+ghInsuredDependent.getLastName();
                    }
                }
            }
        }
        return null;
    }
}
