package com.pla.grouphealth.claim.cashless.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.hcp.presentation.dto.HCPServiceDetailDto;
import com.pla.grouphealth.sharedresource.dto.GHInsuredDto;
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
        /*for (HCPServiceDetailDto hcpServiceDetailDto : hcpServiceDetailDtos) {
            List<Map<Integer, String>> tranformedList = transformInsuredDtoToExcelData(hcpServiceDetailDto, GHCashlessClaimPreAuthExcelHeader.getAllowedHeaders());
            for (Map<Integer, String> insuredMap : tranformedList) {
                excelData.add(insuredMap);
            }
        }*/
        Map<Integer, List<String>> constraintCellDataMap = Maps.newHashMap();
        constraintCellDataMap.put(headers.indexOf("Service"), getAllRelatedServices(hcpServiceDetailDtos));
        constraintCellDataMap.put(headers.indexOf("Type"), Lists.newArrayList("Normal", "After Hour"));
        HSSFWorkbook workbook = ExcelGeneratorUtil.generateExcelWithDvConstraintCell(headers, excelData, constraintCellDataMap);
        return workbook;
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
        headers.forEach(header -> {
            excelDataMap.put(headers.indexOf(header), GHCashlessClaimPreAuthExcelHeader.valueOf(header).getAllowedValue(hcpServiceDetailDto));
        });
        excelRowData.add(excelDataMap);
        return excelRowData;
    }

}
