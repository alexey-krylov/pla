package com.pla.core.hcp.application.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.hcp.domain.model.HCP;
import com.pla.core.hcp.domain.model.HCPCode;
import com.pla.core.hcp.presentation.dto.HCPServiceDetailDto;
import com.pla.core.hcp.query.HCPFinder;
import com.pla.sharedkernel.util.ExcelGeneratorUtil;
import com.pla.sharedkernel.util.SequenceGenerator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by Mohan Sharma on 12/21/2015.
 */
@Service
public class HCPRateExcelGenerator {
    private SimpleJpaRepository<HCP, HCPCode> hcpRepository;
    private HCPFinder hcpFinder;

    @Autowired
    public HCPRateExcelGenerator(JpaRepositoryFactory jpaRepositoryFactory, HCPFinder hcpFinder, SequenceGenerator sequenceGenerator){
        hcpRepository = jpaRepositoryFactory.getCrudRepository(HCP.class);
        this.hcpFinder = hcpFinder;
    }
    public HSSFWorkbook generateInsuredExcel(List<HCPServiceDetailDto> hcpServiceDetailDtos) {
        final List<String> headers = HCPRateExcelHeader.getAllowedHeaders();
        List<Map<Integer, String>> excelData = Lists.newArrayList();
        for (HCPServiceDetailDto hcpServiceDetailDto : hcpServiceDetailDtos) {
            List<Map<Integer, String>> excelRowData = transformInsuredDtoToExcelData(hcpServiceDetailDto, headers);
            for (Map<Integer, String> insuredMap : excelRowData) {
                excelData.add(insuredMap);
            }
        }
        HSSFWorkbook workbook = ExcelGeneratorUtil.generateExcelWithDvConstraintCell(headers, excelData);
        return workbook;
    }

    private List<Map<Integer, String>> transformInsuredDtoToExcelData(HCPServiceDetailDto hcpServiceDetailDto, List<String> headers) {
        List<Map<Integer, String>> excelRowData = Lists.newArrayList();
        Map<Integer, String> excelDataMap = Maps.newHashMap();
        headers.forEach(header -> {
                excelDataMap.put(headers.indexOf(header), HCPRateExcelHeader.getHCPCategory(header).getAllowedValue(hcpServiceDetailDto));
        });
        excelRowData.add(excelDataMap);
        return excelRowData;
    }
}
