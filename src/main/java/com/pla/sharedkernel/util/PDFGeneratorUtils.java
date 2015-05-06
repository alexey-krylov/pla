package com.pla.sharedkernel.util;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 5/5/2015.
 */

public class PDFGeneratorUtils {

    public static <T> byte[] createPDFReportByList(List<T> reportData, String jasperFileName) throws IOException, JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(PDFGeneratorUtils.class.getClassLoader().getResourceAsStream(jasperFileName));
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap(), new JRBeanCollectionDataSource(reportData, true));
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
