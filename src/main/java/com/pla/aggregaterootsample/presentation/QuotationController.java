package com.pla.aggregaterootsample.presentation;

import com.pla.aggregaterootsample.application.CreateQuotationCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.application.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * @author: Samir
 * @since 1.0 10/02/2015
 */
@Controller
@RequestMapping(value = "/quotation")
public class QuotationController {

    private static CommandGateway commandGateway;

    @RequestMapping(value = "/createQuotation", method = RequestMethod.POST)
    public String saveQuotation(@Valid CreateQuotationCommand quotationCommand, BindingResult bindingResult) {
        commandGateway.sendAndWait(quotationCommand);
        return "";
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        CreateQuotationCommand createQuotationCommand = new CreateQuotationCommand("Test Client", "9916971271", "Test Quotation");
        commandGateway.sendAndWait(createQuotationCommand);
    }


    @Autowired
    public void setCommandGateway(CommandGateway commandGateway) {
        QuotationController.commandGateway = commandGateway;
    }
}
