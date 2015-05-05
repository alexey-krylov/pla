package com.pla.sharedkernel.util;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 5/5/2015.
 */

public class PDFGeneratorUtils {

    public static <T> byte[] createPDFReportByList(List<T> reportData, String jasperFileName) throws IOException, JRException {
        InputStream inputStream = PDFGeneratorUtils.class.getClassLoader().getResourceAsStream(jasperFileName);
        JasperPrint jasperPrint = JasperFillManager.fillReport(inputStream, new HashMap(), new JRBeanCollectionDataSource(reportData, false));
        OutputStream outputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdf(jasperPrint);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }
}
