package com.pla.core.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by ASUS on 28-Aug-15.
 */
@Controller
@RequestMapping(value = "/core/productclaimmap")
public class ProductClaimSetUpController {


    @RequestMapping(value = "/opencreateproductclaim",method = RequestMethod.GET)
    public ModelAndView openCreatePage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/productclaim/createProductClaim");
        return modelAndView;
    }

    @RequestMapping(value = "/openviewproductclaim",method = RequestMethod.GET)
    public ModelAndView openViewPage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/productclaim/viewProductClaim");
        return modelAndView;
    }


}
