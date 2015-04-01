package com.pla.core.presentation.controller;

import com.pla.core.application.UpdateRegionalManagerCommand;
import com.pla.core.application.service.RegionService;
import com.pla.core.query.MasterFinder;
import com.pla.core.query.RegionFinder;
import com.pla.publishedlanguage.contract.ISMEGateway;
import com.pla.publishedlanguage.domain.model.EmployeeDto;
import org.nthdimenzion.common.AppConstants;
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Nischitha on 10-Mar-15.
 */
@Controller
@RequestMapping(value = "/core/region", consumes = MediaType.ALL_VALUE)
public class RegionController {


    private static final Logger LOGGER = LoggerFactory.getLogger(RegionController.class);

    private RegionService regionService;

    private RegionFinder regionFinder;

    private MasterFinder masterFinder;

    private ISMEGateway smeGateway;


    @Autowired
    public RegionController(RegionService regionService, RegionFinder regionFinder, MasterFinder masterFinder, ISMEGateway smeGateway) {
        this.regionService = regionService;
        this.regionFinder = regionFinder;
        this.masterFinder = masterFinder;
        this.smeGateway = smeGateway;
    }

    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public ModelAndView viewRegions() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/region/viewRegion");
        modelAndView.addObject("regionList", masterFinder.getAllRegion());
        return modelAndView;
    }

    @RequestMapping(value = "/redirectToAssignPage", method = RequestMethod.GET)
    public ModelAndView redirectToAssignPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/region/assignRegion");
        return modelAndView;
    }

    @RequestMapping(value = "/getregiondetail/{regionId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, Object> getRegionDetail(@PathVariable("regionId") String regionId) {
        return regionFinder.getRegionById(regionId);
    }

    @RequestMapping(value = "/getallregionalmanager", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<EmployeeDto> getAllRegionalManager() {
        List<Map<String, Object>> allRegion = masterFinder.getAllRegion();
        List<EmployeeDto> allRegionalManagers = smeGateway.getEmployeeDetailByDesignation(AppConstants.REGIONAL_MANAGER_DESIGNATION);
        List<EmployeeDto> regionalManagersNotAssociatedWithRegion = allRegionalManagers.stream().filter(new FilterRegionalManagerFromRegionPredicate(allRegion)).collect(Collectors.toList());
        return regionalManagersNotAssociatedWithRegion;
    }

    @RequestMapping(value = "/assign", method = RequestMethod.POST)
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

    private class FilterRegionalManagerFromRegionPredicate implements Predicate<EmployeeDto> {

        List<Map<String, Object>> allRegions;

        FilterRegionalManagerFromRegionPredicate(List<Map<String, Object>> allRegions) {
            this.allRegions = allRegions;
        }

        @Override
        public boolean test(EmployeeDto employeeDto) {
            Optional<Map<String, Object>> regionOptional = allRegions.stream().filter(region -> employeeDto.getEmployeeId().equals((String) region.get("currentRegionalManager"))).findAny();
            return !regionOptional.isPresent();
        }
    }

}
