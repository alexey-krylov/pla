package com.pla.grouphealth.claim.cashless.presentation.controller;

import com.google.common.collect.Maps;
import com.pla.grouphealth.claim.cashless.application.command.preauthorization.UploadPreAuthorizationCommand;
import com.pla.grouphealth.claim.cashless.application.service.preauthorization.PreAuthorizationExcelHeader;
import com.pla.grouphealth.claim.cashless.application.service.preauthorization.PreAuthorizationService;
import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.ClaimUploadedExcelDataDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.ClaimRelatedFileUploadDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.preauthorization.SearchPreAuthorizationRecordDto;
import com.pla.publishedlanguage.contract.IAuthenticationFacade;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.util.Assert.notNull;

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
    @Autowired
    @Qualifier("authenticationFacade")
    IAuthenticationFacade authenticationFacade;

    @RequestMapping(value = "/downloadGHCashlessClaimPreAuthtemplate/{ghCashlessClaimCode}", method = RequestMethod.GET)
    public void downloadInsuredTemplate(@PathVariable("ghCashlessClaimCode") String ghCashlessClaimCode, HttpServletResponse response) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "PreAuthorizationTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook hcpRateExcel = preAuthorizationService.getGHCashlessClaimPreAuthtemplate(ghCashlessClaimCode, PreAuthorizationExcelHeader.class);
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
    public Result uploadPreAuthorizationTemplate(ClaimRelatedFileUploadDto claimRelatedFileUploadDto, HttpServletRequest request) throws IOException {
        MultipartFile file = claimRelatedFileUploadDto.getFile();
        if (!("application/x-ms-excel".equals(file.getContentType())|| "application/ms-excel".equals(file.getContentType()) || "application/msexcel".equals(file.getContentType()) || "application/vnd.ms-excel".equals(file.getContentType()))) {
            return Result.failure("Uploaded file is not valid excel",Boolean.FALSE);
        }
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        HSSFWorkbook preAuthTemplateWorkbook = new HSSFWorkbook(fs);
        try {
            Map dataMap = Maps.newHashMap();
            dataMap.put("hcpCode", claimRelatedFileUploadDto.getHcpCode());
            boolean isValidInsuredTemplate = preAuthorizationService.isValidInsuredTemplate(preAuthTemplateWorkbook, dataMap);
            if (!isValidInsuredTemplate) {
                File insuredTemplateWithError = new File(claimRelatedFileUploadDto.getHcpCode());
                FileOutputStream fileOutputStream = new FileOutputStream(insuredTemplateWithError);
                preAuthTemplateWorkbook.write(fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return Result.failure("Uploaded Pre-Auth template is not valid.Please download to check the errors", Boolean.TRUE);
            }
            String userName = StringUtils.EMPTY;
            Authentication authentication = authenticationFacade.getAuthentication();
            if(!(authentication instanceof AnonymousAuthenticationToken)){
                userName = authentication.getName();
            }
            Set<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtoList = preAuthorizationService.transformToPreAuthorizationDetailDto(preAuthTemplateWorkbook);
            int batchNumber = commandGateway.sendAndWait(new UploadPreAuthorizationCommand(claimRelatedFileUploadDto.getHcpCode(), claimUploadedExcelDataDtoList, claimRelatedFileUploadDto.getBatchDate(), userName));
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
}
