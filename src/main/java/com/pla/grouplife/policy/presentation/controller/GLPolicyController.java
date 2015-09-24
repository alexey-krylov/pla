package com.pla.grouplife.policy.presentation.controller;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.mongodb.gridfs.GridFSDBFile;
import com.pla.grouplife.policy.application.service.GLPolicyService;
import com.pla.grouplife.policy.presentation.dto.GLPolicyMailDto;
import com.pla.grouplife.policy.presentation.model.GLPolicyDocument;
import com.pla.grouplife.policy.query.GLPolicyFinder;
import com.pla.grouplife.proposal.presentation.dto.GLProposalMandatoryDocumentDto;
import com.pla.grouplife.sharedresource.dto.*;
import com.pla.sharedkernel.domain.model.PolicyNumber;
import com.pla.sharedkernel.identifier.PolicyId;
import com.pla.sharedkernel.service.EmailAttachment;
import com.pla.sharedkernel.service.MailService;
import com.wordnik.swagger.annotations.ApiOperation;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.nthdimenzion.presentation.Result;
import org.nthdimenzion.utils.UtilValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.nthdimenzion.utils.UtilValidator.isEmpty;

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
    private MailService mailService;

    @Autowired
    public GLPolicyController(GLPolicyService glPolicyService) {
        this.glPolicyService = glPolicyService;
    }

    public static void deleteTempFileIfExists(List<EmailAttachment> emailAttachments){
        for (EmailAttachment fileTobeDeleted : emailAttachments) {
            fileTobeDeleted.getFile().delete();
        }
    }

    @RequestMapping(value = "/openpolicysearchpage", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/policy/searchPolicy");
        modelAndView.addObject("searchResult", glPolicyService.findAllPolicy());
        modelAndView.addObject("searchCriteria", new SearchGLPolicyDto());
        return modelAndView;
    }

    @RequestMapping(value = "/viewpolicy", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage(@RequestParam("policyId") String policyId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/policy/viewPolicy");
        modelAndView.addObject("policyDetail", glPolicyService.getPolicyDetail(policyId));
        return modelAndView;
    }

    @RequestMapping(value = "/getpolicydetail/{policyId}", method = RequestMethod.GET)
    @ResponseBody
    public GLPolicyDetailDto findPolicyDetail(@PathVariable("policyId") String policyId) {
        GLPolicyDetailDto policyDetailDto = glPolicyService.getPolicyDetail(policyId);
        return policyDetailDto;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public ModelAndView searchPolicy(SearchGLPolicyDto searchGLPolicyDto) {
        List<GLPolicyDetailDto> policyDetailDtos = glPolicyService.searchPolicy(searchGLPolicyDto);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/policy/searchPolicy");
        modelAndView.addObject("searchResult", policyDetailDtos);
        modelAndView.addObject("searchCriteria", searchGLPolicyDto);
        return modelAndView;
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
        response.setHeader("content-disposition", "attachment; filename=" + "Group_Life_InsuredTemplate.xls" + "");
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

    @RequestMapping(value = "/openemailpolicy/{policyId}", method = RequestMethod.GET)
    public ModelAndView openEmailPage(@PathVariable("policyId") String policyId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/policy/emailPolicy");
        modelAndView.addObject("mailContent", glPolicyService.getPreScriptedEmail(new PolicyId(policyId)));
        return modelAndView;
    }

    @RequestMapping(value = "/emailpolicy", method = RequestMethod.POST)
    @ResponseBody
    public Result emailQuotation(@RequestBody GLPolicyMailDto mailDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Email cannot be sent due to wrong data");
        }
        try {
            List<EmailAttachment> emailAttachments = glPolicyService.getPolicyPDF(new PolicyId(mailDto.getPolicyId()));
            mailService.sendMailWithAttachment(mailDto.getSubject(), mailDto.getMailContent(), emailAttachments, mailDto.getRecipientMailAddress());
           /*
           * Given the thread sleep time as 100ms to delete the files which got created while generating the Policy documents....
           * */
            System.gc();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            deleteTempFileIfExists(emailAttachments);
            return Result.success("Email sent successfully");
        } catch (Exception e) {
            Result.failure(e.getMessage());
        }
        return Result.success("Email sent successfully");
    }

    @RequestMapping(value = "/getpoilcydocument/{policyId}",method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getPolicyDocument(@PathVariable("policyId") String policyId){
        List<Map<String,Object>> glPolicyDocument= GLPolicyDocument.getDeclaredPolicyDocument();
        Map policyMap = glPolicyFinder.findPolicyById(policyId);
        String policyNumber =  policyMap.get("policyNumber") != null ? ((PolicyNumber) policyMap.get("policyNumber")).getPolicyNumber() : "";
        Map<String,Object> policyNumberMap = Maps.newLinkedHashMap();
        policyNumberMap.put("policyNumber",policyNumber);
        policyNumberMap.put("glPolicyDocument", glPolicyDocument);
        return policyNumberMap;
    }

    @RequestMapping(value = "/printpolicy/{policyId}/{documents}", method = RequestMethod.GET)
    public void printQuotation(@PathVariable("policyId") String policyId,@PathVariable("documents") List<String> documents, HttpServletResponse response) throws IOException, JRException {
        if (isEmpty(documents)){
            return;
        }
        if (documents.size()==1){
            response.reset();
            response.setContentType("application/pdf");
            OutputStream outputStream = response.getOutputStream();
            List<EmailAttachment> emailAttachments = glPolicyService.getPolicyPDF(new PolicyId(policyId), documents);
            response.setHeader("content-disposition", "attachment; filename=" + emailAttachments.get(0).getFileName());
            outputStream.write(Files.toByteArray(emailAttachments.get(0).getFile()));
            outputStream.flush();
            outputStream.close();
            deleteTempFileIfExists(emailAttachments);
            return;
        }
        response.reset();
        response.setContentType("application/zip");
        OutputStream outputStream = null;
        List<EmailAttachment> emailAttachments =   glPolicyService.getPolicyPDF(new PolicyId(policyId),documents);
        String zipFileName = "glPolicy.zip";
        ZipOutputStream zos = null;
        response.addHeader("Content-Disposition", "attachment; filename =" + zipFileName);
        if (UtilValidator.isNotEmpty(emailAttachments)){
            outputStream = response.getOutputStream();
            zos = new ZipOutputStream(outputStream);
            for (EmailAttachment inputStreamMap : emailAttachments){
                ZipEntry zipEntry = new ZipEntry(inputStreamMap.getFileName());
                zos.putNextEntry(zipEntry);
                IOUtils.copy(FileUtils.openInputStream(inputStreamMap.getFile()), zos);
                zos.closeEntry();
            }
        }
        outputStream.flush();
        if(zos!=null){
            zos.flush();
            zos.finish();
            zos.close();
        }
        response.flushBuffer();
        outputStream.close();
        /*
        * Given the thread sleep time as 100ms to delete the files which got created while generating the Policy documents....
        * */
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        deleteTempFileIfExists(emailAttachments);
    }

    @RequestMapping(value = "/openprintpolicy", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView openPrintPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/groupLife/policy/printPolicy");
        return modelAndView;
    }


}
