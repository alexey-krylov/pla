package com.pla.core.presentation.controller;

import com.pla.core.application.CreateTeamCommand;
import com.pla.core.application.UpdateTeamCommand;
import com.pla.core.query.MasterFinder;
import com.pla.core.query.TeamFinder;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.common.AppConstants;
import org.nthdimenzion.presentation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Nischitha on 10-Mar-15.
 */
@Controller
@RequestMapping(value = "/core")
public class TeamController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamController.class);

    private CommandGateway commandGateway;

    private TeamFinder teamFinder;

    private MasterFinder   masterFinder;

    @Autowired
    public TeamController(CommandGateway commandGateway, TeamFinder teamFinder,MasterFinder masterFinder) {
        this.commandGateway = commandGateway;
        this.teamFinder = teamFinder;
        this.masterFinder=masterFinder;
    }

    @RequestMapping(value = "/team/view", method = RequestMethod.GET)
    public ModelAndView viewTeams(@RequestParam(value = "page", required = false) Integer pageNumber, Model model) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/viewTeam");
        modelAndView.addObject("teamList", teamFinder.getAllTeam());
        return modelAndView;
    }

    @RequestMapping(value = "/team/openCreatePage", method = RequestMethod.GET)
    public ModelAndView openCreatePageTeam() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/createTeam");
        modelAndView.addObject("regions", masterFinder.getAllRegion());
        return modelAndView;
    }

    @RequestMapping(value = "/team/openAssignPage", method = RequestMethod.GET)
    public String openAssignPageTeam() {
        return "pla/core/assignTeam";
    }


    @RequestMapping(value = "/team/create", method = RequestMethod.POST)
    public
    @ResponseBody
    Result createTeam(@RequestBody CreateTeamCommand createTeamCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in creating team", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUSerDetail(request);
            createTeamCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(createTeamCommand);
        } catch (Exception e) {
            LOGGER.error("Error in creating team", e);
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
            LOGGER.error("Error in creating team", e);
            return Result.failure("Error in updating team");
        }
        return Result.success("Team updated successfully");
    }

    private UserDetails getLoggedInUSerDetail(HttpServletRequest request) {
        UserDetails userDetails = (UserDetails) request.getSession().getAttribute(AppConstants.LOGGED_IN_USER);
        return userDetails;
    }
}
