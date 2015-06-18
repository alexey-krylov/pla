package com.pla.grouplife.proposal.presentation.controller;

import com.google.common.collect.Lists;
import com.pla.grouplife.proposal.application.command.*;
import com.pla.grouplife.proposal.presentation.dto.GLProposalMailDto;
import com.pla.grouplife.quotation.application.command.GLRecalculatedInsuredPremiumCommand;
import com.pla.grouplife.quotation.application.command.UploadInsuredDetailDto;
import com.pla.grouplife.quotation.application.service.GLQuotationService;
import com.pla.grouplife.quotation.application.command.SearchGlQuotationDto;
import com.pla.grouplife.quotation.query.*;
import com.pla.sharedkernel.service.EmailAttachment;
import com.pla.sharedkernel.service.MailService;
import com.wordnik.swagger.annotations.ApiOperation;
import net.sf.jasperreports.engine.JRException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.axonframework.commandhandling.gateway.CommandGateway;
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
@RequestMapping(value = "/proposal/grouplife")
public class ProposalSetUpController {

    private CommandGateway commandGateway;
    private MailService mailService;

    private GLQuotationFinder glQuotationFinder;

    private GLQuotationService glQuotationService;

    @Autowired
    public ProposalSetUpController(CommandGateway commandGateway, MailService mailService, GLQuotationFinder glQuotationFinder, GLQuotationService glQuotationService) {
        this.commandGateway = commandGateway;
        this.mailService = mailService;
        this.glQuotationFinder = glQuotationFinder;
        this.glQuotationService = glQuotationService;
    }


    @RequestMapping(value = "/searchGLQuotationforProposal", method = RequestMethod.GET)
    public String searchGroupLifeQuotation() {
        //System.out.println("");
        return "pla/proposal/groupLife/searchQuotationforGLProposal";
    }

    @RequestMapping(value = "/creategrouplifeproposal", method = RequestMethod.GET)
    public ModelAndView createProposalPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/proposal/groupLife/createProposal");
        return modelAndView;
    }

    @RequestMapping(value = "/getversionnumber/{quotationId}", method = RequestMethod.GET)
    @ResponseBody
    public Result getVersionNumber(@PathVariable("quotationId") String quotationId) {
        Map quotationMap = glQuotationFinder.getQuotationById(quotationId);
        return Result.success("Proposal Version number ",quotationMap.get("versionNumber"));
    }

    @RequestMapping(value = "/searchQuotationforGLProposal", method = RequestMethod.POST)
    public ModelAndView searchQuotation(SearchGlQuotationDto searchGlQuotationDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/proposal/groupLife/viewProposal");
        try {
            modelAndView.addObject("searchResult",   glQuotationService.searchQuotation(searchGlQuotationDto));
        } catch (Exception e) {
            modelAndView.addObject("searchResult", Lists.newArrayList());
        }
        modelAndView.addObject("searchCriteria", searchGlQuotationDto);
        return modelAndView;
    }


    @RequestMapping(value = "/getquotationnumber/{quotationId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Get Proposal number for a given quotation Id")
    @ResponseBody
    public Result getProposalNumber(@PathVariable("quotationId") String quotationId) {
        return Result.success("Proposal number ");
    }

    @RequestMapping(value = "/isinsureddetailavailable/{quotationId}", method = RequestMethod.GET)
    @ResponseBody
    public boolean isInsuredDetailAvailable(@PathVariable("quotationId") String quotationId) {
        return false;
    }


    @RequestMapping(value = "/getagentdetail/{agentId}", method = RequestMethod.GET)
    @ResponseBody
    public Result getAgentDetail(@PathVariable("agentId") String agentId) {

        Map<String, Object> agentDetail = null;
        if (isEmpty(agentDetail)) {
            return Result.failure("Agent detail not found");
        }
        checkArgument(agentDetail != null);
        CreateGLProposalCommand createGLProposalCommand = new CreateGLProposalCommand();
        createGLProposalCommand.setAgentId(agentId);
        createGLProposalCommand.setBranchName(agentDetail.get("branchName") != null ? (String) agentDetail.get("branchName") : "");
        createGLProposalCommand.setTeamName(agentDetail.get("teamName") != null ? (String) agentDetail.get("teamName") : "");
        createGLProposalCommand.setAgentName(agentDetail.get("firstName") != null ? (String) agentDetail.get("firstName") : "" + " " + agentDetail.get("lastName") != null ? (String) agentDetail.get("lastName") : "");
        return Result.success("Agent found", createGLProposalCommand);
    }

    @RequestMapping(value = "/getagentdetailfromquotation/{quotationId}", method = RequestMethod.GET)
    @ResponseBody
    public AgentDetailDto getAgentDetailFromProposal(@PathVariable("quotationId") String quotationId) {
        return null;
    }

    @RequestMapping(value = "/creategrouplifeproposal", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "Create Group Life Proposal")
    @ResponseBody
    public Result createProposal(@RequestBody CreateGLProposalCommand createGLProposalCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Create quotation data is not valid", bindingResult.getAllErrors());
        }
        try {
            createGLProposalCommand.setUserDetails(getLoggedInUserDetail(request));
            String quotationId = commandGateway.sendAndWait(createGLProposalCommand);
            return Result.success("Proposal created successfully", quotationId);
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/updatewithagentdetail", method = RequestMethod.POST)
    @ResponseBody
    public Result updateProposalWithAgentDetail(@RequestBody UpdateGLProposalWithAgentCommand updateGLProposalWithAgentCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update quotation agent data is not valid", bindingResult.getAllErrors());
        }
        try {
            updateGLProposalWithAgentCommand.setUserDetails(getLoggedInUserDetail(request));
            String quotationId = commandGateway.sendAndWait(updateGLProposalWithAgentCommand);
            return Result.success("Agent detail updated successfully", quotationId);
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/updatewithproposerdetail", method = RequestMethod.POST)
    @ResponseBody
    public Result updateProposalWithProposerDetail(@RequestBody UpdateGLProposalWithProposerCommand updateGLProposalWithProposerCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update quotation proposer data is not valid", bindingResult.getAllErrors());
        }
        try {
            updateGLProposalWithProposerCommand.setUserDetails(getLoggedInUserDetail(request));
            String quotationId = commandGateway.sendAndWait(updateGLProposalWithProposerCommand);
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
        outputStream.write(null);
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/openemailquotation/{quotationId}", method = RequestMethod.GET)
    public ModelAndView openEmailPage(@PathVariable("quotationId") String quotationId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/proposal/groupLife/emailProposal");
        modelAndView.addObject("mailContent", null);
        return modelAndView;
    }

    @RequestMapping(value = "/emailProposal", method = RequestMethod.POST)
    @ResponseBody
    public Result emailProposal(@RequestBody GLProposalMailDto mailDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Email cannot be sent due to wrong data");
        }
        try {
            byte[] quotationData = null;
            String fileName = "ProposalNo-" + mailDto.getProposalNumber() + ".pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(quotationData);
            fileOutputStream.flush();
            fileOutputStream.close();
            EmailAttachment emailAttachment = new EmailAttachment(fileName, "application/pdf", file);
            mailService.sendMailWithAttachment(mailDto.getSubject(), mailDto.getMailContent(), Arrays.asList(emailAttachment), mailDto.getRecipientMailAddress());
            file.delete();
            return Result.success("Email sent successfully");

        } catch (Exception e) {
            Result.failure(e.getMessage());
        }
        return Result.success("Email sent successfully");
    }


    @RequestMapping(value = "/printquotation/{quotationId}", method = RequestMethod.GET)
    public void printProposal(@PathVariable("quotationId") String quotationId, HttpServletResponse response) throws IOException, JRException {
        response.reset();
        response.setContentType("application/pdf");
        response.setHeader("content-disposition", "attachment; filename=" + "quotation.pdf" + "");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(null);
        outputStream.flush();
        outputStream.close();
    }


    @RequestMapping(value = "/downloadinsuredtemplate/{quotationId}", method = RequestMethod.GET)
    public void downloadInsuredTemplate(@PathVariable("quotationId") String quotationId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "insuredTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook planDetailExcel = null;
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
        if (!("application/x-ms-excel".equals(file.getContentType()) || "application/ms-excel".equals(file.getContentType()) || "application/msexcel".equals(file.getContentType()) || "application/vnd.ms-excel".equals(file.getContentType()))) {
            return Result.failure("Uploaded file is not valid excel");
        }
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        HSSFWorkbook insuredTemplateWorkbook = new HSSFWorkbook(fs);
        try {
            boolean isValidInsuredTemplate = false;
            if (!isValidInsuredTemplate) {
                File insuredTemplateWithError = new File(uploadInsuredDetailDto.getQuotationId());
                FileOutputStream fileOutputStream = new FileOutputStream(insuredTemplateWithError);
                insuredTemplateWorkbook.write(fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return Result.failure("Uploaded Insured template is not valid.Please download to check the errors");
            }
            List<InsuredDto> insuredDtos = null;
            commandGateway.sendAndWait(new UpdateGLProposalWithInsuredCommand(uploadInsuredDetailDto.getQuotationId(), insuredDtos, getLoggedInUserDetail(request)));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(e.getMessage());
        }
        return Result.success("Insured detail uploaded successfully", uploadInsuredDetailDto.getQuotationId());
    }

    @RequestMapping(value = "/getpremiumdetail/{quotationid}", method = RequestMethod.GET)
    @ResponseBody
    public PremiumDetailDto getPremiumDetail(@PathVariable("quotationid") String quotationId) {
        return null;
    }

    @RequestMapping(value = "/recalculatePremium", method = RequestMethod.POST)
    @ResponseBody
    public Result reCalculatePremium(@RequestBody GLRecalculatedInsuredPremiumCommand glRecalculatedInsuredPremiumCommand, HttpServletRequest request) {
        PremiumDetailDto premiumDetailDto = null;
        try {
            glRecalculatedInsuredPremiumCommand.setUserDetails(getLoggedInUserDetail(request));
            premiumDetailDto = null;
            return Result.success("Premium recalculated successfully", premiumDetailDto);
        } catch (Exception e) {
            return Result.success(e.getMessage());
        }
    }

    @RequestMapping(value = "/savepremiumdetail", method = RequestMethod.POST)
    @ResponseBody
    public Result savePremiumDetail(@RequestBody UpdateGLProposalWithPremiumDetailCommand updateGLProposalWithPremiumDetailCommand, HttpServletRequest request) {
        try {
            updateGLProposalWithPremiumDetailCommand.setUserDetails(getLoggedInUserDetail(request));
            commandGateway.sendAndWait(updateGLProposalWithPremiumDetailCommand);
            return Result.success("Premium detail saved successfully");
        } catch (Exception e) {
            return Result.success(e.getMessage());
        }
    }

    @RequestMapping(value = "/generate", method = RequestMethod.POST)
    @ResponseBody
    public Result generateProposal(@RequestBody GenerateGLProposalCommand generateGLProposalCommand) {
        try {
            commandGateway.sendAndWait(generateGLProposalCommand);
            return Result.success("Proposal generated successfully");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/getproposerdetail/{quotationId}")
    @ResponseBody
    public ProposerDto getProposerDetail(@PathVariable("quotationId") String quotationId) {
        return null;
    }

    @RequestMapping(value = "/creategrouplifequotation", method = RequestMethod.GET)
    public String assignRegionalManager() {
        // System.out.println("");
        return "pla/proposal/groupLife/createProposal";
    }

    @RequestMapping(value = "/searchgrouplifequotation", method = RequestMethod.GET)
    public String searchGroupLifeProposal() {
        //System.out.println("");
        return "pla/proposal/groupLife/searchGLProposal";
    }
}
