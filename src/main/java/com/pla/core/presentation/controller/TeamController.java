package com.pla.core.presentation.controller;

import com.pla.core.application.CreateTeamCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Created by ASUS on 02-Mar-15.
 */
@Controller
@RequestMapping(value = "/core/team")
public class TeamController {

    @Autowired
    private CommandGateway commandGateway;
    
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public String viewTeam(@RequestParam(value = "page", required = false) Integer pageNumber, Model model) {
        return "pla/core/viewTeam";
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public
    @ResponseBody
    String createTeam(@RequestBody CreateTeamCommand createTeamCommand) {
        return "success";
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public
    @ResponseBody
    String updateTeam(@RequestBody CreateTeamCommand createTeamCommand) {
        return "success";
    }


}
