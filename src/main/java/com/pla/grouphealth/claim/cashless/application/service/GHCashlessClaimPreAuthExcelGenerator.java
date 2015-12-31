package com.pla.grouphealth.claim.cashless.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.hcp.presentation.dto.HCPServiceDetailDto;
import com.pla.sharedkernel.util.ExcelGeneratorUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Mohan Sharma on 12/30/2015.
 */
@Service
public class GHCashlessClaimPreAuthExcelGenerator {

    public HSSFWorkbook generateInsuredExcel(List<HCPServiceDetailDto> hcpServiceDetailDtos) {
        final List<String> headers = GHCashlessClaimPreAuthExcelHeader.getAllowedHeaders();
        List<Map<Integer, String>> excelData = Lists.newArrayList();
        Map<Integer, List<String>> constraintCellDataMap = getMapContainingTheDropdownValues(hcpServiceDetailDtos, headers);
        HSSFWorkbook workbook = ExcelGeneratorUtil.generateExcelWithDvConstraintCell(headers, excelData, constraintCellDataMap);
        return workbook;
    }

    private Map<Integer, List<String>> getMapContainingTheDropdownValues(List<HCPServiceDetailDto> hcpServiceDetailDtos, List<String> headers) {
        Map<Integer, List<String>> constraintCellDataMap = Maps.newHashMap();
        constraintCellDataMap.put(headers.indexOf("Hospitalization Event"), Lists.newArrayList("Planned", "Emergency"));
        constraintCellDataMap.put(headers.indexOf("Please indicate whether it is a"), Lists.newArrayList("Illness", "Pregnancy", "Trauma"));
        constraintCellDataMap.put(headers.indexOf("Diagnosis/Treatment in case Of Pregnancy- Mode Of Delivery"), Lists.newArrayList("Caesarean", "Norma", "Termination"));
        constraintCellDataMap.put(headers.indexOf("Diagnosis/Treatment - Line of treatment"), Lists.newArrayList("Medical", "Surgical", "Intensive Care", "Investigation"));
        constraintCellDataMap.put(headers.indexOf("Diagnosis/Treatment - If Investigations, indicate tests"), Lists.newArrayList("Blood Tests","Other Lab Tests", "X-Rays", "MRI's","CT's"));
        constraintCellDataMap.put(headers.indexOf("Diagnosis/Treatment - If Surgery Please provide Type Of Accommodation"), Lists.newArrayList("Normal", "Executive"));
        //constraintCellDataMap.put(headers.indexOf("Diagnosis/Treatment  - If Medical Please Provide Drug Type"), Lists.newArrayList("Syrup", "Suspension", "Tablet", "Capsule", "Injectable"));
        constraintCellDataMap.put(headers.indexOf("Past history of any chronic illness - Suffering From Psychiatric Condition"), Lists.newArrayList("Yes", "No"));
        constraintCellDataMap.put(headers.indexOf("Past history of any chronic illness - Suffering From Alcohol/Drug Abuse"), Lists.newArrayList("Yes", "No"));
        constraintCellDataMap.put(headers.indexOf("Past history of any chronic illness - Suffering From STD/HIV/AIDS"), Lists.newArrayList("Yes", "No"));
        constraintCellDataMap.put(headers.indexOf("Past history of any chronic illness - Suffering From Cancer/Tumor/Cyst"), Lists.newArrayList("Yes", "No"));
        constraintCellDataMap.put(headers.indexOf("Past history of any chronic illness - Suffering From Arthritis"), Lists.newArrayList("Yes", "No"));
        constraintCellDataMap.put(headers.indexOf("Past history of any chronic illness - Suffering From Paralysis/CVA/Epilepsy"), Lists.newArrayList("Yes", "No"));
        constraintCellDataMap.put(headers.indexOf("Past history of any chronic illness - Suffering From Asthma/COPD/TB"), Lists.newArrayList("Yes", "No"));
        constraintCellDataMap.put(headers.indexOf("Past history of any chronic illness - Suffering From Diabetes"), Lists.newArrayList("Yes", "No"));
        constraintCellDataMap.put(headers.indexOf("Past history of any chronic illness - Suffering From IHD/CAD"), Lists.newArrayList("Yes", "No"));
        constraintCellDataMap.put(headers.indexOf("Past history of any chronic illness - Suffering From HTN"), Lists.newArrayList("Yes", "No"));
        constraintCellDataMap.put(headers.indexOf("Service to be Availed - Service"), getAllRelatedServices(hcpServiceDetailDtos));
        constraintCellDataMap.put(headers.indexOf("Service to be Availed - Type"), Lists.newArrayList("Normal", "After Hour"));
        return constraintCellDataMap;
    }

    private List<String> getAllRelatedServices(List<HCPServiceDetailDto> hcpServiceDetailDtos) {
        Set<String> serviceSet = hcpServiceDetailDtos.stream().map(new Function<HCPServiceDetailDto, String>() {
            @Override
            public String apply(HCPServiceDetailDto hcpServiceDetailDto) {
                return hcpServiceDetailDto.getServiceAvailed();
            }
        }).collect(Collectors.toSet());
        List<String> services = Lists.newLinkedList(serviceSet);
        services.sort(String::compareTo);
        return services;
    }

    private List<Map<Integer, String>> transformInsuredDtoToExcelData(HCPServiceDetailDto hcpServiceDetailDto, List<String> headers) {
        List<Map<Integer, String>> excelRowData = Lists.newArrayList();
        Map<Integer, String> excelDataMap = Maps.newHashMap();
        headers.forEach(header -> excelDataMap.put(headers.indexOf(header), GHCashlessClaimPreAuthExcelHeader.valueOf(header).getAllowedValue(hcpServiceDetailDto)));
        excelRowData.add(excelDataMap);
        return excelRowData;
    }

}
