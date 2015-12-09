package com.pla.core.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by Rudra on 12/7/2015.
 */
@Controller
@RequestMapping(value="/core/paypoint")
public class PayPointController {

    @RequestMapping(value ="/create", method = RequestMethod.GET)
    public ModelAndView createPayPoint() {
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("pla/core/paypoint/createpaypoint");
        return modelAndView;
    }


    @RequestMapping(value="/update",method=RequestMethod.GET)
    public ModelAndView update(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("pla/core/paypoint/createpaypoint");
        return modelAndView;
    }

    @RequestMapping(value ="/search" , method=RequestMethod.GET)
    public ModelAndView search(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("pla/core/paypoint/searchpaypoint");
        return modelAndView;
    }

    @RequestMapping(value ="/view" , method=RequestMethod.GET)
    public ModelAndView view(){
        ModelAndView modelAndView=new ModelAndView();
        modelAndView.setViewName("pla/core/paypoint/createpaypoint");
        return modelAndView;
    }

}

