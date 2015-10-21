package com.pla.individuallife.endorsement.presentation.controller;

import com.pla.core.query.MasterFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * Created by ASUS on 16-Oct-15.
 */
@Controller
@RequestMapping(value = "/individuallife/endorsement")
public class ILEndorsementController {
    @Autowired
    private MasterFinder masterFinder;

    @RequestMapping(value = "/createendorsement",method = RequestMethod.GET)
    public ModelAndView openViewPageGroup(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/endorsement/createEndorsement");
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getAllBankNames")
    @ResponseBody
    public List<Map<String, Object>> getAllBankNames() {
        return masterFinder.getAllBank();
    }
}
