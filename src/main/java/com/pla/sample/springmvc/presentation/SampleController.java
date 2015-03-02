package com.pla.sample.springmvc.presentation;

import com.pla.sample.springmvc.application.AgentCommand;
import com.pla.sample.springmvc.application.SampleCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.joda.money.Money;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.nthdimenzion.common.AppConstants.DEFAULT_CURRENCY;
import static org.nthdimenzion.presentation.AppUtils.ToLocalDate;
import static org.nthdimenzion.presentation.AppUtils.ToMoney;
import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Author: Nthdimenzion
 */

@Controller
@RequestMapping(value = "/sample")
public class SampleController {


    @Autowired
    private CommandGateway commandGateway;

    @RequestMapping(value = "/saveAgent",method = RequestMethod.POST)
    public String saveAgent(@Valid AgentCommand agentCommand, BindingResult bindingResult){
        System.out.println("saveAgent");
        System.out.println(agentCommand);
        if (bindingResult.hasErrors()){
            return "/pla/sample/AgentCreation";
        }
        return "pla/sample/agentCreation";
    }

    @RequestMapping(value = "/agent",method = GET)
    public String getAgent(Model model){
        model.addAttribute("agentCommand", new AgentCommand());
        return "pla/sample/agentCreation";
    }

    @RequestMapping(produces = "text/html")
    public String sampleFormController(Model model) {
        model.addAttribute("sampleCommand", new SampleCommand());
        return "/pla/sample/sample";
    }

    @RequestMapping(value = "/saveSample",method = RequestMethod.POST)
    public String saveAgent(@Valid SampleCommand sampleCommand, BindingResult bindingResult){
        System.out.println(sampleCommand);
//        System.out.println(sampleCommand.getMultipartFile().getSize());
        return "pla/sample/sample";
    }

    @RequestMapping(value = "/getSampleJsonWithDateAndMoney", method = GET,produces = APPLICATION_JSON_VALUE, consumes = ALL_VALUE)
    @ResponseBody
    public Map getSampleJsonWithDateAndMoney() {
        Map result = new HashMap();
        result.put("date", LocalDate.now());
        result.put("money", Money.of(DEFAULT_CURRENCY, BigDecimal.TEN));
        return result;
    }

    @RequestMapping(value = "/saveSampleJsonWithDateAndMoney", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity saveSampleJsonWithDateAndMoney(@RequestBody Map input) {
        // Assumed that date will come in dd/MM/yyyy format
        System.out.println(ToLocalDate(input.get("date").toString()));
        System.out.println(ToMoney(input.get("money").toString()));
        System.out.println(input);
        return ResponseEntity.ok().build();
    }

}
