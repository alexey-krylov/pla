package com.pla.grouplife.endorsement.presentation.controller;

import com.pla.grouplife.endorsement.application.service.GLEndorsementService;
import com.pla.grouplife.sharedresource.dto.GLPolicyDetailDto;
import com.pla.grouplife.sharedresource.dto.SearchGLPolicyDto;
import com.pla.grouplife.sharedresource.model.GLEndorsementType;
import com.pla.sharedkernel.identifier.EndorsementId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by Samir on 8/4/2015.
 */
@Controller
@RequestMapping(value = "/grouplife/endorsement")
public class GroupLifeEndorsementController {

    @Autowired
    private GLEndorsementService glEndorsementService;


    @RequestMapping(value = "/openpolicysearchpage", method = RequestMethod.GET)
    public ModelAndView openPolicySearchPage() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/endorsement/searchPolicy");
        modelAndView.addObject("searchCriteria",new SearchGLPolicyDto());
        return modelAndView;
    }

    @RequestMapping(value = "/searchpolicy", method = RequestMethod.POST)
    @ResponseBody
    public List<GLPolicyDetailDto> searchPolicy(SearchGLPolicyDto searchGLPolicyDto) {
        List<GLPolicyDetailDto> policyDetailDtos = glEndorsementService.searchPolicy(searchGLPolicyDto);
        return policyDetailDtos;
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
    public ModelAndView openCreateEndorsement() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pla/grouplife/endorsement/createEndorsement");
        return modelAndView;
    }

    @RequestMapping(value = "/getallendorsementype", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, String>> getAllEndorsementType() {
        return GLEndorsementType.getAllEndorsementType();
    }

}
