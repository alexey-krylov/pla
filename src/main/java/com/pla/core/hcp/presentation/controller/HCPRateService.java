package com.pla.core.hcp.presentation.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pla.core.hcp.application.command.UploadHCPServiceRatesCommand;
import com.pla.core.hcp.application.service.HCPRateExcelGenerator;
import com.pla.core.hcp.application.service.HCPRateExcelHeader;
import com.pla.core.hcp.domain.model.*;
import com.pla.core.hcp.presentation.dto.HCPServiceDetailDto;
import com.pla.sharedkernel.util.ExcelUtilityProvider;
import com.pla.core.hcp.query.HCPFinder;
import com.pla.core.hcp.repository.HCPRateRepository;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.bson.types.ObjectId;
import org.nthdimenzion.common.service.JpaRepositoryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.pla.sharedkernel.util.ExcelUtilityProvider.*;
import static com.pla.sharedkernel.util.ExcelGeneratorUtil.getCellValue;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 12/21/2015.
 */
@Service
public class HCPRateService {
    private SimpleJpaRepository<HCP, HCPCode> hcpRepository;
    private HCPRateRepository hcpRateRepository;
    private HCPFinder hcpFinder;
    private HCPRateExcelGenerator hcpRateExcelGenerator;
    private ExcelUtilityProvider excelUtilityProvider;

    @Autowired
    public HCPRateService(JpaRepositoryFactory jpaRepositoryFactory, HCPFinder hcpFinder, HCPRateExcelGenerator hcpRateExcelGenerator, HCPRateRepository hcpRateRepository, ExcelUtilityProvider excelUtilityProvider){
        hcpRepository = jpaRepositoryFactory.getCrudRepository(HCP.class);
        this.hcpRateRepository = hcpRateRepository;
        this.hcpFinder = hcpFinder;
        this.hcpRateExcelGenerator = hcpRateExcelGenerator;
        this.excelUtilityProvider = excelUtilityProvider;
    }

    public HSSFWorkbook getHCPRateTemplateExcel(String hcpCode) {
        HCPRate hcpRate = hcpRateRepository.findHCPRateByHCPCode(hcpCode);
        Set<HCPServiceDetail> hcpServiceDetails = isEmpty(hcpRate) ? Sets.newHashSet() : hcpRate.getHcpServiceDetails();
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
        HSSFWorkbook hssfWorkbook = hcpRateExcelGenerator.generateInsuredExcel(hcpServiceDetailDtos);
        return hssfWorkbook;
    }

    public boolean isValidInsuredTemplate(HSSFWorkbook insuredTemplateWorkbook) {
        return excelUtilityProvider.isValidInsuredExcel(insuredTemplateWorkbook, HCPRateExcelHeader.getAllowedHeaders(), HCPRateExcelHeader.class, Maps.newHashMap());
    }

    public Set<HCPServiceDetailDto> transformToHCPServiceDetailDto(HSSFWorkbook insuredTemplateWorkbook) {
        Set<HCPServiceDetailDto> insuredDtoList = transformToInsuredDto(insuredTemplateWorkbook);
        return insuredDtoList;
    }

    private Set<HCPServiceDetailDto> transformToInsuredDto(HSSFWorkbook hssfWorkbook) {
        Set<HCPServiceDetailDto> hcpServiceDetailDtos = Sets.newLinkedHashSet();
        HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(0);
        Iterator<Row> rowIterator = hssfSheet.rowIterator();
        Row headerRow = rowIterator.next();
        final List<String> headers = getHeaders(headerRow);
        List<Row> dataRows = Lists.newArrayList(rowIterator);
        Iterator<Row> dataRowIterator = dataRows.iterator();
        while (dataRowIterator.hasNext()) {
            Row currentRow = dataRowIterator.next();
            String serviceDepartment = getCellValue(currentRow.getCell(headers.indexOf(HCPRateExcelHeader.SERVICE_DEPARTMENT.getDescription())));
            String serviceAvailed = getCellValue(currentRow.getCell(headers.indexOf(HCPRateExcelHeader.SERVICE_AVAILED.getDescription())));
            String normalAmount = getCellValue(currentRow.getCell(headers.indexOf(HCPRateExcelHeader.NORMAL.getDescription())));
            String afterHours = getCellValue(currentRow.getCell(headers.indexOf(HCPRateExcelHeader.AFTER_HOURS.getDescription())));
            HCPServiceDetailDto hcpServiceDetailDto = new HCPServiceDetailDto()
                    .updateWithServiceDepartment(serviceDepartment)
                    .updateWithServiceAvailed(serviceAvailed)
                    .updateWithNormalAmount(isNotEmpty(normalAmount) ? new BigDecimal(normalAmount) : BigDecimal.ZERO)
                    .updateWithAfterHours(isNotEmpty(afterHours) ? new BigDecimal(afterHours).intValue() : 0);
            hcpServiceDetailDtos.add(hcpServiceDetailDto);
        }
        return hcpServiceDetailDtos;
    }

    public HCPRate uploadHCPServiceRates(UploadHCPServiceRatesCommand uploadHCPServiceRatesCommand) {
        HCPRate hcpRate = hcpRateRepository.findHCPRateByHCPCode(uploadHCPServiceRatesCommand.getHcpCode());
        if(isEmpty(hcpRate)) {
            hcpRate = new HCPRate().updateWithHCPRateId(new HCPRateId(new ObjectId().toString()));
        }
        hcpRate.updateWithHcpServiceDetails(transformToHCPServiceDetail(uploadHCPServiceRatesCommand.getHcpServiceDetailDtos()))
                .updateWithHCPName(uploadHCPServiceRatesCommand.getHcpName())
                .updateWithHCPCode(new HCPCode(uploadHCPServiceRatesCommand.getHcpCode()))
                .updateWithFromDate(uploadHCPServiceRatesCommand.getFromDate())
                .updateWithToDate(uploadHCPServiceRatesCommand.getToDate());
        return hcpRateRepository.save(hcpRate);
    }

    private Set<HCPServiceDetail> transformToHCPServiceDetail(Set<HCPServiceDetailDto> hcpServiceDetailDtos) {
        return hcpServiceDetailDtos.stream().map(new Function<HCPServiceDetailDto, HCPServiceDetail>() {
            @Override
            public HCPServiceDetail apply(HCPServiceDetailDto hcpServiceDetailDto) {
                HCPServiceDetail hcpServiceDetail = new HCPServiceDetail()
                        .updateWithServiceDepartment(hcpServiceDetailDto.getServiceDepartment())
                        .updateWithServiceAvailed(hcpServiceDetailDto.getServiceAvailed())
                        .updateWithNormalAmount(hcpServiceDetailDto.getNormalAmount())
                        .updateWithAfterHours(hcpServiceDetailDto.getAfterHours());
                return hcpServiceDetail;
            }
        }).collect(Collectors.toSet());
    }
}
