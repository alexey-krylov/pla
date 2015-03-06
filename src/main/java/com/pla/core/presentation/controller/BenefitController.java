package com.pla.core.presentation.controller;

import com.google.common.collect.Lists;
import com.pla.core.presentation.dto.Benefit;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by ASUS on 02-Mar-15.
 */
@Controller
@RequestMapping(value = "/core")
public class BenefitController {

    List benefits = Lists.newArrayList("Death Benefit","Accidental Death Benefit","CI Benefit");



    @RequestMapping(value = "/benefits/view",method = RequestMethod.GET)
    public String viewBenefits(@RequestParam(value = "page",required = false) Integer pageNumber,Model model){
        if(null!=pageNumber){
            model.addAttribute("benefits",benefits.subList(pageNumber-1,pageNumber+1));
        }else {
            model.addAttribute("benefits",benefits);
        }

        return "pla/core/viewBenefit";
    }

    @RequestMapping(value = "/benefits/create",method = RequestMethod.POST)
    public  @ResponseBody String createBenefit(@RequestBody Benefit benefit){
        System.out.println(benefit);
        System.out.println("create called......");
        if(benefits.contains(benefit.getBenefitName())){
            return "error";
        }
        benefits.add(benefit.getBenefitName());
        return "success";
    }

    @RequestMapping(value = "/benefits/update",method = RequestMethod.POST)
    public  @ResponseBody String updateBenefit(@RequestBody Benefit benefit){
        System.out.println(benefit);
        System.out.println("Update called.........");
        if(benefits.contains(benefit.getBenefitName())){
            return "error";
        }
        benefits.add(benefit.getBenefitName());
        return "success";
    }

    @RequestMapping(value = "/benefits/delete",method = RequestMethod.POST)
    public  @ResponseBody String deleteBenefit(@RequestBody Benefit benefit){
        System.out.println(benefit);
        System.out.println("Delete called.........");
        benefits.remove(benefit.getBenefitName());
        return "success";
    }

    @RequestMapping(value = "/getTotal",method = RequestMethod.GET)
    public @ResponseBody int getTotal(){
        return benefits.size();
    }
}
