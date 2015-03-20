package com.pla.core.presentation.controller;

import com.pla.core.query.BenefitFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: pradyumna
 * @since 1.0 15/03/2015
 */
@Controller
@RequestMapping(value = "/core/plan")
public class PlanSetupController {

    private static final Logger LOGGER = LoggerFactory.getLogger(BenefitController.class);
    private BenefitFinder benefitFinder;

    @RequestMapping(value = "/plan/configuration", method = RequestMethod.GET)
    public
    @ResponseBody
    String viewBenefits() {
        return null;
    }

}
