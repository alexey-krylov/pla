package com.pla.grouplife.endorsement.application.service.excel.generator;

import com.pla.sharedkernel.identifier.EndorsementId;
import com.pla.sharedkernel.identifier.PolicyId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * Created by Samir on 8/10/2015.
 */
public interface GLEndorsementExcelGenerator {

    HSSFWorkbook generate(PolicyId policyId,EndorsementId endorsementId);

}
