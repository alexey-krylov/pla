package com.pla.individuallife.policy.presentation.controller;

import com.mongodb.gridfs.GridFSDBFile;
import com.pla.individuallife.policy.finder.ILPolicyFinder;
import com.pla.individuallife.policy.presentation.dto.ILPolicyDto;
import com.pla.individuallife.policy.presentation.dto.ILPolicyMailDto;
import com.pla.individuallife.policy.presentation.dto.PolicyDetailDto;
import com.pla.individuallife.policy.presentation.dto.SearchILPolicyDto;
import com.pla.individuallife.policy.service.ILPolicyService;
import com.pla.individuallife.proposal.presentation.dto.ILProposalMandatoryDocumentDto;
import com.pla.sharedkernel.identifier.PolicyId;
import com.pla.sharedkernel.service.EmailAttachment;
import com.pla.sharedkernel.service.MailService;
import com.wordnik.swagger.annotations.ApiOperation;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.io.IOUtils;
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

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Admin on 8/4/2015.
 */
@Controller
@RequestMapping(value = "/individuallife/policy")
public class ILPolicyController {

    private ILPolicyService ilPolicyService;

    @Autowired
    private ILPolicyFinder ilPolicyFinder;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private MailService mailService;

    @Autowired
    public ILPolicyController(ILPolicyService ilPolicyService) {
        this.ilPolicyService = ilPolicyService;
    }

    @RequestMapping(value = "/openpolicysearchpage", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/policy/searchPolicy");
        modelAndView.addObject("searchResult", ilPolicyService.findAllPolicy());
        modelAndView.addObject("searchCriteria", new SearchILPolicyDto());
        return modelAndView;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView searchPolicy(SearchILPolicyDto searchILPolicyDto) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/policy/searchPolicy");
        List<PolicyDetailDto> policyDetailDtos = ilPolicyService.searchPolicy(searchILPolicyDto);
        modelAndView.addObject("searchResult", policyDetailDtos);
        modelAndView.addObject("searchCriteria", searchILPolicyDto);
        return modelAndView;
    }

    @RequestMapping(value = "/viewpolicy", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage(@RequestParam("policyId") String policyId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/policy/viewPolicy");
        modelAndView.addObject("policyDetail", ilPolicyService.getPolicyDetail(policyId));
        return modelAndView;
    }

    @RequestMapping(value = "/getPage/{pageName}", method = RequestMethod.GET)
    public ModelAndView proposal(@PathVariable("pageName") String pageName) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/policy/" + pageName);
        return modelAndView;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/view")
    public ModelAndView viewProposal() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/policy/createProposal");
        return modelAndView;
    }

    @RequestMapping(value = "/getpolicydetail/{policyId}", method = RequestMethod.GET)
    @ResponseBody
    public PolicyDetailDto findPolicyDetail(@PathVariable("policyId") String policyId) {
        PolicyDetailDto policyDetailDto = ilPolicyService.getPolicyDetail(policyId);
        return policyDetailDto;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/getpolicy/{policyId}")
    @ApiOperation(httpMethod = "GET", value = "This call for edit Policy screen.")
    @ResponseBody
    public ILPolicyDto getPolicyById(@PathVariable("policyId") PolicyId policyId) {
        ILPolicyDto dto = ilPolicyFinder.getPolicyById(policyId);
        checkArgument(dto != null, "Policy not found");
        return dto;
    }


    @RequestMapping(value = "/getmandatorydocuments/{policyId}", method = RequestMethod.GET)
    @ResponseBody
    @ApiOperation(httpMethod = "GET", value = "To list mandatory documents which is being configured in Mandatory Document SetUp")
    public List<ILProposalMandatoryDocumentDto> findMandatoryDocuments(@PathVariable("policyId") String policyId) {
        List<ILProposalMandatoryDocumentDto> ilProposalMandatoryDocumentDtos = ilPolicyService.findMandatoryDocuments(policyId);
        return ilProposalMandatoryDocumentDtos;
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

    @RequestMapping(value = "/openprintpolicy", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView openPrintPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/policy/printPolicy");
        return modelAndView;
    }

    @RequestMapping(value = "/openemailpolicy/{policyId}", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView openEmail(@PathVariable("policyId") String policyId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/policy/emailPolicy");
        modelAndView.addObject("mailContent", ilPolicyService.getPreScriptedEmail(policyId));
        return modelAndView;
    }

    @RequestMapping(value = "/printpolicy/{policyId}/{documents}", method = RequestMethod.GET)
    public void downloadPlanDetail(@PathVariable("policyId") String policyId,@PathVariable("documents") List<String> documents, HttpServletResponse response) throws IOException, JRException {
        response.reset();
        response.setContentType("application/pdf");
        response.setHeader("content-disposition", "attachment; filename=" + "IL_Policy.pdf" + "");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(ilPolicyService.getPolicyDocument(new PolicyId(policyId)));
        outputStream.flush();
        outputStream.close();
    }



    //TODO
    /**
     * Quotation Shared with Client (This status should be updated on first click for print/email Quotation and the date should also
     * be updated.
     * Subsequent clicks should have no impact the Status or the Date
     */
    @RequestMapping(value = "/emailpolicy", method = RequestMethod.POST)
    @ResponseBody
    public Result emailPolicy(@RequestBody ILPolicyMailDto mailDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.failure("Email cannot be sent due to wrong data");
        }
        try {
            byte[] policyData = ilPolicyService.getPolicyPDF(mailDto.getPolicyId());
            String fileName = "PolicyNo-" + mailDto.getPolicyNumber() + ".pdf";
            File file = new File(fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(policyData);
            fileOutputStream.flush();
            fileOutputStream.close();
            EmailAttachment emailAttachment = new EmailAttachment(fileName, "application/pdf", file);
            mailService.sendMailWithAttachment(mailDto.getSubject(), mailDto.getMailContent(), Arrays.asList(emailAttachment), mailDto.getRecipientMailAddress());
            file.delete();
            return Result.success("Email sent successfully");

        } catch (Exception e) {
            Result.failure(e.getMessage());
        }
        return Result.success("Email sent successfully");
    }
}
