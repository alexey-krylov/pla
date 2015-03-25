package com.pla.core.presentation.controller;

import com.google.common.collect.Lists;
import com.pla.core.application.CreateTeamCommand;
import com.pla.core.application.UpdateTeamCommand;
import com.pla.core.query.MasterFinder;
import com.pla.core.query.TeamFinder;
import com.sun.org.apache.regexp.internal.RE;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUSerDetail;

/**
 * Created by Nischitha on 10-Mar-15.
 */
@Controller
@RequestMapping(value = "/core", consumes = MediaType.ALL_VALUE)
public class TeamController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamController.class);

    private CommandGateway commandGateway;

    private TeamFinder teamFinder;

    private MasterFinder masterFinder;

    @Autowired
    public TeamController(CommandGateway commandGateway, TeamFinder teamFinder, MasterFinder masterFinder) {
        this.commandGateway = commandGateway;
        this.teamFinder = teamFinder;
        this.masterFinder = masterFinder;
    }

    @RequestMapping(value = "/team/view", method = RequestMethod.GET)
    public ModelAndView viewTeams() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/team/viewTeam");
        modelAndView.addObject("teamList", teamFinder.getAllTeam());
        return modelAndView;
    }

    @RequestMapping(value = "/team/openCreatePage", method = RequestMethod.GET)
    public ModelAndView openCreatePageTeam() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/team/createTeam");
        modelAndView.addObject("regions", masterFinder.getAllRegion());
        modelAndView.addObject("teamLeaders", new ArrayList<>());
        return modelAndView;
    }

    @RequestMapping(value = "/team/openAssignPage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> openAssignPageTeam(@RequestParam(value = "teamId", required = false) String teamId) {
        return teamFinder.getTeamById(teamId);
    }


    @RequestMapping(value = "/team/getteamleaders" ,method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, Object>> getAllTeamLeaders() {
        Map<String, Object> teamLeader = new HashMap<>();
        teamLeader.put("teamLeaderId", "");
        teamLeader.put("firstName", "");
        teamLeader.put("lastName", "");
        return Lists.newArrayList(teamLeader);
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
    Result updateTeamLead(@RequestBody UpdateTeamCommand updateTeamCommand, BindingResult bindingResult, HttpServletRequest request) {
        try {
            UserDetails userDetails = getLoggedInUSerDetail(request);
            updateTeamCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(updateTeamCommand);
        } catch (Exception e) {
            LOGGER.error("Error in creating team", e);
            return Result.failure("Error in updating team");
        }
        return Result.success("Team updated successfully");
    }


}
