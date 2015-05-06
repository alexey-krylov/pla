package com.pla.sharedkernel.util;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 5/5/2015.
 */

public class PDFGeneratorUtils {

    public static <T> byte[] createPDFReportByList(List<T> reportData, String jasperFileName) throws IOException, JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(PDFGeneratorUtils.class.getClassLoader().getResourceAsStream("jasperpdf/template/grouplife/glQuotation.jrxml"));
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap(), new JRBeanCollectionDataSource(reportData, true));
        OutputStream outputStream = new ByteArrayOutputStream();
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
