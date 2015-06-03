package com.pla.core.domain.service;

import com.pla.sharedkernel.exception.ProcessInfoException;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.core.domain.model.generalinformation.ProductLineGeneralInformation;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Samir on 5/31/2015.
 */
@Service(value = "processInfoAdapter")
public class ProcessInfoAdapterImpl implements IProcessInfoAdapter {

    private GeneralInformationService generalInformationService;

    @Autowired
    public ProcessInfoAdapterImpl(GeneralInformationService generalInformationService){
        this.generalInformationService = generalInformationService;
    }

    @Override
    public int getPurgeTimePeriod(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType) throws ProcessInfoException {
        ProductLineGeneralInformation productLineGeneralInformation =  generalInformationService.findProductLineInformationByLineOfBusinessId(lineOfBusinessEnum);
        return productLineGeneralInformation.getProductLineProcessItemValue(processType);
    }

    @Override
    public int getClosureTimePeriod(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType) throws ProcessInfoException {
        ProductLineGeneralInformation productLineGeneralInformation = generalInformationService.findProductLineInformationByLineOfBusinessId(lineOfBusinessEnum);
        return productLineGeneralInformation.getProductLineProcessItemValue(processType);
    }

    @Override
    public int getDaysForFirstReminder(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType) throws ProcessInfoException {
        ProductLineGeneralInformation productLineGeneralInformation = generalInformationService.findProductLineInformationByLineOfBusinessId(lineOfBusinessEnum);
        return productLineGeneralInformation.getProductLineProcessItemValue(processType);
    }

    @Override
    public int getDaysForSecondReminder(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType) throws ProcessInfoException {
        ProductLineGeneralInformation productLineGeneralInformation = generalInformationService.findProductLineInformationByLineOfBusinessId(lineOfBusinessEnum);
        return productLineGeneralInformation.getProductLineProcessItemValue(processType);
    }

}
