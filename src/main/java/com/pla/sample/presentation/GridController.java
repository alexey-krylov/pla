package com.pla.sample.presentation;

import org.joda.time.LocalDate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Nthdimenzion
 */

@Controller
@RequestMapping(value = "/grid")
public class GridController {


    @RequestMapping(value = "/persons",method = RequestMethod.GET)
    public String getPersons(Model model){
        List<Person> personList = new ArrayList<Person>();
        for(int i=1;i<15;i++) {
            LocalDate joinDate = new LocalDate();
            joinDate=joinDate.plusWeeks(i*6);
            personList.add(new Person(i,"John " + i, "New York", "Branch Manager",joinDate,new BigDecimal(1290.23).multiply(new BigDecimal(i)),(i%2==0?true:false)));
        }
        model.addAttribute("persons", personList);
        return "pla/sample/grid";
    }

}
