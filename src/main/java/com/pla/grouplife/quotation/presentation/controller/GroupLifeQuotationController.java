package com.pla.grouplife.quotation.presentation.controller;

import com.google.common.collect.Lists;
import com.pla.grouplife.quotation.application.command.*;
import com.pla.grouplife.quotation.application.service.GLQuotationService;
import com.pla.grouplife.quotation.presentation.dto.GLQuotationMailDto;
import com.pla.grouplife.quotation.query.GLQuotationFinder;
import com.pla.grouplife.sharedresource.dto.*;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.grouplife.sharedresource.dto.PremiumDetailDto;
import com.pla.grouplife.sharedresource.dto.ProposerDto;
import com.pla.publishedlanguage.contract.IClientProvider;
import com.pla.publishedlanguage.dto.ClientDetailDto;
import com.pla.sharedkernel.identifier.QuotationId;
import com.pla.sharedkernel.service.EmailAttachment;
import com.pla.sharedkernel.service.MailService;
import com.wordnik.swagger.annotations.ApiOperation;
import net.sf.jasperreports.engine.JRException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.dom4j.DocumentException;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.presentation.AppUtils.getLoggedInUserDetail;
import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by Samir on 4/14/2015.
 */
@Controller
@RequestMapping(value = "/quotation/grouplife")
public class GroupLifeQuotationController {

    private CommandGateway commandGateway;

    private GLQuotationService glQuotationService;

    private GLQuotationFinder glQuotationFinder;

    private MailService mailService;

    private IClientProvider clientProvider;

    @Autowired
    public GroupLifeQuotationController(CommandGateway commandGateway, GLQuotationService glQuotationService, GLQuotationFinder glQuotationFinder, MailService mailService, IClientProvider clientProvider) {
        this.commandGateway = commandGateway;
        this.glQuotationService = glQuotationService;
        this.glQuotationFinder = glQuotationFinder;
        this.mailService = mailService;
        this.clientProvider = clientProvider;

    }

    @RequestMapping(value = "/creategrouplifequotation", method = RequestMethod.GET)
    public ModelAndView createQuotationPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/quotation/groupLife/createQuotation");
        return modelAndView;
    }

    @RequestMapping(value = "/getquotationnumber/{quotationId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Get Quotation number for a given quotation Id")
    @ResponseBody
    public Result getQuotationNumber(@PathVariable("quotationId") String quotationId) {
        Map quotationMap = glQuotationFinder.getQuotationById(quotationId);
        String versionNumber = (Integer) quotationMap.get("versionNumber") != 0 ? ("/" + quotationMap.get("versionNumber").toString()) : "";
        return Result.success("Quotation number ", quotationMap.get("quotationNumber") + versionNumber);
    }

    @RequestMapping(value = "/isinsureddetailavailable/{quotationId}", method = RequestMethod.GET)
    @ResponseBody
    public boolean isInsuredDetailAvailable(@PathVariable("quotationId") String quotationId) {
        return glQuotationService.isInsuredDataUpdated(quotationId);
    }

    @RequestMapping(value = "/getversionnumber/{quotationId}", method = RequestMethod.GET)
    @ResponseBody
    public Result getVersionNumber(@PathVariable("quotationId") String quotationId) {
        Map quotationMap = glQuotationFinder.getQuotationById(quotationId);
        return Result.success("Quotation Version number ", quotationMap.get("versionNumber"));
    }

    @RequestMapping(value = "/getagentdetail/{agentId}", method = RequestMethod.GET)
    @ResponseBody
    public Result getAgentDetail(@PathVariable("agentId") String agentId) {
        Map<String, Object> agentDetail = glQuotationFinder.getAgentById(agentId);
        if (isEmpty(agentDetail)) {
            return Result.failure("Agent detail not found");
        }
        checkArgument(agentDetail != null);
        CreateGLQuotationCommand createGLQuotationCommand = new CreateGLQuotationCommand();
        createGLQuotationCommand.setAgentId(agentId);
        createGLQuotationCommand.setBranchName(agentDetail.get("branchName") != null ? (String) agentDetail.get("branchName") : "");
        createGLQuotationCommand.setTeamName(agentDetail.get("teamName") != null ? (String) agentDetail.get("teamName") : "");
        createGLQuotationCommand.setAgentName(agentDetail.get("firstName") != null ? (String) agentDetail.get("firstName") : "" + " " + agentDetail.get("lastName") != null ? (String) agentDetail.get("lastName") : "");
        return Result.success("Agent found", createGLQuotationCommand);
    }

    @RequestMapping(value = "/getagentdetailfromquotation/{quotationId}", method = RequestMethod.GET)
    @ResponseBody
    public AgentDetailDto getAgentDetailFromQuotation(@PathVariable("quotationId") String quotationId) {
        return glQuotationService.getActiveInactiveAgentDetail(new QuotationId(quotationId));
    }

    @RequestMapping(value = "/getproposerdetailfromclient/{proposerCode}/{quotationId}", method = RequestMethod.GET)
    @ResponseBody
    public ProposerDto getProposerDetailFromClientRepository(@PathVariable("proposerCode") String proposerCode, @PathVariable("quotationId") String quotationId) {
        ClientDetailDto clientDetailDto = clientProvider.getClientDetail(proposerCode);
        ProposerDto proposerDto = new ProposerDto();
        if (clientDetailDto != null) {
            proposerDto.setProposerName(clientDetailDto.getClientName());
            proposerDto.setAddressLine1(clientDetailDto.getAddress1());
            proposerDto.setAddressLine2(clientDetailDto.getAddress2());
            proposerDto.setPostalCode(clientDetailDto.getPostalCode());
            proposerDto.setContactPersonEmail(clientDetailDto.getEmailAddress());
            proposerDto.setTown(clientDetailDto.getTown());
            proposerDto.setProvince(clientDetailDto.getProvince());
            proposerDto.setProposerCode(proposerCode);
        } else {
            proposerDto = getProposerDetail(quotationId);
            proposerDto.setProposerCode(proposerCode);
        }
        return proposerDto;
    }

    @RequestMapping(value = "/listgrouplifequotation", method = RequestMethod.GET)
    public ModelAndView listQuotation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/quotation/groupLife/viewQuotation");
        modelAndView.addObject("searchCriteria", new SearchGlQuotationDto());
        return modelAndView;
    }

    @RequestMapping(value = "/searchquotation", method = RequestMethod.POST)
    public ModelAndView searchQuotation(SearchGlQuotationDto searchGlQuotationDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/quotation/groupLife/viewQuotation");
        try {
            modelAndView.addObject("searchResult", glQuotationService.searchQuotation(searchGlQuotationDto));
        } catch (Exception e) {
            modelAndView.addObject("searchResult", Lists.newArrayList());
        }
        modelAndView.addObject("searchCriteria", searchGlQuotationDto);
        return modelAndView;
    }

    @RequestMapping(value = "/createquotation", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "Create Group Life Quotation")
    @ResponseBody
    public Result createQuotation(@RequestBody CreateGLQuotationCommand createGLQuotationCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Create quotation data is not valid", bindingResult.getAllErrors());
        }
        try {
            createGLQuotationCommand.setUserDetails(getLoggedInUserDetail(request));
            String quotationId = commandGateway.sendAndWait(createGLQuotationCommand);
            return Result.success("Quotation created successfully", quotationId);
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/updatewithagentdetail", method = RequestMethod.POST)
    @ResponseBody
    public Result updateQuotationWithAgentDetail(@RequestBody UpdateGLQuotationWithAgentCommand updateGLQuotationWithAgentCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update quotation agent data is not valid", bindingResult.getAllErrors());
        }
        try {
            updateGLQuotationWithAgentCommand.setUserDetails(getLoggedInUserDetail(request));
            String quotationId = commandGateway.sendAndWait(updateGLQuotationWithAgentCommand);
            return Result.success("Agent detail updated successfully", quotationId);
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/updatewithproposerdetail", method = RequestMethod.POST)
    @ResponseBody
    public Result updateQuotationWithProposerDetail(@RequestBody UpdateGLQuotationWithProposerCommand updateGLQuotationWithProposerCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update quotation proposer data is not valid", bindingResult.getAllErrors());
        }
        try {
            updateGLQuotationWithProposerCommand.setUserDetails(getLoggedInUserDetail(request));
            String quotationId = commandGateway.sendAndWait(updateGLQuotationWithProposerCommand);
            return Result.success("Proposer detail updated successfully", quotationId);
        } catch (Exception e) {
            return Result.failure();
        }
    }

    @RequestMapping(value = "/downloadplandetail/{quotationId}", method = RequestMethod.GET)
    public void downloadPlanDetail(@PathVariable("quotationId") String quotationId, HttpServletResponse response) throws IOException, JRException {
        response.reset();
        response.setContentType("application/pdf");
        response.setHeader("content-disposition", "attachment; filename=" + "planReadyReckoner.pdf" + "");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(glQuotationService.getPlanReadyReckoner(quotationId));
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/openemailquotation/{quotationId}", method = RequestMethod.GET)
    public ModelAndView openEmailPage(@PathVariable("quotationId") String quotationId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/quotation/groupLife/emailQuotation");
        modelAndView.addObject("mailContent", glQuotationService.getPreScriptedEmail(quotationId));
        return modelAndView;
    }

    @RequestMapping(value = "/openemailquotationwosplit/{quotationId}", method = RequestMethod.GET)
    public ModelAndView openEmailPageWOSplit(@PathVariable("quotationId") String quotationId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/quotation/groupLife/emailQuotationWOSplit");
        modelAndView.addObject("mailContent", glQuotationService.getPreScriptedEmail(quotationId));
        return modelAndView;
    }

    @RequestMapping(value = "/emailQuotation", method = RequestMethod.POST)
    @ResponseBody
    public Result emailQuotation(@RequestBody GLQuotationMailDto mailDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Email cannot be sent due to wrong data");
        }
        try {
            byte[] quotationData = glQuotationService.getQuotationPDF(mailDto.getQuotationId(), false);
            emailQuotation(quotationData, mailDto);
            commandGateway.sendAndWait(new ShareGLQuotationCommand(new QuotationId(mailDto.getQuotationId())));
            return Result.success("Email sent successfully");

        } catch (Exception e) {
            Result.failure(e.getMessage());
        }
        return Result.success("Email sent successfully");
    }

    @RequestMapping(value = "/emailQuotationwithoutsplit", method = RequestMethod.POST)
    @ResponseBody
    public Result emailQuotationWithoutSplit(@RequestBody GLQuotationMailDto mailDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Email cannot be sent due to wrong data");
        }
        try {
            byte[] quotationData = glQuotationService.getQuotationPDF(mailDto.getQuotationId(), true);
            emailQuotation(quotationData, mailDto);
            commandGateway.sendAndWait(new ShareGLQuotationCommand(new QuotationId(mailDto.getQuotationId())));
            return Result.success("Email sent successfully");

        } catch (Exception e) {
            Result.failure(e.getMessage());
        }
        return Result.success("Email sent successfully");
    }


    private void emailQuotation(byte[] pdfData, GLQuotationMailDto mailDto) throws IOException, DocumentException {
        String fileName = "QuotationNo-" + mailDto.getQuotationNumber() + ".pdf";
        File file = new File(fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(pdfData);
        fileOutputStream.flush();
        fileOutputStream.close();
        EmailAttachment emailAttachment = new EmailAttachment(fileName, "application/pdf", file);
        mailService.sendMailWithAttachment(mailDto.getSubject(), mailDto.getMailContent(), Arrays.asList(emailAttachment), mailDto.getRecipientMailAddress());
        file.delete();
    }

    @RequestMapping(value = "/printquotation/{quotationId}", method = RequestMethod.GET)
    public void printQuotation(@PathVariable("quotationId") String quotationId, HttpServletResponse response) throws IOException, JRException {
        response.reset();
        response.setContentType("application/pdf");
        response.setHeader("content-disposition", "attachment; filename=" + "quotation.pdf" + "");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(glQuotationService.getQuotationPDF(quotationId, false));
        outputStream.flush();
        outputStream.close();
        commandGateway.sendAndWait(new ShareGLQuotationCommand(new QuotationId(quotationId)));
    }

    @RequestMapping(value = "/printquotationwithoutsplit/{quotationId}", method = RequestMethod.GET)
    public void printQuotationWithoutSplit(@PathVariable("quotationId") String quotationId, HttpServletResponse response) throws IOException, JRException {
        response.reset();
        response.setContentType("application/pdf");
        response.setHeader("content-disposition", "attachment; filename=" + "quotation.pdf" + "");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(glQuotationService.getQuotationPDF(quotationId, true));
        outputStream.flush();
        outputStream.close();
        commandGateway.sendAndWait(new ShareGLQuotationCommand(new QuotationId(quotationId)));
    }


    @RequestMapping(value = "/downloadinsuredtemplate/{quotationId}", method = RequestMethod.GET)
    public void downloadInsuredTemplate(@PathVariable("quotationId") String quotationId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "insuredTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook planDetailExcel = glQuotationService.getInsuredTemplateExcel(quotationId);
        planDetailExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/downloaderrorinsuredtemplate/{quotationId}", method = RequestMethod.GET)
    public void downloadErrorInsuredTemplate(@PathVariable("quotationId") String quotationId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "insuredTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        File errorTemplateFile = new File(quotationId);
        InputStream inputStream = new FileInputStream(errorTemplateFile);
        outputStream.write(IOUtils.toByteArray(inputStream));
        outputStream.flush();
        outputStream.close();
        errorTemplateFile.delete();
    }

    @RequestMapping(value = "/uploadinsureddetail", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadInsuredDetail(UploadInsuredDetailDto uploadInsuredDetailDto, HttpServletRequest request) throws IOException {
        MultipartFile file = uploadInsuredDetailDto.getFile();
        if (!( "application/x-ms-excel".equals(file.getContentType()) || "application/ms-excel".equals(file.getContentType()) || "application/msexcel".equals(file.getContentType()) || "application/vnd.ms-excel".equals(file.getContentType()))) {
            return Result.failure("Uploaded file is not valid excel");
        }
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        HSSFWorkbook insuredTemplateWorkbook = new HSSFWorkbook(fs);
        try {
            boolean isValidInsuredTemplate = glQuotationService.isValidInsuredTemplate(uploadInsuredDetailDto.getQuotationId(), insuredTemplateWorkbook, uploadInsuredDetailDto.isSamePlanForAllCategory(), uploadInsuredDetailDto.isSamePlanForAllRelation());
            if (!isValidInsuredTemplate) {
                File insuredTemplateWithError = new File(uploadInsuredDetailDto.getQuotationId());
                FileOutputStream fileOutputStream = new FileOutputStream(insuredTemplateWithError);
                insuredTemplateWorkbook.write(fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return Result.failure("Uploaded Insured template is not valid.Please download to check the errors");
            }
            List<InsuredDto> insuredDtos = glQuotationService.transformToInsuredDto(insuredTemplateWorkbook, uploadInsuredDetailDto.getQuotationId(), uploadInsuredDetailDto.isSamePlanForAllCategory(), uploadInsuredDetailDto.isSamePlanForAllRelation());
            String quotationId = commandGateway.sendAndWait(new UpdateGLQuotationWithInsuredCommand(uploadInsuredDetailDto.getQuotationId(), insuredDtos, getLoggedInUserDetail(request), uploadInsuredDetailDto.isApplyIndustryLoadingFactor(), uploadInsuredDetailDto.isSamePlanForAllRelation(), uploadInsuredDetailDto.isSamePlanForAllCategory()));
            return Result.success("Insured detail uploaded successfully", quotationId);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/getpremiumdetail/{quotationid}", method = RequestMethod.GET)
    @ResponseBody
    public PremiumDetailDto getPremiumDetail(@PathVariable("quotationid") String quotationId) {
        return glQuotationService.getPremiumDetail(new QuotationId(quotationId));
    }

    @RequestMapping(value = "/recalculatePremium", method = RequestMethod.POST)
    @ResponseBody
    public Result reCalculatePremium(@RequestBody GLRecalculatedInsuredPremiumCommand glRecalculatedInsuredPremiumCommand, HttpServletRequest request) {
        PremiumDetailDto premiumDetailDto = null;
        try {
            glRecalculatedInsuredPremiumCommand.setUserDetails(getLoggedInUserDetail(request));
            premiumDetailDto = glQuotationService.recalculatePremium(glRecalculatedInsuredPremiumCommand);
            return Result.success("Premium recalculated successfully", premiumDetailDto);
        } catch (Exception e) {
            return Result.success(e.getMessage());
        }
    }

    @RequestMapping(value = "/savepremiumdetail", method = RequestMethod.POST)
    @ResponseBody
    public Result savePremiumDetail(@RequestBody UpdateGLQuotationWithPremiumDetailCommand updateGLQuotationWithPremiumDetailCommand, HttpServletRequest request) {
        try {
            updateGLQuotationWithPremiumDetailCommand.setUserDetails(getLoggedInUserDetail(request));
            commandGateway.sendAndWait(updateGLQuotationWithPremiumDetailCommand);
            return Result.success("Premium detail saved successfully");
        } catch (Exception e) {
            return Result.success(e.getMessage());
        }
    }

    @RequestMapping(value = "/generate", method = RequestMethod.POST)
    @ResponseBody
    public Result generateQuotation(@RequestBody GenerateGLQuotationCommand generateGLQuotationCommand) {
        try {
            commandGateway.sendAndWait(generateGLQuotationCommand);
            return Result.success("Quotation generated successfully");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/getproposerdetail/{quotationId}")
    @ResponseBody
    public ProposerDto getProposerDetail(@PathVariable("quotationId") String quotationId) {
        return glQuotationService.getProposerDetail(new QuotationId(quotationId));
    }
}
