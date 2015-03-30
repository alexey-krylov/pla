package com.pla.core.presentation.controller;

import com.pla.core.application.UpdateRegionalManagerCommand;
import com.pla.core.application.service.RegionService;
import com.pla.core.query.RegionFinder;
import org.nthdimenzion.presentation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Nischitha on 10-Mar-15.
 */
@Controller
@RequestMapping(value = "/core", consumes = MediaType.ALL_VALUE)
public class RegionController {


    private static final Logger LOGGER = LoggerFactory.getLogger(RegionController.class);

    private RegionService regionService;

    private RegionFinder regionFinder;

    @Autowired
    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @RequestMapping(value = "/region/view", method = RequestMethod.GET)
    public ModelAndView viewTeams() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/region/viewRegion");
        modelAndView.addObject("regionList", regionFinder.getAllRegion());
        return modelAndView;
    }

    @RequestMapping(value = "/region/redirectToAssignPage", method = RequestMethod.GET)
    public String redirectToAssignPage(@RequestParam(value = "regionId", required = false) String regionId) {
        return "pla/core/region/assignRegion";
    }

    @RequestMapping(value = "/region/openAssignPage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> openAssignPageTeam(@RequestParam(value = "regionId", required = false) String regionId) {
        return regionFinder.getRegionById(regionId);
    }

    @RequestMapping(value = "/region/assign", method = RequestMethod.POST)
    public
    @ResponseBody
    Result updateRegionalManager(@RequestBody UpdateRegionalManagerCommand updateRegionalManagerCommand, BindingResult bindingResult, HttpServletRequest request) {
        try {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("*****Command Received*****" + updateRegionalManagerCommand);
            }
            regionService.associateRegionalManager(updateRegionalManagerCommand.getRegionCode(), updateRegionalManagerCommand.getEmployeeId(), updateRegionalManagerCommand.getFirstName(), updateRegionalManagerCommand.getLastName(), updateRegionalManagerCommand.getFromDate());
        } catch (Exception e) {
            LOGGER.error("Error in creating region", e);
            return Result.failure("Error in creating region");
        }
        return Result.success("Region manager updated successfully");
    }


}
