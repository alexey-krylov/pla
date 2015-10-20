package com.pla.individuallife.endorsement.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by ASUS on 16-Oct-15.
 */
@Controller
@RequestMapping(value = "/individuallife/endorsement")
public class ILEndorsementController {
    @RequestMapping(value = "/createendorsement",method = RequestMethod.GET)
    public ModelAndView openViewPageGroup(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/endorsement/createEndorsement");
        return modelAndView;
    }
}
