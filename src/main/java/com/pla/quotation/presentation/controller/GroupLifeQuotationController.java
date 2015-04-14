package com.pla.quotation.presentation.controller;

import com.pla.quotation.application.service.grouplife.GLQuotationService;
import com.pla.quotation.presentation.command.grouplife.*;
import com.pla.quotation.query.AgentDetailDto;
import com.pla.quotation.query.PremiumDetailDto;
import com.pla.quotation.query.ProposerDto;
import com.pla.sharedkernel.identifier.QuotationId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Samir on 4/14/2015.
 */
@Controller
@RequestMapping(value = "/quotation/grouplife")
public class GroupLifeQuotationController {

    private CommandGateway commandGateway;

    private GLQuotationService GLQuotationService;

    @Autowired
    public GroupLifeQuotationController(CommandGateway commandGateway, GLQuotationService GLQuotationService) {
        this.commandGateway = commandGateway;
        this.GLQuotationService = GLQuotationService;
    }

    @RequestMapping(value = "/creategrouplifequotation", method = RequestMethod.GET)
    public ModelAndView createQuotationPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/quotation/groupLife/createQuotation");
        return modelAndView;
    }

    @RequestMapping(value = "/getagentdetail/{agentId}", method = RequestMethod.GET)
    @ResponseBody
    public CreateGLCommand getAgentDetail(@PathVariable("agentId") String agentId) {
        return new CreateGLCommand();
    }

    @RequestMapping(value = "/getagentdetailfromquotation/{quotationId}", method = RequestMethod.GET)
    @ResponseBody
    public AgentDetailDto getAgentDetailFromQuotation(@PathVariable("quotationId") String quotationId) {
        return GLQuotationService.getAgentDetail(new QuotationId(quotationId));
    }

    @RequestMapping(value = "/listgrouplifequotation", method = RequestMethod.GET)
    public ModelAndView listQuotation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/quotation/groupLife/listQuotation");
        modelAndView.addObject(GLQuotationService.getAllQuotation());
        return modelAndView;
    }

    @RequestMapping(value = "/createquotation", method = RequestMethod.POST)
    @ResponseBody
    public Result createQuotation(@RequestBody CreateGLCommand createGLCommand, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Create quotation data is not valid", bindingResult.getAllErrors());
        }
        try {
            String quotationId = commandGateway.sendAndWait(createGLCommand);
            return Result.success("Quotation created successfully", quotationId);
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/updatewithagentdetail", method = RequestMethod.POST)
    @ResponseBody
    public Result updateQuotationWithAgentDetail(@RequestBody UpdateGLQuotationWithAgentCommand updateGLQuotationWithAgentCommand, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update quotation agent data is not valid", bindingResult.getAllErrors());
        }
        try {
            commandGateway.sendAndWait(updateGLQuotationWithAgentCommand);
            return Result.success("Agent detail updated successfully");
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/updatewithproposerdetail", method = RequestMethod.POST)
    @ResponseBody
    public Result updateQuotationWithProposerDetail(@RequestBody UpdateGLQuotationWithProposerCommand updateGLQuotationWithProposerCommand, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update quotation proposer data is not valid", bindingResult.getAllErrors());
        }
        try {
            commandGateway.sendAndWait(updateGLQuotationWithProposerCommand);
            return Result.success("Proposer detail updated successfully");
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
        HSSFWorkbook planDetailExcel = GLQuotationService.getPlanDetailExcel();
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
        HSSFWorkbook planDetailExcel = GLQuotationService.getInsuredTemplateExcel();
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
        return GLQuotationService.getPremiumDetail(new QuotationId(quotationId));
    }

    @RequestMapping(value = "/recalculatePremium", method = RequestMethod.POST)
    @ResponseBody
    public PremiumDetailDto reCalculatePremium(@RequestBody PremiumDetailDto premiumDetailDto) {
        return GLQuotationService.getReCalculatePremium(premiumDetailDto);
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
        return GLQuotationService.getProposerDetail(new QuotationId(quotationId));
    }
}
