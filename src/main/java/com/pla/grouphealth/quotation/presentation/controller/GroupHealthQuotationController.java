package com.pla.grouphealth.quotation.presentation.controller;

import com.google.common.collect.Lists;
import com.pla.grouphealth.quotation.application.command.*;
import com.pla.grouphealth.quotation.application.service.GHQuotationService;
import com.pla.grouphealth.quotation.presentation.dto.GLQuotationMailDto;
import com.pla.grouphealth.quotation.query.GHQuotationFinder;
import com.pla.grouphealth.sharedresource.dto.*;
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
@RequestMapping(value = "/quotation/grouphealth")
public class GroupHealthQuotationController {

    private CommandGateway commandGateway;

    private GHQuotationService ghQuotationService;

    private GHQuotationFinder ghQuotationFinder;

    private MailService mailService;

    private IClientProvider clientProvider;

    @Autowired
    public GroupHealthQuotationController(CommandGateway commandGateway, GHQuotationService ghQuotationService, GHQuotationFinder ghQuotationFinder, MailService mailService, IClientProvider clientProvider) {
        this.commandGateway = commandGateway;
        this.ghQuotationService = ghQuotationService;
        this.ghQuotationFinder = ghQuotationFinder;
        this.mailService = mailService;
        this.clientProvider = clientProvider;
    }

    @RequestMapping(value = "/creategrouphealthquotation", method = RequestMethod.GET)
    public ModelAndView createQuotationPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/quotation/createQuotation");
        return modelAndView;
    }

    @RequestMapping(value = "/getquotationnumber/{quotationId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Get Quotation number for a given quotation Id")
    @ResponseBody
    public Result getQuotationNumber(@PathVariable("quotationId") String quotationId) {
        Map quotationMap = ghQuotationFinder.getQuotationById(quotationId);
        String versionNumber = (Integer) quotationMap.get("versionNumber") != 0 ? ("/" + quotationMap.get("versionNumber").toString()) : "";
        return Result.success("Quotation number ", quotationMap.get("quotationNumber") + versionNumber);
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

    @RequestMapping(value = "/isinsureddetailavailable/{quotationId}", method = RequestMethod.GET)
    @ResponseBody
    public boolean isInsuredDetailAvailable(@PathVariable("quotationId") String quotationId) {
        return ghQuotationService.isInsuredDataUpdated(quotationId);
    }

    @RequestMapping(value = "/getversionnumber/{quotationId}", method = RequestMethod.GET)
    @ResponseBody
    public Result getVersionNumber(@PathVariable("quotationId") String quotationId) {
        Map quotationMap = ghQuotationFinder.getQuotationById(quotationId);
        return Result.success("Quotation Version number ", quotationMap.get("versionNumber"));
    }

    @RequestMapping(value = "/getagentdetail/{agentId}", method = RequestMethod.GET)
    @ResponseBody
    public Result getAgentDetail(@PathVariable("agentId") String agentId) {
        Map<String, Object> agentDetail = ghQuotationFinder.getBrokerById(agentId);
        if (isEmpty(agentDetail)) {
            return Result.failure("Please enter a relevant broker ID.");
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
        return ghQuotationService.getActiveInactiveAgentDetail(new QuotationId(quotationId));
    }

    @RequestMapping(value = "/listgrouphealthquotation", method = RequestMethod.GET)
    public ModelAndView listQuotation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/quotation/viewQuotation");
        modelAndView.addObject("searchCriteria", new SearchGlQuotationDto());
        return modelAndView;
    }

    @RequestMapping(value = "/searchquotation", method = RequestMethod.POST)
    public ModelAndView searchQuotation(SearchGlQuotationDto searchGlQuotationDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/quotation/viewQuotation");
        try {
            modelAndView.addObject("searchResult", ghQuotationService.searchQuotation(searchGlQuotationDto));
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
    public Result updateQuotationWithProposerDetail(@RequestBody UpdateGHQuotationWithProposerCommand updateGHQuotationWithProposerCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update quotation proposer data is not valid", bindingResult.getAllErrors());
        }
        try {
            updateGHQuotationWithProposerCommand.setUserDetails(getLoggedInUserDetail(request));
            String quotationId = commandGateway.sendAndWait(updateGHQuotationWithProposerCommand);
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
        outputStream.write(ghQuotationService.getPlanReadyReckoner(quotationId));
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/openemailquotation/{quotationId}", method = RequestMethod.GET)
    public ModelAndView openEmailPage(@PathVariable("quotationId") String quotationId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/quotation/emailQuotation");
        modelAndView.addObject("mailContent", ghQuotationService.getPreScriptedEmail(quotationId));
        return modelAndView;
    }


    @RequestMapping(value = "/openemailquotationwosplit/{quotationId}", method = RequestMethod.GET)
    public ModelAndView openEmailPageWithoutSplit(@PathVariable("quotationId") String quotationId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/quotation/emailQuotationWOSplit");
        modelAndView.addObject("mailContent", ghQuotationService.getPreScriptedEmail(quotationId));
        return modelAndView;
    }


    @RequestMapping(value = "/emailQuotation", method = RequestMethod.POST)
    @ResponseBody
    public Result emailQuotation(@RequestBody GLQuotationMailDto mailDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Email cannot be sent due to wrong data");
        }
        try {
            byte[] quotationData = ghQuotationService.getQuotationPDF(mailDto.getQuotationId(), false);
            emailQuotation(quotationData, mailDto);
            commandGateway.send(new GHSharedQuotationCommand(new QuotationId(mailDto.getQuotationId())));
            return Result.success("Email sent successfully");

        } catch (Exception e) {
            Result.failure(e.getMessage());
        }
        return Result.success("Email sent successfully");
    }

    @RequestMapping(value = "/emailQuotationwosplit", method = RequestMethod.POST)
    @ResponseBody
    public Result emailQuotationWithoutSplit(@RequestBody GLQuotationMailDto mailDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Email cannot be sent due to wrong data");
        }
        try {
            byte[] quotationData = ghQuotationService.getQuotationPDF(mailDto.getQuotationId(), true);
            emailQuotation(quotationData, mailDto);
            commandGateway.send(new GHSharedQuotationCommand(new QuotationId(mailDto.getQuotationId())));
            return Result.success("Email sent successfully");

        } catch (Exception e) {
            Result.failure(e.getMessage());
        }
        return Result.success("Email sent successfully");
    }

    private void emailQuotation(byte[] quotationData, GLQuotationMailDto mailDto) throws IOException, DocumentException {
        String fileName = "QuotationNo-" + mailDto.getQuotationNumber() + ".pdf";
        File file = new File(fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(quotationData);
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
        response.setHeader("content-disposition", "attachment; filename=" + "GHQuotation.pdf" + "");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(ghQuotationService.getQuotationPDF(quotationId, false));
        outputStream.flush();
        outputStream.close();
        commandGateway.send(new GHSharedQuotationCommand(new QuotationId(quotationId)));
    }

    @RequestMapping(value = "/printquotationwosplit/{quotationId}", method = RequestMethod.GET)
    public void printQuotationWithoutSplit(@PathVariable("quotationId") String quotationId, HttpServletResponse response) throws IOException, JRException {
        response.reset();
        response.setContentType("application/pdf");
        response.setHeader("content-disposition", "attachment; filename=" + "GHQuotation.pdf" + "");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(ghQuotationService.getQuotationPDF(quotationId, true));
        outputStream.flush();
        outputStream.close();
        commandGateway.send(new GHSharedQuotationCommand(new QuotationId(quotationId)));
    }


    @RequestMapping(value = "/downloadinsuredtemplate/{quotationId}", method = RequestMethod.GET)
    public void downloadInsuredTemplate(@PathVariable("quotationId") String quotationId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "GHInsuredTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook planDetailExcel = ghQuotationService.getInsuredTemplateExcel(quotationId);
        planDetailExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/downloaderrorinsuredtemplate/{quotationId}", method = RequestMethod.GET)
    public void downloadErrorInsuredTemplate(@PathVariable("quotationId") String quotationId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "GHInsuredTemplate.xls" + "");
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
        if (!("application/x-ms-excel".equals(file.getContentType())|| "application/ms-excel".equals(file.getContentType()) || "application/msexcel".equals(file.getContentType()) || "application/vnd.ms-excel".equals(file.getContentType()))) {
            return Result.failure("Uploaded file is not valid excel");
        }
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        HSSFWorkbook insuredTemplateWorkbook = new HSSFWorkbook(fs);
        try {
            boolean isValidInsuredTemplate = ghQuotationService.isValidInsuredTemplate(uploadInsuredDetailDto.getQuotationId(), insuredTemplateWorkbook, uploadInsuredDetailDto.isSamePlanForAllCategory(), uploadInsuredDetailDto.isSamePlanForAllRelation());
            if (!isValidInsuredTemplate) {
                File insuredTemplateWithError = new File(uploadInsuredDetailDto.getQuotationId());
                FileOutputStream fileOutputStream = new FileOutputStream(insuredTemplateWithError);
                insuredTemplateWorkbook.write(fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return Result.failure("Uploaded Insured template is not valid.Please download to check the errors");
            }
            List<GHInsuredDto> insuredDtos = ghQuotationService.transformToInsuredDto(insuredTemplateWorkbook, uploadInsuredDetailDto.getQuotationId(), uploadInsuredDetailDto.isSamePlanForAllCategory(), uploadInsuredDetailDto.isSamePlanForAllRelation());
            String quotationId = commandGateway.sendAndWait(new UpdateGLQuotationWithInsuredCommand(uploadInsuredDetailDto.getQuotationId(), insuredDtos, getLoggedInUserDetail(request), uploadInsuredDetailDto.isConsiderMoratoriumPeriod(), uploadInsuredDetailDto.isSamePlanForAllRelation(), uploadInsuredDetailDto.isSamePlanForAllCategory()));
            return Result.success("Insured detail uploaded successfully", quotationId);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/getpremiumdetail/{quotationid}", method = RequestMethod.GET)
    @ResponseBody
    public GHPremiumDetailDto getPremiumDetail(@PathVariable("quotationid") String quotationId) {
        return ghQuotationService.getPremiumDetail(new QuotationId(quotationId));
    }

    @RequestMapping(value = "/recalculatePremium", method = RequestMethod.POST)
    @ResponseBody
    public Result reCalculatePremium(@RequestBody GHRecalculatedInsuredPremiumCommand glRecalculatedInsuredPremiumCommand, HttpServletRequest request) {
        GHPremiumDetailDto premiumDetailDto = null;
        try {
            glRecalculatedInsuredPremiumCommand.setUserDetails(getLoggedInUserDetail(request));
            premiumDetailDto = ghQuotationService.recalculatePremium(glRecalculatedInsuredPremiumCommand);
            return Result.success("Premium recalculated successfully", premiumDetailDto);
        } catch (Exception e) {
            return Result.success(e.getMessage());
        }
    }

    @RequestMapping(value = "/savepremiumdetail", method = RequestMethod.POST)
    @ResponseBody
    public Result savePremiumDetail(@RequestBody UpdateGHQuotationWithPremiumDetailCommand updateGLQuotationWithPremiumDetailCommand, HttpServletRequest request) {
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
        return ghQuotationService.getProposerDetail(new QuotationId(quotationId));
    }
}
