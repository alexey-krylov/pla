package com.pla.grouplife.endorsement.application.service.excel.generator;

import com.pla.sharedkernel.util.ExcelGeneratorUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.util.List;
import java.util.Map;

/**
 * Created by Samir on 8/10/2015.
 */
public abstract class AbstractGLEndorsementExcelGenerator implements GLEndorsementExcelGenerator {

    protected HSSFWorkbook createExcel(List<String> headers, List<Map<Integer, String>> rowCellData, Map<Integer, List<String>> constraintCellData) {
        return ExcelGeneratorUtil.generateExcelWithDvConstraintCell(headers, rowCellData, constraintCellData);
    }

}
