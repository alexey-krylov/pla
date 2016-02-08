package com.pla.grouphealth.claim.cashless.application.service.preauthorization;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pla.core.hcp.domain.model.HCPCode;
import com.pla.core.hcp.domain.model.HCPRate;
import com.pla.core.hcp.domain.model.HCPServiceDetail;
import com.pla.core.hcp.presentation.dto.HCPServiceDetailDto;
import com.pla.core.hcp.query.HCPFinder;
import com.pla.core.hcp.repository.HCPRateRepository;
import com.pla.grouphealth.claim.cashless.application.command.preauthorization.UploadPreAuthorizationCommand;
import com.pla.grouphealth.claim.cashless.domain.exception.GenerateReminderFollowupException;
import com.pla.grouphealth.claim.cashless.domain.model.claim.GroupHealthCashlessClaim;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorization;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationDetail;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationId;
import com.pla.grouphealth.claim.cashless.domain.model.preauthorization.PreAuthorizationRequest;
import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.ClaimUploadedExcelDataDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.PreAuthorizationClaimantDetailCommand;
import com.pla.grouphealth.claim.cashless.query.PreAuthorizationFinder;
import com.pla.grouphealth.claim.cashless.repository.claim.GroupHealthCashlessClaimRepository;
import com.pla.grouphealth.claim.cashless.repository.preauthorization.PreAuthorizationRepository;
import com.pla.grouphealth.claim.cashless.repository.preauthorization.PreAuthorizationRequestRepository;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    private GroupHealthCashlessClaimRepository groupHealthCashlessClaimRepository;
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
    public PreAuthorizationService(GroupHealthCashlessClaimRepository groupHealthCashlessClaimRepository, PreAuthorizationRepository preAuthorizationRepository, PreAuthorizationFinder preAuthorizationFinder, PreAuthorizationExcelGenerator preAuthorizationExcelGenerator, ExcelUtilityProvider excelUtilityProvider, HCPRateRepository hcpRateRepository, SequenceGenerator sequenceGenerator, HCPFinder hcpFinder, GHPolicyRepository ghPolicyRepository, PreAuthorizationRequestRepository preAuthorizationRequestRepository) {
        this.groupHealthCashlessClaimRepository = groupHealthCashlessClaimRepository;
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

    public HSSFWorkbook getGHCashlessClaimPreAuthtemplate(String hcpCode, Class classZ) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
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
        HSSFWorkbook hssfWorkbook = preAuthorizationExcelGenerator.generateInsuredExcel(hcpServiceDetailDtos, classZ);
        return hssfWorkbook;
    }

    public List<Map<String,Object>> getAllHcpNameAndCode(){
        List<HCPRate> hcpRates = hcpRateRepository.findAll();
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


    public boolean isValidInsuredTemplate(HSSFWorkbook insuredTemplateWorkbook, Map dataMap) {
        return excelUtilityProvider.isValidInsuredExcel(insuredTemplateWorkbook, PreAuthorizationExcelHeader.getAllowedHeaders(), PreAuthorizationExcelHeader.class, dataMap);
    }

    public Set<ClaimUploadedExcelDataDto> transformToPreAuthorizationDetailDto(HSSFWorkbook preAuthTemplateWorkbook, Class dynamicClass) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = dynamicClass.getDeclaredMethod("getAllowedHeaders");
        final List<String> headers = (List<String>) method.invoke(null);
        Set<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtos = Sets.newLinkedHashSet();
        HSSFSheet hssfSheet = preAuthTemplateWorkbook.getSheetAt(0);
        Iterator<Row> rowIterator = hssfSheet.rowIterator();
        Row headerRow = rowIterator.next();
        List<Row> dataRows = Lists.newArrayList(rowIterator);
        for (Row currentRow : dataRows) {
            ClaimUploadedExcelDataDto claimUploadedExcelDataDto = new ClaimUploadedExcelDataDto();
            for (String header : headers) {
                Method getEnumMethod = dynamicClass.getMethod("getEnum", String.class);
                Object dynamicEnum = getEnumMethod.invoke(null, header);
                Method populatePreAuthorizationDetailMethod = dynamicClass.getMethod("populatePreAuthorizationDetail", ClaimUploadedExcelDataDto.class, Row.class, List.class);
                claimUploadedExcelDataDto = (ClaimUploadedExcelDataDto) populatePreAuthorizationDetailMethod.invoke(dynamicEnum, claimUploadedExcelDataDto, currentRow, headers);
            }
            claimUploadedExcelDataDtos.add(claimUploadedExcelDataDto);
        }
        return claimUploadedExcelDataDtos;
    }

    public int uploadPreAuthorizationDetails(UploadPreAuthorizationCommand uploadPreAuthorizationCommand) throws GenerateReminderFollowupException{
        String runningSequence = sequenceGenerator.getSequence(PreAuthorization.class);
        runningSequence = String.format("%08d", Integer.parseInt(runningSequence.trim()));
        List<List<ClaimUploadedExcelDataDto>> refurbishedSet = createSubListBasedOnSimilarCriteria(uploadPreAuthorizationCommand.getClaimUploadedExcelDataDtos());
        notEmpty(refurbishedSet, "Error uploading no PreAuthorization data list found to save");
        final String finalRunningSequence = runningSequence;
        List<PreAuthorization> preAuthorizations = refurbishedSet.stream().map(new Function<List<ClaimUploadedExcelDataDto>, PreAuthorization>() {
            @Override
            public PreAuthorization apply(List<ClaimUploadedExcelDataDto> preAuthorizationDetailDtos) {
                Set<PreAuthorizationDetail> preAuthorizationDetails = transformToPreAuthorizationDetails(preAuthorizationDetailDtos);
                Set<String> sameServicesPreviouslyAvailedPreAuth = checkAndFindIfServiceAvailedBefore(preAuthorizationDetails);
                PreAuthorization preAuthorization = new PreAuthorization()
                        .updateWithPreAuthorizationId(constructPreAuthorizationId(preAuthorizationDetails))
                        .updateWithPreAuthorizationDetail(preAuthorizationDetails)
                        .updateWithHcpCode(new HCPCode(uploadPreAuthorizationCommand.getHcpCode()))
                        .updateWithBatchDate(uploadPreAuthorizationCommand.getBatchDate())
                        .updateWithBatchNumber(finalRunningSequence)
                        .updateWithBatchUploaderUserId(uploadPreAuthorizationCommand.getBatchUploaderUserId())
                        .updateWithSameServicesPreviouslyAvailedPreAuth(sameServicesPreviouslyAvailedPreAuth);
                return preAuthorization;
            }
        }).collect(Collectors.toList());
        notEmpty(preAuthorizations, "Error uploading no PreAuthorizationRequest data list found to save");
        for(PreAuthorization preAuthorization : preAuthorizations) {
            PreAuthorizationDetail preAuthorizationDetail = preAuthorization.getPreAuthorizationDetails().iterator().next();
            notNull(preAuthorizationDetail, "Not uploaded successfully");
            PreAuthorizationClaimantDetailCommand preAuthorizationClaimantDetailCommand = preAuthorizationRequestService.getPreAuthorizationByPreAuthorizationIdAndClientId(preAuthorization, preAuthorizationDetail.getClientId());
            PreAuthorizationRequest preAuthorizationRequest = preAuthorizationRequestService.createPreAuthorizationRequest(preAuthorizationClaimantDetailCommand);
            preAuthorizationRequest.savedRegisterFollowUpReminders();
        }
        return Integer.parseInt(runningSequence.trim());
    }

    private Set<String> checkAndFindIfServiceAvailedBefore(Set<PreAuthorizationDetail> preAuthorizationDetails) {
        /*
        * getList of previously availed claim having same service
        * */
        Set<String> sameServicesPreviouslyAvailedPreAuth = Sets.newLinkedHashSet();
        if(isNotEmpty(preAuthorizationDetails)){
            PreAuthorizationDetail preAuthorizationDetail = preAuthorizationDetails.iterator().next();
            Set<String> services = preAuthorizationDetails.stream().map(PreAuthorizationDetail::getService).collect(Collectors.toSet());
            List<GroupHealthCashlessClaim> groupHealthCashlessClaims = groupHealthCashlessClaimRepository.findAllByGroupHealthCashlessClaimPolicyDetailAssuredDetailClientIdAndGroupHealthCashlessClaimDrugServicesServiceNameIn(preAuthorizationDetail.getClientId(), services);
            for(GroupHealthCashlessClaim groupHealthCashlessClaim : groupHealthCashlessClaims) {
                sameServicesPreviouslyAvailedPreAuth.add(groupHealthCashlessClaim.getGroupHealthCashlessClaimId());
            }
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

    public List<List<ClaimUploadedExcelDataDto>> createSubListBasedOnSimilarCriteria(Set<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtos) {
        List<List<ClaimUploadedExcelDataDto>> refurbishedList = Lists.newArrayList();
        Map<ClaimUploadedExcelDataDto.ConsultationDateClientIdPolicyNumber, List<ClaimUploadedExcelDataDto>> result = claimUploadedExcelDataDtos.parallelStream().collect(Collectors.groupingBy(ClaimUploadedExcelDataDto::getConsultationDateClientIdPolicyNumber));
        refurbishedList.addAll(result.values().stream().collect(Collectors.toList()));
        return refurbishedList;
    }

    private Set<PreAuthorizationDetail> transformToPreAuthorizationDetails(List<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtos) {
        return isNotEmpty(claimUploadedExcelDataDtos) ? claimUploadedExcelDataDtos.parallelStream().map(new Function<ClaimUploadedExcelDataDto, PreAuthorizationDetail>() {
            @Override
            public PreAuthorizationDetail apply(ClaimUploadedExcelDataDto claimUploadedExcelDataDto) {
                PreAuthorizationDetail preAuthorizationDetail = new PreAuthorizationDetail();
                try {
                    BeanUtils.copyProperties(preAuthorizationDetail, claimUploadedExcelDataDto);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return preAuthorizationDetail;
            }
        }).collect(Collectors.toSet()) : Sets.newHashSet();
    }
}
