package com.pla.underwriter.controller;

import com.google.common.collect.Lists;
import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.underwriter.application.CreateUnderWriterDocumentCommand;
import com.pla.underwriter.application.CreateUnderWriterRoutingLevelCommand;
import com.pla.underwriter.dto.UnderWritingRouterDto;
import com.pla.underwriter.exception.UnderWriterTemplateParseException;
import com.pla.underwriter.finder.UnderWriterFinder;
import com.pla.underwriter.service.UnderWritingService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUSerDetail;

/**
 * Created by Admin on 5/11/2015.
 */
@Controller
@RequestMapping(value = "/underwriter")
public class UnderWriterSetUpController {

    private UnderWritingService underWritingService;
    private CommandGateway commandGateway;
    private UnderWriterFinder underWriterFinder;
    private static final Logger LOGGER = LoggerFactory.getLogger(UnderWriterSetUpController.class);

    private static final String UNDER_WRITER_TEMPLATE_FILE_NAME_SUFFIX = "-UnderWritingTemplate.xls";

    @Autowired
    public UnderWriterSetUpController(UnderWritingService underWritingService, CommandGateway commandGateway, UnderWriterFinder underWriterFinder){
        this.underWritingService = underWritingService;
        this.commandGateway = commandGateway;
        this.underWriterFinder = underWriterFinder;
    }

    @RequestMapping(value = "/getplancoveragedetail",method = RequestMethod.GET)
    @ResponseBody
    public List<PlanCoverageDetailDto> getAllPlanCoverageDetail(){
        return underWritingService.getPlanCoverageDetail();
    }

    @RequestMapping(value = "/getoptionalcoverage/{planId}")
    @ResponseBody
    public List<PlanCoverageDetailDto> getOptionalCoverageFor(@PathVariable("planId") PlanId planId){
        return underWritingService.getAllOptionalCoverageFor(Lists.newArrayList(planId));
    }

    @RequestMapping(value = "/checkforoverlapping",method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity isRowValid(@RequestBody CreateUnderWriterDocumentCommand createUnderWriterDocumentCommand,  BindingResult bindingResult,HttpServletRequest request){
        if (bindingResult.hasErrors()) {
            return new ResponseEntity( bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        try{
            List<String> errorMessageBuilder = Lists.newArrayList();
            boolean isValid = underWritingService.validateTheUnderWriterDocument(createUnderWriterDocumentCommand.getPlanCode(),createUnderWriterDocumentCommand.getCoverageId(),createUnderWriterDocumentCommand.getUnderWriterDocumentItems(),errorMessageBuilder);
            if (!isValid){
                return new ResponseEntity(errorMessageBuilder, HttpStatus.PRECONDITION_FAILED);
            }
        }catch (UnderWriterTemplateParseException e){
            return new ResponseEntity(e.getMessage(), HttpStatus.PRECONDITION_FAILED);
        }
        catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.PRECONDITION_FAILED);
        }
        return new ResponseEntity("Row got created successfully", HttpStatus.OK);
    }

    @RequestMapping(value = "/create/underwriterdocument",method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity createUnderWriterDocument(@RequestBody CreateUnderWriterDocumentCommand createUnderWriterDocumentCommand,  BindingResult bindingResult,HttpServletRequest request){
        if (bindingResult.hasErrors()) {
            return new ResponseEntity( bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        try {
            List<String> errorMessageBuilder = Lists.newArrayList();
            underWritingService.checkValidPlanAndCoverageCode(createUnderWriterDocumentCommand.getPlanCode());
            boolean isValid = underWritingService.validateTheUnderWriterDocumentData(createUnderWriterDocumentCommand.getPlanCode(),createUnderWriterDocumentCommand.getCoverageId(),createUnderWriterDocumentCommand.getUnderWriterDocumentItems(),errorMessageBuilder);
            if (!isValid){
                return new ResponseEntity(errorMessageBuilder, HttpStatus.PRECONDITION_FAILED);
            }
            UserDetails userDetails = getLoggedInUSerDetail(request);
            createUnderWriterDocumentCommand.setUserDetails(userDetails);
            commandGateway.sendAndWait(createUnderWriterDocumentCommand);
        }catch (UnderWriterTemplateParseException e){
            return new ResponseEntity(e.getMessage(), HttpStatus.PRECONDITION_FAILED);
        }
        catch (Exception e) {
            LOGGER.error("Error in creating Under Writing document", e);
            return new ResponseEntity(e.getMessage(), HttpStatus.PRECONDITION_FAILED);
        }
        return new ResponseEntity("Under Writing document created successfully", HttpStatus.OK);
    }

    @RequestMapping(value = "/downloadunderwritingtemplate", method = RequestMethod.POST)
    public void downloadUnderWritingTemplate(@RequestBody UnderWritingRouterDto underWritingRouterDto, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        String templateFileName = underWritingRouterDto.getPlanName()+ UNDER_WRITER_TEMPLATE_FILE_NAME_SUFFIX;
        templateFileName = templateFileName.replaceAll("[\\s]*", "").trim();
        response.setHeader("content-disposition", "attachment; filename=" + templateFileName + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook premiumTemplateWorkbook = underWritingService.generateUnderWriterExcelTemplate(underWritingRouterDto.getUnderWriterInfluencingFactors(),
                underWritingRouterDto.getPlanName());
        premiumTemplateWorkbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        response.flushBuffer();
    }

    @RequestMapping(value = "/uploadunderwriterroutingleveltemplate", method = RequestMethod.POST)
    @ResponseBody
    public Result uploadUnderwriterRoutingLevel(@RequestBody CreateUnderWriterRoutingLevelCommand createUnderWriterRoutingLevelCommand, BindingResult bindingResult, HttpServletRequest request,
                                                HttpServletResponse response) throws IOException {
        if (bindingResult.hasErrors()) {
            return Result.failure();
        }
        FileInputStream fis = new FileInputStream(createUnderWriterRoutingLevelCommand.getFile().getName());
        POIFSFileSystem fs = new POIFSFileSystem(fis);
        HSSFWorkbook premiumTemplateWorkbook = new HSSFWorkbook(fs);
        try {

            String templateFileName = createUnderWriterRoutingLevelCommand.getPlanName()+ UNDER_WRITER_TEMPLATE_FILE_NAME_SUFFIX;
            templateFileName = templateFileName.replaceAll("[\\s]*", "").trim();
            boolean isValidTemplate = underWritingService.isValidUnderWriterRoutingLevelTemplate(premiumTemplateWorkbook,createUnderWriterRoutingLevelCommand.getPlanCode(),createUnderWriterRoutingLevelCommand.getCoverageId(), createUnderWriterRoutingLevelCommand.getUnderWriterInfluencingFactors());
            if (!isValidTemplate) {
                response.reset();
                response.setContentType("application/msexcel");
                response.setHeader("content-disposition", "attachment; filename=" + templateFileName + "");
                OutputStream outputStream = response.getOutputStream();
                premiumTemplateWorkbook.write(outputStream);
                outputStream.flush();
                outputStream.close();
            }
            else {
                UserDetails userDetails = getLoggedInUSerDetail(request);
                createUnderWriterRoutingLevelCommand.setUserDetails(userDetails);
                List<Map<Object,Map<String,Object>>>  underWriterRoutingLevelDataFromExcel = underWritingService.parseUnderWriterExcelTemplate(premiumTemplateWorkbook, createUnderWriterRoutingLevelCommand.getUnderWriterInfluencingFactors());
                createUnderWriterRoutingLevelCommand.setUnderWriterDocumentItem(underWriterRoutingLevelDataFromExcel);
                commandGateway.sendAndWait(createUnderWriterRoutingLevelCommand);
            }
        }
        catch (UnderWriterTemplateParseException e){
            return Result.failure(e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            return Result.failure("Error in Creating Under Writer Routing Level");
        }
        return Result.success("Under Writer Routing Level Created Successfully");
    }

    @RequestMapping(value = "/getalldocument",method = RequestMethod.GET)
    @ResponseBody
    public List<Map> getAllUnderWriterDocument(){
        return underWriterFinder.findAllUnderWriterDocument();
    }

    @RequestMapping(value = "/getallunderwritingRoutinglevel",method = RequestMethod.GET)
    @ResponseBody
    public List<Map> getAllUnderWriterRoutingLevel(){
        return underWriterFinder.findAllUnderWriterRoutingLevel();
    }
}
