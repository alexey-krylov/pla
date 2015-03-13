/*
 * Copyright (c) 3/12/15 6:32 PM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

/**
 * Created with IntelliJ IDEA.
 * User: Tejeswar
 * Date: 3/9/15
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
package com.pla.core.presentation.controller;


import com.pla.core.application.CreateCoverageCommand;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/core")
public class CoverageController {


    @RequestMapping(value = "/coverages/view", method = RequestMethod.GET)
    public String viewCoverages(@RequestParam(value = "page", required = false) Integer pageNumber, Model model) {

        return "pla/core/viewCoverage";
    }

    @RequestMapping(value = "/coverages/create", method = RequestMethod.POST)
    public
    @ResponseBody
    String createCoverage(@RequestBody CreateCoverageCommand coverage) {
        return "success";
    }

    @RequestMapping(value = "/coverages/delete", method = RequestMethod.POST)
    public
    @ResponseBody
    String deleteCoverage(@RequestBody CreateCoverageCommand coverage) {
        return "success";
    }

    @RequestMapping(value = "/coverages/update", method = RequestMethod.POST)
    public
    @ResponseBody
    String updateCoverage(@RequestBody CreateCoverageCommand coverage) {
        return "success";
    }
}
