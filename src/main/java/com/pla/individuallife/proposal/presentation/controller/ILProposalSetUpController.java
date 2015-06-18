package com.pla.individuallife.proposal.presentation.controller;

import com.pla.core.query.PlanFinder;
import com.pla.individuallife.identifier.ProposalId;
import com.pla.individuallife.proposal.application.command.*;
import com.pla.individuallife.proposal.domain.model.QuestionAnswer;
import com.pla.individuallife.proposal.presentation.dto.QuestionAnswerDto;
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
    @RequestMapping(value = "/convert")
    public ResponseEntity<Map> convertToProposal(@RequestBody ILConvertToProposalCommand cmd, BindingResult bindingResult, HttpServletRequest request) {
        ProposalId proposalId = null;

        if (bindingResult.hasErrors()) {
            return new ResponseEntity(bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        try {
            proposalId = new ProposalId(new ObjectId().toString());
            UserDetails userDetails = getLoggedInUserDetail(request);
            cmd.setUserDetails(userDetails);
            proposalCommandGateway.convertToProposal(cmd);
        } catch (TimeoutException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        Map map = new HashMap<>();
        map.put("msg", "Proposal got created successfully");
        map.put("proposalId", proposalId.toString());
//        return new ResponseEntity("Quotation got converted to Proposal successfully", HttpStatus.OK);
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

}
