package com.pla.core.hcp.presentation.controller;

import com.pla.core.hcp.application.command.UploadHCPServiceRatesCommand;
import com.pla.core.hcp.application.service.HCPService;
import com.pla.core.hcp.domain.model.HCPRate;
import com.pla.core.hcp.domain.model.HCPRateId;
import com.pla.core.hcp.presentation.dto.HCPServiceDetailDto;
import com.pla.core.hcp.presentation.dto.UploadHCPServiceRatesDto;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

/**
 * Created by Mohan Sharma on 12/17/2015.
 */
@RequestMapping(value = "/core/hcp")
@RestController
public class HCPRateController {
    @Autowired
    CommandGateway commandGateway;
    @Autowired
    HCPRateService hcpRateService;

    @RequestMapping(value = "/downloadhcpratetemplate/{hcpCode}", method = RequestMethod.GET)
    public void downloadInsuredTemplate(@PathVariable("hcpCode") String hcpCode, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "HCPRateTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook hcpRateExcel = hcpRateService.getHCPRateTemplateExcel(hcpCode);
        hcpRateExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/uploadhcpratedetails", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadInsuredDetail(UploadHCPServiceRatesDto uploadHCPServiceRatesDto, HttpServletRequest request) throws IOException {
        MultipartFile file = uploadHCPServiceRatesDto.getFile();
        if (!("application/x-ms-excel".equals(file.getContentType())|| "application/ms-excel".equals(file.getContentType()) || "application/msexcel".equals(file.getContentType()) || "application/vnd.ms-excel".equals(file.getContentType()))) {
            return Result.failure("Uploaded file is not valid excel",Boolean.FALSE);
        }
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        HSSFWorkbook insuredTemplateWorkbook = new HSSFWorkbook(fs);
        try {
            boolean isValidInsuredTemplate = hcpRateService.isValidInsuredTemplate(insuredTemplateWorkbook);
            if (!isValidInsuredTemplate) {
                File insuredTemplateWithError = new File(uploadHCPServiceRatesDto.getHcpCode());
                FileOutputStream fileOutputStream = new FileOutputStream(insuredTemplateWithError);
                insuredTemplateWorkbook.write(fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return Result.failure("Uploaded HCP Service Rate template is not valid.Please download to check the errors", Boolean.TRUE);
            }
            Set<HCPServiceDetailDto> hcpServiceDetailDtos = hcpRateService.transformToHCPServiceDetailDto(insuredTemplateWorkbook);
            HCPRate hcpRate = commandGateway.sendAndWait(new UploadHCPServiceRatesCommand(new HCPRateId(uploadHCPServiceRatesDto.getHcpRateId()),hcpServiceDetailDtos, uploadHCPServiceRatesDto.getHcpCode(), uploadHCPServiceRatesDto.getHcpName(), uploadHCPServiceRatesDto.getFromDate(), uploadHCPServiceRatesDto.getToDate()));
            return Result.success("HCP Service Rates uploaded successfully", hcpRate.getHcpRateId());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(e.getMessage(), Boolean.FALSE);
        }
    }
}
