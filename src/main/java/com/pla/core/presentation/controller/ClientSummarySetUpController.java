package com.pla.core.presentation.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by ASUS on 13-Oct-15.
 */

@Controller
@RequestMapping(value = "/core/clientsummary")
public class ClientSummarySetUpController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientSummarySetUpController.class);

    @RequestMapping(value = "/individualclientsummary",method = RequestMethod.GET)
    public ModelAndView openViewPageIndividual(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/clientSummary/individual/viewIndividualClientSummary");
        return modelAndView;
    }

    @RequestMapping(value = "/groupclientsummary",method = RequestMethod.GET)
    public ModelAndView openViewPageGroup(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/clientSummary/group/viewGroupClientSummary");
        return modelAndView;
    }

    @RequestMapping(value = "/opencreateindividualclientsummary",method = RequestMethod.GET)
    public ModelAndView openCreatePage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/clientSummary/individual/viewUpdateClientSummary");
        return modelAndView;
    }

    @RequestMapping(value = "/opencreategrouplclientsummary",method = RequestMethod.GET)
    public ModelAndView openCreatePage2(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/clientSummary/group/viewUpdateClientSummary");
        return modelAndView;
    }

}
