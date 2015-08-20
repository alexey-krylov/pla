package com.pla.individuallife.policy.presentation.controller;

import com.mongodb.gridfs.GridFSDBFile;
import com.pla.individuallife.policy.finder.ILPolicyFinder;
import com.pla.individuallife.policy.presentation.dto.ILPolicyDto;
import com.pla.individuallife.policy.presentation.dto.PolicyDetailDto;
import com.pla.individuallife.policy.presentation.dto.SearchILPolicyDto;
import com.pla.individuallife.policy.service.ILPolicyService;
import com.pla.individuallife.proposal.presentation.dto.ILProposalMandatoryDocumentDto;
import com.pla.sharedkernel.identifier.PolicyId;
import com.wordnik.swagger.annotations.ApiOperation;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.io.IOUtils;
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
    public ILPolicyController(ILPolicyService ilPolicyService) {
        this.ilPolicyService = ilPolicyService;
    }

    @RequestMapping(value = "/openpolicysearchpage", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/individuallife/policy/searchPolicy");
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

    @RequestMapping(value = "/printpolicy/{policyId}", method = RequestMethod.GET)
    public void downloadPlanDetail(@PathVariable("policyId") String policyId, HttpServletResponse response) throws IOException, JRException {
        response.reset();
        response.setContentType("application/pdf");
        response.setHeader("content-disposition", "attachment; filename=" + "policy.pdf" + "");
        OutputStream outputStream = response.getOutputStream();
        outputStream.write(ilPolicyService.getPolicyDocument(new PolicyId(policyId)));
        outputStream.flush();
        outputStream.close();

    }
}
