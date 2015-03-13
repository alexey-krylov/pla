/**
 * Created with IntelliJ IDEA.
 * User: Tejeswar
 * Date: 3/9/15
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
package com.pla.core.presentation.controller;


import com.google.common.collect.Lists;
import com.pla.core.presentation.dto.Coverage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping(value = "/core")
public class CoverageController {
    List coverages = Lists.newArrayList( "Accidental Death Benefit", "CI Benefit","Permanent Total Disability");
    List benefits = Lists.newArrayList("Accidental Death Benefit", "CI Benefit","Death Benefit");

   @RequestMapping(value = "/coverages/view",method = RequestMethod.GET)
    public String viewCoverages(@RequestParam(value = "page",required = false) Integer pageNumber,Model model){
       System.out.println("");
        if(null!=pageNumber){
            model.addAttribute("coverages",coverages.subList(pageNumber-1,pageNumber+1));
            model.addAttribute("benefits",benefits);
        }else {
            model.addAttribute("coverages",coverages);
            model.addAttribute("benefits",benefits);
        }

        return "pla/core/viewCoverage";
    }

    @RequestMapping(value = "/coverages/create",method = RequestMethod.POST)
    public  @ResponseBody String createCoverage(@RequestBody Coverage coverage){
        System.out.println(coverage);
        System.out.println("create called......");

        if(coverages.contains(coverage.getCoverageName())){
            return "error";
        }
        coverages.add(coverage.getCoverageName());
        return "success";
    }
    @RequestMapping(value = "/coverages/delete",method = RequestMethod.POST)
    public  @ResponseBody String deleteCoverage(@RequestBody Coverage coverage){
        System.out.println(coverage);
        System.out.println("Delete called.........");
        coverages.remove(coverage.getCoverageName());
        return "success";
    }
    @RequestMapping(value = "/coverages/update",method = RequestMethod.POST)
    public  @ResponseBody String updateCoverage(@RequestBody Coverage coverage){
        System.out.println(coverage);
        System.out.println("Update called.........");
        if(coverages.contains(coverage.getCoverageName())){
            return "ERROR";
        }
        coverages.add(coverage.getCoverageName());
        return "success";
    }
}
