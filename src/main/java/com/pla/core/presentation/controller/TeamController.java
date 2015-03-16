package com.pla.core.presentation.controller;

import com.pla.core.application.CreateTeamCommand;
import com.pla.core.application.UpdateTeamCommand;
import com.pla.core.query.TeamFinder;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


/**
 * Created by ASUS on 02-Mar-15.
 */
@Controller
@RequestMapping(value = "/core")
public class TeamController {

    @Autowired
    private CommandGateway commandGateway;

    private TeamFinder teamFinder;

    @Autowired
    public TeamController(CommandGateway commandGateway, TeamFinder teamFinder) {
        this.commandGateway = commandGateway;
        this.teamFinder = teamFinder;
    }

    @RequestMapping(value = "/team/view", method = RequestMethod.GET)
    public ModelAndView viewTeams(@RequestParam(value = "page", required = false) Integer pageNumber, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/viewTeam");
        modelAndView.addObject("teamList", teamFinder.getAllTeam());
        return modelAndView;
    }

    @RequestMapping(value = "/team/create", method = RequestMethod.POST)
    public
    @ResponseBody
    Result createTeam(@RequestBody CreateTeamCommand createTeamCommand, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in creating team", bindingResult.getAllErrors());
        }
        try {
            commandGateway.sendAndWait(createTeamCommand);
        } catch (Exception e) {
            return Result.failure("Error in creating team");
        }
        return Result.success("Team created successfully");
    }

    @RequestMapping(value = "/team/assign", method = RequestMethod.POST)
    public
    @ResponseBody
    Result updateTeamLead(@RequestBody UpdateTeamCommand updateTeamCommand) {

        try {
            commandGateway.sendAndWait(updateTeamCommand);
        } catch (Exception e) {
            return Result.failure("Error in updating team");
        }
        return Result.success("Team created successfully");
    }
}
