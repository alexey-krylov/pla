package com.pla.grouphealth.presentation.controller;

import com.pla.grouphealth.application.command.quotation.*;
import com.pla.grouphealth.application.service.quotation.GHQuotationService;
import com.pla.grouphealth.query.AgentDetailDto;
import com.pla.grouphealth.query.GHQuotationFinder;
import com.pla.grouphealth.query.PremiumDetailDto;
import com.pla.grouphealth.query.ProposerDto;
import com.pla.sharedkernel.identifier.QuotationId;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.presentation.AppUtils.getLoggedInUSerDetail;

/**
 * Created by Karunakar on 4/30/2015.
 */
@Controller
@RequestMapping(value = "/quotation/grouphealth")
public class GroupHealthQuotationController {

    private CommandGateway commandGateway;

    private GHQuotationService ghQuotationService;

    private GHQuotationFinder ghQuotationFinder;

    @Autowired
    public GroupHealthQuotationController(CommandGateway commandGateway, GHQuotationService ghQuotationService, GHQuotationFinder ghQuotationFinder) {
        this.commandGateway = commandGateway;
        this.ghQuotationService = ghQuotationService;
        this.ghQuotationFinder = ghQuotationFinder;
    }

    @RequestMapping(value = "/creategrouphealthquotation", method = RequestMethod.GET)
    public ModelAndView createQuotationPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/quotation/groupHealth/createQuotation");
        return modelAndView;
    }

    @RequestMapping(value = "/getquotationnumber/{quotationId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Get Quotation number for a given quotation Id")
    @ResponseBody
    public Result getQuotationNumber(@PathVariable("quotationId") String quotationId) {
        Map quotationMap = ghQuotationFinder.getQuotationById(quotationId);
        return Result.success("Quotation number ", (String) quotationMap.get("quotationNumber"));
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
        Map<String, Object> agentDetail = null;
        try {
            agentDetail = ghQuotationFinder.getAgentById(agentId);
        } catch (Exception e) {
            return Result.failure("Agent not found");
        }
        checkArgument(agentDetail != null);
        CreateGHQuotationCommand createGHQuotationCommand = new CreateGHQuotationCommand();
        createGHQuotationCommand.setAgentId(agentId);
        createGHQuotationCommand.setBranchName((String) agentDetail.get("branchName"));
        createGHQuotationCommand.setTeamName((String) agentDetail.get("teamName"));
        createGHQuotationCommand.setAgentName((String) agentDetail.get("firstName") + " " + (String) agentDetail.get("lastName"));
        return Result.success("Agent found", createGHQuotationCommand);
    }

    @RequestMapping(value = "/getagentdetailfromquotation/{quotationId}", method = RequestMethod.GET)
    @ResponseBody
    public AgentDetailDto getAgentDetailFromQuotation(@PathVariable("quotationId") String quotationId) {
        return ghQuotationService.getAgentDetail(new QuotationId(quotationId));
    }

    @RequestMapping(value = "/listgrouphealthquotation", method = RequestMethod.GET)
    public ModelAndView listQuotation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/quotation/groupHealth/viewQuotation");
        modelAndView.addObject("searchCriteria", new SearchGHQuotationDto());
        return modelAndView;
    }

    @RequestMapping(value = "/searchquotation", method = RequestMethod.POST)
    public ModelAndView searchQuotation(SearchGHQuotationDto searchGHQuotationDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/quotation/groupHealth/viewQuotation");
        modelAndView.addObject("searchResult", ghQuotationService.searchQuotation(searchGHQuotationDto));
        modelAndView.addObject("searchCriteria", searchGHQuotationDto);
        return modelAndView;
    }

    @RequestMapping(value = "/createquotation", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "Create Group Health Quotation")
    @ResponseBody
    public Result createQuotation(@RequestBody CreateGHQuotationCommand createGHQuotationCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Create quotation data is not valid", bindingResult.getAllErrors());
        }
        try {
            createGHQuotationCommand.setUserDetails(getLoggedInUSerDetail(request));
            String quotationId = commandGateway.sendAndWait(createGHQuotationCommand);
            return Result.success("Quotation created successfully", quotationId);
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/updatewithagentdetail", method = RequestMethod.POST)
    @ResponseBody
    public Result updateQuotationWithAgentDetail(@RequestBody UpdateGHQuotationWithAgentCommand updateGHQuotationWithAgentCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update quotation agent data is not valid", bindingResult.getAllErrors());
        }
        try {
            updateGHQuotationWithAgentCommand.setUserDetails(getLoggedInUSerDetail(request));
            String quotationId = commandGateway.sendAndWait(updateGHQuotationWithAgentCommand);
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
            updateGHQuotationWithProposerCommand.setUserDetails(getLoggedInUSerDetail(request));
            String quotationId = commandGateway.sendAndWait(updateGHQuotationWithProposerCommand);
            return Result.success("Proposer detail updated successfully", quotationId);
        } catch (Exception e) {
            return Result.failure();
        }
    }

    @RequestMapping(value = "/downloadplandetail", method = RequestMethod.GET)
    public void downloadPlanDetail(HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "plandetail.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook planDetailExcel = ghQuotationService.getPlanDetailExcel();
        planDetailExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/downloadinsuredtemplate", method = RequestMethod.GET)
    public void downloadInsuredTemplate(HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "insuredTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook planDetailExcel = ghQuotationService.getInsuredTemplateExcel();
        planDetailExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/uploadinsureddetail", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadInsuredDetail(UploadInsuredDetailDto uploadInsuredDetailDto) {
        MultipartFile file = uploadInsuredDetailDto.getFile();
        if (!("application/msexcel".equals(file.getContentType()) || "application/vnd.ms-excel".equals(file.getContentType()))) {
            return Result.failure("Uploaded file is not valid excel");
        }
        return Result.success("Insured detail uploaded successfully");
    }

    @RequestMapping(value = "/getpremiumdetail/{quotationid}", method = RequestMethod.GET)
    @ResponseBody
    public PremiumDetailDto getPremiumDetail(@PathVariable("quotationid") String quotationId) {
        return ghQuotationService.getPremiumDetail(new QuotationId(quotationId));
    }

    @RequestMapping(value = "/recalculatePremium", method = RequestMethod.POST)
    @ResponseBody
    public PremiumDetailDto reCalculatePremium(@RequestBody PremiumDetailDto premiumDetailDto) {
        return ghQuotationService.getReCalculatePremium(premiumDetailDto);
    }

    @RequestMapping(value = "/updatewithpremiumdetail", method = RequestMethod.POST)
    @ResponseBody
    public Result updatePremiumDetail(@RequestBody UpdateGHQuotationWithPremiumDetailCommand updateGHQuotationWithPremiumDetailCommand) {
        try {
            commandGateway.sendAndWait(updateGHQuotationWithPremiumDetailCommand);
            return Result.success("Premium detail updated successfully");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/generate", method = RequestMethod.POST)
    @ResponseBody
    public Result generateQuotation(@RequestBody GenerateGHQuotationCommand generateGHQuotationCommand) {
        try {
            commandGateway.sendAndWait(generateGHQuotationCommand);
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
