package com.pla.grouplife.policy.presentation.controller;

import com.mongodb.gridfs.GridFSDBFile;
import com.pla.grouplife.policy.application.service.GLPolicyService;
import com.pla.grouplife.policy.presentation.dto.GLPolicyDetailDto;
import com.pla.grouplife.policy.presentation.dto.SearchGLPolicyDto;
import com.pla.grouplife.policy.query.GLPolicyFinder;
import com.pla.grouplife.proposal.presentation.dto.GLProposalMandatoryDocumentDto;
import com.pla.grouplife.sharedresource.dto.AgentDetailDto;
import com.pla.grouplife.sharedresource.dto.PremiumDetailDto;
import com.pla.grouplife.sharedresource.dto.ProposerDto;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.identifier.PolicyId;
import com.wordnik.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by Samir on 7/9/2015.
 */
@Controller
@RequestMapping(value = "/grouplife/policy")
public class GLPolicyController {

    private GLPolicyService glPolicyService;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GLPolicyFinder glPolicyFinder;

    @Autowired
    public GLPolicyController(GLPolicyService glPolicyService) {
        this.glPolicyService = glPolicyService;
    }

    @RequestMapping(value = "/openpolicysearchpage", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/policy/searchPolicy");
        modelAndView.addObject("searchResult", glPolicyService.findAllPolicy());
        return modelAndView;
    }


    @RequestMapping(value = "/viewpolicy", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage(@RequestParam("policyId") String policyId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/policy/viewPolicy");
        modelAndView.addObject("policyDetail", glPolicyService.getPolicyDetail(policyId));
        return modelAndView;
    }

    @RequestMapping(value = "/getpolicydetail/{policyId}", method = RequestMethod.GET)
    @ResponseBody
    public GLPolicyDetailDto findPolicyDetail(@PathVariable("policyId") String policyId) {
        GLPolicyDetailDto policyDetailDto = glPolicyService.getPolicyDetail(policyId);
        return policyDetailDto;
    }

    @RequestMapping(value = "/search/", method = RequestMethod.POST)
    @ResponseBody
    public List<GLPolicyDetailDto> searchPolicy(SearchGLPolicyDto searchGLPolicyDto) {
        List<GLPolicyDetailDto> policyDetailDtos = glPolicyService.searchPolicy(searchGLPolicyDto);
        return policyDetailDtos;
    }

    @RequestMapping(value = "/getagentdetailfrompolicy/{policyId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "Fetch agent detail for a given Policy ID")
    public AgentDetailDto getAgentDetailFromQuotation(@PathVariable("policyId") String policyId) {
        return glPolicyService.getAgentDetail(new PolicyId(policyId));
    }

    @RequestMapping(value = "/getpremiumdetail/{policyId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To get premium detail")
    public PremiumDetailDto getPremiumDetail(@PathVariable("policyId") String policyId) {
        return glPolicyService.getPremiumDetail(new PolicyId(policyId));
    }


    @RequestMapping(value = "/getproposerdetail/{policyId}")
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To get proposer detail from proposer")
    public ProposerDto getProposerDetail(@PathVariable("policyId") String policyId) {
        return glPolicyService.getProposerDetail(new PolicyId(policyId));
    }

    @RequestMapping(value = "/downloadinsuredtemplate/{policyId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To download insured template")
    public void downloadInsuredTemplate(@PathVariable("policyId") String policyId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "GHInsuredTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook planDetailExcel = glPolicyService.getInsuredTemplateExcel(policyId);
        planDetailExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }


    @RequestMapping(value = "/getpolicynumber/{policyId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Get Policy number for a given Policy ID")
    @ResponseBody
    public Result getPolicyNumber(@PathVariable("policyId") String policyId) {
        Map policyMap = glPolicyFinder.findPolicyById(policyId);
        return Result.success("Proposal number ", policyMap.get("policyNumber") != null ? ((PolicyNumber) policyMap.get("policyNumber")).getPolicyNumber() : "");
    }

    @RequestMapping(value = "/downloadmandatorydocument/{gridfsdocid}", method = RequestMethod.GET)
    public void downloadMandatoryDocument(@PathVariable("gridfsdocid") String gridfsDocId, HttpServletResponse response) throws IOException {
        GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(gridfsDocId)));
        response.reset();
        response.setContentType(gridFSDBFile.getContentType());
        response.setHeader("content-disposition", "attachment; filename=" + gridFSDBFile.getFilename() + "");
        OutputStream outputStream = response.getOutputStream();
        IOUtils.copy(gridFSDBFile.getInputStream(), outputStream);
        outputStream.flush();
        outputStream.close();
    }

    @RequestMapping(value = "/getmandatorydocuments/{policyId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list mandatory documents which is being configured in Mandatory Document SetUp")
    public List<GLProposalMandatoryDocumentDto> findMandatoryDocuments(@PathVariable("policyId") String policyId) {
        List<GLProposalMandatoryDocumentDto> ghProposalMandatoryDocumentDtos = glPolicyService.findMandatoryDocuments(policyId);
        return ghProposalMandatoryDocumentDtos;
    }
}
