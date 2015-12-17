package com.pla.core.hcp.presentation.controller;

import com.pla.core.hcp.application.command.CreateOrUpdateHCPCommand;
import com.pla.core.hcp.application.service.HCPService;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.IOException;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

/**
 * Created by Mohan Sharma on 12/17/2015.
 */
@RequestMapping(value = "/core/hcp")
@RestController
public class HCPController {
    @Autowired
    CommandGateway commandGateway;
    @Autowired
    HCPService hcpService;

    @RequestMapping(value = "/createOrUpdateHCP", method = RequestMethod.POST)
    @ResponseBody
    public Result createHCP(@Valid @RequestBody CreateOrUpdateHCPCommand createOrUpdateHCPCommand, BindingResult bindingResult, ModelMap modelMap){
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while creating HCP", bindingResult.getAllErrors());
        }
        commandGateway.sendAndWait(createOrUpdateHCPCommand);
        return Result.success();
    }

    @RequestMapping(value = "/getHCPByHCPCode", method = RequestMethod.GET)
    @ResponseBody
    public CreateOrUpdateHCPCommand getHCPByHCPCode(@RequestParam String hcpCode, HttpServletResponse response) throws IOException {
        if (isEmpty(hcpCode)) {
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "hcpCode cannot be empty");
            return null;
        }
        return hcpService.getHCPByHCPCode(hcpCode);
    }
}
