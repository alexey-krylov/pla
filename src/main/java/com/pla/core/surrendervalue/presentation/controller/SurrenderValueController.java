package com.pla.core.surrendervalue.presentation.controller;

import com.pla.core.surrendervalue.domain.model.PolicyYearExpression;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * Created by ak on 4/2/2016.
 */

@Controller
@RequestMapping(value="/core/surrendervalue")

public class SurrenderValueController {

    @RequestMapping(value = "/getpolicyexpressiontypes", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> getClaimType() {
        return PolicyYearExpression.getPolicyYearExpression();
    }

}
