package com.pla.individuallife.proposal.presentation.controller;

import com.pla.core.query.PlanFinder;
import com.pla.individuallife.proposal.application.command.ILCreateProposalCommand;
import com.pla.individuallife.proposal.application.command.ILCreateQuestionCommand;
import com.pla.individuallife.proposal.application.command.ILProposalCommandGateway;
import com.pla.individuallife.proposal.application.command.ILUpdateCompulsoryHealthStatementCommand;
import com.pla.individuallife.proposal.domain.model.QuestionAnswer;
import com.pla.individuallife.proposal.presentation.dto.QuestionAnswerDto;
import com.pla.individuallife.quotation.application.service.ILQuotationAppService;
import com.pla.individuallife.quotation.presentation.dto.ILSearchDto;
import com.pla.individuallife.quotation.query.ILQuotationFinder;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.GatewayProxyFactory;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUserDetail;

/**
 * Created by Prasant on 26-May-15.
 */
@Controller
@RequestMapping(value = "/individuallife/proposal")
public class ILProposalSetUpController {

    private final ILProposalCommandGateway proposalCommandGateway;
    private final PlanFinder planFinder;

    @Autowired
    private ILQuotationFinder ilQuotationFinder;

    @Autowired
    private ILQuotationAppService ilQuotationService;

    @Autowired
    public ILProposalSetUpController(CommandBus commandBus, PlanFinder planFinder) {
        this.planFinder = planFinder;
        GatewayProxyFactory factory = new GatewayProxyFactory(commandBus);
        proposalCommandGateway = factory.createGateway(ILProposalCommandGateway.class);
    }

    @ResponseBody
    @RequestMapping(value = "/create")
    public ResponseEntity<Map> createProposal(@RequestBody ILCreateProposalCommand createProposalCommand, BindingResult bindingResult, HttpServletRequest request) {
        String proposalId = null;

        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        try {
            proposalId = new ObjectId().toString();
            UserDetails userDetails = getLoggedInUserDetail(request);
            createProposalCommand.setUserDetails(userDetails);
            createProposalCommand.setProposalId(proposalId);
            proposalCommandGateway.createProposal(createProposalCommand);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Map map = new HashMap<>();
        map.put("msg", "Proposal got created successfully");
        map.put("proposalId", proposalId);
//        return new ResponseEntity("Proposal got created successfully", HttpStatus.OK);
        return new ResponseEntity(map, HttpStatus.OK);
    }

    @ResponseBody
    @RequestMapping(value = "/updateCompulsoryHealthStatement/{proposalId}", method = RequestMethod.POST)
    public ResponseEntity<List<QuestionAnswerDto>> updateCompulsoryHealthStatement(
            @RequestBody ILUpdateCompulsoryHealthStatementCommand updateCompulsoryHealthStatementCommand,
            @PathVariable("proposalId") String proposalIdUrl, HttpServletRequest request,
            BindingResult bindingResult) {
        String proposalId = null;
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        try {
            proposalId = proposalIdUrl;
            UserDetails userDetails = getLoggedInUserDetail(request);
            updateCompulsoryHealthStatementCommand.setUserDetails(userDetails);
            updateCompulsoryHealthStatementCommand.setProposalId(proposalId);
            proposalCommandGateway.updateCompulsoryHealthStatement(updateCompulsoryHealthStatementCommand);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<QuestionAnswer> list=updateCompulsoryHealthStatementCommand.getQuestions();
        return new ResponseEntity(list, HttpStatus.OK);
    }


    @ResponseBody
    @RequestMapping(value = "/createQuestion/{proposalId}", method = RequestMethod.POST)
    public ResponseEntity createQuestion(@RequestBody ILCreateQuestionCommand createQuestionCommand,@PathVariable("proposalId") String proposalIdUrl,HttpServletRequest request,
                                         BindingResult bindingResult) {

        String proposalId = null;
        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }

        try {
            proposalId = proposalIdUrl;
            UserDetails userDetails = getLoggedInUserDetail(request);
            createQuestionCommand.setUserDetails(userDetails);
            createQuestionCommand.setProposalId(proposalId);
            proposalCommandGateway.createCompulsoryQuestion(createQuestionCommand);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new ResponseEntity("SucessFull",HttpStatus.OK);
    }

    @RequestMapping(value="/searchQuotation", method = RequestMethod.POST)
    public ModelAndView searchQuotation(ILSearchDto searchIlDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("searchResult", ilQuotationService.searchQuotation(searchIlDto));
        modelAndView.addObject("searchCriteria", searchIlDto);
        modelAndView.setViewName("pla/individuallife/proposal/searchquotationforilproposal");
        return modelAndView;
    }

}
