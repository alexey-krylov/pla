package com.pla.grouphealth.claim.cashless.presentation.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.grouphealth.claim.cashless.application.command.claim.UploadGroupHealthCashlessClaimCommand;
import com.pla.grouphealth.claim.cashless.application.service.claim.GHCashlessClaimExcelHeader;
import com.pla.grouphealth.claim.cashless.application.service.claim.GroupHealthCashlessClaimService;
import com.pla.grouphealth.claim.cashless.application.service.preauthorization.PreAuthorizationService;
import com.pla.grouphealth.claim.cashless.presentation.dto.ClaimRelatedFileUploadDto;
import com.pla.grouphealth.claim.cashless.presentation.dto.ClaimUploadedExcelDataDto;
import com.pla.grouphealth.proposal.presentation.dto.GHProposalMandatoryDocumentDto;
import com.pla.publishedlanguage.contract.IAuthenticationFacade;
import com.pla.sharedkernel.domain.model.FamilyId;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.axonframework.commandhandling.callbacks.FutureCallback;
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

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Author - Mohan Sharma Created on 2/03/2016.
 */
@RestController
@RequestMapping(value = "/grouphealth/claim/cashless/claim")
public class GroupHealthCashlessClaimController {

    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private PreAuthorizationService preAuthorizationService;
    @Autowired
    private GroupHealthCashlessClaimService groupHealthCashlessClaimService;
    @Autowired
    @Qualifier("authenticationFacade")
    private IAuthenticationFacade authenticationFacade;

    @RequestMapping(value = "/downloadghcashlessclaimtemplate/{hcpCode}", method = RequestMethod.GET)
    public void downloadInsuredTemplate(@PathVariable("hcpCode") String ghCashlessClaimCode, HttpServletResponse response) throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "GroupHealthCashlessClaimTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook hcpRateExcel = preAuthorizationService.getGHCashlessClaimPreAuthtemplate(ghCashlessClaimCode, GHCashlessClaimExcelHeader.class);
        hcpRateExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/getghcashlessclaimuploadview" ,method = RequestMethod.GET)
    public ModelAndView preAuthUpload(){
        ModelAndView modelAndView=new ModelAndView();
        /*
        * create html and send the file
        * */
        //modelAndView.setViewName("pla/grouphealth/claim/preauthorizationupload");
        return modelAndView;
    }


    @RequestMapping(value = "/uploadghcashlessclaimtemplate", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadPreAuthorizationTemplate(ClaimRelatedFileUploadDto claimRelatedFileUploadDto, HttpServletRequest request) throws IOException {
        MultipartFile file = claimRelatedFileUploadDto.getFile();
        if (!("application/x-ms-excel".equals(file.getContentType())|| "application/ms-excel".equals(file.getContentType()) || "application/msexcel".equals(file.getContentType()) || "application/vnd.ms-excel".equals(file.getContentType()))) {
            return Result.failure("Uploaded file is not valid excel",Boolean.FALSE);
        }
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        HSSFWorkbook ghCashlessCLaimTemplate = new HSSFWorkbook(fs);
        try {
            Map dataMap = Maps.newHashMap();
            dataMap.put("hcpCode", claimRelatedFileUploadDto.getHcpCode());
            boolean isValidInsuredTemplate = groupHealthCashlessClaimService.isValidInsuredTemplate(ghCashlessCLaimTemplate, dataMap);
            if (!isValidInsuredTemplate) {
                File insuredTemplateWithError = new File(claimRelatedFileUploadDto.getHcpCode());
                FileOutputStream fileOutputStream = new FileOutputStream(insuredTemplateWithError);
                ghCashlessCLaimTemplate.write(fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return Result.failure("Uploaded claim template is not valid.Please download to check the errors", Boolean.TRUE);
            }
            String userName = StringUtils.EMPTY;
            Authentication authentication = authenticationFacade.getAuthentication();
            if(!(authentication instanceof AnonymousAuthenticationToken)){
                userName = authentication.getName();
            }
            Set<ClaimUploadedExcelDataDto> claimUploadedExcelDataDtoList = preAuthorizationService.transformToPreAuthorizationDetailDto(ghCashlessCLaimTemplate);
            FutureCallback callback = new FutureCallback();
            commandGateway.send(new UploadGroupHealthCashlessClaimCommand(claimRelatedFileUploadDto.getHcpCode(), claimUploadedExcelDataDtoList, claimRelatedFileUploadDto.getBatchDate(), userName), callback);
            return Result.success("claim details successfully uploaded.");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(e.getMessage(), Boolean.FALSE);
        }
    }

    @RequestMapping(value = "/downloaderrorpreauthtemplate/{hcpCode}", method = RequestMethod.GET)
    public void downloadErrorInsuredTemplate(@PathVariable("hcpCode") String hcpCode, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "GroupHealthCashlessClaimTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        File errorTemplateFile = new File(hcpCode);
        InputStream inputStream = new FileInputStream(errorTemplateFile);
        outputStream.write(IOUtils.toByteArray(inputStream));
        outputStream.flush();
        outputStream.close();
        errorTemplateFile.delete();
    }

    @RequestMapping(value = "/getmandatorydocuments/{clientId}/{groupHealthCashlessClaimId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list mandatory documents which is being configured in Mandatory Document SetUp")
    public List<GHProposalMandatoryDocumentDto> findMandatoryDocuments(@PathVariable("clientId") String clientId, @PathVariable("groupHealthCashlessClaimId") String groupHealthCashlessClaimId, HttpServletResponse response) throws Exception {
        if (isEmpty(clientId)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "clientId cannot be empty");
            return Lists.newArrayList();
        }
        List<GHProposalMandatoryDocumentDto> ghProposalMandatoryDocumentDtos = groupHealthCashlessClaimService.findMandatoryDocuments(new FamilyId(clientId), groupHealthCashlessClaimId);
        return ghProposalMandatoryDocumentDtos;
    }
}
