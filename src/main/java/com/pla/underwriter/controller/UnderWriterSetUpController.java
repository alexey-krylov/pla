package com.pla.underwriter.controller;

import com.google.common.collect.Lists;
import com.pla.publishedlanguage.dto.PlanCoverageDetailDto;
import com.pla.sharedkernel.identifier.PlanId;
import com.pla.underwriter.application.CreateUnderWriterDocumentCommand;
import com.pla.underwriter.application.CreateUnderWriterRoutingLevelCommand;
import com.pla.underwriter.domain.model.UnderWriterInfluencingFactor;
import com.pla.underwriter.dto.UnderWritingRouterDto;
import com.pla.underwriter.exception.UnderWriterTemplateParseException;
import com.pla.underwriter.finder.UnderWriterFinder;
import com.pla.underwriter.service.UnderWriterService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUserDetail;

/**
 * Created by Admin on 5/11/2015.
 */
@Controller
@RequestMapping(value = "/underwriter")
public class UnderWriterSetUpController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnderWriterSetUpController.class);
    private static final String UNDER_WRITER_TEMPLATE_FILE_NAME_SUFFIX = "-UnderWritingTemplate.xls";
    private UnderWriterService underWriterService;
    private CommandGateway commandGateway;
    private UnderWriterFinder underWriterFinder;

    @Autowired
    public UnderWriterSetUpController(UnderWriterService underWriterService, CommandGateway commandGateway, UnderWriterFinder underWriterFinder){
        this.underWriterService = underWriterService;
        this.commandGateway = commandGateway;
        this.underWriterFinder = underWriterFinder;
    }


    /*
    *       ========== thymeleaf page routing starts ===========
    * */
    @RequestMapping(value = "/viewroutinglevel",method = RequestMethod.GET)
    public String viewRoutingLevelSetup(){
        return "pla/core/underwriter/routingLevelSetup/viewRoutingLevelSetup";
    }

    @RequestMapping(value = "/opencreateroutinglevel",method = RequestMethod.GET)
    public ModelAndView openCreatePage( ){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("createUnderWriterRoutingLevelCommand", new CreateUnderWriterRoutingLevelCommand());
        modelAndView.setViewName("pla/core/underwriter/routingLevelSetup/createRoutingLevelSetup");
        return modelAndView;
    }

    @RequestMapping(value = "/viewdocumentsetup",method = RequestMethod.GET)
    public String viewDocumentSetup(){
        return "pla/core/underwriter/documentSetup/viewDocumentSetup";
    }


    @RequestMapping(value = "/opencreatedocumentsetup",method = RequestMethod.GET)
    public ModelAndView openCreatePageForDocument( ) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("CreateUnderWriterDocumentCommand", new CreateUnderWriterDocumentCommand());
        modelAndView.setViewName("pla/core/underwriter/documentSetup/createDocumentSetup");
        return modelAndView;
    }

    /*
     *   ========== thymeleaf page routing ends ===========
     * */



    @RequestMapping(value = "/getplancoveragedetail",method = RequestMethod.GET)
    @ResponseBody
    public List<PlanCoverageDetailDto> getAllPlanCoverageDetail(){
        return underWriterService.getPlanCoverageDetail();
    }

    @RequestMapping(value = "/getoptionalcoverage/{planId}",method = RequestMethod.GET)
    @ResponseBody
    public List<PlanCoverageDetailDto> getOptionalCoverageFor(@PathVariable("planId") PlanId planId){
        return underWriterService.getAllOptionalCoverageFor(Lists.newArrayList(planId));
    }


    @RequestMapping(value = "/checkforoverlapping",method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity isRowValid(@RequestBody CreateUnderWriterDocumentCommand createUnderWriterDocumentCommand,  BindingResult bindingResult,HttpServletRequest request){
        if (bindingResult.hasErrors()) {
            return new ResponseEntity( bindingResult.getAllErrors(), HttpStatus.PRECONDITION_FAILED);
        }
        try{
            List<String> errorMessageBuilder = Lists.newArrayList();
            boolean isValid = underWriterService.validateTheUnderWriterDocument(createUnderWriterDocumentCommand.getPlanCode(),createUnderWriterDocumentCommand.getCoverageId(),createUnderWriterDocumentCommand.getUnderWriterDocumentItems(),errorMessageBuilder);
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
            underWriterService.checkValidPlanAndCoverageCode(createUnderWriterDocumentCommand.getPlanCode());
            boolean isValid = underWriterService.validateTheUnderWriterDocumentData(createUnderWriterDocumentCommand.getPlanCode(),createUnderWriterDocumentCommand.getCoverageId(),createUnderWriterDocumentCommand.getUnderWriterDocumentItems(),errorMessageBuilder);
            if (!isValid){
                return new ResponseEntity(errorMessageBuilder, HttpStatus.PRECONDITION_FAILED);
            }
            UserDetails userDetails = getLoggedInUserDetail(request);
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
        HSSFWorkbook premiumTemplateWorkbook = underWriterService.generateUnderWriterExcelTemplate(underWritingRouterDto.getUnderWriterInfluencingFactors(),
                underWritingRouterDto.getPlanName());
        premiumTemplateWorkbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        response.flushBuffer();
    }

    @RequestMapping(value = "/uploadunderwriterroutingleveltemplate", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView uploadUnderwriterRoutingLevel(@RequestBody CreateUnderWriterRoutingLevelCommand createUnderWriterRoutingLevelCommand, BindingResult bindingResult, HttpServletRequest request,
                                                      HttpServletResponse response) throws IOException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/core/underwriter/routingLevelSetup/createRoutingLevelSetup");
        if (bindingResult.hasErrors()) {
            return modelAndView;
        }
        MultipartFile file = createUnderWriterRoutingLevelCommand.getFile();
        POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
        HSSFWorkbook premiumTemplateWorkbook = new HSSFWorkbook(fs);
        try {
            String templateFileName = createUnderWriterRoutingLevelCommand.getPlanName()+ UNDER_WRITER_TEMPLATE_FILE_NAME_SUFFIX;
            templateFileName = templateFileName.replaceAll("[\\s]*", "").trim();
            if (!("application/msexcel".equals(createUnderWriterRoutingLevelCommand.getFile().getContentType()) || "application/vnd.ms-excel".equals(file.getContentType())) && !templateFileName.equals(file.getOriginalFilename())) {
                bindingResult.addError(new ObjectError("message", "Uploaded file is not valid excel"));
                return modelAndView;
            }
            boolean isValidTemplate = underWriterService.isValidUnderWriterRoutingLevelTemplate(premiumTemplateWorkbook,createUnderWriterRoutingLevelCommand.getPlanCode(),createUnderWriterRoutingLevelCommand.getCoverageId(), createUnderWriterRoutingLevelCommand.getUnderWriterInfluencingFactors());
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
                UserDetails userDetails = getLoggedInUserDetail(request);
                createUnderWriterRoutingLevelCommand.setUserDetails(userDetails);
                List<Map<Object,Map<String,Object>>>  underWriterRoutingLevelDataFromExcel = underWriterService.parseUnderWriterExcelTemplate(premiumTemplateWorkbook, createUnderWriterRoutingLevelCommand.getUnderWriterInfluencingFactors());
                createUnderWriterRoutingLevelCommand.setUnderWriterDocumentItem(underWriterRoutingLevelDataFromExcel);
                commandGateway.sendAndWait(createUnderWriterRoutingLevelCommand);
                modelAndView.setViewName("redirect:viewroutinglevel");
            }
        }
        catch (UnderWriterTemplateParseException e){
            modelAndView.addObject("message", e.getMessage());
        }
        catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("message", e.getMessage());
        }
        return modelAndView;
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

    @RequestMapping(value = "/getunderwriterprocess",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,String>> getUnderWriterProcessType(){
        return underWriterService.getUnderWriterProcess();
    }

    @RequestMapping(value = "/getunderwriterinfluencingfactor/{processtype}",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,String>> getUnderWriterInfluencingFactor(@PathVariable("processtype") String processType){
        return underWriterService.getUnderWritingInfluencingFactor(processType);
    }

    @RequestMapping(value = "/getinfluencingfactorrange/{influencingFactors}",method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String,Object>> getUnderWriterInfluencingFactorRange(@PathVariable("influencingFactors") List<UnderWriterInfluencingFactor> underWriterInfluencingFactors){
        return underWriterService.getInfluencingFactorRange(underWriterInfluencingFactors);
    }

    @RequestMapping(value = "/getdocumentapprovedbyprovider", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String, Object>> getAllDocumentApprovedByServiceProvider() {
        return underWriterFinder.getAllDocumentApprovedByServiceProvider();
    }
}
