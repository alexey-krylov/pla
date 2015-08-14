package com.pla.core.presentation.controller;

import com.pla.core.application.CreateTeamCommand;
import com.pla.core.application.InactivateTeamCommand;
import com.pla.core.application.UpdateTeamCommand;
import com.pla.core.query.MasterFinder;
import com.pla.core.query.TeamFinder;
import com.pla.publishedlanguage.contract.ISMEGateway;
import com.pla.publishedlanguage.domain.model.EmployeeDto;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.common.AppConstants;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUserDetail;

/**
 * Created by Nischitha on 10-Mar-15.
 */
@Controller
@RequestMapping(value = "/core/team", consumes = MediaType.ALL_VALUE)
public class TeamController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamController.class);

    private CommandGateway commandGateway;

    private TeamFinder teamFinder;

    private MasterFinder masterFinder;

    private ISMEGateway smeGateway;

    @Autowired
    public TeamController(CommandGateway commandGateway, TeamFinder teamFinder, MasterFinder masterFinder, ISMEGateway smeGateway) {
        this.commandGateway = commandGateway;
        this.teamFinder = teamFinder;
        this.masterFinder = masterFinder;
        this.smeGateway = smeGateway;
    }

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public ModelAndView viewTeams() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/team/viewTeam");
        modelAndView.addObject("teamList", teamFinder.getAllActiveTeamFulfillmentGreaterThanCurrentDate());
        return modelAndView;
    }

    @RequestMapping(value = "/opencreatepage", method = RequestMethod.GET)
    public ModelAndView openCreatePageTeam() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/team/createTeam");
        modelAndView.addObject("regions", masterFinder.getAllRegion());
        modelAndView.addObject("teamLeaders", getAllTeamLeaders());
        return modelAndView;
    }

    @RequestMapping(value = "/redirecttoassignPage", method = RequestMethod.GET)
    public ModelAndView redirectToAssignPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/team/assignTeam");
        return modelAndView;
    }

    @RequestMapping(value = "/getteamdetail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getTeamDetail(@RequestParam(value = "teamId", required = false) String teamId) {
        return teamFinder.getTeamById(teamId);
    }


    @RequestMapping(value = "/getteamleaders", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<EmployeeDto> getAllTeamLeaders() {
        List<Map<String, Object>> allTeams = teamFinder.getAllActiveTeamLeaders();
        List<EmployeeDto> allTeamLeaders = smeGateway.getEmployeeDetailByDesignation(AppConstants.TEAM_LEADER_DESIGNATION);
        List<EmployeeDto> teamLeadersNotAssociatedWithTeam = allTeamLeaders.stream().filter(new FilterTeamLeaderFromTeamPredicate(allTeams)).collect(Collectors.toList());
        return teamLeadersNotAssociatedWithTeam;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public
    @ResponseBody
    Result createTeam(@RequestBody CreateTeamCommand createTeamCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in creating team", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            createTeamCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(createTeamCommand);
        } catch (Exception e) {
            LOGGER.error("Error in creating team", e);
            return Result.failure(e.getMessage());
        }
        return Result.success("Team created successfully");
    }

    @RequestMapping(value = "/assign", method = RequestMethod.POST)
    public
    @ResponseBody
    Result updateTeamLead(@RequestBody UpdateTeamCommand updateTeamCommand, BindingResult bindingResult, HttpServletRequest request) {
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            updateTeamCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(updateTeamCommand);
        } catch (Exception e) {
            LOGGER.error("Error in creating team", e);
            return Result.failure(e.getMessage());
        }
        return Result.success("Team updated successfully");
    }

    @RequestMapping(value = "/inactivate", method = RequestMethod.POST)
    @ResponseBody
    Result inactivateTeam(@RequestBody InactivateTeamCommand inactivateTeamCommand, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Error in inactivating team", bindingResult.getAllErrors());
        }
        try {
            UserDetails userDetails = getLoggedInUserDetail(request);
            inactivateTeamCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(inactivateTeamCommand);
        } catch (Exception e) {
            LOGGER.error("Error in inactivating team", e);
            return Result.failure(e.getMessage());
        }
        return Result.success("Team inactivated successfully");
    }

    private class FilterTeamLeaderFromTeamPredicate implements Predicate<EmployeeDto> {

        List<Map<String, Object>> allTeams;

        FilterTeamLeaderFromTeamPredicate(List<Map<String, Object>> allTeams) {
            this.allTeams = allTeams;
        }

        @Override
        public boolean test(EmployeeDto employeeDto) {
            Optional<Map<String, Object>> teamOptional = allTeams.stream().filter(team -> employeeDto.getEmployeeId().equals((String) team.get("currentTeamLeader"))).findAny();
            return !teamOptional.isPresent();
        }
    }
}
