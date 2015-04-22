package com.pla.quotation.presentation.controller;

import com.pla.quotation.application.command.grouplife.*;
import com.pla.quotation.application.service.grouplife.GLQuotationService;
import com.pla.quotation.query.AgentDetailDto;
import com.pla.quotation.query.GLQuotationFinder;
import com.pla.quotation.query.PremiumDetailDto;
import com.pla.quotation.query.ProposerDto;
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
 * Created by Samir on 4/14/2015.
 */
@Controller
@RequestMapping(value = "/quotation/grouplife")
public class GroupLifeQuotationController {

    private CommandGateway commandGateway;

    private GLQuotationService glQuotationService;

    private GLQuotationFinder glQuotationFinder;

    @Autowired
    public GroupLifeQuotationController(CommandGateway commandGateway, GLQuotationService glQuotationService, GLQuotationFinder glQuotationFinder) {
        this.commandGateway = commandGateway;
        this.glQuotationService = glQuotationService;
        this.glQuotationFinder = glQuotationFinder;
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
        return Result.success("Quotation number ", (String) quotationMap.get("quotationNumber"));
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
        Map<String, Object> agentDetail = null;
        try {
            agentDetail = glQuotationFinder.getAgentById(agentId);
        } catch (Exception e) {
            return Result.failure("Agent not found");
        }
        checkArgument(agentDetail != null);
        CreateGLQuotationCommand createGLQuotationCommand = new CreateGLQuotationCommand();
        createGLQuotationCommand.setAgentId(agentId);
        createGLQuotationCommand.setBranchName((String) agentDetail.get("branchName"));
        createGLQuotationCommand.setTeamName((String) agentDetail.get("teamName"));
        createGLQuotationCommand.setAgentName((String) agentDetail.get("firstName") + " " + (String) agentDetail.get("lastName"));
        return Result.success("Agent found", createGLQuotationCommand);
    }

    @RequestMapping(value = "/getagentdetailfromquotation/{quotationId}", method = RequestMethod.GET)
    @ResponseBody
    public AgentDetailDto getAgentDetailFromQuotation(@PathVariable("quotationId") String quotationId) {
        return glQuotationService.getAgentDetail(new QuotationId(quotationId));
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
        modelAndView.addObject("searchResult", glQuotationService.searchQuotation(searchGlQuotationDto));
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
            createGLQuotationCommand.setUserDetails(getLoggedInUSerDetail(request));
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
            updateGLQuotationWithAgentCommand.setUserDetails(getLoggedInUSerDetail(request));
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
            updateGLQuotationWithProposerCommand.setUserDetails(getLoggedInUSerDetail(request));
            String quotationId = commandGateway.sendAndWait(updateGLQuotationWithProposerCommand);
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
        HSSFWorkbook planDetailExcel = glQuotationService.getPlanDetailExcel();
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
        HSSFWorkbook planDetailExcel = glQuotationService.getInsuredTemplateExcel();
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
        return glQuotationService.getPremiumDetail(new QuotationId(quotationId));
    }

    @RequestMapping(value = "/recalculatePremium", method = RequestMethod.POST)
    @ResponseBody
    public PremiumDetailDto reCalculatePremium(@RequestBody PremiumDetailDto premiumDetailDto) {
        return glQuotationService.getReCalculatePremium(premiumDetailDto);
    }

    @RequestMapping(value = "/updatewithpremiumdetail", method = RequestMethod.POST)
    @ResponseBody
    public Result updatePremiumDetail(@RequestBody UpdateGLQuotationWithPremiumDetailCommand updateGLQuotationWithPremiumDetailCommand) {
        try {
            commandGateway.sendAndWait(updateGLQuotationWithPremiumDetailCommand);
            return Result.success("Premium detail updated successfully");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
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
