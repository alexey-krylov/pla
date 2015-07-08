package com.pla.grouplife.proposal.presentation.controller;

import com.google.common.collect.Lists;
import com.pla.grouplife.proposal.application.command.*;
import com.pla.grouplife.proposal.application.service.GLProposalService;
import com.pla.grouplife.sharedresource.dto.AgentDetailDto;
import com.pla.grouplife.sharedresource.dto.InsuredDto;
import com.pla.grouplife.sharedresource.dto.PremiumDetailDto;
import com.pla.grouplife.sharedresource.dto.ProposerDto;
import com.pla.publishedlanguage.contract.IClientProvider;
import com.pla.sharedkernel.identifier.ProposalId;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
@RequestMapping(value = "/grouplife/proposal", consumes = MediaType.ALL_VALUE)
public class GroupLifeProposalController {

    private CommandGateway commandGateway;

    private GLProposalService glProposalService;

    private IClientProvider clientProvider;

    @Autowired
    public GroupLifeProposalController(CommandGateway commandGateway, GLProposalService glProposalService, IClientProvider clientProvider) {
        this.commandGateway = commandGateway;
        this.glProposalService = glProposalService;
        this.clientProvider = clientProvider;
    }
    @RequestMapping(value = "/searchgrouplifeproposal",method = RequestMethod.GET)
    public String searchGroupLifeProposal() {
        return "pla/proposal/groupLife/searchQuotationforGLProposal";
    }

    @RequestMapping(value = "/searchquotation/{quotationNumber}", method = RequestMethod.GET)
    public ModelAndView searchQuotation(String quotationNumber) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/quotation/viewQuotation");
        try {
            modelAndView.addObject("searchResult", glProposalService.searchGeneratedQuotation(quotationNumber));
        } catch (Exception e) {
            modelAndView.addObject("searchResult", Lists.newArrayList());
        }
        modelAndView.addObject("searchCriteria", quotationNumber);
        return modelAndView;
    }

    @RequestMapping(value = "/opengrouplifeproposal", method = RequestMethod.POST)
    @ResponseBody
    public Result createProposal(@RequestBody GLQuotationToProposalCommand gQuotationToProposalCommand, BindingResult bindingResult,  HttpServletRequest request) {
        if (glProposalService.hasProposalForQuotation(gQuotationToProposalCommand.getQuotationId())) {
            return Result.failure("Proposal Already Exists..Do you want to override the same?");
        }
        else {
            UserDetails userDetails = getLoggedInUserDetail(request);
            gQuotationToProposalCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(gQuotationToProposalCommand);
        }
        return Result.success("Proposal successfully created");
    }

/*
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
    }*/

    @RequestMapping(value = "/getagentdetailfromproposal/{proposalId}", method = RequestMethod.GET)
    @ResponseBody
    public AgentDetailDto getAgentDetailFromQuotation(@PathVariable("proposalId") String proposalId) {
        return glProposalService.getAgentDetail(new ProposalId(proposalId));
    }

    @RequestMapping(value = "/updatewithagentid", method = RequestMethod.POST)
    @ResponseBody
    public Result updateQuotationWithAgentId(@RequestBody UpdateGLProposalWithAgentCommand updateGLProposalWithAgentCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update proposal agent data is not valid", bindingResult.getAllErrors());
        }
        try {
            updateGLProposalWithAgentCommand.setUserDetails(getLoggedInUserDetail(request));
            String quotationId = commandGateway.sendAndWait(updateGLProposalWithAgentCommand);
            return Result.success("Agent detail updated successfully", quotationId);
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }
    @RequestMapping(value = "/getproposerdetailfromproposal/{proposalId}")
    @ResponseBody
    public ProposerDto getProposerDetail(@PathVariable("proposalId") String proposalId) {
        return glProposalService.getProposerDetail(new ProposalId(proposalId));
    }
    @RequestMapping(value = "/updatewithproposerdetail", method = RequestMethod.POST)
    @ResponseBody
    public Result updateProposalWithProposerDetail(@RequestBody UpdateGLProposalWithProposerCommand updateGLProposalWithProposerCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update proposal proposer data is not valid", bindingResult.getAllErrors());
        }
        try {
            updateGLProposalWithProposerCommand.setUserDetails(getLoggedInUserDetail(request));
            String quotationId = commandGateway.sendAndWait(updateGLProposalWithProposerCommand);
            return Result.success("Proposer detail updated successfully", quotationId);
        } catch (Exception e) {
            return Result.failure();
        }
    }

    @RequestMapping(value = "/uploadinsureddetail", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadInsuredDetail(@RequestBody UploadInsuredDetailDto uploadInsuredDetailDto, HttpServletRequest request) throws IOException {
            MultipartFile file = uploadInsuredDetailDto.getFile();
        if (!("application/ms-excel".equals(file.getContentType()) || "application/msexcel".equals(file.getContentType()) || "application/vnd.ms-excel".equals(file.getContentType()))) {
            return Result.failure("Uploaded file is not valid excel");
        }
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        HSSFWorkbook insuredTemplateWorkbook = new HSSFWorkbook(fs);
        try {
            boolean isValidInsuredTemplate = glProposalService.isValidInsuredTemplate(uploadInsuredDetailDto.getProposalId(), insuredTemplateWorkbook, uploadInsuredDetailDto.isSamePlanForAllCategory(), uploadInsuredDetailDto.isSamePlanForAllRelation());
            if (!isValidInsuredTemplate) {
                File insuredTemplateWithError = new File(uploadInsuredDetailDto.getProposalId());
                FileOutputStream fileOutputStream = new FileOutputStream(insuredTemplateWithError);
                insuredTemplateWorkbook.write(fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
                return Result.failure("Uploaded Insured template is not valid.Please download to check the errors");
            }
            List<InsuredDto> insuredDtos = glProposalService.transformToInsuredDto(insuredTemplateWorkbook, uploadInsuredDetailDto.getProposalId(), uploadInsuredDetailDto.isSamePlanForAllCategory(), uploadInsuredDetailDto.isSamePlanForAllRelation());
            String quotationId = commandGateway.sendAndWait(new UpdateGLProposalWithInsuredCommand(uploadInsuredDetailDto.getProposalId(), insuredDtos, getLoggedInUserDetail(request)));
            return Result.success("Insured detail uploaded successfully", quotationId);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(e.getMessage());
        }
    }


    @RequestMapping(value = "/downloadinsuredtemplate/{proposalId}", method = RequestMethod.GET)
    public void downloadInsuredTemplate(@PathVariable("proposalId") String proposalId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "insuredTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook planDetailExcel = glProposalService.getInsuredTemplateExcel(proposalId);
        planDetailExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/downloaderrorinsuredtemplate/{proposalId}", method = RequestMethod.GET)
    public void downloadErrorInsuredTemplate(@PathVariable("proposalId") String proposalId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "GLInsuredTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        File errorTemplateFile = new File(proposalId);
        InputStream inputStream = new FileInputStream(errorTemplateFile);
        outputStream.write(IOUtils.toByteArray(inputStream));
        outputStream.flush();
        outputStream.close();
        errorTemplateFile.delete();
    }

    @RequestMapping(value = "/downloadplandetail/{proposalId}", method = RequestMethod.GET)
    public void downloadPlanDetail(@PathVariable("proposalId") String proposalId, HttpServletResponse response) throws IOException, JRException {
        response.reset();
        response.setContentType("application/pdf");
        response.setHeader("content-disposition", "attachment; filename=" + "planReadyReckoner.pdf" + "");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(glProposalService.getPlanReadyReckoner(proposalId));
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/getpremiumdetail/{proposalid}", method = RequestMethod.GET)
    @ResponseBody
    public PremiumDetailDto getPremiumDetail(@PathVariable("proposalid") String proposalId) {
        return glProposalService.getPremiumDetail(new ProposalId(proposalId));
    }

    @RequestMapping(value = "/recalculatePremium", method = RequestMethod.POST)
    @ResponseBody
    public Result reCalculatePremium(@RequestBody GLRecalculatedInsuredPremiumCommand glRecalculatedInsuredPremiumCommand, HttpServletRequest request) {
        PremiumDetailDto premiumDetailDto = null;
        try {
            glRecalculatedInsuredPremiumCommand.setUserDetails(getLoggedInUserDetail(request));
            premiumDetailDto = glProposalService.recalculatePremium(glRecalculatedInsuredPremiumCommand);
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
/*
    @RequestMapping(value = "/listgrouplifeproposal", method = RequestMethod.GET)
    public ModelAndView listQuotation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/proposal/viewProposal");
        modelAndView.addObject("searchCriteria", new SearchGLProposalDto());
        return modelAndView;
    }

    @RequestMapping(value = "/searchproposal", method = RequestMethod.POST)
    public ModelAndView searchQuotation(SearchGLProposalDto searchGLProposalDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/proposal/viewProposal");
        try {
            modelAndView.addObject("searchResult", glProposalService.searchProposal(searchGLProposalDto));
        } catch (Exception e) {
            modelAndView.addObject("searchResult", Lists.newArrayList());
        }
        modelAndView.addObject("searchCriteria", searchGLProposalDto);
        return modelAndView;
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
    }*/


}
