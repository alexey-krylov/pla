/*
 * Copyright (c) 3/30/15 9:06 AM .NthDimenzion,Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package com.pla.core.presentation.controller;

import com.pla.core.application.exception.PremiumTemplateParseException;
import com.pla.core.application.plan.premium.CreatePremiumCommand;
import com.pla.core.application.plan.premium.PremiumTemplateDto;
import com.pla.core.application.service.plan.premium.PremiumService;
import com.pla.core.domain.model.plan.Plan;
import com.pla.core.repository.PlanRepository;
import com.pla.sharedkernel.domain.model.PremiumInfluencingFactor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    private PremiumService premiumService;

    private CommandGateway commandGateway;

    private PlanRepository planRepository;

    private static final String PREMIUM_TEMPLATE_FILE_NAME_SUFFIX = "_PremiumTemplate.xls";

    @Autowired
    public PremiumSetUpController(PremiumService premiumService, CommandGateway commandGateway, PlanRepository planRepository) {
        this.premiumService = premiumService;
        this.commandGateway = commandGateway;
        this.planRepository = planRepository;
    }

    @RequestMapping(value = "/getallpremium", method = RequestMethod.GET)
    @ResponseBody
    public List<Map> findAllPremium() {
        return premiumService.getAllPremium();
    }

    @RequestMapping(value = "/getpremiuminfluencingfactors", method = RequestMethod.GET)
    @ResponseBody
    public PremiumInfluencingFactor[] getPremiumInfluencingFactor() {
        return PremiumInfluencingFactor.values();
    }

    /*@RequestMapping(value = "/downloadpremiumtemplate", method = RequestMethod.POST)
    public void downloadPremiumTemplate(@RequestBody PremiumTemplateDto premiumTemplateDto, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        Plan plan = planRepository.findByPlanId(premiumTemplateDto.getPlanId());
        String templateFileName = plan.getPlanDetail().getPlanName() + PREMIUM_TEMPLATE_FILE_NAME_SUFFIX;
        response.setHeader("content-disposition", "attachment; filename=" + templateFileName + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook premiumTemplateWorkbook = premiumService.generatePremiumExcelTemplate(premiumTemplateDto.getPremiumInfluencingFactors(),
                premiumTemplateDto.getPlanId(), premiumTemplateDto.getCoverageId());
        premiumTemplateWorkbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }
*/
    @RequestMapping(value = "/downloadpremiumtemplate", method = RequestMethod.GET)
    public void downloadPremiumTemplate(PremiumTemplateDto premiumTemplateDto, HttpServletRequest request, HttpServletResponse response) throws IOException {
        premiumTemplateDto = new PremiumTemplateDto();
        premiumTemplateDto.setPlanId("551bd46c88604220872bb22b");
        premiumTemplateDto.setPremiumInfluencingFactors(new PremiumInfluencingFactor[]{PremiumInfluencingFactor.SUM_ASSURED, PremiumInfluencingFactor.POLICY_TERM, PremiumInfluencingFactor.AGE});
        response.reset();
        response.setContentType("application/msexcel");
        Plan plan = planRepository.findByPlanId(premiumTemplateDto.getPlanId());
        String templateFileName = plan.getPlanDetail().getPlanName() + PREMIUM_TEMPLATE_FILE_NAME_SUFFIX;
        response.setHeader("content-disposition", "attachment; filename=" + templateFileName + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook premiumTemplateWorkbook = premiumService.generatePremiumExcelTemplate(premiumTemplateDto.getPremiumInfluencingFactors(),
                premiumTemplateDto.getPlanId(), premiumTemplateDto.getCoverageId());
        premiumTemplateWorkbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }


    @RequestMapping(value = "/verifypremiumdata", method = RequestMethod.POST)
    @ResponseBody
    public Result validatePremiumData(PremiumTemplateDto premiumTemplateDto, HttpServletRequest servletRequest, HttpServletResponse response) throws IOException {
        Plan plan = planRepository.findByPlanId(premiumTemplateDto.getPlanId());
        String templateFileName = plan.getPlanDetail().getPlanName() + PREMIUM_TEMPLATE_FILE_NAME_SUFFIX;
        MultipartFile file = premiumTemplateDto.getFile();
        if (!("application/msexcel".equals(file.getContentType()) || "application/vnd.ms-excel".equals(file.getContentType())) && !templateFileName.equals(file.getOriginalFilename())) {
            return Result.failure("Uploaded file is not valid premium template");
        }
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        HSSFWorkbook premiumTemplateWorkbook = new HSSFWorkbook(fs);
        try {
            boolean isValidTemplate = premiumService.validatePremiumTemplateData(premiumTemplateWorkbook, premiumTemplateDto.getPremiumInfluencingFactors(), premiumTemplateDto.getPlanId(), premiumTemplateDto.getCoverageId());
            if (isValidTemplate) {
                return Result.success("Premium Template is valid");
            }
            response.reset();
            response.setContentType("application/msexcel");
            response.setHeader("content-disposition", "attachment; filename=" + "premiumTemplate.xls" + "");
            OutputStream outputStream = response.getOutputStream();
            premiumTemplateWorkbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (PremiumTemplateParseException exception) {
            return Result.failure(exception.getMessage());
        }
        return Result.success("Verified the file");
    }

    @RequestMapping(value = "/uploadpremiumdata", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadPremiumData(CreatePremiumCommand createPremiumCommand, HttpServletRequest servletRequest, HttpServletResponse response) throws IOException {
        MultipartFile file = createPremiumCommand.getFile();
        if ("application/msexcel".equals(file.getContentType())) {
            return Result.failure("Uploaded file is not valid excel");
        }
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        HSSFWorkbook premiumTemplateWorkbook = new HSSFWorkbook(fs);
        try {
            boolean isValidTemplate = premiumService.validatePremiumTemplateData(premiumTemplateWorkbook, createPremiumCommand.getPremiumInfluencingFactors(), createPremiumCommand.getPlanId(), createPremiumCommand.getCoverageId());
            if (!isValidTemplate) {
                return Result.failure("Premium Template is not valid");
            } else {
                List<Map<Map<PremiumInfluencingFactor, String>, Double>> premiumLineItem = premiumService.parsePremiumTemplate(premiumTemplateWorkbook, createPremiumCommand.getPremiumInfluencingFactors(), createPremiumCommand.getPlanId(), createPremiumCommand.getCoverageId());
                createPremiumCommand.setPremiumLineItem(premiumLineItem);
                commandGateway.send(createPremiumCommand);
            }

        } catch (PremiumTemplateParseException exception) {
            return Result.failure(exception.getMessage());
        } catch (Exception e) {
            return Result.failure(e.getMessage());
        }
        return Result.success("Premiums uploaded successfully");
    }
}
