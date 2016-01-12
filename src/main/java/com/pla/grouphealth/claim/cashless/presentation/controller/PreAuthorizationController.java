package com.pla.grouphealth.claim.cashless.presentation.controller;

import com.pla.grouphealth.claim.cashless.application.command.UploadPreAuthorizationCommand;
import com.pla.grouphealth.claim.cashless.application.service.PreAuthorizationService;
import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationDetailDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.PreAuthorizationUploadDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.SearchPreAuthorizationRecordDto;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author - Mohan Sharma Created on 12/30/2015.
 */
@RestController
@RequestMapping(value = "/grouphealth/claim/cashless/preauthorization")
public class PreAuthorizationController {

    @Autowired
    CommandGateway commandGateway;
    @Autowired
    PreAuthorizationService preAuthorizationService;

    @RequestMapping(value = "/downloadGHCashlessClaimPreAuthtemplate/{ghCashlessClaimCode}", method = RequestMethod.GET)
    public void downloadInsuredTemplate(@PathVariable("ghCashlessClaimCode") String ghCashlessClaimCode, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "PreAuthorizationTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook hcpRateExcel = preAuthorizationService.getGHCashlessClaimPreAuthtemplate(ghCashlessClaimCode);
        hcpRateExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/preAuthUpload" ,method = RequestMethod.GET)
    public ModelAndView preAuthUpload(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/claim/preauthorizationupload");
        return modelAndView;
    }

    @RequestMapping(value = "/uploadPreAuthorizationTemplate", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadPreAuthorizationTemplate(PreAuthorizationUploadDto preAuthorizationUploadDto, HttpServletRequest request) throws IOException {
        MultipartFile file = preAuthorizationUploadDto.getFile();
        if (!("application/x-ms-excel".equals(file.getContentType())|| "application/ms-excel".equals(file.getContentType()) || "application/msexcel".equals(file.getContentType()) || "application/vnd.ms-excel".equals(file.getContentType()))) {
            return Result.failure("Uploaded file is not valid excel",Boolean.FALSE);
        }
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        HSSFWorkbook preAuthTemplateWorkbook = new HSSFWorkbook(fs);
        try {
            boolean isValidInsuredTemplate = preAuthorizationService.isValidInsuredTemplate(preAuthTemplateWorkbook);
            if (!isValidInsuredTemplate) {
                File insuredTemplateWithError = new File(preAuthorizationUploadDto.getHcpCode());
                FileOutputStream fileOutputStream = new FileOutputStream(insuredTemplateWithError);
                preAuthTemplateWorkbook.write(fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return Result.failure("Uploaded Pre-Auth template is not valid.Please download to check the errors", Boolean.TRUE);
            }
            Set<PreAuthorizationDetailDto> preAuthorizationDetailDtoList = preAuthorizationService.transformToPreAuthorizationDetailDto(preAuthTemplateWorkbook);
            int batchNumber = commandGateway.sendAndWait(new UploadPreAuthorizationCommand(preAuthorizationUploadDto.getHcpCode(), preAuthorizationDetailDtoList, preAuthorizationUploadDto.getBatchDate()));
            return Result.success("Insured detail uploaded successfully - "+batchNumber);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(e.getMessage(), Boolean.FALSE);
        }
    }

    @RequestMapping(value = "/downloaderrorpreauthtemplate/{hcpCode}", method = RequestMethod.GET)
    public void downloadErrorInsuredTemplate(@PathVariable("hcpCode") String hcpCode, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "PreAuthorizationTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        File errorTemplateFile = new File(hcpCode);
        InputStream inputStream = new FileInputStream(errorTemplateFile);
        outputStream.write(IOUtils.toByteArray(inputStream));
        outputStream.flush();
        outputStream.close();
        errorTemplateFile.delete();
    }

    @RequestMapping(value = "/getAllHcpNameAndCode",method = RequestMethod.GET)
    public List<Map<String,Object>> getAllHcpNameAndCode(){
        return  preAuthorizationService.getAllHcpNameAndCode();
    }

    @RequestMapping(value="/loadsearchPreAuthorizationRecordPage",method = RequestMethod.GET)
    public ModelAndView loadsearchPreAuthorizationRecordPage(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/claim/searchPreAuthorizationRecord");
        modelAndView.addObject("searchCriteria", new SearchPreAuthorizationRecordDto());
        return  modelAndView;
    }

    @RequestMapping(value="/searchpreAuthorizationrecord",method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView searchPreAuthorizationRecord(SearchPreAuthorizationRecordDto searchPreAuthorizationRecordDto){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.addObject("searchCriteria", searchPreAuthorizationRecordDto);
        modelAndView.setViewName("pla/grouphealth/claim/searchPreAuthorizationRecord");
        List<PreAuthorizationDto> preAuthorizationResult =  preAuthorizationService.searchPreAuthorizationRecord(searchPreAuthorizationRecordDto);
        modelAndView.addObject("preAuthorizationResult",preAuthorizationResult);
        return  modelAndView;
    }

    @RequestMapping(value="/searchPreAuthorizationclaimAmendment",method = RequestMethod.GET)
    public ModelAndView searchPreAuthorizationRecor(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/claim/claimAmendment");

        return  modelAndView;
    }
    @RequestMapping(value="/searchPreAuthorizationcashlessClaim",method = RequestMethod.GET)
    public ModelAndView searchPreAuthorizationReco(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/claim/cashlessClaim");
        return  modelAndView;
    }
}
