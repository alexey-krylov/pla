package com.pla.core.application.agent;

import com.pla.core.query.AgentFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by pradyumna on 23-06-2015.
 */
@Controller
@RequestMapping(value = "/core/broker")
public class BrokerController {

    @Autowired
    private AgentFinder agentFinder;

    @RequestMapping(value = "/new")
    public Callable index() {
        return new Callable() {
            @Override
            public Object call() throws Exception {
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.setViewName("pla/core/agent/broker");
                return modelAndView;
            }
        };
    }

    @RequestMapping(value = "/list")
    public Callable list() {
        return new Callable() {
            @Override
            public Object call() throws Exception {
                ModelAndView modelAndView = new ModelAndView();
                List<Map<String, Object>> nonTerminatedBrokers = agentFinder.getAllNonTerminatedAgent();
                modelAndView.setViewName("pla/core/agent/broker-list");
                modelAndView.addObject("brokers", nonTerminatedBrokers);
                return modelAndView;
            }
        };
    }

    @RequestMapping(value = "/edit")
    public Callable editBroker() {
        return new Callable() {
            @Override
            public Object call() throws Exception {
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.setViewName("pla/core/agent/broker");
                return modelAndView;
            }
        };
    }

    @RequestMapping(value = "/view")
    public Callable viewBroker() {
        return new Callable() {
            @Override
            public Object call() throws Exception {
                ModelAndView modelAndView = new ModelAndView();
                modelAndView.setViewName("pla/core/agent/broker");
                return modelAndView;
            }
        };
    }

}
