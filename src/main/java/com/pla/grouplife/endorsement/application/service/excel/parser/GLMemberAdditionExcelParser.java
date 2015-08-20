package com.pla.grouplife.endorsement.application.service.excel.parser;

import com.pla.sharedkernel.identifier.PolicyId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Created by Samir on 8/19/2015.
 */
@Component
public class GLMemberAdditionExcelParser extends AbstractGLEndorsementExcelParser {


    @Override
    public boolean isValidExcel(HSSFWorkbook excelFile, PolicyId policyId) {
        return false;
    }

    @Override
    protected String validateRow(Row row, List<String> headers) {
        return null;
    }
}
