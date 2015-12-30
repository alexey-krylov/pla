package com.pla.grouphealth.claim.cashless.presentation.controller;

import com.pla.core.hcp.presentation.controller.HCPRateService;
import com.pla.grouphealth.claim.cashless.application.service.GHCashlessClaimService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Mohan Sharma on 12/30/2015.
 */
@RestController
@RequestMapping(value = "/grouphealth/claim/cashless")
public class GHCashlessClaimController {

    @Autowired
    CommandGateway commandGateway;
    @Autowired
    GHCashlessClaimService ghCashlessClaimService;

    @RequestMapping(value = "/downloadGHCashlessClaimPreAuthtemplate/{ghCashlessClaimCode}", method = RequestMethod.GET)
    public void downloadInsuredTemplate(@PathVariable("ghCashlessClaimCode") String ghCashlessClaimCode, HttpServletResponse response) throws IOException {
        response.reset();
        response.setContentType("application/msexcel");
        response.setHeader("content-disposition", "attachment; filename=" + "HCPRateTemplate.xls" + "");
        OutputStream outputStream = response.getOutputStream();
        HSSFWorkbook hcpRateExcel = ghCashlessClaimService.getGHCashlessClaimPreAuthtemplate(ghCashlessClaimCode);
        hcpRateExcel.write(outputStream);
        outputStream.flush();
        outputStream.close();
    }

}
