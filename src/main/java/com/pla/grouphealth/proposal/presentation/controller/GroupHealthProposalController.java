package com.pla.grouphealth.proposal.presentation.controller;

import com.google.common.collect.Lists;
import com.pla.grouphealth.proposal.application.service.GHProposalService;
import com.pla.grouphealth.proposal.presentation.dto.SearchGHProposalDto;
import com.pla.grouphealth.quotation.application.command.*;
import com.pla.grouphealth.sharedresource.dto.AgentDetailDto;
import com.pla.grouphealth.sharedresource.dto.GHInsuredDto;
import com.pla.grouphealth.sharedresource.dto.GHPremiumDetailDto;
import com.pla.grouphealth.sharedresource.dto.ProposerDto;
import com.pla.publishedlanguage.contract.IClientProvider;
import com.pla.publishedlanguage.dto.ClientDetailDto;
import com.pla.sharedkernel.identifier.ProposalId;
import com.pla.sharedkernel.identifier.QuotationId;
import net.sf.jasperreports.engine.JRException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUserDetail;

/**
 * Created by Samir on 6/24/2015.
 */
@Controller
@RequestMapping(value = "/grouphealth/proposal")
public class GroupHealthProposalController {

    private CommandGateway commandGateway;

    private GHProposalService ghProposalService;

    private IClientProvider clientProvider;

    @Autowired
    public GroupHealthProposalController(CommandGateway commandGateway, GHProposalService ghProposalService, IClientProvider clientProvider) {
        this.commandGateway = commandGateway;
        this.ghProposalService = ghProposalService;
        this.clientProvider = clientProvider;
    }

    @RequestMapping(value = "/opengrouphealthproposal/{quotationId}", method = RequestMethod.GET)
    public ModelAndView createQuotationPage(@PathVariable("quotationId") String quotationId, BindingResult bindingResult) {
        ModelAndView modelAndView = new ModelAndView();
        if (ghProposalService.hasProposalForQuotation(quotationId)) {
            bindingResult.addError(new ObjectError("message", "Proposal already exists for the selected quotation"));
            modelAndView.setViewName("pla/grouphealth/proposal/searchQuotation");
            return modelAndView;
        }
        modelAndView.setViewName("pla/grouphealth/proposal/createProposal");
        return modelAndView;
    }

    @RequestMapping(value = "/searchquotation/{quotationNumber}", method = RequestMethod.POST)
    public ModelAndView searchQuotation(String quotationNumber) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/quotation/viewQuotation");
        try {
            modelAndView.addObject("searchResult", ghProposalService.searchGeneratedQuotation(quotationNumber));
        } catch (Exception e) {
            modelAndView.addObject("searchResult", Lists.newArrayList());
        }
        modelAndView.addObject("searchCriteria", quotationNumber);
        return modelAndView;
    }

    @RequestMapping(value = "/getproposerdetailfromclient/{proposerCode}/{proposalId}", method = RequestMethod.GET)
    @ResponseBody
    public ProposerDto getProposerDetailFromClientRepository(@PathVariable("proposerCode") String proposerCode, @PathVariable("proposalId") String proposalId) {
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
            proposerDto = getProposerDetail(proposalId);
            proposerDto.setProposerCode(proposerCode);
        }
        return proposerDto;
    }

    @RequestMapping(value = "/getagentdetailfromproposal/{proposalId}", method = RequestMethod.GET)
    @ResponseBody
    public AgentDetailDto getAgentDetailFromQuotation(@PathVariable("proposalId") String proposalId) {
        return ghProposalService.getAgentDetail(new ProposalId(proposalId));
    }

    @RequestMapping(value = "/listgrouphealthproposal", method = RequestMethod.GET)
    public ModelAndView listQuotation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/proposal/viewProposal");
        modelAndView.addObject("searchCriteria", new SearchGHProposalDto());
        return modelAndView;
    }

    @RequestMapping(value = "/searchproposal", method = RequestMethod.POST)
    public ModelAndView searchQuotation(SearchGHProposalDto searchGHProposalDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/proposal/viewProposal");
        try {
            modelAndView.addObject("searchResult", ghProposalService.searchProposal(searchGHProposalDto));
        } catch (Exception e) {
            modelAndView.addObject("searchResult", Lists.newArrayList());
        }
        modelAndView.addObject("searchCriteria", searchGHProposalDto);
        return modelAndView;
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
        outputStream.write(ghProposalService.getPlanReadyReckoner(quotationId));
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/downloadinsuredtemplate/{proposalId}", method = RequestMethod.GET)
    public void downloadInsuredTemplate(@PathVariable("proposalId") String proposalId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "GHInsuredTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook planDetailExcel = ghProposalService.getInsuredTemplateExcel(proposalId);
        planDetailExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/downloaderrorinsuredtemplate/{proposalId}", method = RequestMethod.GET)
    public void downloadErrorInsuredTemplate(@PathVariable("proposalId") String proposalId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "GHInsuredTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        File errorTemplateFile = new File(proposalId);
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
        if (!("application/ms-excel".equals(file.getContentType()) || "application/msexcel".equals(file.getContentType()) || "application/vnd.ms-excel".equals(file.getContentType()))) {
            return Result.failure("Uploaded file is not valid excel");
        }
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        HSSFWorkbook insuredTemplateWorkbook = new HSSFWorkbook(fs);
        try {
            boolean isValidInsuredTemplate = ghProposalService.isValidInsuredTemplate(uploadInsuredDetailDto.getQuotationId(), insuredTemplateWorkbook, uploadInsuredDetailDto.isSamePlanForAllCategory(), uploadInsuredDetailDto.isSamePlanForAllRelation());
            if (!isValidInsuredTemplate) {
                File insuredTemplateWithError = new File(uploadInsuredDetailDto.getQuotationId());
                FileOutputStream fileOutputStream = new FileOutputStream(insuredTemplateWithError);
                insuredTemplateWorkbook.write(fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return Result.failure("Uploaded Insured template is not valid.Please download to check the errors");
            }
            List<GHInsuredDto> insuredDtos = ghProposalService.transformToInsuredDto(insuredTemplateWorkbook, uploadInsuredDetailDto.getQuotationId(), uploadInsuredDetailDto.isSamePlanForAllCategory(), uploadInsuredDetailDto.isSamePlanForAllRelation());
            String quotationId = commandGateway.sendAndWait(new UpdateGLQuotationWithInsuredCommand(uploadInsuredDetailDto.getQuotationId(), insuredDtos, getLoggedInUserDetail(request)));
            return Result.success("Insured detail uploaded successfully", quotationId);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/getpremiumdetail/{quotationid}", method = RequestMethod.GET)
    @ResponseBody
    public GHPremiumDetailDto getPremiumDetail(@PathVariable("quotationid") String quotationId) {
        return ghProposalService.getPremiumDetail(new QuotationId(quotationId));
    }

    @RequestMapping(value = "/recalculatePremium", method = RequestMethod.POST)
    @ResponseBody
    public Result reCalculatePremium(@RequestBody GHRecalculatedInsuredPremiumCommand glRecalculatedInsuredPremiumCommand, HttpServletRequest request) {
        GHPremiumDetailDto premiumDetailDto = null;
        try {
            glRecalculatedInsuredPremiumCommand.setUserDetails(getLoggedInUserDetail(request));
            premiumDetailDto = ghProposalService.recalculatePremium(glRecalculatedInsuredPremiumCommand);
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
    public ProposerDto getProposerDetail(@PathVariable("proposalId") String proposalId) {
        return ghProposalService.getProposerDetail(new ProposalId(proposalId));
    }
}
