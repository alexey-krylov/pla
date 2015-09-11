package com.pla.grouphealth.policy.presentation.controller;

import com.mongodb.gridfs.GridFSDBFile;
import com.pla.grouphealth.policy.application.service.GHPolicyService;
import com.pla.grouphealth.policy.presentation.dto.GHPolicyMailDto;
import com.pla.grouphealth.policy.presentation.dto.PolicyDetailDto;
import com.pla.grouphealth.policy.presentation.dto.SearchGHPolicyDto;
import com.pla.grouphealth.policy.query.GHPolicyFinder;
import com.pla.grouphealth.proposal.presentation.dto.GHProposalMandatoryDocumentDto;
import com.pla.grouphealth.sharedresource.dto.AgentDetailDto;
import com.pla.grouphealth.sharedresource.dto.GHPremiumDetailDto;
import com.pla.grouphealth.sharedresource.dto.ProposerDto;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.identifier.PolicyId;
import com.pla.sharedkernel.service.EmailAttachment;
import com.pla.sharedkernel.service.MailService;
import com.wordnik.swagger.annotations.ApiOperation;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.dom4j.DocumentException;
import org.nthdimenzion.presentation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Samir on 7/9/2015.
 */
@Controller
@RequestMapping(value = "/grouphealth/policy")
public class GHPolicyController {

    private GHPolicyService ghPolicyService;

    @Autowired
    private GHPolicyFinder ghPolicyFinder;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private MailService mailService;

    @Autowired
    public GHPolicyController(GHPolicyService ghPolicyService) {
        this.ghPolicyService = ghPolicyService;
    }

    @RequestMapping(value = "/openpolicysearchpage", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/policy/searchPolicy");
        modelAndView.addObject("searchResult", ghPolicyService.findAllPolicy());
        modelAndView.addObject("searchCriteria", new SearchGHPolicyDto());
        return modelAndView;
    }


    @RequestMapping(value = "/viewpolicy", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage(@RequestParam("policyId") String policyId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/policy/viewPolicy");
        modelAndView.addObject("policyDetail", ghPolicyService.getPolicyDetail(policyId));
        return modelAndView;
    }

    @RequestMapping(value = "/getpolicydetail/{policyId}", method = RequestMethod.GET)
    @ResponseBody
    public PolicyDetailDto findPolicyDetail(@PathVariable("policyId") String policyId) {
        PolicyDetailDto policyDetailDto = ghPolicyService.getPolicyDetail(policyId);
        return policyDetailDto;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView searchPolicy(SearchGHPolicyDto searchGHPolicyDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/policy/searchPolicy");
        List<PolicyDetailDto> policyDetailDtos = ghPolicyService.searchPolicy(searchGHPolicyDto);
        modelAndView.addObject("searchResult", policyDetailDtos);
        modelAndView.addObject("searchCriteria", searchGHPolicyDto);
        return modelAndView;
    }

    @RequestMapping(value = "/getagentdetailfrompolicy/{policyId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "Fetch agent detail for a given Policy ID")
    public AgentDetailDto getAgentDetailFromQuotation(@PathVariable("policyId") String policyId) {
        return ghPolicyService.getAgentDetail(new PolicyId(policyId));
    }

    @RequestMapping(value = "/getpremiumdetail/{policyId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To get premium detail")
    public GHPremiumDetailDto getPremiumDetail(@PathVariable("policyId") String policyId) {
        return ghPolicyService.getPremiumDetail(new PolicyId(policyId));
    }


    @RequestMapping(value = "/getproposerdetail/{policyId}")
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To get proposer detail from proposer")
    public ProposerDto getProposerDetail(@PathVariable("policyId") String policyId) {
        return ghPolicyService.getProposerDetail(new PolicyId(policyId));
    }

    @RequestMapping(value = "/downloadinsuredtemplate/{policyId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "To download insured template")
    public void downloadInsuredTemplate(@PathVariable("policyId") String policyId, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "GHInsuredTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook planDetailExcel = ghPolicyService.getInsuredTemplateExcel(policyId);
        planDetailExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }


    @RequestMapping(value = "/getpolicynumber/{policyId}", method = RequestMethod.GET)
    @ApiOperation(httpMethod = "GET", value = "Get Policy number for a given Policy ID")
    @ResponseBody
    public Result getPolicyNumber(@PathVariable("policyId") String policyId) {
        Map policyMap = ghPolicyFinder.findPolicyById(policyId);
        return Result.success("Policy number ", policyMap.get("policyNumber") != null ? ((PolicyNumber) policyMap.get("policyNumber")).getPolicyNumber() : "");
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
    public List<GHProposalMandatoryDocumentDto> findMandatoryDocuments(@PathVariable("policyId") String policyId) {
        List<GHProposalMandatoryDocumentDto> ghProposalMandatoryDocumentDtos = ghPolicyService.findMandatoryDocuments(policyId);
        return ghProposalMandatoryDocumentDtos;
    }

    @RequestMapping(value = "/openemailpolicy/{policyId}", method = RequestMethod.GET)
    public ModelAndView openEmailPage(@PathVariable("policyId") String policyId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouphealth/quotation/emailQuotation");
        modelAndView.addObject("mailContent", ghPolicyService.getPreScriptedEmail(new PolicyId(policyId)));
        return modelAndView;
    }

    @RequestMapping(value = "/emailpolicy", method = RequestMethod.POST)
    @ResponseBody
    public Result emailPolicy(@RequestBody GHPolicyMailDto mailDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Email cannot be sent due to wrong data");
        }
        try {
            byte[] policyPDF = ghPolicyService.getPolicyPDF(mailDto.getPolicyId());
            emailPolicy(policyPDF, mailDto);
            return Result.success("Email sent successfully");

        } catch (Exception e) {
            Result.failure(e.getMessage());
        }
        return Result.success("Email sent successfully");
    }

    @RequestMapping(value = "/printquotation/{policyId}", method = RequestMethod.GET)
    public void printQuotation(@PathVariable("policyId") String policyId, HttpServletResponse response) throws IOException, JRException {
        response.reset();
        response.setContentType("application/pdf");
        response.setHeader("content-disposition", "attachment; filename=" + "GHPolicy.pdf" + "");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(ghPolicyService.getPolicyPDF(policyId));
        outputStream.flush();
        outputStream.close();
    }

    private void emailPolicy(byte[] quotationData, GHPolicyMailDto mailDto) throws IOException, DocumentException {
        String fileName = "PolicyNo-" + mailDto.getPolicyNumber() + ".pdf";
        File file = new File(fileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(quotationData);
        fileOutputStream.flush();
        fileOutputStream.close();
        EmailAttachment emailAttachment = new EmailAttachment(fileName, "application/pdf", file);
        mailService.sendMailWithAttachment(mailDto.getSubject(), mailDto.getMailContent(), Arrays.asList(emailAttachment), mailDto.getRecipientMailAddress());
        file.delete();
    }

}
