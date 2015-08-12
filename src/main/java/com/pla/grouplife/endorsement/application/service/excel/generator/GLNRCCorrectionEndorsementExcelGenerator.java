package com.pla.grouplife.endorsement.application.service.excel.generator;

import com.pla.sharedkernel.identifier.EndorsementId;
import com.pla.sharedkernel.identifier.PolicyId;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;

/**
 * Created by Samir on 8/10/2015.
 */
@Component
public class GLNRCCorrectionEndorsementExcelGenerator extends AbstractGLEndorsementExcelGenerator {

    @Override
    public HSSFWorkbook generate(PolicyId policyId, EndorsementId endorsementId) {
        return null;
    }
}
