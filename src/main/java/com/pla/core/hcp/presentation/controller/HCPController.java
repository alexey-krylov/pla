package com.pla.core.hcp.presentation.controller;

import com.pla.core.hcp.application.command.CreateOrUpdateHCPCommand;
import com.pla.core.hcp.application.service.HCPService;
import com.pla.core.hcp.domain.model.HCP;
import com.wordnik.swagger.annotations.ApiOperation;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
   @RequestMapping(value="/loadcreatepage",method=RequestMethod.GET)
   public ModelAndView loadPage(){
       ModelAndView modelAndView = new ModelAndView();
       modelAndView.setViewName("pla/core/hcp/createhcp");
       return  modelAndView;
   }

    @RequestMapping(value = "/createOrUpdateHCP", method = RequestMethod.POST)
    @ResponseBody
    public Result createOrUpdateHCP(@Valid @RequestBody CreateOrUpdateHCPCommand createOrUpdateHCPCommand, BindingResult bindingResult, ModelMap modelMap){
        if (bindingResult.hasErrors()) {
            modelMap.put(BindingResult.class.getName() + ".copyCartForm", bindingResult);
            return Result.failure("error occured while creating HCP", bindingResult.getAllErrors());
        }
        try {
            HCP hcp = commandGateway.sendAndWait(createOrUpdateHCPCommand);
            return Result.success("HCP created successfully", hcp.getHcpCode());
        } catch(Exception e){
            return Result.failure(e.getMessage());
        }
    }

    @RequestMapping(value = "/getHCPByHCPCode", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET",value = "Get HCP for Given HCPCode")
    public CreateOrUpdateHCPCommand getHCPByHCPCode(@RequestParam String hcpCode, HttpServletResponse response) throws IOException {
        if (isEmpty(hcpCode)) {
            response.sendError(HttpServletResponse.SC_BAD_GATEWAY, "hcpCode cannot be empty");
            return null;
        }
        return hcpService.getHCPByHCPCode(hcpCode);
    }

    @RequestMapping(value ="/getAllHCP" , method=RequestMethod.GET)
    public ModelAndView getAllHCP(){
        ModelAndView modelAndView = new ModelAndView();
        List<CreateOrUpdateHCPCommand> hcpCommandList = hcpService.getAllHCP();
        modelAndView.addObject("searchResult", hcpCommandList);
        modelAndView.setViewName("pla/core/hcp/viewhcp");
        return modelAndView;
    }

    @RequestMapping(value = "/getAllHCPStatus", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET",value = "Get the List of all HCPStatus")
    public @ResponseBody Set<String> getAllHCPStatus(){
        return hcpService.getAllHCPStatus();
    }

    @RequestMapping(value = "/getAllHCPCategories", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET",value = "Get the List of all HCPCategories")
    public @ResponseBody List<Map<String, String>> getAllHCPCategories(){
        return hcpService.getAllHCPCategories();
    }

}
