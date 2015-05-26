package com.pla.individuallife.presentation.controller;

import com.pla.individuallife.application.command.quotation.CreateILQuotationCommand;
import com.pla.individuallife.application.command.quotation.UpdateILQuotationWithAssuredCommand;
import com.pla.individuallife.application.command.quotation.UpdateILQuotationWithPlanCommand;
import com.pla.individuallife.application.command.quotation.UpdateILQuotationWithProposerCommand;
import com.pla.individuallife.application.service.quotation.ILQuotationService;
import com.pla.individuallife.presentation.dto.ProposedAssuredDto;
import com.pla.individuallife.presentation.dto.ProposerDto;
import com.pla.individuallife.query.ILQuotationFinder;
import com.pla.individuallife.query.PremiumDetailDto;
import com.pla.sharedkernel.identifier.QuotationId;
import com.wordnik.swagger.annotations.ApiOperation;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUSerDetail;

/**
 * Created by Karunakar on 5/13/2015.
 */
@Controller
@RequestMapping(value = "/quotation/individuallife")
public class IndidualLifeQuotationController {

    private CommandGateway commandGateway;

    private ILQuotationService ilQuotationService;

    private ILQuotationFinder ilQuotationFinder;

    @Autowired
    public IndidualLifeQuotationController(CommandGateway commandGateway, ILQuotationService ilQuotationService, ILQuotationFinder ilQuotationFinder) {
        this.commandGateway = commandGateway;
        this.ilQuotationService = ilQuotationService;
        this.ilQuotationFinder = ilQuotationFinder;
    }

    @RequestMapping(value = "/createindividuallifequotation", method = RequestMethod.GET)
    public ModelAndView createQuotationPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/quotation/individualLife/createQuotation");
        return modelAndView;
    }

    @RequestMapping(value = "/getquotationnumber/{quotationId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Get Quotation number for a given quotation Id")
    @ResponseBody
    public Result getQuotationNumber(@PathVariable("quotationId") String quotationId) {
        Map quotationMap = ilQuotationFinder.getQuotationById(quotationId);
        return Result.success("Quotation number ", (String) quotationMap.get("quotationNumber"));
    }

    @RequestMapping(value = "/getversionnumber/{quotationId}", method = RequestMethod.GET)
    @ResponseBody
    public Result getVersionNumber(@PathVariable("quotationId") String quotationId) {
        Map quotationMap = ilQuotationFinder.getQuotationById(quotationId);
        return Result.success("Quotation Version number ", quotationMap.get("versionNumber"));
    }

    @RequestMapping(value = "/createquotation", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "Create Individual Life Quotation")
    @ResponseBody
    public Result createQuotation(@RequestBody CreateILQuotationCommand createILQuotationCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Create quotation data is not valid", bindingResult.getAllErrors());
        }
        try {
            createILQuotationCommand.setUserDetails(getLoggedInUSerDetail(request));
            String quotationId = commandGateway.sendAndWait(createILQuotationCommand);
            return Result.success("Quotation created successfully", quotationId);
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/updatewithproposerdetail", method = RequestMethod.POST)
      @ResponseBody
      public Result updateQuotationWithProposerDetail(@RequestBody UpdateILQuotationWithProposerCommand updateILQuotationWithProposerCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update quotation proposer data is not valid", bindingResult.getAllErrors());
        }
        try {
            updateILQuotationWithProposerCommand.setUserDetails(getLoggedInUSerDetail(request));
            String quotationId = commandGateway.sendAndWait(updateILQuotationWithProposerCommand);
            return Result.success("Proposer detail updated successfully", quotationId);
        } catch (Exception e) {
            return Result.failure();
        }
    }

    @RequestMapping(value = "/updatewithassureddetail", method = RequestMethod.POST)
    @ResponseBody
    public Result updateQuotationWithAssuredDetail(@RequestBody UpdateILQuotationWithAssuredCommand updateILQuotationWithAssuredCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update quotation Assured data is not valid", bindingResult.getAllErrors());
        }
        try {
            updateILQuotationWithAssuredCommand.setUserDetails(getLoggedInUSerDetail(request));
            String quotationId = commandGateway.sendAndWait(updateILQuotationWithAssuredCommand);
            return Result.success("Assured detail updated successfully", quotationId);
        } catch (Exception e) {
            return Result.failure();
        }
    }

    @RequestMapping(value = "/updatewithplandetail", method = RequestMethod.POST)
    @ResponseBody
    public Result updateQuotationWithPlanDetail(@RequestBody UpdateILQuotationWithPlanCommand updateILQuotationWithPlanCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Update quotation Plan details data is not valid", bindingResult.getAllErrors());
        }
        try {
            updateILQuotationWithPlanCommand.setUserDetails(getLoggedInUSerDetail(request));
            String quotationId = commandGateway.sendAndWait(updateILQuotationWithPlanCommand);
            return Result.success("Plan details updated successfully", quotationId);
        } catch (Exception e) {
            return Result.failure();
        }
    }

    @RequestMapping(value = "/getpremiumdetail/{quotationid}", method = RequestMethod.GET)
    @ResponseBody
    public PremiumDetailDto getPremiumDetail(@PathVariable("quotationid") String quotationId) {
        return ilQuotationService.getPremiumDetail(new QuotationId(quotationId));
    }

    @RequestMapping(value = "/recalculatePremium", method = RequestMethod.POST)
    @ResponseBody
    public PremiumDetailDto reCalculatePremium(@RequestBody PremiumDetailDto premiumDetailDto) {
        return ilQuotationService.getReCalculatePremium(premiumDetailDto);
    }

    @RequestMapping(value = "/getproposerdetail/{quotationId}")
     @ResponseBody
     public ProposerDto getProposerDetail(@PathVariable("quotationId") String quotationId) {
        return ilQuotationService.getProposerDetail(new QuotationId(quotationId));
    }

    @RequestMapping(value = "/getassureddetail/{quotationId}")
    @ResponseBody
    public ProposedAssuredDto getAssuredDetail(@PathVariable("quotationId") String quotationId) {
        return ilQuotationService.getAssuredDetail(new QuotationId(quotationId));
    }


}
