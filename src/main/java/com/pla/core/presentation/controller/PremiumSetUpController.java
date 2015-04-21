/*
 * Copyright (c) 3/30/15 9:06 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.presentation.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pla.core.application.plan.premium.CreatePremiumCommand;
import com.pla.core.application.plan.premium.PremiumTemplateDto;
import com.pla.core.application.service.plan.premium.PremiumService;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.repository.PlanRepository;
import com.pla.publishedlanguage.domain.model.PremiumInfluencingFactor;
import com.pla.sharedkernel.identifier.PlanId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author: Samir
 * @since 1.0 30/03/2015
 */
@Controller
@RequestMapping(value = "/core/premium")
public class PremiumSetUpController {

    private static final String PREMIUM_TEMPLATE_FILE_NAME_SUFFIX = "_PremiumTemplate.xls";
    private PremiumService premiumService;
    private CommandGateway commandGateway;
    private PlanRepository planRepository;

    @Autowired
    public PremiumSetUpController(PremiumService premiumService, CommandGateway commandGateway, PlanRepository planRepository) {
        this.premiumService = premiumService;
        this.commandGateway = commandGateway;
        this.planRepository = planRepository;
    }

    @RequestMapping(value = "/listpremium", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView viewPremiums() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/premium/viewPremium");
        modelAndView.addObject("listOfPremium", premiumService.getAllPremium());
        return modelAndView;
    }

    @RequestMapping(value = "/getallpremium", method = RequestMethod.GET)
    @ResponseBody
    public List<Map> findAllPremium() {
        return premiumService.getAllPremium();
    }

    @RequestMapping(value = "/getpremiuminfluencingfactors", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, String>> getPremiumInfluencingFactor() {
        List<Map<String, String>> premiumInfluencingFactors = Lists.newArrayList();
        for (PremiumInfluencingFactor premiumInfluencingFactor : PremiumInfluencingFactor.values()) {
            Map<String, String> premiumInfluencingFactorMap = Maps.newHashMap();
            premiumInfluencingFactorMap.put("code", premiumInfluencingFactor.name());
            premiumInfluencingFactorMap.put("description", premiumInfluencingFactor.getDescription());
            premiumInfluencingFactors.add(premiumInfluencingFactorMap);
        }
        return premiumInfluencingFactors;
    }

    @RequestMapping(value = "/createpremium", method = RequestMethod.GET)
    public ModelAndView openCreatePagePremium() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("createPremiumCommand", new CreatePremiumCommand());
        modelAndView.setViewName("pla/core/premium/createPremium");
        return modelAndView;
    }

    @RequestMapping(value = "/downloadpremiumtemplate", method = RequestMethod.POST)
    public void downloadPremiumTemplate(@RequestBody PremiumTemplateDto premiumTemplateDto, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        Plan plan = planRepository.findOne(new PlanId(premiumTemplateDto.getPlanId()));
        String templateFileName = plan.getPlanDetail().getPlanName() + PREMIUM_TEMPLATE_FILE_NAME_SUFFIX;
        response.setHeader("content-disposition", "attachment; filename=" + templateFileName + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook premiumTemplateWorkbook = premiumService.generatePremiumExcelTemplate(premiumTemplateDto.getPremiumInfluencingFactors(),
                premiumTemplateDto.getPlanId(), premiumTemplateDto.getCoverageId());
        premiumTemplateWorkbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/uploadpremiumdata", method = RequestMethod.POST)
    public String uploadPremiumData(@Valid @ModelAttribute("createPremiumCommand") CreatePremiumCommand createPremiumCommand, BindingResult bindingResult,
                                    HttpServletResponse response) throws IOException {
        MultipartFile file = createPremiumCommand.getFile();
        Plan plan = planRepository.findOne(new PlanId(createPremiumCommand.getPlanId()));
        String templateFileName = plan.getPlanDetail().getPlanName() + PREMIUM_TEMPLATE_FILE_NAME_SUFFIX;
        if (!("application/msexcel".equals(file.getContentType()) || "application/vnd.ms-excel".equals(file.getContentType())) && !templateFileName.equals(file.getOriginalFilename())) {
            bindingResult.addError(new ObjectError("message", "Uploaded file is not valid excel"));
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("pla/core/premium/createPremium");
            return "pla/core/premium/createPremium";
        }
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        HSSFWorkbook premiumTemplateWorkbook = new HSSFWorkbook(fs);
        try {
            boolean isValidTemplate = premiumService.validatePremiumTemplateData(premiumTemplateWorkbook, createPremiumCommand.getPremiumInfluencingFactors(), createPremiumCommand.getPlanId(), createPremiumCommand.getCoverageId());
            if (!isValidTemplate) {
                response.reset();
                response.setContentType("application/msexcel");
                response.setHeader("content-disposition", "attachment; filename=" + templateFileName + "");
                OutputStream outputStream = response.getOutputStream();
                premiumTemplateWorkbook.write(outputStream);
                outputStream.flush();
                outputStream.close();
            } else {
                List<Map<Map<PremiumInfluencingFactor, String>, Double>> premiumLineItem = premiumService.parsePremiumTemplate(premiumTemplateWorkbook, createPremiumCommand.getPremiumInfluencingFactors(), createPremiumCommand.getPlanId(), createPremiumCommand.getCoverageId());
                createPremiumCommand.setPremiumLineItem(premiumLineItem);
                commandGateway.send(createPremiumCommand);
            }

        } catch (Exception e) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("message", e.getMessage());
            return "pla/core/premium/createPremium";
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/premium/viewPremium");
        modelAndView.addObject("listOfPremium", premiumService.getAllPremium());
        return "redirect:/core/premium/viewPremium";
    }
}
