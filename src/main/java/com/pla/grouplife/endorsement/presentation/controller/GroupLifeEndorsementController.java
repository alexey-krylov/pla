package com.pla.grouplife.endorsement.presentation.controller;

import com.pla.grouplife.endorsement.application.command.GLCreateEndorsementCommand;
import com.pla.grouplife.endorsement.application.service.GLEndorsementService;
import com.pla.grouplife.endorsement.presentation.dto.SearchGLEndorsementDto;
import com.pla.grouplife.endorsement.query.GLEndorsementFinder;
import com.pla.grouplife.sharedresource.dto.GLPolicyDetailDto;
import com.pla.grouplife.sharedresource.dto.SearchGLPolicyDto;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.sharedkernel.domain.model.EndorsementNumber;
import com.pla.sharedkernel.identifier.EndorsementId;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import static org.nthdimenzion.presentation.AppUtils.getLoggedInUserDetail;

/**
 * Created by Samir on 8/4/2015.
 */
@Controller
@RequestMapping(value = "/grouplife/endorsement")
public class GroupLifeEndorsementController {

    @Autowired
    private GLEndorsementService glEndorsementService;

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private GLEndorsementFinder glEndorsementFinder;


    @RequestMapping(value = "/openpolicysearchpage", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/endorsement/searchPolicy");
        modelAndView.addObject("searchCriteria", new SearchGLPolicyDto());
        return modelAndView;
    }

    @RequestMapping(value = "/searchpolicy", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView searchPolicy(SearchGLPolicyDto searchGLPolicyDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/endorsement/searchPolicy");
        modelAndView.addObject("searchCriteria", new SearchGLPolicyDto());
        List<GLPolicyDetailDto> policyDetailDtos = glEndorsementService.searchPolicy(searchGLPolicyDto);
        modelAndView.addObject("searchResult", policyDetailDtos);
        return modelAndView;
    }

    @RequestMapping(value = "/downloadtemplatebyendorsementtype/{endorsementType}/{endorsementId}", method = RequestMethod.GET)
    public void downloadTemplateByEndorsementType(@PathVariable("endorsementType") GLEndorsementType glEndorsementType, @PathVariable("endorsementId") String endorsementId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        String fileName = glEndorsementType.getDescription().replaceAll("[\\s]*", "");
        response.setHeader("content-disposition", "attachment; filename=" + (fileName + "_Template.xls") + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook planDetailExcel = glEndorsementService.generateEndorsementExcel(glEndorsementType, new EndorsementId(endorsementId));
        planDetailExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/opencreateendorsementpage", method = RequestMethod.GET)
    public ResponseEntity createEndorsement(@RequestParam(value = "policyId", required = true) String policyId, @RequestParam(value = "endorsementType", required = true) GLEndorsementType endorsementType, HttpServletRequest request) {
        UserDetails userDetails = getLoggedInUserDetail(request);
        GLCreateEndorsementCommand glCreateEndorsementCommand = new GLCreateEndorsementCommand(userDetails, endorsementType, policyId);
        String endorsementId = commandGateway.sendAndWait(glCreateEndorsementCommand);
        return new ResponseEntity(Result.success("Endorsement successfully created", endorsementId), HttpStatus.OK);
    }


    @RequestMapping(value = "/editEndorsement", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To open edit proposal page")
    private ModelAndView openCreateEndorsement() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/endorsement/createEndorsement");
        return modelAndView;
    }

    @RequestMapping(value = "/getendorsementnumber/{endorsementId}", method = RequestMethod.GET)
    public Result getEndorsementNumber(@PathVariable("endorsementId") String endorsementId) {
        Map endorsmentMap = glEndorsementFinder.findEndorsementById(endorsementId);
        return Result.success("Endorsement number ", endorsmentMap.get("endorsementNumber") != null ? ((EndorsementNumber) endorsmentMap.get("endorsementNumber")).getEndorsementNumber() : "");
    }

    @RequestMapping(value = "/opensearchendorsement", method = RequestMethod.GET)
    public ModelAndView openSearchEndorsementPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/endorsement/searchEndorsement");
        SearchGLEndorsementDto searchGLEndorsementDto = new SearchGLEndorsementDto();
        searchGLEndorsementDto.setEndorsementTypes(GLEndorsementType.getAllEndorsementType());
        modelAndView.addObject("searchCriteria", searchGLEndorsementDto);
        return modelAndView;
    }
    @RequestMapping(value = "/openapprovalendorsement", method = RequestMethod.GET)
    public ModelAndView gotoApprovalEndorsementPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/endorsement/viewApprovalEndorsement");
        SearchGLEndorsementDto searchGLEndorsementDto = new SearchGLEndorsementDto();
        searchGLEndorsementDto.setEndorsementTypes(GLEndorsementType.getAllEndorsementType());

        modelAndView.addObject("searchCriteria", searchGLEndorsementDto);

        return modelAndView;
    }

    @RequestMapping(value = "/searchEndorsementApprovalpolicy", method = RequestMethod.POST)
    public ModelAndView searchEndorsementPolicy(SearchGLEndorsementDto searchGLEndorsementDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/endorsement/viewApprovalEndorsement");
        modelAndView.addObject("searchCriteria", searchGLEndorsementDto);
        searchGLEndorsementDto.setEndorsementTypes(GLEndorsementType.getAllEndorsementType());
        modelAndView.addObject("searchResult", glEndorsementService.searchEndorsement(searchGLEndorsementDto, new String[]{"APPROVER_PENDING_ACCEPTANCE", "UNDERWRITER_LEVEL1_PENDING_ACCEPTANCE", "UNDERWRITER_LEVEL2_PENDING_ACCEPTANCE"}));
        return modelAndView;
    }



    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ModelAndView searchEndorsement(SearchGLEndorsementDto searchGLEndorsementDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/endorsement/searchEndorsement");
        modelAndView.addObject("searchCriteria", searchGLEndorsementDto);
        searchGLEndorsementDto.setEndorsementTypes(GLEndorsementType.getAllEndorsementType());

        modelAndView.addObject("searchResult", glEndorsementService.searchEndorsement(searchGLEndorsementDto, new String[]{"DRAFT", "APPROVER_PENDING_ACCEPTANCE", "UNDERWRITER_LEVEL1_PENDING_ACCEPTANCE", "UNDERWRITER_LEVEL2_PENDING_ACCEPTANCE"}));
        return modelAndView;
    }
}
