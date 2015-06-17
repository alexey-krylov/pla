package com.pla.individuallife.quotation.presentation.controller;

import com.pla.core.query.AgentFinder;
import com.pla.core.query.PlanFinder;
import com.pla.individuallife.quotation.application.command.*;
import com.pla.individuallife.quotation.application.service.ILQuotationAppService;
import com.pla.individuallife.quotation.presentation.dto.ILQuotationMailDto;
import com.pla.individuallife.quotation.presentation.dto.ILSearchQuotationDto;
import com.pla.individuallife.quotation.presentation.dto.RiderDetailDto;
import com.pla.individuallife.quotation.query.ILQuotationDto;
import com.pla.individuallife.quotation.query.ILQuotationFinder;
import com.pla.individuallife.quotation.query.PremiumDetailDto;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.sharedkernel.identifier.QuotationId;
import com.pla.sharedkernel.service.EmailAttachment;
import com.pla.sharedkernel.service.MailService;
import com.wordnik.swagger.annotations.ApiOperation;
import net.sf.jasperreports.engine.JRException;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUserDetail;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Controller
@RequestMapping(value = "/individuallife/quotation")
public class ILQuotationController {

    @Autowired
    private CommandGateway commandGateway;
    @Autowired
    private ILQuotationAppService ilQuotationService;
    @Autowired
    private ILQuotationFinder ilQuotationFinder;
    @Autowired
    private AgentFinder agentFinder;
    @Autowired
    private PlanFinder planFinder;
    @Autowired
    private MailService mailService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("searchCriteria", new ILSearchQuotationDto());
        modelAndView.setViewName("pla/quotation/individuallife/index");
        return modelAndView;
    }

    @RequestMapping(value = "/searchForm", method = RequestMethod.GET)
    public ModelAndView gotoSearchForm(ILSearchQuotationDto searchDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("searchCriteria", new ILSearchQuotationDto());
        modelAndView.setViewName("pla/quotation/individuallife/index");
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.POST)
    public ModelAndView searchQuotation(ILSearchQuotationDto searchGlQuotationDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("searchResult", ilQuotationService.searchQuotation(searchGlQuotationDto));
        modelAndView.addObject("searchCriteria", searchGlQuotationDto);
        modelAndView.setViewName("pla/quotation/individuallife/index");
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/new")
    public ModelAndView newQuotation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/quotation/individuallife/createQuotation");
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/edit")
    public ModelAndView editQuotation() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/quotation/individuallife/createQuotation");
        return modelAndView;
    }


    @RequestMapping(value = "/getquotationnumber/{quotationId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Get Quotation number for a given quotation Id")
    @ResponseBody
    public Result getQuotationNumber(@PathVariable("quotationId") String quotationId) {
        ILQuotationDto quotationMap = ilQuotationFinder.getQuotationById(quotationId);
        return Result.success("Quotation number ", quotationMap.getQuotationNumber());
    }

    @RequestMapping(value = "/getversionnumber/{quotationId}", method = RequestMethod.GET)
    @ResponseBody
    public Result getVersionNumber(@PathVariable("quotationId") String quotationId) {
        ILQuotationDto quotationMap = ilQuotationFinder.getQuotationById(quotationId);
        return Result.success("Quotation Version number ", quotationMap.getVersionNumber());
    }

    @RequestMapping(value = "/createquotation", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "Create Individual Life Quotation")
    @ResponseBody
    public Result createQuotation(@RequestBody ILCreateQuotationCommand createILQuotationCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Create quotation data is not valid", bindingResult.getAllErrors());
        }
        try {
            createILQuotationCommand.setUserDetails(getLoggedInUserDetail(request));
            QuotationId quotationId = commandGateway.sendAndWait(createILQuotationCommand);
            return Result.success("Quotation created successfully", quotationId.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "getquotation/{quotationId}")
    @ApiOperation(httpMethod = "GET", value = "This call for edit quotation screen.")
    @ResponseBody
    public ILQuotationDto getQuotationById(@PathVariable("quotationId") String quotationId) {
        ILQuotationDto dto = ilQuotationFinder.getQuotationById(quotationId);
        Map agentDetail = agentFinder.getAgentById(dto.getAgentId());
        Map planDetail = planFinder.findPlanByPlanId(new PlanId(dto.getPlanId()));
        dto.setAgentDetail(agentDetail);
        dto.setPlanDetail(planDetail);
        return dto;
    }

    @RequestMapping(method = RequestMethod.GET, value = "getridersforplan/{planId}")
    @ApiOperation(httpMethod = "GET", value = "This call for edit quotation screen.")
    @ResponseBody
    public List<RiderDetailDto> getRidersForPlan(@PathVariable("planId") String planId) {
        List<Map<String, Object>> optionalCoverages = ilQuotationFinder.findAllOptionalCoverages(planId);
        List<RiderDetailDto> riderDetails = new ArrayList<>();
        for (Map<String, Object> m : optionalCoverages) {
            RiderDetailDto dto = new RiderDetailDto();
            dto.setCoverageName(m.get("coverage_name").toString());
            dto.setCoverageId(m.get("coverage_id").toString());
            riderDetails.add(dto);
        }
        return riderDetails;
    }


    @RequestMapping(value = "/updatewithproposerdetail", method = RequestMethod.POST)
    @ResponseBody
    public Result updateQuotationWithProposerDetail(@RequestBody ILUpdateQuotationWithProposerCommand updateILQuotationWithProposerCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update quotation proposer data is not valid", bindingResult.getAllErrors());
        }
        try {
            updateILQuotationWithProposerCommand.setUserDetails(getLoggedInUserDetail(request));
            QuotationId quotationId = commandGateway.sendAndWait(updateILQuotationWithProposerCommand);
            return Result.success("Proposer detail updated successfully", quotationId.toString());
        } catch (Exception e) {
            return Result.failure();
        }
    }

    @RequestMapping(value = "/updatewithassureddetail", method = RequestMethod.POST)
    @ResponseBody
    public Result updateQuotationWithAssuredDetail(@RequestBody ILUpdateQuotationWithAssuredCommand updateILQuotationWithAssuredCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update quotation Assured data is not valid", bindingResult.getAllErrors());
        }
        try {
            updateILQuotationWithAssuredCommand.setUserDetails(getLoggedInUserDetail(request));
            QuotationId quotationId = commandGateway.sendAndWait(updateILQuotationWithAssuredCommand);
            return Result.success("Assured detail updated successfully", quotationId.toString());
        } catch (Exception e) {
            return Result.failure();
        }
    }

    @RequestMapping(value = "/updatewithplandetail", method = RequestMethod.POST)
    @ResponseBody
    public Result updateQuotationWithPlanDetail(@RequestBody ILUpdateQuotationWithPlanCommand updateILQuotationWithPlanCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update quotation Plan details data is not valid", bindingResult.getAllErrors());
        }
        try {
            updateILQuotationWithPlanCommand.setUserDetails(getLoggedInUserDetail(request));
            QuotationId quotationId = commandGateway.sendAndWait(updateILQuotationWithPlanCommand);
            return Result.success("Plan details updated successfully", quotationId.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failure();
        }
    }

    @RequestMapping(value = "/getpremiumdetail/{quotationid}", method = RequestMethod.GET)
    @ResponseBody
    public PremiumDetailDto getPremiumDetail(@PathVariable("quotationid") String quotationId) {
        return ilQuotationService.getPremiumDetail(new QuotationId(quotationId));
    }

    @RequestMapping(value = "/generatequotation", method = RequestMethod.POST)
    @ResponseBody
    public Result reCalculatePremium(@RequestBody ILGenerateQuotationCommand generateQuotationCommand) {
        try{
            String quotationId =commandGateway.sendAndWait(generateQuotationCommand)   ;
            return Result.success("Quotation Generated Successfully",quotationId);
        }catch (Exception e){
            e.printStackTrace();
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/printquotation/{quotationId}", method = RequestMethod.GET)
    public void printQuotation(@PathVariable("quotationId") String quotationId, HttpServletResponse response) throws IOException, JRException {
        response.reset();
        response.setContentType("application/pdf");
        response.setHeader("content-disposition", "attachment; filename=" + "quotation.pdf" + "");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(ilQuotationService.getQuotationPDF(quotationId));
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/emailQuotation/{quotationId}", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView openEmail(@PathVariable("quotationId") String quotationId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/quotation/individuallife/emailQuotation");
        modelAndView.addObject("mailContent", ilQuotationService.getPreScriptedEmail(quotationId));
        return modelAndView;
    }

    //TODO
    /**
     * Quotation Shared with Client (This status should be updated on first click for print/email Quotation and the date should also
     * be updated.
     * Subsequent clicks should have no impact the Status or the Date
     */
    @RequestMapping(value = "/emailQuotation", method = RequestMethod.POST)
    @ResponseBody
    public Result emailQuotation(@RequestBody ILQuotationMailDto mailDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Email cannot be sent due to wrong data");
        }
        try {
            byte[] quotationData = ilQuotationService.getQuotationPDF(mailDto.getQuotationId());
            String fileName = "QuotationNo-" + mailDto.getQuotationNumber() + ".pdf";
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

}
