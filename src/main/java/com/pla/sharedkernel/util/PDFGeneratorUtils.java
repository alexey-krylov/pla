package com.pla.sharedkernel.util;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.nthdimenzion.utils.UtilValidator.isNotEmpty;

/**
 * Created by User on 5/5/2015.
 */

public class PDFGeneratorUtils {

    public static <T> byte[] createPDFReportByList(List<T> reportData, String jasperFileName) throws IOException, JRException {
        checkArgument(isNotEmpty(jasperFileName), "Template file cannot be empty");
        JasperReport jasperReport = JasperCompileManager.compileReport(PDFGeneratorUtils.class.getClassLoader().getResourceAsStream(jasperFileName));
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap(), new JRBeanCollectionDataSource(reportData, true));
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
