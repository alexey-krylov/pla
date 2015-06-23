package com.pla.individuallife.proposal.presentation.controller;

import com.pla.core.domain.model.agent.AgentId;
import com.pla.core.query.PlanFinder;
import com.pla.individuallife.proposal.application.command.ILCreateProposalCommand;
import com.pla.individuallife.proposal.application.command.ILCreateQuestionCommand;
import com.pla.individuallife.proposal.application.command.ILProposalCommandGateway;
import com.pla.individuallife.proposal.application.command.ILUpdateCompulsoryHealthStatementCommand;
import com.pla.individuallife.proposal.domain.model.*;
import com.pla.individuallife.proposal.presentation.dto.ProposedAssuredDto;
import com.pla.individuallife.proposal.presentation.dto.ProposerDto;
import com.pla.individuallife.proposal.presentation.dto.QuestionAnswerDto;
import com.pla.individuallife.quotation.application.service.ILQuotationAppService;
import com.pla.individuallife.quotation.presentation.dto.ILSearchQuotationDto;
import com.pla.individuallife.quotation.query.ILQuotationDto;
import com.pla.individuallife.quotation.query.ILQuotationFinder;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.GatewayProxyFactory;
import org.bson.types.ObjectId;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
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

    public ILCreateProposalCommand convertToProposal(ILCreateProposalCommand cmd, String QuotationId) {

        ILQuotationDto quotationMap = ilQuotationFinder.getQuotationById(QuotationId);
        AgentCommissionShareModel agentCommissionShareModel = new AgentCommissionShareModel();
        agentCommissionShareModel.addAgentCommission(new AgentId(quotationMap.getAgentId()), new BigDecimal(1.00));

        ProposerDto proposerDto = new ProposerDto();
        ProposedAssuredDto proposedAssuredDto = new ProposedAssuredDto();

        Proposer proposer = new ProposerBuilder().withTitle(quotationMap.getProposer().getTitle()).withFirstName(quotationMap.getProposer().getFirstName()).withSurname(quotationMap.getProposer().getSurname()).withNrc(quotationMap.getProposer().getNrcNumber()).withDateOfBirth(quotationMap.getProposer().getDateOfBirth()).withGender(quotationMap.getProposer().getGender()).withMobileNumber(quotationMap.getProposer().getMobileNumber()).withEmailAddress(quotationMap.getProposer().getEmailAddress()).createProposer();
        ProposedAssured proposedAssured = new ProposedAssuredBuilder().withTitle(quotationMap.getProposedAssured().getTitle()).withFirstName(quotationMap.getProposedAssured().getFirstName()).withSurname(quotationMap.getProposedAssured().getSurname()).withNrc(quotationMap.getProposedAssured().getNrcNumber()).withDateOfBirth(quotationMap.getProposedAssured().getDateOfBirth()).withGender(quotationMap.getProposedAssured().getGender()).withMobileNumber(quotationMap.getProposedAssured().getMobileNumber()).withEmailAddress(quotationMap.getProposedAssured().getEmailAddress()).createProposedAssured();
        ProposalPlanDetail proposalPlanDetail = new ProposalPlanDetail();

        try {
            BeanUtils.copyProperties(proposerDto, quotationMap.getProposer());
            BeanUtils.copyProperties(proposalPlanDetail, quotationMap.getPlanDetailDto());
            BeanUtils.copyProperties(proposedAssuredDto, quotationMap.getProposedAssured());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        cmd.setProposer(proposerDto);
        cmd.setProposedAssured(proposedAssuredDto);
        cmd.setPlanDetail(proposalPlanDetail);

        return cmd;
    }

    @ResponseBody
    @RequestMapping(value = "/convert", method = RequestMethod.POST)
    @ApiOperation(httpMethod = "POST", value = "This call to convert quotation to Proposal.")
    public Result convertToProposal(@RequestBody String quotationId, BindingResult bindingResult, HttpServletRequest request) {
        String proposalId = null;

        if (bindingResult.hasErrors()) {
            return Result.failure("Convert quotation data is not valid", bindingResult.getAllErrors());
        }
            ILCreateProposalCommand cmd = new ILCreateProposalCommand();
            UserDetails userDetails = getLoggedInUserDetail(request);
            cmd.setUserDetails(userDetails);
            cmd = convertToProposal(cmd, quotationId);
            proposalId = new ObjectId().toString();
            cmd.setProposalId(proposalId);
        try {
            proposalCommandGateway.createProposal(cmd);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        return Result.success("Quotation created successfully", quotationId.toString());
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
    public ModelAndView searchQuotation(ILSearchQuotationDto searchIlQuotationDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("searchResult", ilQuotationService.searchQuotation(searchIlQuotationDto));
        modelAndView.addObject("searchCriteria", searchIlQuotationDto);
        modelAndView.setViewName("pla/individuallife/proposal/searchquotationforilproposal");
        return modelAndView;
    }

}
