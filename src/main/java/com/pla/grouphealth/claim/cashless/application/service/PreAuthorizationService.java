package com.pla.grouphealth.claim.cashless.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.core.hcp.domain.model.HCPCode;
import com.pla.core.hcp.domain.model.HCPRate;
import com.pla.core.hcp.domain.model.HCPServiceDetail;
import com.pla.core.hcp.presentation.dto.HCPServiceDetailDto;
import com.pla.core.hcp.query.HCPFinder;
import com.pla.core.hcp.repository.HCPRateRepository;
import com.pla.grouphealth.claim.cashless.application.command.UploadPreAuthorizationCommand;
import com.pla.grouphealth.claim.cashless.domain.exception.GenerateReminderFollowupException;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorization;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationDetail;
import com.pla.grouphealth.claim.cashless.domain.model.PreAuthorizationId;
import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationClaimantDetailCommand;
import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationDetailDto;
import com.pla.grouphealth.claim.cashless.query.PreAuthorizationFinder;
import com.pla.grouphealth.claim.cashless.repository.GHCashlessClaimRepository;
import com.pla.grouphealth.claim.cashless.repository.PreAuthorizationRepository;
import com.pla.grouphealth.claim.cashless.repository.PreAuthorizationRequestRepository;
import com.pla.grouphealth.policy.repository.GHPolicyRepository;
import com.pla.sharedkernel.util.ExcelUtilityProvider;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.joda.time.LocalDate;
import org.nthdimenzion.ddd.domain.annotations.DomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;
import static org.springframework.util.Assert.notEmpty;
import static org.springframework.util.Assert.notNull;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
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
    private PreAuthorizationRequestRepository preAuthorizationRequestRepository;
    @Autowired
    private PreAuthorizationRequestService preAuthorizationRequestService;

    @Autowired
    public PreAuthorizationService(GHCashlessClaimRepository ghCashlessClaimRepository, PreAuthorizationRepository preAuthorizationRepository, PreAuthorizationFinder preAuthorizationFinder, PreAuthorizationExcelGenerator preAuthorizationExcelGenerator, ExcelUtilityProvider excelUtilityProvider, HCPRateRepository hcpRateRepository, SequenceGenerator sequenceGenerator,HCPFinder hcpFinder, GHPolicyRepository ghPolicyRepository, PreAuthorizationRequestRepository preAuthorizationRequestRepository) {
        this.ghCashlessClaimRepository = ghCashlessClaimRepository;
        this.preAuthorizationRepository = preAuthorizationRepository;
        this.preAuthorizationFinder = preAuthorizationFinder;
        this.preAuthorizationExcelGenerator = preAuthorizationExcelGenerator;
        this.excelUtilityProvider = excelUtilityProvider;
        this.hcpRateRepository = hcpRateRepository;
        this.sequenceGenerator = sequenceGenerator;
        this.hcpFinder = hcpFinder;
        this.ghPolicyRepository = ghPolicyRepository;
        this.preAuthorizationRequestRepository = preAuthorizationRequestRepository;
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
                Map<String,Object> map=new HashMap<>();
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
        for (Row currentRow : dataRows) {
            PreAuthorizationDetailDto preAuthorizationDetailDto = new PreAuthorizationDetailDto();
            for (String header : headers) {
                preAuthorizationDetailDto = PreAuthorizationExcelHeader.getEnum(header).populatePreAuthorizationDetail(preAuthorizationDetailDto, currentRow, headers);
            }
            preAuthorizationDetailDtos.add(preAuthorizationDetailDto);
        }
        return preAuthorizationDetailDtos;
    }

    @Transactional
    public int uploadPreAuthorizationDetails(UploadPreAuthorizationCommand uploadPreAuthorizationCommand) throws GenerateReminderFollowupException{
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
                    .updateWithBatchNumber(runningSequence);
            if(isNotEmpty(sameServicesPreviouslyAvailedPreAuth))
                preAuthorization.updateWithSameServicesPreviouslyAvailedPreAuth(sameServicesPreviouslyAvailedPreAuth);
            preAuthorizationRepository.save(preAuthorization);
        }
        List<PreAuthorization> preAuthorizations = preAuthorizationRepository.findAllPreAuthorizationByBatchNumber(runningSequence);
        notEmpty(preAuthorizations, "Not uploaded successfully");
        for(PreAuthorization preAuthorization : preAuthorizations) {
            PreAuthorizationDetail preAuthorizationDetail = preAuthorization.getPreAuthorizationDetails().iterator().next();
            notNull(preAuthorizationDetail, "Not uploaded successfully");
            PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand = preAuthorizationRequestService.getPreAuthorizationByPreAuthorizationIdAndClientId(preAuthorization.getPreAuthorizationId(), preAuthorizationDetail.getClientId());
            preAuthorizationRequestService.createUpdatePreAuthorizationRequest(preAuthorizationClaimantDetailCommand);
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
        notEmpty(preAuthorizationDetails, "PreAuthorizationDetail cannot be empty");
        PreAuthorizationDetail preAuthorizationDetail = preAuthorizationDetails.iterator().next();
        LocalDate consultationDate = preAuthorizationDetail.getConsultationDate();
        String preAuthIdSequence = sequenceGenerator.getSequence(PreAuthorizationId.class);
        String year = String.format("%02d", consultationDate.getYear());
        preAuthIdSequence = String.format("%07d", Integer.parseInt(preAuthIdSequence.trim()))+String.format("%02d", consultationDate.getMonthOfYear())+ (year.length() > 2 ? year.substring(year.length() - 2) : year);
        return new PreAuthorizationId(preAuthIdSequence);
    }

    private List<List<PreAuthorizationDetailDto>> createSubListBasedOnSimilarCriteria(Set<PreAuthorizationDetailDto> preAuthorizationDetailDtos) {
        List<List<PreAuthorizationDetailDto>> refurbishedList = Lists.newArrayList();
        Map<PreAuthorizationDetailDto.ConsultationDateClientIdPolicyNumber, List<PreAuthorizationDetailDto>> result = preAuthorizationDetailDtos.parallelStream().collect(Collectors.groupingBy(PreAuthorizationDetailDto::getConsultationDateClientIdPolicyNumber));
        refurbishedList.addAll(result.values().stream().collect(Collectors.toList()));
        return refurbishedList;
    }

    private Set<PreAuthorizationDetail> transformToPreAuthorizationDetails(List<PreAuthorizationDetailDto> preAuthorizationDetailDtos) {
        return isNotEmpty(preAuthorizationDetailDtos) ? preAuthorizationDetailDtos.parallelStream().map(new Function<PreAuthorizationDetailDto, PreAuthorizationDetail>() {
            @Override
            public PreAuthorizationDetail apply(PreAuthorizationDetailDto preAuthorizationDetailDto) {
                PreAuthorizationDetail preAuthorizationDetail = new PreAuthorizationDetail();
                try {
                    BeanUtils.copyProperties(preAuthorizationDetail, preAuthorizationDetailDto);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return preAuthorizationDetail;
            }
        }).collect(Collectors.toSet()) : Sets.newHashSet();
    }
}
