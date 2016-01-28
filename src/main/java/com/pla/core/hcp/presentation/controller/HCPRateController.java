package com.pla.core.hcp.presentation.controller;

import com.pla.core.hcp.application.command.UploadHCPServiceRatesCommand;
import com.pla.core.hcp.domain.model.HCPRate;
import com.pla.core.hcp.domain.model.HCPRateId;
import com.pla.core.hcp.presentation.dto.HCPServiceDetailDto;
import com.pla.core.hcp.presentation.dto.UploadHCPServiceRatesDto;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.*;
import java.util.Collections;
import java.util.Set;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Author - Mohan Sharma Created on 12/17/2015.
 */
@RequestMapping(value = "/core/hcprate")
@RestController
public class HCPRateController {
    @Autowired
    CommandGateway commandGateway;
    @Autowired
    HCPRateService hcpRateService;

    @RequestMapping(value = "/gethcprate",method = RequestMethod.GET)
    public ModelAndView hcpRate() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("uploadHCPServiceRatesDto", new UploadHCPServiceRatesDto());
        modelAndView.setViewName("pla/core/hcp/searchhcprate");
        return modelAndView;
    }

    @RequestMapping(value = "/downloadhcpratetemplate", method = RequestMethod.GET)
    public void downloadInsuredTemplate(@RequestParam(required = false) String hcpCode, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "HCPRateTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook hcpRateExcel = hcpRateService.getHCPRateTemplateExcel(hcpCode);
        hcpRateExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/downloaderrorhcpratetemplate/{hcpCode}", method = RequestMethod.GET)
    public void downloadErrorInsuredTemplate(@PathVariable("hcpCode") String hcpCode, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "HCPRateTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        File errorTemplateFile = new File(hcpCode);
        InputStream inputStream = new FileInputStream(errorTemplateFile);
        outputStream.write(IOUtils.toByteArray(inputStream));
        outputStream.flush();
        outputStream.close();
        errorTemplateFile.delete();
    }

    @RequestMapping(value = "/uploadhcpratedetails", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadInsuredDetail(@Valid @ModelAttribute("uploadHCPServiceRatesDto") UploadHCPServiceRatesDto uploadHCPServiceRatesDto, BindingResult bindingResult, HttpServletRequest request) throws IOException {
        MultipartFile file = uploadHCPServiceRatesDto.getFile();
        if(bindingResult.hasErrors()){
            return Result.failure("Error occured while upload", bindingResult.getAllErrors());
        }
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
    @RequestMapping(value = "/gethcprateservicebyhcpcode/{hcpCode}", method = RequestMethod.GET)
    public Set<String> getHcpRateServiceByHcpCode(@PathVariable("hcpCode") String hcpCode) {
        if (isNotEmpty(hcpCode)) {
            Set<String> hcpRateServices = hcpRateService.getHcpRateServiceByHcpCode(hcpCode);
            return hcpRateServices;
        }
        return Collections.EMPTY_SET;
    }
}
