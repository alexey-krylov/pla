package com.pla.core.domain.service;

import com.pla.publishedlanguage.domain.model.PremiumFrequency;
import com.pla.sharedkernel.domain.model.ProductLineProcessType;
import com.pla.sharedkernel.exception.ProcessInfoException;
import com.pla.sharedkernel.domain.model.ProcessType;
import com.pla.core.domain.model.generalinformation.ProductLineGeneralInformation;
import com.pla.publishedlanguage.contract.IProcessInfoAdapter;
import com.pla.sharedkernel.identifier.LineOfBusinessEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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
        return productLineGeneralInformation.getProductLineProcessItemValue(processType, ProductLineProcessType.PURGE_TIME_PERIOD);
    }

    @Override
    public int getClosureTimePeriod(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType) throws ProcessInfoException {
        ProductLineGeneralInformation productLineGeneralInformation = generalInformationService.findProductLineInformationByLineOfBusinessId(lineOfBusinessEnum);
        return productLineGeneralInformation.getProductLineProcessItemValue(processType, ProductLineProcessType.CLOSURE);
    }

    @Override
    public int getDaysForFirstReminder(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType) throws ProcessInfoException {
        ProductLineGeneralInformation productLineGeneralInformation = generalInformationService.findProductLineInformationByLineOfBusinessId(lineOfBusinessEnum);
        return productLineGeneralInformation.getProductLineProcessItemValue(processType,ProductLineProcessType.FIRST_REMAINDER );
    }

    @Override
    public int getDaysForSecondReminder(LineOfBusinessEnum lineOfBusinessEnum, ProcessType processType) throws ProcessInfoException {
        ProductLineGeneralInformation productLineGeneralInformation = generalInformationService.findProductLineInformationByLineOfBusinessId(lineOfBusinessEnum);
        return productLineGeneralInformation.getProductLineProcessItemValue(processType,ProductLineProcessType.SECOND_REMAINDER );
    }

    @Override
    public int getPolicyLapseTimePeriod(LineOfBusinessEnum lineOfBusinessEnum, PremiumFrequency premiumFrequency) throws ProcessInfoException {
        ProductLineGeneralInformation productLineGeneralInformation = generalInformationService.findProductLineInformationByLineOfBusinessId(lineOfBusinessEnum);
        return productLineGeneralInformation.getPremiumFollowUpFrequencyLineItem(premiumFrequency, ProductLineProcessType.LAPSE);
    }

    @Override
    public int getDaysForFirstPremiumReminder(LineOfBusinessEnum lineOfBusinessEnum, PremiumFrequency premiumFrequency) throws ProcessInfoException {
        ProductLineGeneralInformation productLineGeneralInformation = generalInformationService.findProductLineInformationByLineOfBusinessId(lineOfBusinessEnum);
        return productLineGeneralInformation.getPremiumFollowUpFrequencyLineItem(premiumFrequency, ProductLineProcessType.FIRST_REMAINDER);
    }

    @Override
    public int getDaysForSecondPremiumReminder(LineOfBusinessEnum lineOfBusinessEnum, PremiumFrequency premiumFrequency) throws ProcessInfoException {
        ProductLineGeneralInformation productLineGeneralInformation = generalInformationService.findProductLineInformationByLineOfBusinessId(lineOfBusinessEnum);
        return productLineGeneralInformation.getPremiumFollowUpFrequencyLineItem(premiumFrequency, ProductLineProcessType.SECOND_REMAINDER);
    }

    @Override
    public BigDecimal getServiceTaxAmount() {
        return generalInformationService.getTheServiceTaxAmount();
    }

    @Override
    public int getMoratoriumPeriod(LineOfBusinessEnum lineOfBusiness) {
        if (!LineOfBusinessEnum.GROUP_HEALTH.equals(lineOfBusiness))
            return 0;
        ProductLineGeneralInformation productLineGeneralInformation = generalInformationService.findProductLineInformationByLineOfBusinessId(lineOfBusiness);
        return productLineGeneralInformation.getMoratoriumPeriod();
    }

}
