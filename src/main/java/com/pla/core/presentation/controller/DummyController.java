package com.pla.core.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by ASUS on 27-Apr-15.
 */
@Controller
@RequestMapping(value = "/proposal/individualLife")
public class DummyController {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView proposal() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/proposal/individualLife/createProposal/index");
        return modelAndView;
    }

    @RequestMapping(value = "/getPage/{pageName}", method = RequestMethod.GET)
    public ModelAndView proposal(@PathVariable("pageName") String pageName) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/proposal/individualLife/createProposal/"+pageName);
        return modelAndView;
    }
}
